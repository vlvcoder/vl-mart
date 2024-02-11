package com.antonov.vlmart.shell;

import com.antonov.vlmart.config.ShopCapacityConfiguration;
import com.antonov.vlmart.config.StoreCapacityConfiguration;
import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.enums.PLACE_VOLUME;
import com.antonov.vlmart.model.enums.SPACE_TYPE;
import com.antonov.vlmart.model.enums.STAFF_ROLE;
import com.antonov.vlmart.service.business.BusinessService;
import com.antonov.vlmart.service.business.UpgradeService;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.service.work.EmployeeService;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import com.antonov.vlmart.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@ShellComponent
public class BusinessCommands {
    private final MartService martService;
    private final BusinessService businessService;
    private final EmployeeService employeeService;
    private final UpgradeService upgradeService;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    public BusinessCommands(
            MartService martService, BusinessService businessService,
            EmployeeService employeeService,
            UpgradeService upgradeService) {
        this.martService = martService;
        this.businessService = businessService;
        this.employeeService = employeeService;
        this.upgradeService = upgradeService;
    }

    @ShellMethod(value = "Добавление кассира")
    public String cashier(@ShellOption(help = "Имя сотрудника (в 1 слово)") String name) {
        if (businessService.moneyCount() < STAFF_ROLE.CASHIER.cost()) {
            return Utils.error(String.format("Недостаточно средств. Стоимость найма сотрудника %S составляет %s",
                    STAFF_ROLE.CASHIER,
                    Utils.longNumberFormat(STAFF_ROLE.CASHIER.cost())));
        }

        var staff = employeeService.createCashier(name);
        if (staff == null) {
            return Utils.error("Сотрудник " + name + " уже существует");
        }
        businessService.spendMoney(STAFF_ROLE.CASHIER.cost());
        return "Создан сотрудник:\n" + staff;
    }

    @ShellMethod(value = "Добавление мерчендайзера")
    public String shelver(@ShellOption(help = "Имя сотрудника (в 1 слово)") String name) {
        if (businessService.moneyCount() < STAFF_ROLE.SHELVER.cost()) {
            return Utils.error(String.format("Недостаточно средств. Стоимость найма сотрудника %S составляет %s",
                    STAFF_ROLE.SHELVER,
                    Utils.longNumberFormat(STAFF_ROLE.SHELVER.cost())));
        }

        var staff = employeeService.createShelver(name);
        if (staff == null) {
            return Utils.error("Сотрудник " + name + " уже существует");
        }
        businessService.spendMoney(STAFF_ROLE.SHELVER.cost());
        return "Создан сотрудник:\n" + staff;
    }

    @ShellMethod(value = "Расширение места")
    public String expand(String spaceType, String good, String volume) {
        SPACE_TYPE spaceTypeEnum = SPACE_TYPE.valueOf(spaceType.toUpperCase());
        GOOD goodEnum = GOOD.valueOf(good.toUpperCase());
        PLACE_VOLUME volumeEnum = PLACE_VOLUME.valueOf(volume.toUpperCase());

        var response = upgradeService.expandPlace(spaceTypeEnum, goodEnum, volumeEnum);
        if (response.isSuccess()) {
            return response.getMessage();
        }
        return Utils.error(response.getMessage());
    }

    private GOOD requestExpandFlow(Map<String, String> goodMap, int volume, int cost) {
        var SELECTOR_NAME = "goodType";
        var CONFIRMATION_NAME = "Confirmation";
        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withSingleItemSelector(SELECTOR_NAME)
                .name("Товар")
                .selectItems(goodMap)
                .and()
                .withConfirmationInput(CONFIRMATION_NAME)
                .name("Расширить объем места на " +
                        volume +
                        "?\n Стоимость расширения составляет " +
                        cost)
                .and()
                .build();
        var result = flow.run();
        var res = result.getContext().get(SELECTOR_NAME, String.class);
        GOOD good = Enum.valueOf(GOOD.class, res);
        return result.getContext().get(CONFIRMATION_NAME, Boolean.class) ? good : null;
    }

    private String banner1000000() {
        try {
            var builder = new AnsiStringBuilder().line();
            congratulation(builder);
            builder.line();
            millionBalk(builder);
            builder
                    .line()
                    .line()
                    .color(AnsiStringBuilder.ForegroundColor.LIGHT_YELLOW);
            Files
                    .lines(Path.of("banners/million.txt"))
                    .forEach(builder::appendLine);
            builder
                    .line()
                    .line()
                    .reset();
            millionBalk(builder);
            builder.line();
            return builder + martService.totalStatus();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void congratulation(AnsiStringBuilder builder) {
        builder
                .reset()
                .bold()
                .color(AnsiStringBuilder.ForegroundColor.WHITE)
                .append("                                             П О З Д Р А В Л Я Е М !!!")
                .line()
                .reset();
    }

    private void millionBalk(AnsiStringBuilder builder) {
        builder.append("                            ");
        int size = 59;
        while (true) {
            for (AnsiStringBuilder.ForegroundColor col : AnsiStringBuilder.ForegroundColor.values()) {
                if (col == AnsiStringBuilder.ForegroundColor.BLACK) {
                    continue;
                }
                if (size-- == 0) {
                    builder.line();
                    return;
                }
                builder
                        .color(col)
                        .append("#");
            }
        }
    }

    public String check1000000() {
        if (!businessService.reachMillion() && businessService.moneyCount() >= 1_000_000) {
            businessService.setReachMillion(true);
            return banner1000000();
        }
        return "";
    }
}
