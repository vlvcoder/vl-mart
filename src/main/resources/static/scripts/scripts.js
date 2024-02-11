$(document).ready(function () {
    if (!wrap.opened) {
        showManual();
    }
    requestData();
});

const ROLE_MANAGER = 'MANAGER';
const ROLE_SHELVER = 'SHELVER';
const ROLE_CASHIER = 'CASHIER';
const ROLE_LOADER = 'LOADER';

const QUANT_PLACEMENT = 'PLACEMENT';
const QUANT_CASH_CHECKOUT = 'CASH_CHECKOUT';
const QUANT_CASH_UNLOAD = 'UNLOAD';

let backgroundImage = 'https://images.wallpaperscraft.ru/image/single/amalfi_italiya_kholm_more_112927_1920x1080.jpg';

function requestData() {
    if (wrap.shopPlaces === undefined) {
        console.log('==================================================>>');
        console.log(wrap);
        console.log('<<==================================================');
    } else {
        calcOpenCloseButtons();
        calcShopTable();
        calcStoreTable();
        calcMediatorTable();
        calcBtnCash();
        calcScore();
        calcBuyers();
        calcSupply();
        calcStaff();
        calcNotify();
        calcAlert();
        calcExpands();
        calcDecrease();
    }

    setTimeout(() => {
            rest('data', (data) => {
                wrap = data;
                if (wrap.gameOver) {
                    restartGame();
                } else {
                    requestData();
                }
            })
        },
        1000);
}

function restartGame() {
    calcScore();
    let $score = $('.score').clone();
    $score.find('td').removeAttr('id');
    confirm(
        'Игра окончена!',
        'Количество дизлайков превысило количество лайков<br><br>',
        'danger',
        false,
        'Начать заново',
        $score,
        null,
        function () {
            rest('init', (data) => {
                if (data.success) {
                    requestData();
                } else {
                    showAlert('Ошибка', data.message, 'alert-danger', 3000);
                }
            });
        });
}

function openCloseShop(needOpen) {
    rest(needOpen ? 'open' : 'close', (data) => {
        wrap.opened = data.opened;
        calcOpenCloseButtons();
    })
}

function calcOpenCloseButtons() {
    if (wrap.opened) {
        $('#btnOpen').hide();
        $('#btnClose').show();
    } else {
        $('#btnClose').hide();
        $('#btnOpen').show();
    }
    return false;
}

function fillProgressClick(tr) {
    let good = $(tr).attr('good');
    rest('fill?good=' + good, (data) => {
        if (!data.success) {
            showAlert('Ошибка', data.message, 'alert-danger', 3000);
        }
    });
    return false;
}

function calcBtnCash() {
    if (wrap.buyers.CASH_WAIT > 0) {
        $('#btnCash').removeClass('disabled');
    } else {
        $('#btnCash').addClass('disabled');
    }

    let managerQuant = activeManagerQuant(QUANT_CASH_CHECKOUT);
    if (managerQuant == null) {
        $('#btnCash').show();
        $('#cashProcessing').hide();
    } else {
        $('#btnCash').hide();
        $('#cashProcessing').show();
        if (cleintServerTimeDiff === null) {
            setQuantProgress($('#cashProcessing div.progress-div'), managerQuant);
        }
    }
}

function calcShopTable() {
    let $tbody = $('#shopTable tbody');
    if ($tbody.find('tr').length !== wrap.shopPlaces.length) {
        location.reload();
        return;
    }
    let places = wrap.shopPlaces;
    fillTable($tbody, places, 'bg-warning');
}

function calcStoreTable() {
    let $tbody = $('#storeTable tbody');
    let places = wrap.storePlaces;
    fillTable($tbody, places, 'bg-info');
}

function fillTable($tbody, places, normalColor) {
    for (let place of places) {
        let $tr = $tbody.find("tr[good='" + place.good + "']");
        $tr.find('td').eq(2).html(place.stock);
        $tr.find('td').eq(3).html(place.capacity);
        if (place.waitingSupply != null) {
            if (place.waitingSupply > 0) {
                $tr.find('td').eq(4).html('+' + place.waitingSupply);
            } else {
                $tr.find('td').eq(4).html('');
            }
        }

        let percent = Math.round(place.stock / place.capacity * 100) + '%';
        let $pBar = $tr.find('td').eq(1).find('.progress-bar');
        $pBar.css('width', percent);
        clearColors($pBar);
        switch (place.fillLevel) {
            case 'HIGH':
                $pBar.addClass('bg-success');
                break;
            case 'LOW':
                $pBar.addClass('bg-danger');
                break;
            default:
                $pBar.addClass(normalColor);
        }

        $tr.removeClass('table-light');
        if (place.stock === 0) {
            $tr.addClass('table-danger');
        } else {
            $tr.addClass('table-light');
        }
    }
}

let cleintServerTimeDiff = null;

function setQuantProgress($div, quant) {
    if (cleintServerTimeDiff === null) {
        cleintServerTimeDiff = wrap.currentMillis - Date.now();
    }
    let k = (Date.now() + cleintServerTimeDiff - quant.startMillis) / (quant.durationSec * 10);
    let $bar = $div.find('.progress-bar');
    $bar.css('width', '' + k + '%');
    $div.show();

    setTimeout(
        () => {
            if (
                (Date.now() - cleintServerTimeDiff) >
                (quant.startMillis + quant.durationSec * 1000)
            ) {
                cleintServerTimeDiff = null;
                $div.hide();
            } else {
                setQuantProgress($div, quant);
            }
        },
        100);
}

