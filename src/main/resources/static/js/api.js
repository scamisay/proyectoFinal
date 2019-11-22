function getParamsForAjax(fieldContainerReference){
    var params = [];
    $(fieldContainerReference)
        .find('.apiParam')
        .each(
            function(){
                var campo = $(this).attr('id');
                if ($(this).is(':checkbox')) {
                    params.push( '"'+campo +'":"'+ $(this).prop('checked') + '"' );
                }else{
                    params.push( '"'+campo +'":"'+ $(this).val() + '"' );
                }

            }
        );
    return JSON.parse( '{' + params.join() + '}');
}

function pushInArray(array, key, value){
    array.push( '"'+key +'":"'+ value + '"' );
    return array;
}

function getParamsForAjaxFromCollection(collection, params){
    if(params == undefined){
        params = [];
    }

    $(collection).each(
        function(){
           $(this).find('.apiParam')
               .each(
                   function(){
                       var campo = $(this).attr('id');
                       if ($(this).is(':checkbox')) {
                           params.push( '"'+campo +'":"'+ $(this).prop('checked') + '"' );
                       }else{
                           params.push( '"'+campo +'":"'+ $(this).val() + '"' );
                       }
                   }
               );
        }
    );

    return JSON.parse( '{' + params.join() + '}');
}

function sendToAPI(jsonParams){
    var data = {};
    if(jsonParams.hasOwnProperty('fieldContainerId')){
        data = getParamsForAjax($('#' + jsonParams.fieldContainerId));
    }else if(jsonParams.hasOwnProperty('data')){
        data = jsonParams.data;
    }else if(jsonParams.hasOwnProperty('fieldContainerClass')){
        data = getParamsForAjaxFromCollection($('.' + jsonParams.fieldContainerClass));
    }

    var async = false;
    if(jsonParams.hasOwnProperty('async')){
        async = jsonParams.async;
    }

    var success = function () {};
    if(jsonParams.hasOwnProperty('success')){
        success = jsonParams.success;
    }

    var error = function () {};
    if(jsonParams.hasOwnProperty('error')){
        error = jsonParams.error;
    }

    var type = 'POST';
    if(jsonParams.hasOwnProperty('type')){
        type = jsonParams.type;
    }

    var processData = true;
    if(jsonParams.hasOwnProperty('processData')) {
        processData = jsonParams.processData;
    }

    var contentType = 'application/x-www-form-urlencoded; charset=UTF-8';
    if(jsonParams.hasOwnProperty('contentType')) {
        contentType = jsonParams.contentType;
    }


    $.ajax({
        type: type,
        url: jsonParams.url,
        data: data,
        processData: processData,
        contentType: contentType,
        success: success,
        error: error,
        async:async
    });
}

function clearForm(jsonParams){
    if(jsonParams.hasOwnProperty('fieldContainerId')){
        $('#'+jsonParams.fieldContainerId).find('input,select').val('');
    }
}

function redirect(redirectURL){
    window.location.href = redirectURL;
}

function successAndReload(redirectURL) {
    swal("Los cambios se han realizado con exito")
        .then((value) => {
                if(redirectURL == undefined || redirectURL == ""){
                location.reload();
            }else {
                window.location.href = redirectURL;
            }
        });
}

function validateInputFile(oInput, oType) {
    var _validFileExtensions;

    switch(oType) {
        case "WORD":
            _validFileExtensions = [".doc", ".docx"];
            break;
        case "PDF":
            _validFileExtensions = [".pdf"];
            break;
        case "HTML":
            _validFileExtensions = [".html"];
            break;
        case "TXT":
            _validFileExtensions = [".txt"];
            break;
    }

    if (oInput.type == "file") {
        var sFileName = oInput.value;
        if (sFileName.length > 0) {
            var blnValid = false;
            for (var j = 0; j < _validFileExtensions.length; j++) {
                var sCurExtension = _validFileExtensions[j];
                if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
                    blnValid = true;
                    break;
                }
            }

            if (!blnValid) {
                alert("Lo siento, " + sFileName + " no es válido, las extensiones permitidas son: " + _validFileExtensions.join(", "));
                oInput.value = "";
                return false;
            }
        }
    }
    return true;
}

//sendToAPI({fieldContainerId: 'addNewRawMaterial', url: '/ip/api/ip/add', callback: function(){}});