/**
 * Enthält nützliche Hilfsfunktionen, die uns die Arbeit erleichtern.
 */

/**
 * Füllt eine gegebenee selection mit optionen
 * @param select
 * @param optArray Array mit den Einträgen als String
 * @param slectedOptName Option die gewählt sein soll
 * @param clearFirst Wenn true, dann werden alle alten Optionen erst entfernt
 * @param placeholder String kann uebergeben werden, falls eine Default Option gesetzt werden soll
 */
function fillSelectWithOptions(select, optArray, selectedOptName, clearFirst, placeholder) 
{
	if(clearFirst == true)
		$(select).empty();
	
	if(placeholder != undefined)
	    $(select).append("<option value='' disabled selected>"+placeholder+"</option>");

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
$(document).ready(function() {
	$("#dialog_sicher_popup_overlay").click(function() {
		$("#dialog_sicher").addClass("hidden");
		$("#dialog_sicher").fadeOut(300);
		$("#dialog_sicher_popup_overlay").fadeOut(300);
	    $("#dialog_sicher_ja").off("click");
	});
});
function sindSieSicher(anchorElem, message, doCriticalThing, locV, locH)
{
	$("#dialog_sicher_popup_overlay").css("z-index","4000");
    $("#dialog_sicher").removeClass("hidden");
    $("#dialog_sicher").css({
        top: 0,
        left: 0
    });
    
    var pos = anchorElem.offset();
    $("#dialog_sicher").offset({
        top: pos.top,
        left: pos.left
    });
    
    // Workaround
    var clone = $("#dialog_sicher").clone();
    clone.css("visibility","hidden");
    $('body').append(clone);
    // Breite + Padding !
    var width = clone.find(".sindSieSicher_head").outerWidth() + 1*clone.find(".sindSieSicher_head").css("padding-left").replace(/[^-\d\.]/g, '');
    clone.remove();
    
    var overflow = pos.left + width - $(window).width();
    if(overflow > 0)
    {
        $("#dialog_sicher").offset({
            top: $("#dialog_sicher").offset().top,
            left: $("#dialog_sicher").offset().left - overflow
        });
    }
    
    $("#dialog_sicher_popup_overlay").fadeIn(300);
    $("#dialog_sicher").fadeIn(300);

    $("#dialog_sicher_frage").text(message);
    
    $("#dialog_sicher_ja").click(function(e) {
        doCriticalThing();
    });
}

var popupSeitenIterator = 0;
/**
 * Blendet ein Popup auf der GUI ein. Hat das Popup mehrere Elemente mit der Klasse popup_fenster_body,
 * werden diese als Seiten dargestellt.
 * @param popupOverlayWrapper jQuery Objekt. Element, das den dunklen Hintergrund darstellt.
 * @param closeElems Array aus jQuery Objekten. Elemente, die bei Klick das Popup schliessen.
 * @param closeFunc Funktion, die bei Schliessen (egal auf welchem Weg!) ausgefuehrt wird.
 * @param submitElem jQuery Objekt. Element, das das Abschicken der Daten aus diesem Popup triggert.
 * @param submitFunc Funktion, die das Abschicken der Daten aus diesem Popup ausfuehrt.
 * Gibt sie true zurueck wird das Popup geschlossen, andernfalls bleibt es bestehen.
 * @param focusElem jQuery Objekt. Element, das nach dem Oeffnen fokussiert werden soll.
 * @param weiterElem jQuery Objekt. Element, das das Umschalten zur naechsten Seite triggert.
 * @param zurueckElem jQuery Objekt. Element, das das Umschalten zur vorherigen Seite triggert.
 */
function popupFenster(popupOverlayWrapper, closeElems, closeFunc, submitElem, submitFunc, focusElem, weiterElem, zurueckElem)
{
    // Scrollbar ausserhalb Popup deaktivieren, sodass nur Popup Scrollbar sichtbar
    $("body").css("overflow-y","hidden");

    if(submitFunc == undefined)
    	submitFunc = function(){return true;};
    if(closeFunc == undefined)
    	closeFunc = function(){return true;};
    
    var seitenArr = popupOverlayWrapper.find(".popup_fenster_body");
    $(seitenArr[0]).show();
    if(seitenArr.length > 1)
    {
        for(var i=1; i<seitenArr.length; i++)
        {
            $(seitenArr[i]).hide();
        }
        submitElem.hide();
        if(weiterElem!= undefined){
        	weiterElem.show();

        	weiterElem.off();
        	weiterElem.click(function() {
        		$(seitenArr[popupSeitenIterator]).slideUp();
        		popupSeitenIterator++;
        		$(seitenArr[popupSeitenIterator]).slideDown();
        		if(popupSeitenIterator > 0)
        		{
        			zurueckElem.show();
        		}
        		if(popupSeitenIterator == seitenArr.length-1)
        		{
        			weiterElem.hide();
        			submitElem.show();
        		}
        	});
        }
        if(zurueckElem!= undefined){
        	zurueckElem.hide();
        	zurueckElem.off();
        	zurueckElem.click(function() {
        		$(seitenArr[popupSeitenIterator]).slideUp();
        		popupSeitenIterator--;
        		$(seitenArr[popupSeitenIterator]).slideDown();
        		if(popupSeitenIterator == 0)
        		{
        			zurueckElem.hide();
        		}
        		if(popupSeitenIterator < seitenArr.length-1)
        		{
        			weiterElem.show();
        			submitElem.hide();
        		}
        	});
        }
    }
    else
    {
    	if(weiterElem!= undefined)
    		weiterElem.hide();
    	if(zurueckElem!= undefined)
    		zurueckElem.hide();
    }
    
    popupOverlayWrapper.fadeIn(300);
    popupOverlayWrapper.find(".popup_fenster").removeClass("hidden");
    
    if(focusElem != undefined)
    	focusElem.focus();
    
    for(var i in closeElems)
    {
        closeElems[i].off(),
        closeElems[i].click(function() {
            $("body").css("overflow-y","scroll"); // Scrollbar ausserhalb Popup wieder aktivieren
            popupOverlayWrapper.fadeOut(300);
            popupOverlayWrapper.find(".popup_fenster").addClass("hidden");
            closeFunc();
            popupSeitenIterator = 0;
        });
    }
    submitElem.off();
    submitElem.click(function() {
        if(submitFunc())
        {
            $("body").css("overflow-y","scroll"); // Scrollbar ausserhalb Popup wieder aktivieren
            popupOverlayWrapper.fadeOut(300);
            popupOverlayWrapper.find(".popup_fenster").addClass("hidden");
            closeFunc();
            popupSeitenIterator = 0;
        }
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
 * @param noerrorFunc ist eine Funktion, die bei einer Antwort mit errCode == 'noerror' ausgefuehrt wird.
 * @param errorHandlingFunc ist eine Funktion, die bei einer Antwort mit errCode != 'noerror' ausgefuehrt wird (kann optional uebergeben werden).
 * Achtung: Die errorHandlingFunc muss true zurueckgeben, falls der Error behandelt werden konnte und false andernfalls 
 * (dann wird der Default-Error-Text fuer den jeweiligen Code auf der GUI angezeigt).
 * @param beforeFunc ist eine Funktion, die beforeSend ausgefuehrt wird (kann optional uebergeben werden).
 * @param completeFunc ist eine Funktion, die bei complete ausgefuehrt wird (kann optional uebergeben werden).
 * @returns Ajax Objekt, das Informatioen ueber den Antwortstatus enthaelt.
 */
function ajaxCall(servletUrl, action, noerrorFunc, params, errorHandlingFunc, beforeFunc, completeFunc, timeOut)
{
    console.log("[AJAX] action="+action);
    if(timeOut == undefined)
    	timeOut = 6000;
    
//    return $.ajax({
//    	timeout: timeOut,
//        url: servletUrl,
//        data: "action="+action + "&"+ toUrlParamString(params),
//        beforeSend: beforeFunc,
//        success: function(jsonResponse) {
//            if(verifyResponse(jsonResponse,errorHandlingFunc)) {
//            	if(noerrorFunc!= undefined)
//            		noerrorFunc(jsonResponse);
//            }
//        },
//        complete: completeFunc
//    });
    
    if(params == undefined)
		params = {};
	params["action"] = action;
	
    return $.ajax({
    	timeout: timeOut,
        url: servletUrl,
        data: params,
        beforeSend: beforeFunc,
        success: function(jsonResponse) {
            if(verifyResponse(jsonResponse,errorHandlingFunc)) {
            	if(noerrorFunc!= undefined)
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
    			// ESCAPING WICHTIG! Sonst können wir bspw. keine umlaute übertragen
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

/**
 * Fügt Autovervollstaendigung zu einem input[type="text"] Element hinzu.
 * Tippt der Benutzer wird ein Timer gestartet. Der bisher eingegebene String wird an den Server gesendet.
 * Dieser startet in der Datenbank eine Funktion, welche nach aehnlichen Strings sucht (Editierdistanz) und diese zurueckgibt.
 * Der Server verpackt die Daten in geeignete Objekte und schickt sie als JSON zurueck.
 * Der Client zeigt die Ergebnisse auf der GUI an. Dazu wird ein div Container mit Suchergebnissen unter dem Textfeld ausgeklappt.
 * Der Benutzer kann mit den Pfeiltasten darin navigieren und Ergebnisse entweder per Enter-Taste oder Mausklick auswaehlen.
 * <p><strong>WICHTIG:</strong> Die Objekte categories und categoryClassMapping muessen gleich gross sein!</p>
 * <p><strong>WICHTIG:</strong> Autocomplete Textfelder muessen auf required gesetzt sein!</p>
 * @param textInput jQuery Objekt des input[type="text"] Elementes
 * @param categories Object, das als Keys Strings enthaelt, welche als Unterteilungen in der Suchergebnisliste erscheinen;
 * und als Values Funktionen, welche bei Auswahl eines Suchergebnisses aus der jeweiligen Kategorie ausgefuehrt werden.
 * @param categoryClassMapping Object, das als Keys die Klassen von Objekten enthaelt, die vom Server als Suchergebnisse kommen (z.B. Benutzer, Veranstaltungen...);
 * Value ist hier jeweils die Kategorie, in die das Objekt eingeordnet werden soll.
 */
function autoComplete(textInput, categories, categoryClassMapping, action)
{
    var suchergHtmlStr = "<div id='suche_ergebnisse_"+textInput.attr("id")+"' class='suche_ergebnisse'>"
                   +         "<div id='sucherg_x_"+textInput.attr("id")+"' class='sucherg_x'><span class='octicon octicon-x'></span></div>";
    for(var i in categoryClassMapping)
    {
        suchergHtmlStr +=    "<div class='sucherg_titel'>"+categoryClassMapping[i]+"</div>"
                   +         "<div id='sucherg_"+categoryClassMapping[i]+"' class='sucherg'></div>";
    }
    suchergHtmlStr +=    "</div>";
    
    var suchergJQueryObj = $(suchergHtmlStr);

    textInput.after(suchergJQueryObj);
        
    textInput.keydown(function(event) {
        if(event.keyCode == 40 || // Pfeil runter
           event.keyCode == 38 || // Pfeil hoch
           event.keyCode == 13 || // ENTER
           event.keyCode == 27)   // ESC
        {
            // Sende bei diesen Eingaben keinen Ajax Call
            // sondern navigiere in den Suchergebnissen
            handlePfeiltastenEvents(event.keyCode, suchergJQueryObj, textInput);
            event.preventDefault();
            return;
        }
        if(textInput.val() == "")
        {
            for(var i in categoryClassMapping)
            {
                $("#sucherg_"+categoryClassMapping[i]).slideUp("fast").empty();
            }
        }
        if($("#suche_ergebnisse_"+textInput.attr("id")).is(":hidden"))
        {
            $("#suche_ergebnisse_"+textInput.attr("id")).show();
        }
        suchTimer.reset();
        suchTimer.set(textInput, action, suchergJQueryObj,  categories, categoryClassMapping);
    });
           
    // Reagiere auf Klick auf das x zum Schliessen des Suchergebniscontainers
    suchergJQueryObj.find(".sucherg_x span").click(function() {
        textInput.val("");
        textInput.focus();
    });
}

var autoCompleteTimerMillis = 400;

/**
 * Der Suchtimer kontrolliert, in welchen Zeitabstaenden Ajax Calls an den Server gesendet werden,
 * waehrend der Benutzer in das Textfeld tippt.
 */
var suchTimer = function(){
    var that = this,
    time = 15,
    timer;
    that.set = function(textInput, action, suchergContainer, categories, categoryClassMapping) {
        timer = setTimeout(function() {
            for(var i in categoryClassMapping)
            {
            	suchergContainer.find("#sucherg_"+categoryClassMapping[i]).slideUp("fast").empty();
            }
            var params = {};
            params[paramSuchmuster] = textInput.val();
            ajaxCall (
                suchfeldServlet,
                action,
                function(response) {
                    var arrSuchErgebnisse = response[keyJsonArrResult];
                    fillSuchergebnisse(arrSuchErgebnisse,suchergContainer, categories, categoryClassMapping, textInput);
                    suchErgIterator = 0;
                    for(var i in categoryClassMapping)
                    {
                    	suchergContainer.find("#sucherg_"+categoryClassMapping[i]).slideDown("fast");
                    }
                },
                params
            );
        },autoCompleteTimerMillis);
    }
    that.reset = function(){
        clearTimeout(timer);
    }
    return that;
}();

/**
 * Diese Funktion wird von autoComplete verwendet. Sie baut HTML Strings zusammen und fuegt sie in die Suchergebnisliste ein.
 * <strong>WICHTIG:</strong> Hier muss entschieden werden, welche Teile des empfangenen JSON Objekts angezeigt werden
 * (z.B. bei Benutzern: Vorname Nachname, bei Veranstaltungen: Titel). Standardmaessig werden IDs angezeigt.
 * @param arrSuchErgebnisse
 * @param categories siehe autoComplete
 * @param categoryClassMapping siehe autoComplete
 */
function fillSuchergebnisse(arrSuchErgebnisse, suchergContainer, categories, categoryClassMapping, textInput)
{
    var isCategoryEmpty = {};
    for(var i in categoryClassMapping)
    {
        isCategoryEmpty[categoryClassMapping[i]] = true;
    }
    // Callback Function to call inside the for loop
    // @see http://stackoverflow.com/questions/4091765/assign-click-handlers-in-for-loop
    function clickHandler(categories, category, jsonSuchErgebnis)
    {
        return function() {
            // Fuehre die Funktion aus, die Suchergebnissen in dieser Kategorie zugeordnet wurde
            categories[category](jsonSuchErgebnis);
            suchergContainer.find(".sucherg_x span").trigger("click");
        }
    }
    for(var i in arrSuchErgebnisse)
    {
        var jsonSuchErgebnis = arrSuchErgebnisse[i]["key"];
        var abstand = arrSuchErgebnisse[i]["value"];
        var klasse = jsonSuchErgebnis[keyJsonObjKlasse];
        var category = categoryClassMapping[klasse];
        var id = jsonSuchErgebnis[paramId];
        var suchErgHtmlString = "<div data-abstand = '"+ abstand +"'id='sucherg_"+category+"_"+id+"' class='sucherg_item sucherg_item_"+category+"'>";
        if(klasse == keyJsonObjKlasseBenutzer)
        {
            suchErgHtmlString += "<span class='octicon octicon-person'></span>" + 
                                 "<span class='sucherg_item_benutzer_vorname'>" + jsonSuchErgebnis[paramVorname] + "</span>" + 
                                 " " + 
                                 "<span class='sucherg_item_benutzer_nachname'>" + jsonSuchErgebnis[paramNachname] + "</span>";
        }
        else if(klasse == keyJsonObjKlasseVeranst)
        {
            suchErgHtmlString += "<span class='octicon octicon-podium'></span>" +
                                 "<span class='sucherg_item_vn_titel'>" + jsonSuchErgebnis[paramTitel] + "</span>" + 
                                 "<br>" +
                                 "<span class='sucherg_item_detail'>" +
                                 jsonSuchErgebnis[paramErsteller][paramVorname] + " " + jsonSuchErgebnis[paramErsteller][paramNachname] +
                                 " | " + jsonSuchErgebnis[paramSemester] +
                                 "</span>";
        }
        else if(klasse == keyJsonObjKlasseStudiengang)
        {
            suchErgHtmlString += "<span class='octicon octicon-mortar-board'></span>" +
                                 jsonSuchErgebnis[paramStudiengang];
        }
        // TODO Falls noch andere Objekte als Suchergebnisse kommen koennen, muessen diese hier nachgetragen werden.
        else
        {
            suchErgHtmlString += jsonSuchErgebnis[paramId];
        }
        suchErgHtmlString +=    "</div>";
        var suchErgJQueryObj = $(suchErgHtmlString);
        
        // Hebe exakte Treffer fett hervor
        switch(textInput.val())
        {
            case jsonSuchErgebnis[paramVorname]:
                suchErgJQueryObj.find(".sucherg_item_benutzer_vorname").css("font-weight","bold");
                break;
            case jsonSuchErgebnis[paramNachname]:
                suchErgJQueryObj.find(".sucherg_item_benutzer_nachname").css("font-weight","bold");
                break;
            case jsonSuchErgebnis[paramVorname]+" "+jsonSuchErgebnis[paramNachname]:
                suchErgJQueryObj.find(".sucherg_item_benutzer_nachname, .sucherg_item_benutzer_vorname").css("font-weight","bold");
                break;
            case jsonSuchErgebnis[paramTitel]:
                suchErgJQueryObj.find(".sucherg_item_vn_titel").css("font-weight","bold");
        }
        
        suchergContainer.find("#sucherg_"+category).append(suchErgJQueryObj);
        isCategoryEmpty[category] = false;

        suchErgJQueryObj.click( clickHandler(categories, category, jsonSuchErgebnis) );
    }

    // Sortiere die Suchergebnisse nach Editierdistanz
    $.each(suchergContainer.find(".sucherg"), function(index, obj) {
    	var elem = $(obj).find('div').sort( function(a,b)
	    	{
    			return $(a).attr("data-abstand") - $(b).attr("data-abstand");
    		});
    	$(obj).append(elem);
	});
    
    // Markiere initial das erste Suchergebnis
    $(suchergContainer.find(".sucherg_item")[0]).addClass("selected");
    
    // Markieren der Suchergebnisse mit Hover
    // suchergContainer.find(".sucherg_item").off(); // damit entfernt man auch die click handler
    suchergContainer.find(".sucherg_item").each(function(i, elem) {
        $(elem).hover(function(e) {
            suchergContainer.find(".sucherg_item").removeClass("selected");
            $(e.target).addClass("selected");
            suchErgIterator = i;
        }, function(e) {
            $(e.target).removeClass("selected");
            suchErgIterator = -1;
        });
    });
    
//    var elem = contentDiv.find('div').sort( function(a,b)
//    		{
//    			return $(a).attr("data-abstand") - $(b).attr("data-abstand");
//    		});
//
//    contentDiv.append(elem);
    
    for(var i in isCategoryEmpty)
    {
        if(isCategoryEmpty[i])
        {
            suchergContainer.find("#sucherg_"+i).append("<div class='sucherg_leer'>Nichts gefunden.</div>");
        }
    }
}

var suchErgIterator = 0;
/**
 * Diese Funktion wird von autoComplete genutzt.
 * Sie ermoeglicht es mit der Tastatur in den Suchergebnissen zu navigieren.
 * @param pressedKey Keycode
 * @param suchergJQueryObj Container mit Suchergebnissen
 */
function handlePfeiltastenEvents(pressedKey, suchergJQueryObj, textInput) {
    var arr = suchergJQueryObj.find(".sucherg_item");
    if(pressedKey == 40) // Pfeil runter
    {
        $(arr[suchErgIterator]).removeClass("selected");
        if(suchErgIterator+1 < arr.length)
            suchErgIterator++;
        else
            suchErgIterator = 0
    }
    else if(pressedKey == 38) // Pfeil hoch
    {
        $(arr[suchErgIterator]).removeClass("selected");
        if(suchErgIterator-1 >= 0)
            suchErgIterator--;
        else
            suchErgIterator = arr.length-1;
    }
    else if(pressedKey == 13) // ENTER
    {
        if(!textInput.val()) {}
        else
            $(arr[suchErgIterator]).trigger("click");
    }
    else if(pressedKey == 27) // ESC
    {
        suchergJQueryObj.find(".sucherg_x span").trigger("click");
    }
    $(arr[suchErgIterator]).addClass("selected");
}

function destroyCKeditors(container)
{
	for (var i in CKEDITOR.instances) {
	    var instance = CKEDITOR.instances[i];
	    if (instance.container != undefined && jQuery.contains( container, instance.container.$ ) )
	    {
	    	 $timeout(function() 
	    	 {
	 	    	instance.editor.removeAllListeners();
		    	instance.destroy();
	    	   },0);
	    	 
	    }
	}
}

function concatStrArr(strArr, seperator)
{
	str = "";
	for(i in strArr)
	{
		str += strArr[i];
		if(i < strArr.length-1)
			str += seperator;
	}
	return str;
}

function checkIfAllowedVn(veranstObj, checkErsteller, checkAdmin, checkModerator){
	if(checkErsteller == undefined)
		checkErsteller = true;
	if(checkAdmin == undefined)
		checkAdmin = true;
	if(checkModerator == undefined)
		checkModerator = true;
	
	if(checkAdmin)
	{
		if(jsonBenutzer[paramNutzerstatus] == "ADMIN")
			return true;
	}
	
	if(checkErsteller)
	{
		if(veranstObj[paramErsteller][paramId] == jsonBenutzer[paramId])
			return true;
	}
	
	if(checkModerator)
	{
		if(veranstObj[paramModeratoren] == undefined || 
				$.inArray(jsonBenutzer[paramId],veranstObj[paramModeratoren]) == -1)
				return false;
		else
			return true;
	}
	return false;
}

function myRound(zahl,n){
    var faktor;
    faktor = Math.pow(10,n);
    return(Math.round(zahl * faktor) / faktor);
}
function isEven(n) {
	return n == parseFloat(n)? !(n%2) : void 0;
}
