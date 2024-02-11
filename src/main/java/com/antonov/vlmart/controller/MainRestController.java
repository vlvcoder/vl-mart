package com.antonov.vlmart.controller;

import com.antonov.vlmart.model.enums.*;
import com.antonov.vlmart.render.*;
import com.antonov.vlmart.rest.OpenCloseResponse;
import com.antonov.vlmart.rest.SimpleResponse;
import com.antonov.vlmart.service.RootService;
import com.antonov.vlmart.service.business.UpgradeService;
import com.antonov.vlmart.service.config.ConfigsBuilder;
import com.antonov.vlmart.service.notify.NotificationService;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.service.product.SupplyService;
import com.antonov.vlmart.service.work.BoardService;
import com.antonov.vlmart.service.work.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/rest")
@AllArgsConstructor
public class MainRestController {

    //todo  - Убрать переход в начало по ссылке добавления сотрудника

    // todo - схема магазина. графика. По филиалам. Можно - по игрокам.
    //todo Постранично: Основная, Сотрудники, схема

    //todo Многопользовательский режим. Список онлайн-пользователей. Текущий статус, время в игре. Регулировать, Что показывать в статусе: кол-во сотрудников, открыт/закрыт
    // Синхронная игра
    // Филиалы!!! Вкладки. Можно - команда игроков развивают свою сеть. Поставка - Одна на сеть. Оптовая скидка. Можно переводить сотрудников в другой магазин. Общий бюджет??
    // Несколько поставщиков
    // Отделы

    // BuyerIncash synchronized
    // Полный сброс - сбросить дизлайки, деньги, лайки. Оставить остатки в магазине и на складе, сотрудников, темп прихода покупателей
    // темп прихода покупателей. Отображать. РАспродажи...

    //todo Выбор фона окна
    //todo fillraise - отдельно для каждого магазина

    //todo
    // ПОСТАВКА
    //ЗАКАЗ
    //	Заказ поставки делает менеджер
    //	Автоматический заказ по достижении низкого уровня одного из товаров на складе
    //	Автоматический заказ по завершению разгрузки
    //ОБЪЕМ ПОСТАВКИ
    //	Количество товаров, предоставленное поставщиком
    //	Полное заполнение склада

    // https://app.logo.com/dashboard/logo_0127ca9b-5b72-4ebb-a959-0bc4c1be5096/your-logo-files




//    https://www.epokraskin.ru/mt-content/uploads/2022/06/epokraskin-pokraska-001375.jpg
//    https://s0.rbk.ru/v6_top_pics/media/img/3/53/754913081541533.jpg
//    https://i4.photo.2gis.com/images/branch/0/30258560053724657_9427.jpg
//    https://medconsalting.ru/wp-content/uploads/2022/06/%D0%A1%D0%B5%D1%82%D1%8C-%D0%BC%D0%B0%D0%B3%D0%B0%D0%B7%D0%B8%D0%BD%D0%BE%D0%B2.jpg
//    https://liter.kz/cache/imagine/1200/uploads/news/2020/03/24/depositphotos_2169440_m-2015.jpg
//    https://alterainvest.ru/upload/resize_cache/iblock/9f9/730_520_2a1e21f4fea0ab2ad727e33a1002f238a/9f99bdb08fd658be0df670a59d2dd822.jpeg
//    https://stramigioliassociati.com/wp-content/uploads/2017/11/HiRes-Interiors-01.jpg
//    https://cashback2you.ru/wp-content/uploads/2018/07/66-1-4.jpg
//    https://vik-hitline.com/wp-content/uploads/2016/11/8-rost-uk.jpg
//    https://mall59.ru/wp-content/uploads/2019/07/eda.jpg
//    https://news1.ru/wp-content/uploads/2021/10/1619253652_38-phonoteka_org-p-fon-magazina-vnutri-50.jpg
//    https://marksteelephotography.com/site/assets/files/1287/msp_hannaford01.1200x0.jpg
//    https://yntymak.ru/oc-content/uploads/946/44956.jpg
//    https://www.tulapressa.ru/wp-content/uploads/2020/02/11/%D1%81%D1%83%D0%BF%D0%B5%D1%80%D0%BC%D0%B0%D1%80%D0%BA%D0%B5%D1%82-1300x918.jpg
//    https://arxy.ru/uploads/images/dizajn-magazina-produktov-14_4120_16x9.jpg