function calcMediatorTable() {
    let $tbody = $('#tableMediator tbody');
    let managerQuant = activeManagerQuant(QUANT_PLACEMENT);
    for (let mw of wrap.mediatorWraps) {
        let $tr = $tbody.find("tr[good='" + mw.good + "']");

        let $divBuyersNeeds = $tr.find('td').eq(0).find('div');
        if (mw.buyersNeeds > 0) {
            $divBuyersNeeds.css('opacity', '100');
        } else {
            $divBuyersNeeds.css('opacity', '0');
        }

        let $divMovings = $tr.find('td').eq(2).find('div');
        if (mw.movings > 0) {
            $divMovings.css('opacity', '100');
        } else {
            $divMovings.css('opacity', '0');
        }

        let $divProgress = $tr.find('td').eq(1).find('div');
        if (managerQuant != null && mw.good === managerQuant.good) {
            $divMovings.addClass('text-primary');
            if (cleintServerTimeDiff === null) {
                setQuantProgress($divProgress, managerQuant);
            }
        } else {
            $divMovings.removeClass('text-primary');
            $divProgress.hide();
        }

        $divBuyersNeeds.find('span').html(mw.buyersNeeds);
        $divMovings.find('span').html(mw.movings);
    }
}

function btnCashClick() {
    rest('cash', (data) => {
        if (!data.success) {
            showAlert('Ошибка', data.message, 'alert-danger', 3000);
        }
    });
    return false;
}

function calcScore() {
    $('#scoreMoney').html(wrap.business.money);
    $('#scoreLike').html(wrap.business.like);
    $('#scoreDislike').html(wrap.business.dislike);
}

function calcBuyers() {
    let $list = $('#buyerList');

    let $span = $list.find('span[state=BUY]');
    $span.empty();
    for (let i = 0; i < wrap.buyers.BUY; i++) {
        $span.append('<i class="fas fa-male" style="color: teal; width: 20px"></i>');
    }

    $span = $list.find('span[state=CASH_WAIT]');
    $span.empty();
    for (let i = 0; i < wrap.buyers.CASH_WAIT; i++) {
        $span.append('<i class="fas fa-street-view" style="color: chocolate; width: 20px"></i>');
    }

    $span = $list.find('span[state=CASH]');
    $span.empty();
    for (let i = 0; i < wrap.buyers.CASH; i++) {
        $span.append('<i class="fas fa-male" style="color: #2cb004; width: 20px"></i>');
    }
}

function activeManagerQuant(quantType) {
    return wrap
        .quants
        .find(q => q.quantType === quantType && q.staff.role === ROLE_MANAGER)
}

function resetClick() {
    confirm(
        'Начать заново?',
        'Сбросить все текущие достижения и начать заново?',
        'danger',
        true,
        'Ok',
        null,
        null,
        function () {
            rest('init', (data) => {
                if (!data.success) {
                    showAlert('Ошибка', data.message, 'alert-danger', 3000);
                }
            });
            $('#homeSubmenu').removeClass('show');
            window.scrollTo(0, 0);
        })
    return true;
}

function addGoodClick(a) {
    let good = $(a).attr('good');
    rest('addgood?good=' + good, (data) => {
        if (!data.success) {
            showAlert('Ошибка', data.message, 'alert-danger', 3000);
        }
    });
    window.scrollTo(0, 0);
    return false;
}

function setBackgroundClick() {

    let back = prompt("URL изображения фона", "");
    if (back) {
        backgroundImage = back;
        $('#content').css('background-image', 'url("' + backgroundImage + '")');
    }

    // $('#content').css('background-image', 'url("http://selt-nn.ru/upload/iblock/8bf/8bff577c5915e2bf2718d02ff868cd2f.jpg")');
    // $('#content').css('background-image',  'url("../images/ozero_les_sneg.jpg")');
    // $('#content').css('background',  '#fafafa url("../images/ozero_derevia_sneg.jpg") repeat top center');
    // $('#content').css('background-image',  'url("https://images.wallpaperscraft.ru/image/single/more_lodka_pliazh_208597_1920x1080.jpg")');
    // $('#content').css('background-image',  'url("../images/poberezhe_more_pesok.jpg")');
    /*background: #fafafa url("../images/gorod_vecher_snegopad.jpg") repeat top center;*/
    /*background: #fafafa url("https://www.toptal.com/designers/subtlepatterns/uploads/leaves.png") repeat top center;*/
    return false;
}

function fillRaiseClick(a) {
    let quantity = $(a).attr('quantity');
    rest('fillraise?quantity=' + quantity, showRestResult);
    window.scrollTo(0, 0);
    return false;
}

function cashRaiseClick(a) {
    let quantity = $(a).attr('quantity');
    rest('cashraise?quantity=' + quantity, showRestResult);
    window.scrollTo(0, 0);
    return false;
}

function configsClick() {
    rest('configs', (data) => {
        let $modal = $('#configsModal');
        $modal.find('.btn-ok')
            .on('click', function () {
                $modal.modal('hide');
            });
        let $tbody = $('#configsTbody');
        $tbody.empty();

        for (let section of data.sections) {
            $tbody.append($('<tr><td colspan="4" class="config_section">' + section.title + '</td></tr>'));
            for (let item of data.items.filter(it => it.section === section.name)) {
                let $tr = $('<tr></tr>');
                $tr.append($('<td>' + item.title + '</td>'));
                $tr.append($(tdval(item.current)));
                $tr.append($(tdval(item.max)));
                $tr.append($(tdval(item.increaseLikesStep)));
                $tbody.append($tr);
            }
        }

        $modal.modal('show');
    });
    return false;
}

function tdval(val) {
    if (val > 0) {
        return '<td class="tdintval">' + val + '</td>';
    } else {
        return '<td class="tdintval"></td>';
    }
}
