/**
 * @author mk
 */

/**
 * Konfiguriert alle Ajax Calls.
 * Kommt vom Server keine Antwort,
 * wird ein Fehler ausgegeben.
 * Setzt einen Timeout.
 */
$.ajaxSetup({
    type: "GET",
	timeout: 3000,
	error: function(jqXHR, textStatus, errorThrown) { 
	    //status === 'timeout' if it took too long.
	    //handle that however you want.
	    alert("Ajax Call returned error. Debug Info: "+jqXHR.status+", "+textStatus+", "+errorThrown);
	}
});

/**
 * Bequeme Funktion um einen Ajax Call an ein Servlet zu senden.
 * Antwortet das Servlet mit dem Code "noerror" dann wird eine Funktion ausgefuehrt,
 * andernfalls wird die entsprechende Fehlermeldung auf der GUI angezeigt.
 * @param servletUrl ist ein String
 * @param action ist ein String
 * @param params ist ein Objekt mit Parameternamen und jeweiligem Wert
 * @param noerrorFunc ist eine Funktion, die bei "noerror"-Antwort ausgefuehrt wird.
 * @returns Ajax Objekt, das Informatioen ueber den Antwortstatus enthaelt.
 */
function ajaxCall(servletUrl, action, params, noerrorFunc)
{
    return $.ajax({
        url: servletUrl,
        data: "action="+action+"&"+toUrlParamString(params),
        success: function(responseJson) {
            var errCode = responseJson["error"];
            if(errCode == "noerror") 
            {
                noerrorFunc();
            }
            else
            {
                message(0, buildMessage(errCode));
            }
        }
    });
}

/**
 * Bequeme Funktion um einen Ajax Call an ein Servlet zu senden.
 * Antwortet das Servlet mit dem Code "noerror" dann wird eine Funktion ausgefuehrt,
 * andernfalls wird die entsprechende Fehlermeldung auf der GUI angezeigt.
 * Zusaetzlich koennen Funktionen uebergeben werden, die bei beforeSend und complete ausgefuehrt werden,
 * um etwa eine Lade-Meldung auf der GUI anzuzeigen.
 * @param servletUrl ist ein String
 * @param action ist ein String
 * @param params ist ein Objekt mit Parameternamen und jeweiligem Wert
 * @param noerrorFunc ist eine Funktion, die bei "noerror"-Antwort ausgefuehrt wird.
 * @param beforeFunc ist eine Funktion, die beforeSend ausgefuehrt wird.
 * @param completeFunc ist eine Funktion, die bei complete ausgefuehrt wird.
 * @returns Ajax Objekt, das Informatioen ueber den Antwortstatus enthaelt.
 */
function ajaxCall(servletUrl, action, params, noerrorFunc, beforeFunc, completeFunc)
{
    return $.ajax({
        url: servletUrl,
        data: "action="+action+"&"+toUrlParamString(params),
        beforeSend: beforeFunc(),
        success: function(responseJson) {
            var errCode = responseJson["error"];
            if(errCode == "noerror") 
            {
                noerrorFunc();
            }
            else
            {
                message(0, buildMessage(errCode));
            }
        },
        complete: completeFunc()
    });
}

/**
 * Gibt einen URL Parameter String der Form
 * key1=val1&key2=val2&key3=val3...
 * zurueck.
 * Im Unterschied zu buildUrlQuery(paramObj)
 * steht hier kein ? vorne.
 * @param paramObj
 */
function toUrlParamString(paramObj) 
{
    var locationSearchTmp = "";
    var i = 0;
    // Anzahl elemente Bestimmen
    var maxI = 0;
    for(var param in paramObj) 
    {
        maxI++;
    }
    
    for(var param in paramObj) 
    {
        locationSearchTmp += param + "=" + paramObj[param]
        if(i < maxI-1)
            locationSearchTmp += "&";
        
        i++;
    }
    return locationSearchTmp;
}