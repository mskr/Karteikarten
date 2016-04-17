/**
 * @author Andreas, Marius
 */

/**
 * Sendet eine Datei per asynchronem HTTP POST Request an das FileUploadServlet.
 * @param file JS File Objekt
 * @param successFkt wird nach erfolgreichen Upload ausgefuehrt
 * @param uploadAction der obligatorische 'action' Parameter fuer den Server
 * @param params weitere Parameter fuer den Server
 * @param beforeFkt wird unmittelbar vor dem Absenden ausgefuehrt
 * @param completeFkt wird ausgefuehrt wenn der Server geantwortet hat
 * @see https://developer.mozilla.org/de/docs/Web/API/FormData
 * @see https://developer.mozilla.org/de/docs/Web/API/File
 */
function uploadFile(file, successFkt, uploadAction, params, beforeFkt, completeFkt) 
{
    var formData = new FormData();
    formData.append('file', file);
    formData.append('action', uploadAction);
    for(var key in params)
        formData.append(key, params[key]);
        
    $.ajax({
        type: "POST",
        url: fileUploadServlet,
        timeout: 0, // == Kein Timeout (bedenke Video-Uploads!)
        enctype: "multipart/form-data",
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: beforeFkt,
        success: successFkt,
        complete: completeFkt
    });
}