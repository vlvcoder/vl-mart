package com.antonov.vlmart.service.config;

import com.antonov.vlmart.config.*;
import com.antonov.vlmart.model.enums.CONFIG_SECTION;
import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.enums.STAFF_ROLE;
import com.antonov.vlmart.render.ConfigItemWrap;
import com.antonov.vlmart.render.ConfigSectionWrap;
import com.antonov.vlmart.render.ConfigWrap;
import com.antonov.vlmart.service.buyer.BuyerGenerator;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.service.work.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConfigsBuilderImpl implements ConfigsBuilder {
    private final MainConfiguration mainConfiguration;
    private final QuantConfiguration quantConfiguration;
    private final StaffConfiguration staffConfiguration;
    private final BuyerConfiguration buyerConfiguration;
    private final SupplyConfiguration supplyConfiguration;
    private final BuyerGenerator buyerGenerator;
    private final MartService martService;
    private final EmployeeService employeeService;

    @Override
    public ConfigWrap configs() {
        var wrap = new ConfigWrap();
        wrap.setSections(
                Arrays.stream(CONFIG_SECTION.values())
                        .map(SECTION -> new ConfigSectionWrap(SECTION.name(), SECTION.getTitle()))
                        .collect(Collectors.toList()));

        List<ConfigItemWrap> list = new ArrayList<>();
        list.add(ConfigItemWrap.builder()
                .name("position_duration_shelf")
                .title("Время на сбор покупателем одной позиции товара")
                .section(CONFIG_SECTION.BUYER)
                .current(buyerConfiguration.getPosition_duration_shelf())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("position_duration_cash")
                .title("Время пробития на кассе одной позиции товара")
                .section(CONFIG_SECTION.BUYER)
                .current(buyerConfiguration.getPosition_duration_cash())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("income_interval_sec_min")
                .title("Минимальный интервал прихода следующего покупателя")
                .section(CONFIG_SECTION.BUYER)
                .current(buyerConfiguration.getIncome_interval_sec_min())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("income_interval_sec_min")
                .title("Максимальный интервал прихода следующего покупателя")
                .section(CONFIG_SECTION.BUYER)
                .current(buyerGenerator.getIncome_interval_sec_max())
                .max(buyerConfiguration.getIncome_interval_sec_min())
                .increaseLikesStep(buyerConfiguration.getSpeedup_like_level())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("unit_count_min")
                .title("Минимальное количество единиц каждого товара в покупке")
                .section(CONFIG_SECTION.BUYER)
                .current(1)
                .build());
        list.add(ConfigItemWrap.builder()
                .name("unit_count_max")
                .title("Максимальное количество единиц каждого товара в покупке")
                .section(CONFIG_SECTION.BUYER)
                .current(buyerGenerator.getUnit_count_max())
                .max(buyerConfiguration.getUnit_count_max() + buyerConfiguration.getMax_purchase_count())
                .increaseLikesStep(buyerConfiguration.getUnit_speedup_like_level())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("good_group")
                .title("Количество наименований товара")
                .section(CONFIG_SECTION.ASSORTMENT)
                .current(martService.shopPlaces().size())
                .max(GOOD.values().length)
                .increaseLikesStep(mainConfiguration.getGood_group_update_likes())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("supply_delivery_duration")
                .title("Время выполнения поставки товаров")
                .section(CONFIG_SECTION.SUPPLY)
                .current(supplyConfiguration.getDuration())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("supply_delivery_duration")
                .title("Стоимость товаров у поставщика (% от стоимости товаров в магазине)")
                .section(CONFIG_SECTION.SUPPLY)
                .current(supplyConfiguration.getCost_percent())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("shelver_rest_duration")
                .title("Длительность перерывов в работе мерчендайзера")
                .section(CONFIG_SECTION.STAFF)
                .current(staffConfiguration.getShelver_rest_duration())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("cashier_rest_duration")
                .title("Длительность перерывов в работе кассира")
                .section(CONFIG_SECTION.STAFF)
                .current(staffConfiguration.getCashier_rest_duration())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("duration_placement")
                .title("Длительность выполнения задания пополнения полки")
                .section(CONFIG_SECTION.STAFF)
                .current(quantConfiguration.getPlacement())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("manager_power")
                .title("Грузоподъемность менеджера")
                .section(CONFIG_SECTION.STAFF)
                .current(STAFF_ROLE.MANAGER.power())
                .max(staffConfiguration.getManager_power_max())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("shelver_power")
                .title("Грузоподъемность мерчендайзера")
                .section(CONFIG_SECTION.STAFF)
                .current(STAFF_ROLE.SHELVER.power())
                .max(staffConfiguration.getShelver_power_max())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("shelver_power")
                .title("Грузоподъемность грузчика")
                .section(CONFIG_SECTION.STAFF)
                .current(STAFF_ROLE.LOADER.power())
                .max(staffConfiguration.getLoader_power_max())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("cashier_power")
                .title("Скорость работы кассира")
                .section(CONFIG_SECTION.STAFF)
                .current(STAFF_ROLE.CASHIER.power())
                .max(staffConfiguration.getCashier_power_max())
                .build());
        list.add(ConfigItemWrap.builder()
                .name("cashier_rest_duration")
                .title("Длительность перерыва кассира между заданиями")
                .section(CONFIG_SECTION.STAFF)
                .current(employeeService.getCashierRestDuration())
                .max(0)
                .build());
        list.add(ConfigItemWrap.builder()
                .name("shelver_rest_duration")
                .title("Длительность перерыва мерчендайзера между заданиями")
                .section(CONFIG_SECTION.STAFF)
                .current(employeeService.getShelverRestDuration())
                .max(0)
                .build());
        wrap.setItems(list);
        return wrap;
    }
}
