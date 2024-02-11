function staffFolderClick() {
    wrap.staffFolderExpanded = !wrap.staffFolderExpanded;
    rest('stafffolder?open=' + wrap.staffFolderExpanded, null);
    calcStaff();
}

function staffClick(tr) {
    $tr = $(tr);
    rest('gowork?name=' + $tr.attr('name'), (data) => {
        if (!data.success) {
            showAlert('Ошибка', data.message, 'alert-danger', 3000);
        }
    });
}

function staffAddClick(tr) {
    $tr = $(tr);
    let role = $tr.attr('role');
    if (role === ROLE_MANAGER) {
        return false;
    }
    let staffCollapsed = wrap.staffsCollapsed.find(s => s.role === role);

    let $staffCards = $('#staffCards');
    let $card = $staffCards.find('div.' + staffCollapsed.role);
    console.log('div.' + staffCollapsed.role);
    console.log($card.html());

    confirm(
        'Прием нового сотрудника',
        $card.html(),
        'info',
        true,
        'Принять',
        null,
        null,
        function () {
            rest('addstaff?role=' + role, showRestResult);
        })
    return false;
}

function addStaffClick(role) {
    rest('addstaff?role=' + role, showRestResult);
    window.scrollTo(0, 0);
    return false;
}

function calcStaff() {
    let $staffTable = $('#staffTable');
    let $staffCollapsedTable = $('#staffCollapsedTable');
    let icon = $('#staffFolderHeader').find('i');

    icon.removeClass();
    if (wrap.staffFolderExpanded) {
        icon.addClass('bi bi-caret-down-fill');
        $staffTable.show();
        $staffCollapsedTable.hide();
    } else {
        icon.addClass('bi bi-caret-right-fill');
        $staffTable.hide();
        $staffCollapsedTable.show();
    }

    // fill $staffTable
    let $lines = $staffTable.find('tr');
    if ($lines.length !== wrap.staffs.length) {
        location.reload();
        return;
    }
    $lines.each(function () {
        let $tr = $(this);
        $tr.find('td.staff-state').remove();

        let staff = wrap.staffs.find(s => s.name === $tr.attr('name'));
        if (staff.busy) {
            $tr.append('<td class="tdright staff-state"><span class="badge rounded-pill">undef!!!</span></td>');
            let $td = $tr.find('td.staff-state');
            $td.appendTo($tr);
            let $span = $td.find('span');
            let quant = wrap.quants.find(q => q.staff.name === staff.name);
            if (quant !== null && quant !== undefined) {
                switch (quant.quantType) {
                    case QUANT_PLACEMENT:
                        $span.html('<img src="/images/goods/' + quant.good + '.png" width="16px">');
                        $span.addClass('bg-primary');
                        break;
                    case QUANT_CASH_CHECKOUT:
                        $span.html(quant.durationSec + ' сек');
                        $span.addClass('bg-warning');
                        break;
                    case QUANT_CASH_UNLOAD:
                        if (wrap.supply !== undefined && wrap.supply !== null) {
                            $span.html(wrap.supply.quantity + ' ед');
                            $span.addClass('bg-info');
                        }
                }
            }
        } else {
            let $tdStatus = $('#staffStatusRest').clone();
            $tdStatus.removeClass('id');
            $tdStatus.appendTo($tr);
        }
    });

    // fill $staffCollapsedTable
    $lines = $staffCollapsedTable.find('tr');
    $lines.each(function () {
        let $tr = $(this);
        $tr.find('td.staff-state').remove();

        let staff = wrap.staffsCollapsed.find(s => s.role === $tr.attr('role'));
        if (staff.busyCount > 0) {
            $tr.append('<td class="tdright staff-state"><span class="badge rounded-pill">undef!!!</span></td>');
            let $td = $tr.find('td.staff-state');
            $td.appendTo($tr);
            let $span = $td.find('span');

            let role = staff.role;
            if (role === ROLE_MANAGER) {
                let quant = wrap.quants.find(q => q.staff.role === ROLE_MANAGER);
                switch (quant.quantType) {
                    case QUANT_PLACEMENT:
                        $span.html(quant.goodTitle);
                        role = ROLE_SHELVER;
                        break;
                    case QUANT_CASH_CHECKOUT:
                        $span.html(quant.durationSec + ' сек');
                        role = ROLE_CASHIER;
                        break;
                    case QUANT_CASH_UNLOAD:
                        if (wrap.supply !== undefined && wrap.supply !== null) {
                            $span.html(wrap.supply.quantity + ' ед');
                        }
                        role = ROLE_LOADER;
                }
            } else {
                $span.html(staff.busyCount + ' / ' + staff.count);
            }

            switch (role) {
                case ROLE_SHELVER:
                    $span.addClass('bg-primary');
                    break;
                case ROLE_CASHIER:
                    $span.addClass('bg-warning');
                    break;
                case ROLE_LOADER:
                    $span.addClass('bg-info');
            }

        } else {
            let $tdStatus = $('#staffStatusRest').clone();
            $tdStatus.removeClass('id');
            $tdStatus.appendTo($tr);
            if (staff.count > 1) {
                $tdStatus.find('.rest-count').html(staff.count + '&nbsp');
            }
        }
    });

}

