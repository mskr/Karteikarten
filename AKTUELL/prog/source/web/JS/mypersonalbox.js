/**
 * @author mk
 */

// Statische Handler einmal registrieren
$(document).ready(function() {
	
	$(".username").click(function() {
		gotoProfil(jsonBenutzer[paramId]);
	});

	$(".bn_header").click(function() {
		$(".bn_container").slideToggle("slow");
	});
	
	$(".r-usercontainer-toggle").click(function(e) {
	    if($("#r-usercontainer-toggle-radio").prop("checked"))
	    {
	        $("#r-usercontainer-toggle-radio").prop("checked",false);
            e.preventDefault();
            e.stopPropagation();
	    }
	});
	
	$(".r-bn-toggle").click(function(e) {
	    if($("#r-bn-toggle-radio").prop("checked"))
	    {
            $("#r-bn-toggle-radio").prop("checked", false);
            e.preventDefault();
            e.stopPropagation();
	    }
	});

});


/**
 * Fuellt die mypersonalbox mit den benoetigten Informationen.
 */
function fillMyPersonalBox()
{
    if(jsonBenutzer == undefined)
    {
        return;
    }
    fillUserContainer();
    handleReturnLink();
    ajaxCall(
        benachrichtungsServlet,
        actionLeseBenachrichtungen,
        function(response) {
            var bens = response[keyJsonArrResult];
            updateBenachrichtigungen(bens);
        }
    )
}

/**
 * Zeigt Benutzername und Rolle
 * in der mypersonalbox an.
 */
function fillUserContainer()
{
    var vorname = jsonBenutzer[paramVorname];
    var nachname = jsonBenutzer[paramNachname];
    var nutzerstatus = jsonBenutzer[paramNutzerstatus];
    $(".username").html(vorname+" "+nachname);
    $(".rolle").html(" "+nutzerstatus);
    $(".user_MyProfilBild").attr("src", jsonBenutzer[paramProfilBild]);
}

/**
 * Fügt eine neue Benachrichtung hinzu.
 */
function addBenachrichtigung(ben)
{
	var type = ben[paramType];
	var onClickfkt = function() {};
	if(type == paramBenTypeKarteikarte)
	{
		// TODO
	}
	else if(type == paramBenTypeKommentar)
	{
		onClickfkt = function() {
			gotoVeranstaltung(ben[paramVnId], ben[paramKkId]);
		};
	}
	else if(type == paramBenTypeModerator)
	{
		onClickfkt = function() {
			gotoVeranstaltung(ben[paramBenVeranst][paramId]);
		};
	}
	else if(type == paramBenTypeProfil)
	{
		onClickfkt = function() {
			gotoProfil(jsonBenutzer[paramId]);
		};
	}
	else if(type == paramBenTypeVeranstaltung)
	{
		onClickfkt = function() {
			gotoVeranstaltung(ben[paramBenVeranst][paramId]);
		};
	}

	var contentDiv = $("#bn_container");
	var divBn = $("<div></div>");
	divBn.addClass("bn");
	divBn.attr("data-id",ben[paramId]);
	
	if(ben[paramGelesen] != true)
	{
		divBn.addClass("neu");
	}
	else
		divBn.addClass("gelesen");
	
	var spanCntnt = $("<span></span>");
	spanCntnt.addClass("bn_text");
	spanCntnt.append(ben[paramInhalt]);
	spanCntnt.append("<span class='bn_zeit' style='float: right;'>" + ben[paramBenErstelldaum] + "</span>");
	
	divBn.append(spanCntnt);

	$(divBn).click(function() {
		// Markiert benachrichtigung als gelesen
	    var params = {};
	    params[paramId] = ben[paramId]
		$.when(ajaxCall(
		    benachrichtungsServlet,
		    actionMarkiereBenGelesen,
		    undefined,
		    params
		)).done(onClickfkt);
	});

	divBn.hide();
	contentDiv.append(divBn);
	
	divBn.slideDown("slow");
}
function sortDivByClassName(a,b)
{
	if($(a).hasClass("neu") && $(b).hasClass("neu"))
	{
		return $(b).attr("data-id") - $(a).attr("data-id");
	}
	if($(a).hasClass("neu"))
		return -1;
	if($(b).hasClass("neu"))
		return 1;
	else 
		return 0;
}

