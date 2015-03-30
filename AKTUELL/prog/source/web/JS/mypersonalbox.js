/**
 * @author mk
 */

// Statische Handler einmal registrieren
$(document).ready(function() {
	
	$(".username").click(function() {
		var paramObj = {};
		paramObj[urlParamLocation] = ansichtProfilseite;
		paramObj[urlParamId] = jsonBenutzer[paramId];
		buildUrlQuery(paramObj);
	});

	$(".bn_header").click(function() {
		$(".bn_container").slideToggle("slow");
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
    
    $.ajax({
        url: benachrichtungsServlet,
        data: "action="+actionLeseBenachrichtungen,
        success: function(response) {
            var jsonObj = response;
            var errCode = jsonObj["error"];
            if(errCode == "noerror") 
            {
            	var bens = jsonObj[keyJsonArrResult];
            	
            	for (var i in bens)
            	{
            	    // TODO Vllt direkt in anzeigeBenachrichtigung packen
            		var type = bens[i][paramBenType];
            		var fkt = function() {
						
					};
					
					if(type == paramBenTypeKarteikarte)
					{

					}
					else if(type == paramBenTypeKommentar)
					{

					}
					else if(type == paramBenTypeModerator)
					{
						fkt = function() {
					        var paramObj = {};
					        paramObj[urlParamLocation] = ansichtVeranstaltungseite;
					        paramObj[urlParamId] = bens[i][paramBenVeranst][paramId];
					        buildUrlQuery(paramObj);
						};
					}
					else if(type == paramBenTypeProfil)
					{
						fkt = function() {
					        var paramObj = {};
					        paramObj[urlParamLocation] = ansichtProfilseite;
					        paramObj[urlParamId] = jsonBenutzer[paramId];
					        buildUrlQuery(paramObj);
						};
					}
					else if(type == paramBenTypeVeranstaltung)
					{
						fkt = function() {
					        var paramObj = {};
					        paramObj[urlParamLocation] = ansichtVeranstaltungseite;
					        paramObj[urlParamId] = bens[i][paramBenVeranst][paramId];
					        buildUrlQuery(paramObj);
						};
					}
					setTimeout(function() {
	            		addBenachrichtigung(bens[i], fkt);
					}, 100);
            	}
            }
            else 
            {
                message(0, buildMessage(errCode));
            }
        }
     });
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
    
    // ProfilBild anzeigen
    $(".user_MyProfilBild").attr("src", jsonBenutzer[paramProfilBild]);
}

/**
 * Fügt eine Benachrichtung hinzu, wenn sie nicht schon hinzugefügt wurde.
 * @param text Inhalt der angezeigt werden soll
 * @param isNeu Als neu markieren
 * @param onClickFkt CallBack-Function, die beim click aufgerufen werden soll
 */
var newBnCount = 0;
var benCount = 0;
var aktuelleBenArr = [];
function addBenachrichtigung(ben, onClickFkt)
{
	for(var i in aktuelleBenArr)
	{
		// Benachrichtigung schon gepeichert
		if(aktuelleBenArr[i][paramId] == ben[paramId])
			return;
	}
	
	if(benCount == 0)
	{
		// Jetzt existieren Benachrichtigungen
		$("#keine_bn").slideUp("slow");
	}
	// Benachrichtigung speichern
	aktuelleBenArr[benCount++] = ben;
		
	var contentDiv = $("#bn_container");
	var divBn = $("<div></div>");
	divBn.addClass("bn");
	
	if(ben[paramBenGelesen] != true)
	{
		divBn.addClass("neu");
		newBnCount++;
		$("#bn_anzahl").text("(" + newBnCount + " neu)");
		contentDiv.slideDown("slow");
	}
	else
		divBn.addClass("gelesen");
	
	var spanCntnt = $("<span></span>");
	spanCntnt.addClass("bn_text");
	spanCntnt.append(ben[paramBenInhalt]);
	spanCntnt.append("<span class='bn_zeit' style='float: right;'>" + ben[paramBenErstelldaum] + "</span>");
	
	divBn.append(spanCntnt);
	$(divBn).click(onClickFkt);
	divBn.hide();
	contentDiv.append(divBn);
	
	divBn.slideDown("slow");
	
	var elem = contentDiv.find('div').sort(sortDivByClassName);
	contentDiv.append(elem);
}
function sortDivByClassName(a,b)
{
	if($(a).hasClass("neu") && $(b).hasClass("neu"))
		return 1;
	if($(a).hasClass("neu"))
		return -1;
	if($(b).hasClass("neu"))
		return 1;
	else 
		return 0;
}

/**
 * Entfernt alle benachrichtigungen
 */
function clearBenachrichtigungen()
{
	aktuelleBenArr = [];
	newBnCount = 0;
	benCount = 0;
	
	$("#bn_anzahl").text("");
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
            var paramObj = {};
            paramObj[urlParamLocation] = ansichtHauptseite;
            buildUrlQuery(paramObj);
        });
    }
    else
    {
        $(".return").hide();
        $(".return").off();
    }
}