    private final MartService martService;
    private final RootService rootService;
    private final EmployeeService employeeService;
    private final BoardService boardService;
    private final SupplyService supplyService;
    private final ConfigsBuilder configsBuilder;
    private final NotificationService notificationService;
    private final UpgradeService upgradeService;

    @GetMapping("/data")
    public StatusWrap data() {
        return rootService.data();
    }

    @GetMapping("/open")
    public OpenCloseResponse open() {
        martService.open();
        return new OpenCloseResponse().isOpened(martService.opened());
    }

    @GetMapping("/close")
    public OpenCloseResponse close() {
        martService.close();
        return new OpenCloseResponse().isOpened(martService.opened());
    }

    @GetMapping("/cash")
    public SimpleResponse cash() {
        var response = employeeService.checkManagerBusy();
        if (!response.isSuccess()) {
            return response;
        }
        var staff = employeeService.manager();
        var quant = boardService.createCashCheckoutQuant(staff);
        if (quant == null) {
            response.setMessage("Задание не создано");
            response.setSuccess(false);
            return response;
        }
        return response;
    }

    @GetMapping("/fill")
    public SimpleResponse fill(@RequestParam GOOD good) {
        var response = employeeService.checkManagerBusy();
        if (!response.isSuccess()) {
            return response;
        }
        var staff = employeeService.manager();
        var quant = boardService.createPlacementQuant(staff, good);
        if (quant == null) {
            response.setMessage("Задание не создано");
            response.setSuccess(false);
        }
        return response;
    }

    @GetMapping("/shipment")
    public SimpleResponse shipment() {
        return supplyService.createSupplyBlockWrap();
    }

    @GetMapping("/unload")
    public SimpleResponse unload() {
        var response = employeeService.checkManagerBusy();
        if (!response.isSuccess()) {
            return response;
        }
        if (supplyService.checkExistsDeliveredBlock()) {
            var res = boardService.unloadStart(true);
            if (!res) {
                response.setMessage("Задание не создано");
                response.setSuccess(false);
            }
        } else {
            response.setSuccess(false);
            response.setMessage("Нет поставки для разгрузки");
        }
        return response;
    }

    @GetMapping("/addstaff")
    public SimpleResponse addStaff(@RequestParam STAFF_ROLE role) {
        return upgradeService.addStaff(role);
    }

    @GetMapping("/gowork")
    public SimpleResponse goWork(@RequestParam String name) {
        employeeService.goWork(name);
        return new SimpleResponse();
    }

    @GetMapping("/init")
    public SimpleResponse init() {
        rootService.initData();
        return new SimpleResponse();
    }

    @GetMapping("/addgood")
    public SimpleResponse addGood(@RequestParam GOOD good) {
        return upgradeService.addGood(good);
    }

    @GetMapping("/fillraise")
    public SimpleResponse fillRaise(@RequestParam int quantity) {
        return upgradeService.fillRaise(quantity);
    }

    @GetMapping("/cashraise")
    public SimpleResponse cashRaise(@RequestParam int quantity) {
        return upgradeService.cashRaise(quantity);
    }

    @GetMapping("/flushnotify")
    public SimpleResponse fushNotify(@RequestParam NOTIFY_TYPE type) {
        notificationService.flush(type);
        return new SimpleResponse();
    }

    @GetMapping("/configs")
    public ConfigWrap configs() {
        return configsBuilder.configs();
    }

    @GetMapping("/expandprices")
    public ExpandWrap expandPrices() {
        return upgradeService.expandPrices();
    }

    @GetMapping("/expandplace")
    public SimpleResponse expandPlace(
            @RequestParam SPACE_TYPE space,
            @RequestParam GOOD good,
            @RequestParam PLACE_VOLUME volume) {
        return upgradeService.expandPlace(space, good, volume);
    }

    @GetMapping("/stafffolder")
    public SimpleResponse staffFolder(@RequestParam boolean open) {
        martService.setStaffFolderExpanded(open);
        return new SimpleResponse();
    }

    @GetMapping("/supplyplan")
    public SimpleResponse setSupplyPlan(@RequestParam SUPPLY_CALL call, SUPPLY_VOLUME volume) {
        return upgradeService.setSupplyPlan(call, volume);
    }

    @GetMapping("/decreaserest")
    public SimpleResponse decreaseRest(
            @RequestParam STAFF_ROLE role,
            @RequestParam int value) {
        return upgradeService.decreaseRestDuration(role, value);
    }

    @GetMapping("/supplyplans")
    public SupplyPlanWrap getSupplyPlans() {
        return rootService.getSupplyPlans();
    }

}
