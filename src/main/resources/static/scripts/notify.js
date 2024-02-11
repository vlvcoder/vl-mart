function calcNotify() {
    if (wrap.notify === null || wrap.notify === undefined) {
        return;
    }

    rest('flushnotify?type=' + wrap.notify.name);

    $notify = $("#liveToast");
    $header = $notify.find('.toast-header');
    $body = $notify.find('.toast-body');
    clearColors($header);
    clearColors($body);
    $body.addClass('text-white');
    $header.find('strong').html(wrap.notify.header);

    switch (wrap.notify.style) {
        case 'SUCCESS':
            $header.addClass('bg-success');
            $body.addClass('bg-success');
            $header.find('small').html('Поздравляем!');
            break;
        case 'PRIMARY':
            $header.addClass('bg-primary');
            $body.addClass('bg-primary');
            $header.find('small').html('Сообщение');
            break;
        case 'WARNING':
            $header.addClass('bg-warning');
            $body.removeClass('text-white');
            $header.find('small').html('Извещение');
            break;
        case 'DANGER':
            $header.addClass('bg-danger');
            $body.addClass('bg-danger');
            $header.find('small').html('Предупреждение');
            break;
    }
    $body.html(wrap.notify.text);
    $notify.toast("show");
}
