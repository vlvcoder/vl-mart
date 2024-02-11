function calcDecrease() {
    let $ul = $('#decreaseRest');
    $ul.empty();

    if (wrap.upgrade.cashierRestDuration > 0) {
        addDecreaseItem(
            $ul,
            'Кассир [' + wrap.upgrade.cashierRestDuration + '] - 5',
            wrap.upgrade.restDecreasePrice_5,
            'CASHIER',
            5);
    }
    if (wrap.upgrade.cashierRestDuration > 5) {
        addDecreaseItem(
            $ul,
            'Кассир [' + wrap.upgrade.cashierRestDuration + '] - 10',
            wrap.upgrade.restDecreasePrice_10,
            'CASHIER',
            10);
    }
    if (wrap.upgrade.shelverRestDuration > 0) {
        addDecreaseItem(
            $ul,
            'Мерчендайзер [' + wrap.upgrade.shelverRestDuration + '] - 5',
            wrap.upgrade.restDecreasePrice_5,
            'SHELVER',
            5);
    }
    if (wrap.upgrade.shelverRestDuration > 5) {
        addDecreaseItem(
            $ul,
            'Мерчендайзер [' + wrap.upgrade.shelverRestDuration + '] - 10',
            wrap.upgrade.restDecreasePrice_10,
            'SHELVER',
            10);
    }
}

function addDecreaseItem($ul, title, price, role, value) {
    let $li = $('<li></li>').html($('#decreaseRestSample').html());
    $li.find('a')
        .attr('role', role)
        .attr('value', value);
    $li.find('td.decrease-rest-title').html(title);
    $li.find('td.decrease-rest-price').html(price);
    $ul.append($li);
}

function decreaseRestClick(a) {
    let $a = $(a);
    rest('decreaserest?role=' + $a.attr('role') + '&value=' + $a.attr('value'), showRestResult);
    return false;
}
