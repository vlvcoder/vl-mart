package com.antonov.vlmart.service.notify;

import com.antonov.vlmart.config.BuyerConfiguration;
import com.antonov.vlmart.model.enums.NOTIFY_TYPE;
import com.antonov.vlmart.model.notify.Notify;
import com.antonov.vlmart.render.NotifyWrap;
import com.antonov.vlmart.rest.SimpleResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final List<Notify> notifies = Collections.synchronizedList(new ArrayList<>());
    private SimpleResponse alert = null;
    private final BuyerConfiguration buyerConfiguration;

    public NotificationServiceImpl(BuyerConfiguration buyerConfiguration) {
        this.buyerConfiguration = buyerConfiguration;
    }

    @Override
    public void init() {
        notifies.clear();
        Notify notify = new Notify(
                NOTIFY_TYPE.WELCOME,
                "Добро пожаловать!",
                "Сейчас магазин закрыт для покупателей.<br>Чтобы начать игру нажмите на кнопку" +
                        "<br><strong>\"Магазин закрыт\"</strong>&nbsp;&nbsp;&nbsp;&nbsp;-->");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.FIRST_LIKE,
                "С первым лайком!",
                "Первый покупатель отправил вам лайк: <strong>\"Отличный шопинг!\"</strong><br>Так держать!");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.DISLIKE_ABSENT_POSITIONS,
                "Дизлайк покупателя",
                "Первый покупатель отправил вам дизлайк: <strong>\"Отсутствуют некоторые товары\"</strong><br>Старайтесь вовремя пополнять полки");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.FIRST_BUYER_IN_CASH,
                "Покупатель на кассе",
                "Для того, чтобы рассчитать покупателя, нажмите на кнопку" +
                        "<br><strong>\"На кассу\"</strong>&nbsp;&nbsp;&nbsp;&nbsp;-->" +
                        "<br>Когда у вас накопится сумма денег <strong>10 000</strong> вы сможете нанять на работу кассира, который будет рассчитывать покупателей");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.ALL_SUPPLY_NEEDED,
                "Пополнение склада",
                "<strong>Заканчиваются все товары на складе.</strong><br>" +
                        "Срочно закажите поставку товаров<br>");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.SUPPLY_NEEDED,
                "Поставка товара",
                "<strong>Заканчиваются товары на складе.</strong><br>" +
                        "Нажмите на кнопку \"Заказать поставку\"<br>");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.SUPPLY_PRICE,
                "Поставка товара",
                "Стоимость товара у поставщика составляет <strong>10%</strong> от продажной цены товара");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.SUPPLY_UNLOAD_NEEDED,
                "Поставка товара",
                "Товары доставлены.<br>Нажмите на кнопку <strong>\"Начать разгрузку\"</strong>");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.LOW_STOCK_IN_SHOP,
                "Пополнение полок",
                "<strong>Заканчивается товар на полке.</strong><br>" +
                        "Для пополнения перемещайте товары со склада на полки " +
                        "(нажмите на товар в списке) или примите нового мерчендайзера в меню<br>" +
                        "<a href=\"#\" class=\"alert-link\" data-bs-toggle=\"offcanvas\" data-bs-target=\"#offcanvasBottom\"" +
                        "   aria-controls=\"offcanvasBottom\">" +
                        "   <span class=\"menuitem\">Модернизация</span>" +
                        "</a>");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.ALL_LOW_STOCK_IN_SHOP,
                "Пополнение полок",
                "<strong>Заканчиваются товары на всех полках.</strong><br>" +
                        "Срочно пополняйте полки " +
                        "(нажмите на товар в списке) или примите нового мерчендайзера в меню<br>" +
                        "<a href=\"#\" class=\"alert-link\" data-bs-toggle=\"offcanvas\" data-bs-target=\"#offcanvasBottom\"" +
                        "   aria-controls=\"offcanvasBottom\">" +
                        "   <span class=\"menuitem\">Модернизация</span>" +
                        "</a>");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.LONG_CASH_QUEUE,
                "Очередь в кассу",
                "<strong>Большая очередь в кассу.</strong><br>Рассчитайте очередного покупателя (Кнопка \"На кассу\") " +
                        "или примите нового кассира в меню<br>" +
                        "<a href=\"#\" class=\"alert-link\" data-bs-toggle=\"offcanvas\" data-bs-target=\"#offcanvasBottom\"" +
                        "   aria-controls=\"offcanvasBottom\">" +
                        "<span class=\"menuitem\">Модернизация</span>" +
                        "</a>" +
                        "<br>Если количество покупателей в очереди превысит " +
                        buyerConfiguration.getCash_queue_treshhold() +
                        " чел., то вы будете получать дизлайки каждые " +
                        buyerConfiguration.getCash_handle_duration_treshhold() + " сек");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.CAN_BUY_CAHIER,
                "Модернизация магазина",
                "Вы можете нанять <strong class=\"menuitem\">кассира</strong>." +
                        "<br>Он будет заниматься расчетами с покупателями на кассе");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.CAN_BUY_CAHIER,
                "Модернизация магазина",
                "Вы можете нанять <strong class=\"menuitem\">мерчендайзера</strong>." +
                        "<br>Он будет заниматься пополнением полок товарами");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.CAN_BUY_SHELVER,
                "Модернизация магазина",
                "Вы можете нанять <strong class=\"menuitem\">мерчендайзера</strong>." +
                        "<br>Он будет заниматься пополнением полок товарами");
        notifies.add(notify);
        notify = new Notify(
                NOTIFY_TYPE.CAN_BUY_LOADER,
                "Модернизация магазина",
                "Вы можете нанять <strong class=\"menuitem\">грузчика</strong>." +
                        "<br>Он будет заниматься разгрузкой товаров поставки");
        notifies.add(notify);
    }

    @Override
    public void sendAlert(SimpleResponse alert) {
        this.alert = alert;
    }

    @Override
    public SimpleResponse fulshAlert() {
        var res = alert;
        alert = null;
        return res;

    }

    @Override
    public void send(NOTIFY_TYPE notifyType) {
        notifies.stream()
                .filter(n -> n.getType() == notifyType)
                .forEach(n -> n.setActive(true));
    }

    @Override
    public void sendOnce(NOTIFY_TYPE notifyType) {
        notifies.stream()
                .filter(n -> n.getType() == notifyType)
                .forEach(n -> n.setActive(n.getCount() == 0));
    }

    @Override
    public void flush(NOTIFY_TYPE notifyType) {
        notifies.stream()
                .filter(n -> n.getType() == notifyType)
                .forEach(n -> {
                    n.setActive(false);
                    n.setCount(n.getCount() + 1);
                });
    }

    @Override
    public NotifyWrap activeNotify() {
        return notifies.stream()
                .filter(Notify::isActive)
                .map(NotifyWrap::new)
                .findFirst()
                .orElse(null);
    }

}