var aktuelleBenArr = [];
function updateBenachrichtigungen(newBens)
{
	var contentDiv = $("#bn_container");
	var ungelesenCount = 0;
	
	
	var initalArrSize = aktuelleBenArr.length;
	
	for(var i in newBens)
	{
		if(newBens[i][paramGelesen] != true)
			ungelesenCount++;
		
		var isNew = true;
		for(var j in aktuelleBenArr)
		{
			// Wenn Benachrichtigung schon vorhanden, dann Objekt überschreiben,
			// aber keinen neuen Container erzeugen
			if(aktuelleBenArr[j][paramId] == newBens[i][paramId])
			{
				aktuelleBenArr[j][paramId] = newBens[i][paramId];
				isNew = false;
				break;
			}
		}
		
		if(isNew)
		{
			// Benachrichtigung speichern
			aktuelleBenArr.push(newBens[i]);
			
			// Benachrichtigung anzeigen
			addBenachrichtigung(newBens[i]);
			
			if(initalArrSize == 0)
			{
				// Jetzt existieren Benachrichtigungen
				$("#keine_bn").slideUp("slow");
			}			
		}
		// Sollte nicht auftreten. WEnn eine benachrichtigung von gelesen zu neu wird z.b.
		else
		{
			var benDiv = contentDiv.find("[data-id="+newBens[i][paramId]+"]");
			if(newBens[i][paramGelesen] == true)
			{
				benDiv.addClass("gelesen");
				benDiv.removeClass("neu");
			}
			else
			{
				benDiv.addClass("neu");
				benDiv.removeClass("gelesen");
			}
		}
		
		newBens[i][paramGelesen]
	}
	
	// Prüfen, welche Benachrichtigngen verschwunden sind.
	// Diese auf gelesen setzen
	for(var j in aktuelleBenArr)
	{
		var verschwunden = true;
		for(var i in newBens)
		{
			if(aktuelleBenArr[j][paramId] == newBens[i][paramId])
			{
				verschwunden = false;
				break;
			}
		}
		
		if(verschwunden)
		{
			var benDiv = contentDiv.find("[data-id="+aktuelleBenArr[j][paramId]+"]");
			benDiv.addClass("gelesen");
			benDiv.removeClass("neu");
		}
	}
	
	if(ungelesenCount > 0)
	{
		$(".bn_anzahl").html("<span>(" + ungelesenCount + " neu)</span>");
		contentDiv.slideDown("slow");
	}
	else
	{
		$(".bn_anzahl").empty();
	}

	// Neu Sortieren
	var elem = contentDiv.find('div').sort(sortDivByClassName);
	contentDiv.append(elem);
}


/**
 * Entfernt alle benachrichtigungen
 */
function clearBenachrichtigungen()
{
	aktuelleBenArr = [];
	newBnCount = 0;
	benCount = 0;
	
	$(".bn_anzahl").empty();
	var contentDiv = $("#bn_container");
	
	// Alle Benachrichtiugngen einklappen und "keine"-span anzeigen
	contentDiv.children().slideToggle();
	
	// Alle benachrichtigungen entfernen
	contentDiv.children().not('#keine_bn').remove();
}

/**
 * Blendet den Pfeil, ein der bei Klick zurueck auf die Hauptseite fuehrt,
 * falls der Benutzer sich nicht schon auf der Hauptseite befindet.
 */
function handleReturnLink() {
    if(getUrlParameterByName(urlParamLocation) != ansichtHauptseite && getUrlParameterByName(urlParamLocation) != "" )
    {
        $(".return").show();
        $(".return").click(function() {
        	gotoHauptseite();
        });
    }
    else
    {
        $(".return").hide();
        $(".return").off();
    }
}

/**
 * Ermoeglicht das Ausklappen der mypersonalbox im Smartphonemodus
 */
function naviconTransformicon() {
    $(".lines-button").toggleClass("close");
}