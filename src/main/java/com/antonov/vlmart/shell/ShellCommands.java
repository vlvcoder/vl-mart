package com.antonov.vlmart.shell;

import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.model.place.SupplyPlace;
import com.antonov.vlmart.rest.SimpleResponse;
import com.antonov.vlmart.service.RootService;
import com.antonov.vlmart.service.business.BusinessService;
import com.antonov.vlmart.service.business.UpgradeService;
import com.antonov.vlmart.service.buyer.BuyerService;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.service.product.SupplyService;
import com.antonov.vlmart.service.work.BoardService;
import com.antonov.vlmart.service.work.EmployeeService;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import com.antonov.vlmart.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@ShellComponent
public class ShellCommands {

    //todo
    //                Список ВСЕХ товаров с ценами . В т.ч. тех, которых нет на полках
    //                Главное меню: товары, покупатели, команды, сотрудники, улучшения/покупки, архив

    //todo Спецзаказы, производство, ферма
    //
    //todo Задания, поставки - в архив
    // Ограничить задание на размещение по резерву полки

    //todo Редактирование товаров у существующего сотрудника. Выбор - multi
    //todo
    //todo Чистить кассы
    //todo Убрать привязку сотрудников к товарам
    //todo Справа - краткий статус по магазину, сотрудникам, покупателям
    //todo Больше товаров


    private final MartService martService;
    private final EmployeeService employeeService;
    private final BoardService boardService;
    private final SupplyService supplyService;
    private final RootService rootService;
    private final BusinessService businessService;
    private final BusinessCommands businessCommands;
    private final Scanner scanner = new Scanner(System.in);
    private final CustomClear customClear;
    private final BuyerService buyerService;
    private final UpgradeService upgradeService;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    public ShellCommands(
            MartService martService,
            EmployeeService employeeService,
            BoardService boardService,
            SupplyService supplyService,
            RootService rootService,
            BusinessService businessService,
            BusinessCommands businessCommands,
            CustomClear customClear,
            BuyerService buyerService,
            UpgradeService upgradeService) {
        this.martService = martService;
        this.employeeService = employeeService;
        this.boardService = boardService;
        this.supplyService = supplyService;
        this.rootService = rootService;
        this.businessService = businessService;
        this.businessCommands = businessCommands;
        this.customClear = customClear;
        this.buyerService = buyerService;
        this.upgradeService = upgradeService;
    }

    @ShellMethod("Hello!!!")
    public String hello() {
        return "Hello from VL-mart!!!";
    }

    @ShellMethod("Main menu")
    public String menu() {
        return "M - goto MARKET\nS - goto STORE";
    }

    @ShellMethod(value = "boolean value")
    public String boo(
            @ShellOption(value = "--r") boolean rememberMe
    ) {
        System.out.println("\u001B[32m" + "red text" + "\u001B[0m" + " OK!");
        return String.format("remember me option is '%s'", rememberMe);
    }

