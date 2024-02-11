function calcSupply() {
    $supplyDraw = $('#supplyDraw');
    $unloadDraw = $('#unloadDraw');
    $supplyDeliveryButton = $('#supplyDeliveryButton');
    $supplyUnloadButton = $('#supplyUnloadButton');

    $supplyDraw.hide();
    $unloadDraw.hide();
    $supplyDeliveryButton.hide();
    $supplyUnloadButton.hide();

    let $header = $('#supplyHeader');
    if (wrap.supply == null) {
        $header.text('Доставка товаров');
        $supplyDeliveryButton.show();
    } else {
        switch (wrap.supply.status) {
            case 'WAIT':
                $header.text('Доставка товаров');
                $supplyDraw.show();
                supplyDeliveryProgress();
                break;
            case 'DELIVERED':
                $header.text('Товары доставлены');
                $supplyUnloadButton.show();
                break;
            case 'UNLOADING':
                $header.text('Разгрузка товаров');
                $unloadDraw.show();
                if (cleintServerTimeDiff === null) {
                    let supplyQuant = {
                        startMillis: wrap.supply.startUnloadMillis,
                        durationSec: wrap.supply.durationSec
                    };
                    setQuantProgress($('#unloadDraw div.progress-div'), supplyQuant);
                }
                break;
        }
    }
}

function supplyDeliveryButtonClick() {
    rest('shipment', showRestResult);
    return false;
}

function supplyUnloadButtonClick() {
    rest('unload', (data) => {
        if (!data.success) {
            showAlert('Ошибка', data.message, 'alert-danger', 3000);
        }
    });
    return false;
}

let supplyCleintServerTimeDiff = null;

function supplyDeliveryProgress() {
    setTimeout(() => {
        const widthfactor = 0.12;
        if (wrap.supply == null) {
            return;
        }
        if (supplyCleintServerTimeDiff === null) {
            supplyCleintServerTimeDiff = wrap.currentMillis - Date.now();
        }
        let k = (Date.now() + cleintServerTimeDiff - wrap.supply.startMillis) * widthfactor / wrap.supply.durationSec;
        $('#supplyTruck').css('margin-left', '' + k + 'px');
        if (k >= 100) {
            supplyCleintServerTimeDiff = null;
        } else {
            supplyDeliveryProgress();
        }

    }, 100);
}

function supplyPlanCall() {
    rest('supplyplans', (data) => {
        let $modal = $('#supplyPlanModal');

        let $divCalls = $modal.find('#divCalls').empty();
        constructPlan('call', $modal, $divCalls, data.supplyCalls);
        let $divVolumes = $modal.find('#divVolumes').empty();
        constructPlan('volume', $modal, $divVolumes, data.supplyVolumes);

        $modal.modal('show');
        $modal.find('.btn-ok')
            .on('click', function () {
                $modal.modal('hide');
            });
    });
    return false;
}

function constructPlan(name, $modal, $divContainer, dataSource) {
    for (let plan of dataSource) {
        let $item = $('#supplyItem').clone();
        let $inp = $item.find('input');
        $inp.attr('name', name).attr('id', plan.name).attr('value', plan.name);
        $item
            .find('label')
            .attr('for', plan.name);
        $item
            .find('td.tdsupply-title')
            .html('<small>' + plan.title + '</small>');

        let $tdPrice = $item.find('td.tdsupply-price');
        let $i = $tdPrice.find('i').hide();
        let $span = $tdPrice.find('span').hide();
        $inp.removeAttr('disabled');
        if (plan.enabled) {
            $i.show();
        } else {
            $span.html(plan.price).show();
            if (plan.priceValue > wrap.business.moneyCount) {
                $inp.attr('disabled', 'disabled');
            }
        }

        if (plan.active) {
            $inp.attr('checked', 'checked');
        } else {
            $inp.removeAttr('checked');
        }

        $divContainer.append($item);
    }
}

function setSupplyPlan() {
    let call = $('input[name="call"]:checked').val();
    let volume = $('input[name="volume"]:checked').val();
    rest('supplyplan?call=' + call + '&volume=' + volume, showRestResult);


    return false;
}