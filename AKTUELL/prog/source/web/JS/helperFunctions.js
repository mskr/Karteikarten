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
		$(select).empty();

    for(var i in optArray) 
    {
        $(select).append("<option value = '" + optArray[i] +"'>"+optArray[i]+"</option>");
    }

    $(select).find("option[value='"+ selectedOptName +"']").prop('selected', true);
}

/**
 * Triggert einen Sind-Sicher-Dialog auf der GUI.
 * @param anchorElem ist das Triggerelement (jQuery- oder DOM-Element).
 * @param message ist die Nachricht, die der Benutzer bestaetigen soll.
 * @param doCriticalThing ist eine Funktion, die nach Bestaetigung mit Ok ausgefuehrt wird.
 */
function sindSieSicher(anchorElem, message, doCriticalThing, locV, locH)
{	
    $("#dialog_sicher").popup({
        type: 'tooltip',
        vertical: locV,
        horizontal: locH,
        tooltipanchor: anchorElem,
        transition: 'none'
    });
    $("#dialog_sicher").popup("show");
    $(".dialog_sicher_frage").text(message);
    $(".dialog_sicher_ja").click(function() {
        doCriticalThing();
        $("#dialog_sicher").popup("hide");
    });
}

/**
 * Bequeme Funktion um einen Ajax Call an ein Servlet zu senden.
 * Antwortet das Servlet mit dem Code "noerror" dann wird eine Funktion ausgefuehrt,
 * die dieser Funktion uebergeben wird.
 * Andernfalls wird standardmaessig der Default-Error-Text auf der GUI angezeigt,
 * aber man kann auch eine errorHandlingFunc uebergeben, die eine Spezialbehandlung erlaubt.
 * Zusaetzlich koennen Funktionen uebergeben werden, die bei beforeSend und complete ausgefuehrt werden,
 * um etwa eine Lade-Meldung auf der GUI anzuzeigen.
 * @param servletUrl ist ein String, der das richtige Servlet adressiert.
 * @param action ist ein String, der als Kommando fuer den Server fungiert.
 * @param params ist ein Objekt mit Parameternamen und jeweiligem Wert, die vom Server ausgelesen werden.
 * @param noerrorFunc ist eine Funktion, die bei einer Antwort mit errCode 'noerror' ausgefuehrt wird.
 * @param errorHandlingFunc ist eine Funktion, die bei einer Antwort mit errCode != 'noerror' ausgefuehrt wird (kann optional uebergeben werden).
 * @param beforeFunc ist eine Funktion, die beforeSend ausgefuehrt wird (kann optional uebergeben werden).
 * @param completeFunc ist eine Funktion, die bei complete ausgefuehrt wird (kann optional uebergeben werden).
 * @returns Ajax Objekt, das Informatioen ueber den Antwortstatus enthaelt.
 */
function ajaxCall(servletUrl, action, noerrorFunc, params, errorHandlingFunc, beforeFunc, completeFunc)
{
    return $.ajax({
        url: servletUrl,
        data: "action="+action+"&"+toUrlParamString(params),
        beforeSend: beforeFunc,
        success: function(jsonResponse) {
            if(verifyResponse(jsonResponse,errorHandlingFunc)) {
                noerrorFunc(jsonResponse);
            }
        },
        complete: completeFunc
    });
}

/**
 * Gibt einen URL Parameter String der Form
 * key1=val1&key2=val2&key3=val3...
 * zurueck.
 * Im Unterschied zu buildUrlQuery(paramObj)
 * steht hier kein ? vorne.
 * Wird undefined uebergeben kommt ein leerer String zurueck!
 * @param paramObj enthaelt die schoen verpackten Parameter.
 */
function toUrlParamString(paramObj) 
{
    if(paramObj == undefined)
    {
        return "";
    }
    var locationSearchTmp = "";
    
    for(var param in paramObj)
    {
    	if($.isArray(paramObj[param]))
    	{
    		for( var i in paramObj[param])
    		{
                locationSearchTmp += param + "=" + paramObj[param][i] + "&";
    		}
    	}
    	else
    	{
            locationSearchTmp += param + "=" + paramObj[param] + "&";
    	}
    }
    return locationSearchTmp;
}


function addItemToList(itemMap, container, displayName, data, removeFkt, clickFkt) 
{
	if(itemMap[displayName])
		return false;
	
	var html = "<span class='itemListItem'>"; 
	
	if(clickFkt != undefined)
		html += "<a class='itemListItemName'>" + displayName + "</a>";
	else
		html += "<span class='itemListItemName'>" + displayName + "</span>";
	
	html += "<a class='octicon octicon-x itemListItemClose'></a>" +
				"</span>"
	
	// Zu jquery konvertieren
	html = $(html);
	container.append(html);
	
	// Map hinzufügen
	itemMap[displayName] = data;
	
	html.find(".itemListItemName").click(function() 
	{
		if(clickFkt != undefined)
			clickFkt(data,displayName);
	});

	html.find(".itemListItemClose").click(function() 
	{
		html.remove();

		if(removeFkt != undefined)
			removeFkt(itemMap[displayName]);

		// Aus map löschen
		delete itemMap[displayName];
	});
	
	
	
	return true;
}
