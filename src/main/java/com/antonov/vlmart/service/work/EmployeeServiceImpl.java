package com.antonov.vlmart.service.work;

import com.antonov.vlmart.config.StaffConfiguration;
import com.antonov.vlmart.model.area.Employee;
import com.antonov.vlmart.model.enums.STAFF_ROLE;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.model.quant.Quant;
import com.antonov.vlmart.model.staff.Staff;
import com.antonov.vlmart.render.StaffCollapsedWrap;
import com.antonov.vlmart.render.StaffFolderWrap;
import com.antonov.vlmart.render.StaffWrap;
import com.antonov.vlmart.rest.SimpleResponse;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.wrap.QuantCompleter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService, QuantCompleter {
    private final StaffConfiguration staffConfiguration;
    private final BoardService boardService;
    private final MartService martService;

    private Employee employee;

    public EmployeeServiceImpl(
            StaffConfiguration staffConfiguration,
            BoardService boardService,
            MartService martService) {
        this.staffConfiguration = staffConfiguration;
        this.boardService = boardService;
        this.martService = martService;
    }

    @Override
    public void init() {
        employee = new Employee();
        var manager = new Staff()
                .name("Менеджер")
                .role(STAFF_ROLE.MANAGER);
        employee.staffs().add(manager);
        boardService.setQuantCompleter(this);
        employee.shelverRestDuration(staffConfiguration.getShelver_rest_duration());
        employee.cashierRestDuration(staffConfiguration.getCashier_rest_duration());
    }

    @Override
    public void loopStaffs() {
        employee.staffs().stream()
                .filter(staff -> !staff.isBusy())
                .filter(staff -> System.currentTimeMillis() > staff.restFinishMillis())
                .forEach(staff -> {
                    switch (staff.role()) {
                        case SHELVER:
                            loopShelver(staff);
                            break;
                        case CASHIER:
                            loopCashier(staff);
                            break;
                        case COOK:
                            break;
                    }
                });
    }

    private void loopShelver(Staff staff) {
        var places = martService.shopPlaces().stream()
                .filter(p -> p.capacity() > p.stock())
                .sorted((p1, p2) -> {
                    var q1 = (double) (p1.capacity() - p1.stock() - boardService.lockedQuant(p1.good())) / (double) p1.capacity();
                    var q2 = (double) (p2.capacity() - p2.stock() - boardService.lockedQuant(p2.good())) / (double) p2.capacity();
                    return Double.compare(q2, q1);
                })
                .collect(Collectors.toList());

        for (Place place : places) {
            if (boardService.createPlacementQuant(staff, place.good()) != null) {
                return;
            }
        }
    }

    private void loopCashier(Staff staff) {
        boardService.createCashCheckoutQuant(staff);
    }

    @Override
    public Staff manager() {
        return employee
                .staffs().stream()
                .filter(s -> s.role().equals(STAFF_ROLE.MANAGER))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String employeeToString() {
        return employee.statusText();
    }

    @Override
    public Staff createCashier() {
        long n = employee.staffs().stream()
                .filter(staff -> staff.role() == STAFF_ROLE.CASHIER)
                .count() + 1;
        return createCashier("Кассир-" + n);
    }

    @Override
    public Staff createCashier(String name) {
        if (employee.staffs().stream().anyMatch(staff -> staff.name().equals(name))) {
            return null;
        }

        var cashier = new Staff()
                .name(name)
                .role(STAFF_ROLE.CASHIER);
        employee.staffs().add(cashier);
        return cashier;
    }

    @Override
    public Staff createShelver() {
        long n = employee.staffs().stream()
                .filter(staff -> staff.role() == STAFF_ROLE.SHELVER)
                .count() + 1;
        return createShelver("Мерч-" + n);
    }

    @Override
    public Staff createShelver(String name) {
        if (employee.staffs().stream().anyMatch(staff -> staff.name().equals(name))) {
            return null;
        }
        Staff staff = new Staff();
        staff.name(name);
        staff.role(STAFF_ROLE.SHELVER);
        employee.staffs().add(staff);
        return staff;
    }

    @Override
    public StaffFolderWrap staffs() {
        var list = employee.staffs().stream()
                .map(StaffWrap::new)
                .collect(Collectors.toList());

        var map = list.stream()
                .collect(Collectors.groupingBy(
                        staff -> StaffCollapsedWrap.builder()
                                .role(staff.getRole())
                                .roleTitle(staff.getRole().title())
                                .attrColorClass(staff.getAttrColorClass())
                                .attrIconClass(staff.getAttrIconClass())
                                .build(),
                        Collectors.counting()));

        for (var entry : map.entrySet()) {
            entry.getKey().setCount(entry.getValue());
            long busyCount = list.stream()
                    .filter(staff -> staff.getRole() == entry.getKey().getRole() && staff.isBusy())
                    .count();
            entry.getKey().setBusyCount(busyCount);
        }

        return StaffFolderWrap.builder()
                .staffs(list)
                .staffsCollapsed(map.keySet().stream()
                        .sorted()
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public SimpleResponse checkManagerBusy() {
        var staff = manager();
        var response = new SimpleResponse();
        if (staff.isBusy()) {
            response.setMessage(String.format("Сотрудник [%s] выполняет другое задание", staff.name()));
            response.setSuccess(false);
        }
        return response;
    }

    @Override
    public void goWork(String name) {
        employee.staffs().stream()
                .filter(staff -> staff.name().equals(name) && !staff.isBusy())
                .findFirst()
                .ifPresent(staff -> staff.restFinishMillis(0));
    }

    @Override
    public void fillRaise(int quantity) {
        STAFF_ROLE.SHELVER.power(STAFF_ROLE.SHELVER.power() + quantity);
        STAFF_ROLE.LOADER.power(STAFF_ROLE.LOADER.power() + quantity);
        STAFF_ROLE.MANAGER.power(STAFF_ROLE.MANAGER.power() + quantity);
    }

    @Override
    public synchronized void quantStart(Staff staff) {
        staff.isBusy(true);
    }

    @Override
    public synchronized void quantComplete(Staff staff) {
        switch (staff.role()) {
            case CASHIER:
                staff.restFinishMillis(System.currentTimeMillis() + staffConfiguration.getCashier_rest_duration() * 1000L);
                break;
            case SHELVER:
                staff.restFinishMillis(System.currentTimeMillis() + staffConfiguration.getShelver_rest_duration() * 1000L);
                break;
        }
        staff.isBusy(false);
    }

    @Override
    public List<Staff> staffs(STAFF_ROLE role) {
        return employee.staffs().stream()
                .filter(staff -> staff.role() == role)
                .collect(Collectors.toList());
    }

    @Override
    public Staff createLoader() {
        long n = employee.staffs().stream()
                .filter(staff -> staff.role() == STAFF_ROLE.LOADER)
                .count() + 1;
        return createLoader("Грузчик-" + n);
    }

    @Override
    public synchronized void cleanBusyStaffs() {
        var realBusyStaffs = boardService.quants().stream()
                .map(Quant::staff)
                .collect(Collectors.toList());
        employee.staffs().stream()
                .filter(staff -> staff.isBusy() && !realBusyStaffs.contains(staff))
                .forEach(staff -> staff.isBusy(false));
    }

    @Override
    public void cashRaise(int quantity) {
        STAFF_ROLE.CASHIER.power(STAFF_ROLE.CASHIER.power() + quantity);
    }

    @Override
    public int getCashierRestDuration() {
        return employee.cashierRestDuration();
    }

    @Override
    public int getShelverRestDuration() {
        return employee.shelverRestDuration();
    }

    @Override
    public SimpleResponse decreaseRestDuration(STAFF_ROLE role, int value) {
        SimpleResponse response = new SimpleResponse();
        int current;
        switch (role) {
            case CASHIER:
                current = employee.cashierRestDuration() - value;
                if (current < 0) {
                    response.setSuccess(false);
                    response.setMessage("Достигнуто минимальное значение");
                }
                else {
                    employee.cashierRestDuration(current);
                    response.setMessage("Длительность перерыва кассира между заданиями = " + current + " сек");
                }
                break;
            case SHELVER:
                current = employee.shelverRestDuration() - value;
                if (current < 0) {
                    response.setSuccess(false);
                    response.setMessage("Достигнуто минимальное значение");
                }
                else {
                    employee.shelverRestDuration(current);
                    response.setMessage("Длительность перерыва мерчендайзера между заданиями = " + current + " сек");
                }
                break;
        }

        return response;
    }

    private Staff createLoader(String name) {
        if (employee.staffs().stream().anyMatch(staff -> staff.name().equals(name))) {
            return null;
        }

        var loader = new Staff()
                .name(name)
                .role(STAFF_ROLE.LOADER);
        employee.staffs().add(loader);
        boardService.unloadStart(false);
        return loader;
    }

}
