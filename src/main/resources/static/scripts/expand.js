const SPACE_TYPE_SHOP = 'SHOP';

function placeExpandClick(a) {
    let good = $(a).attr('good');
    let spaceType = $(a).attr('space');
    rest('expandprices', (data) => {
        let space = spaceType === SPACE_TYPE_SHOP ? wrap.shopPlaces : wrap.storePlaces;
        let place = space.find(p => p.good === good);
        let $expand = $('#divExpand');
        $expand.find('td.current-volume').html(place.capacity);
        let $tbody = $expand.find('tbody');
        $tbody.empty();
        let variants = spaceType === SPACE_TYPE_SHOP ? data.shopPlaceExpands : data.storePlaceExpands;

        for (let variant of variants) {
            let $tr = $('<tr>');
            $tr.attr('onclick', 'callExpand(this);');
            $tr.attr('space', spaceType);
            $tr.attr('good', good);
            $tr.attr('volume', variant.volume);
            $tr.append('<td class="exp1"><h5><span class="badge bg-warning">+' + variant.quantity + '</span></h5></td>')
            $tr.append('<td>=</td>')
            $tr.append('<td class="tdright">' + (place.capacity + variant.quantity) + '</td>')
            $tr.append('<td class="tdright score-money exp4">' + variant.priceString + '</td>')
            $tbody.append($tr);
        }

        showExpandModal(spaceType, place);

    });
    return false;
}

function callExpand(tr) {
    let $modal = $('#expandModal');
    $modal.modal('hide');

    let $tr = $(tr);
    let space = $tr.attr('space');
    let good = $tr.attr('good');
    let volume = $tr.attr('volume');
    rest('expandplace?space=' + space + '&good=' + good +'&volume=' + volume, showRestResult);
    window.scrollTo(0, 0);
}

function showExpandModal(spaceType, place) {
    let $modal = $('#expandModal');
    $modal.modal('show');
    let dialogTitle = spaceType === SPACE_TYPE_SHOP ?
        '<i class="fas fa-th"></i><span class="menuitem">Расширение полки товара&nbsp;&nbsp;&nbsp;&nbsp;<strong>' +
        place.title + '</strong></span>' :
        '<i class="fas fa-th-large"></i><span class="menuitem">Расширение складского места товара&nbsp;&nbsp;&nbsp;&nbsp;<strong>' +
        place.title + '</strong></span>';
    let $header = $modal.find('.modal-header');
    $header
        .removeClass()
        .addClass('modal-header')
        .addClass(spaceType === SPACE_TYPE_SHOP ? 'card-shop' : 'card-store')
    $modal.find('.modal-title').html(dialogTitle);
}

function calcExpands() {
    subCalcExpands(wrap.shopPlaces, $('#shopExpandList'))
    subCalcExpands(wrap.storePlaces, $('#storeExpandList'))
}

function subCalcExpands(list, $ul) {
    for (let place of list) {
        $ul
            .find("a[good='" + place.good + "']")
            .find('td.td-capacity')
            .html(place.capacity);
    }
}
