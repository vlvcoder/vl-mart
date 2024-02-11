function confirm(header, text, bgBtnClass, cancelPermit, btnOkText, $body, modalSize, handler) {
    let $modal = $('#modalConfirm');
    if (!cancelPermit) {
        let $modalGameover = $('#modalGameover');
        $modalGameover.html($modal.html());
        $modal = $modalGameover;
    }

    let $dialog = $modal.find('.modal-dialog');
    clearModalSize($dialog);
    if (modalSize !== null && modalSize !== undefined) {
        $dialog.addClass(modalSize);
    }

    $modal.modal('show');

    $modal.find('.modal-header').addClass('bg-' + bgBtnClass);
    $modal.find('.modal-title').html(header);

    if (cancelPermit) {
        $modal.find('.btn-cancel').show();
        $modal.find('.btn-close').show();
    } else {
        $modal.find('.btn-cancel').hide();
        $modal.find('.btn-close').hide();
    }
    $btnOk = $modal.find('.btn-ok');
    if (btnOkText === null) {
        $btnOk.hide();
    } else {
        $btnOk
            .html(btnOkText)
            .addClass('btn-' + bgBtnClass)
            .on('click', function () {
                $modal.modal('hide');
                handler();
            })
            .show();
    }
    let $modalBody = $modal.find('.modal-body');
    $modalBody.empty();
    if (text !== null && text !== undefined) {
        $modal.find('.modal-body').append(text);
    }
    if ($body !== null && $body !== undefined) {
        $body.clone().appendTo($modalBody);
    }
}