    @ShellMethod(value = "Перезапуск игры")
    public String init() {
        System.out.println("Начать заново? (y / n):");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            rootService.initData();
            String mes;
            mes = "\n=======================================================";
            mes += "\n=====  Выполнена начальная инициализация VL-mart  =====";
            mes += "\n=======================================================\n\n" + status();
            return mes;
        }
        return "";
    }

    @ShellMethod(value = "Текущий статус маркета")
    public String status() {
        if (rootService.checkGameOver()) {
            return bannerGameOver();
        }
        customClear.cls();
        return easyStatus();
    }

    private String bannerGameOver() {
        var builder = new AnsiStringBuilder()
                .color(AnsiStringBuilder.ForegroundColor.RED);
        try {
            Files
                    .lines(Path.of("banners/game_over.txt"))
                    .forEach(builder::appendLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    public String easyStatus() {
        return martService.status() + businessCommands.check1000000();
    }

    @ShellMethod(value = "Открыть магазин")
    public String open() {
        martService.open();
        return status();
    }

    @ShellMethod(value = "Закрыть магазин")
    public String close() {
        martService.close();
        return status();
    }

    @ShellMethod(value = "Поставка товаров")
    public String shipment() {
        if (supplyService.checkExistsActiveSupply()) {
            return Utils.error("Текущая поставка не завершена");
        }
        var response = supplyService.createSupplyBlockWrap();
        if (!response.isSuccess()) {
            return Utils.error(response.getMessage());
        }
        var block = supplyService.getActiveSupply();
        var builder = new AnsiStringBuilder()
                .color(AnsiStringBuilder.ForegroundColor.LIGHT_BLUE);
        for (SupplyPlace place : block.places()) {
            builder.appendLine(place);
        }
        builder
                .reset()
                .line()
                .appendLine("Создана поставка " + block);

        return builder.toString();
    }

    @ShellMethod(value = "Задание на разгрузку поставки")
    public String unload() {
        if (!supplyService.checkExistsDeliveredBlock()) {
            return Utils.error("Нет поставки для разгрузки");
        }

        var res = boardService.unloadStart(true);

        if (!res) {
            return Utils.warn("Задание не создано");
        }
        return "Создано задание на разгрузку товаров поставки";
    }

    @ShellMethod(value = "Задание на пополнение полки")
    public String fill(String good) {
        GOOD goodEnum;
        try {
            goodEnum = Enum.valueOf(GOOD.class, good.toUpperCase());
        } catch (Exception e) {
            return Utils.error("Неверно указан товар " + good);
        }

        var staff = employeeService.manager();
        if (staff.isBusy()) {
            return Utils.error(String.format("Сотрудник %s выполняет другое задание", staff));
        }

        var quant = boardService.createPlacementQuant(staff, goodEnum);
        if (quant == null) {
            return Utils.warn("Задание не создано");
        }
        return quant.toString();
    }

    @ShellMethod(value = "Задание на пополнение полки. Ожидает завершения")
    public String fillw(String good) {
        GOOD goodEnum;
        try {
            goodEnum = Enum.valueOf(GOOD.class, good.toUpperCase());
        } catch (Exception e) {
            return Utils.error("Неверно указан товар " + good);
        }

        var staff = employeeService.manager();
        if (staff.isBusy()) {
            System.out.println("Ожидание завершения предыдущего задания...");
        }
        while (staff.isBusy()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        var quant = boardService.createPlacementQuant(staff, goodEnum);
        if (quant == null) {
            return Utils.warn("Задание не создано");
        }
        System.out.println("Ожидание выполнения задания...");

        while (staff.isBusy()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return quant.toString();
    }

    @ShellMethod(value = "Задание на пробитие по кассе")
    public String cash() {
        var staff = employeeService.manager();
        if (staff.isBusy()) {
            return Utils.error(String.format("Сотрудник [%s] выполняет другое задание", staff));
        }

        var quant = boardService.createCashCheckoutQuant(staff);
        if (quant == null) {
            return Utils.warn("Задание не создано");
        }
        return quant.toString();
    }

    @ShellMethodAvailability({"fill", "unload", "cash"})
    public Availability managerOperationsAvailability() {
        var staff = employeeService.manager();
        return staff.isBusy()
                ? Availability.unavailable(String.format("сотрудник [%s] выполняет другое задание", staff))
                : Availability.available();
    }

    @ShellMethod(value = "ansi string builder test")
    public String cols() {
        AnsiStringBuilder line = new AnsiStringBuilder();

        for (AnsiStringBuilder.ForegroundColor col : AnsiStringBuilder.ForegroundColor.values()) {
            line
                    .color(col)
                    .bold()
                    .append("This is " + col + " bold text.")
                    .resetBold()
                    .line()
                    .append("This is " + col + " text.")
                    .line();
        }

        final Object[][] table = new String[4][];
        table[0] = new String[]{"foo", "bar", "baz"};
        table[1] = new String[]{"bar2", "foo2", "baz2"};
        table[2] = new String[]{"baz3", "bar3", "foo3"};
        table[3] = new String[]{"foo4", "bar4", "baz4"};

        for (final Object[] row : table) {
            System.out.format("|%15s\t|%15s\t|%15s|%n", row);
        }
        System.out.println();

        for (final Object[] row : table) {
            System.out.format("|\t%-15s|\t%-15s|\t%-15s|%n", row);
        }
        System.out.println();

        return line.toString();
    }

    @ShellMethod(value = "Список сотрудников")
    public String employee() {
        return employeeService.employeeToString();
    }

    private Disposable refreshFlux;

    @ShellMethod(value = "flux")
    public String flux() {
        refreshFlux = Flux
                .interval(Duration.ofSeconds(5))
                .subscribe(f -> showStatus());
        return status();
    }

    @ShellMethod(value = "flux")
    public void fstop() {
        refreshFlux.dispose();
    }

    private void showStatus() {
        System.out.println(status());
    }

    @ShellMethod(value = "Добавление нового товара")
    public String addgood() {
        var SELECTOR_NAME = "goodType";
        List<GOOD> existing = martService.shopPlaces().stream()
                .map(Place::good)
                .collect(Collectors.toList());
        var goodMap = Arrays.stream(GOOD.values())
                .filter(good -> !existing.contains(good))
                .collect(Collectors.toMap(GOOD::title, GOOD::toString));

        if (goodMap.size() == 0) {
            return Utils.warn("Все товары добавлены");
        }

        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withSingleItemSelector(SELECTOR_NAME)
                .name("Тип товара")
                .selectItems(goodMap)
                .and()
                .build();
        var result = flow.run();
        var res = result.getContext().get(SELECTOR_NAME, String.class);

        GOOD good = Enum.valueOf(GOOD.class, res);
        if (martService.createPlace(good)) {
            return "В торговый зал и на склад добавлены места для товара: " + good.title();
        } else {
            return Utils.warn("Товар " + good.title() + " уже есть в торговом зале и на складе");
        }
    }

    @ShellMethod(value = "Добавление новых товаров")
    public void addgoodtest() {
        martService.addAllGoods();
    }

    @ShellMethod(value = "Логотип магазина")
    public void logo() {
        try {
            Files.lines(Path.of("banners/banner.txt")).forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @ShellMethod(value = "coltest")
//    public String coltest() {
//        var builderShop = new PlaceOwnerFormatter().build(martService.getShop());
//        var builderStore = new PlaceOwnerFormatter().build(martService.getStore());
//        return builderShop.toString();
//    }

    @ShellMethod(value = "Текущий статус внутренних объектов")
    public String internal() {
        var builder = new AnsiStringBuilder();
        builder
                .append("Покупатели - \t\t")
                .appendLine(buyerService.buyerCount())
                .append("Покупки - \t\t\t")
                .appendLine(buyerService.purchaseCount())
                .append("Задания - \t\t\t")
                .appendLine(boardService.quantCount())
                .append("Позиции поставки - \t")
                .appendLine(supplyService.placeCount());
        return builder.toString();
    }

    @ShellMethod(value = "Увеличение грузоподъемности пополнения полок")
    public String fillraise(int quantity) {
        SimpleResponse response = upgradeService.fillRaise(quantity);
        var res = response.getMessage().replace("<br>", System.lineSeparator());
        if (response.isSuccess()) {
            return res;
        } else {
            return Utils.error(res);
        }
    }
}
