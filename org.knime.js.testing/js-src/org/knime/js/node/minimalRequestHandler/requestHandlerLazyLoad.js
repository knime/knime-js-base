if (window.requestHandler) {
    /* global requestHandler:false */
    requestHandler.initRequest = function (request, textArea, representation, curRequests) {

        /* this is where the magic happens */
        var promise = knimeService.requestViewUpdate(request, representation.keepOrder);
        promise
            .progress(function (monitor) {
                return requestHandler.displayProgress(monitor);
            })
            .then(function (response) {
                return requestHandler.displayResponse(response);
            })
            .catch(function (error) {
                return requestHandler.displayError(request.sequence, error);
            });
        /* end magic */

        if (promise.monitor && promise.monitor.requestSequence) {
            curRequests.push(promise);
            var text = 'Issued request sequences: [';
            for (var i = 0; i < curRequests.length; i++) {
                text += curRequests[i].monitor.requestSequence;
                if (i < curRequests.length - 1) {
                    text += ', ';
                }
            }
            text += ']\n';
            textArea.value += text;
            textArea.scrollTop = textArea.scrollHeight;
        }

    };
}
