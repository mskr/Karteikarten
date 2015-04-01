/**
 * Enthält nützliche Hilfsfunktionen, die uns die Arbeit erleichtern.
 */

/**
 * Füllt eine gegebenee selection mit optionen
 * @param select
 * @param optArray Array mit den Einträgen als String
 * @param slectedOptName Option die gewählt sein soll
 * @param clearFirst Wenn true, dann werden alle alten Optionen erst entfernt
 */
function fillSelectWithOptions(select, optArray, selectedOptName, clearFirst) 
{
	if(clearFirst == true)
		$(select).find('option').remove();

    for(var i in optArray) 
    {
        $(select).append("<option value = '" + optArray[i] +"'>"+optArray[i]+"</option>");
    }

    $(select).find("option[value='"+ selectedOptName +"']").prop('selected', true);
}

/**
 * Triggert einen Sind-Sicher-Dialog.
 * @param anchorElem ist das Triggerelement (jQuery- oder DOM-Element).
 * @param message ist die Nachricht, die der Benutzer bestaetigen soll.
 * @param doCriticalThing ist eine Funktion, die nach Bestaetigung mit Ok ausgefuehrt wird.
 */
function sindSieSicher(anchorElem, message, doCriticalThing)
{
    $("#dialog_sicher").popup({
        type: 'tooltip',
        vertical: 'top',
        horizontal: 'center',
        tooltipanchor: anchorElem,
        transition: 'none'
    });
    $("#dialog_sicher").popup("show");
    $(".dialog_sicher_frage").text(message);
    $(".dialog_sicher_ja").click(function() {
        doCriticalThing();
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