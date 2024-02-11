package com.antonov.vlmart.service.business;

import com.antonov.vlmart.config.MainConfiguration;
import com.antonov.vlmart.config.ShopCapacityConfiguration;
import com.antonov.vlmart.config.StaffConfiguration;
import com.antonov.vlmart.config.StoreCapacityConfiguration;
import com.antonov.vlmart.model.enums.*;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.model.staff.Staff;
import com.antonov.vlmart.render.ExpandWrap;
import com.antonov.vlmart.render.RaiseWrap;
import com.antonov.vlmart.render.UpgradeWrap;
import com.antonov.vlmart.rest.SimpleResponse;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.service.work.EmployeeService;
import com.antonov.vlmart.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UpgradeServiceImpl implements UpgradeService {
    private final StaffConfiguration staffConfiguration;
    private final MainConfiguration mainConfiguration;
    private final ShopCapacityConfiguration shopCapacityConfiguration;
    private final StoreCapacityConfiguration storeCapacityConfiguration;
    private final EmployeeService employeeService;
    private final BusinessService businessService;
    private final MartService martService;

    public UpgradeServiceImpl(
            StaffConfiguration staffConfiguration,
            MainConfiguration mainConfiguration,
            ShopCapacityConfiguration shopCapacityConfiguration,
            StoreCapacityConfiguration storeCapacityConfiguration,
            EmployeeService employeeService,
            BusinessService businessService,
            MartService martService) {
        this.staffConfiguration = staffConfiguration;
        this.mainConfiguration = mainConfiguration;
        this.shopCapacityConfiguration = shopCapacityConfiguration;
        this.storeCapacityConfiguration = storeCapacityConfiguration;
        this.employeeService = employeeService;
        this.businessService = businessService;
        this.martService = martService;
    }

    @Override
    public List<RaiseWrap> fillRaising() {
        List<RaiseWrap> list = new ArrayList<>();
        list.add(new RaiseWrap(4, "+4", Utils.longNumberFormat(staffConfiguration.getFill_raising_price_4())));
        list.add(new RaiseWrap(8, "+8", Utils.longNumberFormat(staffConfiguration.getFill_raising_price_8())));
        list.add(new RaiseWrap(16, "+16", Utils.longNumberFormat(staffConfiguration.getFill_raising_price_16())));
        return list;
    }

    @Override
    public SimpleResponse fillRaise(int quantity) {
        int price;
        SimpleResponse response = new SimpleResponse();
        switch (quantity) {
            case 4:
                price = staffConfiguration.getFill_raising_price_4();
                break;
            case 8:
                price = staffConfiguration.getFill_raising_price_8();
                break;
            case 16:
                price = staffConfiguration.getFill_raising_price_16();
                break;
            default:
                response.setMessage("Неверное значение quantity");
                response.setSuccess(false);
                return response;
        }
        if (businessService.moneyCount() < price) {
            response.setMessage(String.format("Недостаточно средств.<br>Стоимость увеличения грузоподъемности составляет %s",
                    Utils.longNumberFormat(price)));
            response.setSuccess(false);
            return response;
        }
        if (STAFF_ROLE.MANAGER.power() + quantity > staffConfiguration.getManager_power_max() ||
                STAFF_ROLE.SHELVER.power() + quantity > staffConfiguration.getShelver_power_max()
        ) {
            response.setMessage("Достигнута максимальная грузоподъемность");
            response.setSuccess(false);
            return response;
        }

        businessService.spendMoney(price);
        employeeService.fillRaise(quantity);
        var mess = String.format(
                "Грузоподъемность Грузчика = %d<br>Грузоподъемность мерчендайзера = %d<br>Грузоподъемность менеджера     = %d",
                STAFF_ROLE.LOADER.power(), STAFF_ROLE.SHELVER.power(), STAFF_ROLE.MANAGER.power()
        );
        response.setMessage(mess);
        return response;
    }

    @Override
    public List<RaiseWrap> cashRaising() {
        List<RaiseWrap> list = new ArrayList<>();
        list.add(new RaiseWrap(1, "+1", Utils.longNumberFormat(staffConfiguration.getCash_raising_price_1())));
        list.add(new RaiseWrap(3, "+3", Utils.longNumberFormat(staffConfiguration.getCash_raising_price_3())));
        return list;
    }

    @Override
    public SimpleResponse cashRaise(int quantity) {
        int price;
        SimpleResponse response = new SimpleResponse();
        switch (quantity) {
            case 1:
                price = staffConfiguration.getCash_raising_price_1();
                break;
            case 3:
                price = staffConfiguration.getCash_raising_price_3();
                break;
            default:
                response.setMessage("Неверное значение quantity");
                response.setSuccess(false);
                return response;
        }
        if (businessService.moneyCount() < price) {
            response.setMessage(String.format("Недостаточно средств.<br>Стоимость увеличения скорости составляет %s",
                    Utils.longNumberFormat(price)));
            response.setSuccess(false);
            return response;
        }
        if (STAFF_ROLE.CASHIER.power() + quantity > staffConfiguration.getCashier_power_max()) {
            response.setMessage("Достигнут максимальный показатель");
            response.setSuccess(false);
            return response;
        }

        businessService.spendMoney(price);
        employeeService.cashRaise(quantity);
        var mess = String.format("Скорость работы кассира = %d", STAFF_ROLE.CASHIER.power());
        response.setMessage(mess);
        return response;
    }

    @Override
    public SimpleResponse addGood(GOOD good) {
        SimpleResponse response = new SimpleResponse();
        long targetCount = Arrays.stream(GOOD.values())
                .filter(g -> g.groupIndex() >= 0)
                .count();
        int inShopCount = martService.shopPlaces().size();
        if (inShopCount < targetCount) {
            response.setMessage("Добавление товаров будет доступно после автоматического расширения ассортимента до " + targetCount + " позиций.");
            response.setSuccess(false);
            return response;
        }

        var price = mainConfiguration.getAdd_good_price();
        if (businessService.moneyCount() < price) {
            response.setMessage(String.format("Недостаточно средств. Стоимость добавления товара составляет %s",
                    Utils.longNumberFormat(price)));
            response.setSuccess(false);
            return response;
        }
        businessService.spendMoney(price);
        martService.createPlace(good);
        response.setMessage("На полки добавлен товар: " + good.title());
        return response;
    }

    @Override
    public SimpleResponse addStaff(STAFF_ROLE role) {
        SimpleResponse response = new SimpleResponse();
        if (businessService.moneyCount() < role.cost()) {
            response.setMessage(String.format("Недостаточно средств. Стоимость найма сотрудника %S составляет %s",
                    role.title(),
                    Utils.longNumberFormat(role.cost())));
            response.setSuccess(false);
            return response;
        }
        Staff staff = null;
        switch (role) {
            case SHELVER:
                staff = employeeService.createShelver();
                break;
            case CASHIER:
                staff = employeeService.createCashier();
                break;
            case LOADER:
                staff = employeeService.createLoader();
                break;
        }

        businessService.spendMoney(role.cost());
        response.setMessage("Создан сотрудник:\n" + staff);
        return response;
    }

    @Override
    public ExpandWrap expandPrices() {
        return ExpandWrap.builder()
                .shopPlaceExpands(shopCapacityConfiguration.expandVariants())
                .storePlaceExpands(storeCapacityConfiguration.expandVariants())
                .build();
    }

    @Override
    public SimpleResponse expandPlace(SPACE_TYPE spaceType, GOOD good, PLACE_VOLUME volume) {
        var response = new SimpleResponse();
        var space = spaceType == SPACE_TYPE.SHOP ?
                shopCapacityConfiguration :
                storeCapacityConfiguration;

        int price = space.expandCostByVolume(volume);
        if (businessService.moneyCount() < price) {
            response.setMessage(
                    "Недостаточно средств. Стоимость операции составляет " +
                            Utils.longNumberFormat(price));
            response.setSuccess(false);
            return response;
        }

        businessService.spendMoney(price);
        Place place = martService.expandPlace(spaceType, good, volume);
        response.setMessage(place.toString());
        return response;
    }

    @Override
    public SimpleResponse setSupplyPlan(SUPPLY_CALL call, SUPPLY_VOLUME volume) {
        var response = new SimpleResponse();
        long money = 0;
        if (!call.isActive() && !call.isEnabled()) {
            money += call.getPrice();
        }
        if (!volume.isActive() && !volume.isEnabled()) {
            money += volume.getPrice();
        }
        if (money > businessService.moneyCount()) {
            response.setSuccess(false);
            response.setMessage("Недостаточно денег для установки указанной конфигурации.<br>Требуется: " + money);
            return response;
        }
        businessService.spendMoney(money);
        call.setEnabled(true);
        call.turnActive();
        volume.setEnabled(true);
        volume.turnActive();
        response.setMessage(String.format(
                "Установлен план заказа поставки: <br><br>%s<br>%s",
                call.getTitle(),
                volume.getTitle()));
        return response;
    }

    @Override
    public SimpleResponse decreaseRestDuration(STAFF_ROLE role, int value) {
        SimpleResponse response = new SimpleResponse();
        if (
                (role != STAFF_ROLE.SHELVER && role != STAFF_ROLE.CASHIER) ||
                        (value != 5 && value != 10)
        ) {
            response.setSuccess(false);
            response.setMessage("Неверное значение параметра");
            return response;
        }

        int money = value == 5 ?
                staffConfiguration.getRest_duration_decrease_price_5() :
                staffConfiguration.getRest_duration_decrease_price_10();

        if (money > businessService.moneyCount()) {
            response.setSuccess(false);
            response.setMessage("Недостаточно денег для установки указанной конфигурации.<br>Требуется: " + money);
            return response;
        }

        response = employeeService.decreaseRestDuration(role, value);
        if (response.isSuccess()) {
            businessService.spendMoney(money);
        }
        return response;
    }

    @Override
    public UpgradeWrap currentUpgrade() {
        return UpgradeWrap.builder()
                .addGoodPrice(Utils.longNumberFormat(mainConfiguration.getAdd_good_price()))
                .cashierRestDuration(employeeService.getCashierRestDuration())
                .shelverRestDuration(employeeService.getShelverRestDuration())
                .restDecreasePrice_5(Utils.longNumberFormat(staffConfiguration.getRest_duration_decrease_price_5()))
                .restDecreasePrice_10(Utils.longNumberFormat(staffConfiguration.getRest_duration_decrease_price_10()))
                .build();
    }
}
