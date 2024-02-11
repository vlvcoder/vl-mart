function rest(url, handler) {
    url = 'rest/' + url;
    fetch(url)
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            if (handler !== null && handler !== undefined) {
                handler(data);
            }
        });
}

function showRestResult(data) {
    if (data.success) {
        showAlert('', data.message, 'alert-info', 5000);
    } else {
        showAlert('Ошибка', data.message, 'alert-danger', 5000);
    }
}

function showAlert(header, content, alertClass, delayAutoHide) {
    let $alert = $('#alertDiv');
    clearColors($alert);
    $alert.addClass(alertClass);
    $alert.find('.alert-header').html(header);
    $alert.find('.alert-content').html(content);
    $alert.show();

    if (delayAutoHide > 0) {
        setTimeout(
            () => {
                $alert.hide();
            },
            delayAutoHide);
    }
}

function clearModalSize($element) {
    $element.removeClass('modal-sm');
    $element.removeClass('modal-lg');
    $element.removeClass('modal-xl');
}

function clearColors($element) {
    $element.removeClass('alert-primary');
    $element.removeClass('alert-secondary');
    $element.removeClass('alert-danger');
    $element.removeClass('alert-warning');
    $element.removeClass('alert-info');
    $element.removeClass('alert-light');
    $element.removeClass('alert-dark');
    $element.removeClass('alert-success');

    $element.removeClass('bg-primary');
    $element.removeClass('bg-secondary');
    $element.removeClass('bg-danger');
    $element.removeClass('bg-warning');
    $element.removeClass('bg-info');
    $element.removeClass('bg-light');
    $element.removeClass('bg-dark');
    $element.removeClass('bg-success');
}

function calcAlert() {
    if (wrap.alert !== null && wrap.alert !== undefined) {
        showRestResult(wrap.alert);
    }
}
function closeAlert(btn) {
    $(btn).parent().hide();
    return false;
}

function showManual() {
    $('.manual').show();
    return false;
}
