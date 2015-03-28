/**
 * @author mk
 */

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
    
    $(".username").click(function() {
        var paramObj = {};
        paramObj[urlParamLocation] = ansichtProfilseite;
        paramObj[urlParamId] = jsonBenutzer[paramId];
        buildUrlQuery(paramObj);
    });
    
    $(".bn_header").click(function() {
		$(".bn_container").slideToggle("slow");
	});
    
    // TODO Benachrichtigungen laden
    
    $.ajax({
        url: benachrichtungsServlet,
        data: "action="+actionLeseBenachrichtungen,
        success: function(response) {
            var jsonObj = response;
            var errCode = jsonObj["error"];
            if(errCode == "noerror") 
            {
            	setTimeout(function() {
            	var bens = jsonObj[keyJsonArrResult];
            	for (var i in bens)
            	{
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
	            		addBenachrichtigung(bens[i][paramBenInhalt], !bens[i][paramBenGelesen], bens[i][paramBenErstelldaum], fkt);
					}, 300);
            	}}, 3000);
            }
            else 
            {
                message(0, buildMessage(errCode));
            }
        }
     });
    
    // Hier ein paar Beispiele
//    addBenachrichtigung("Ihr Profil wurde geändert. Klicken sie, um zum eigenen Profil zu wechseln.", true, function() {
//        var paramObj = {};
//        paramObj[urlParamLocation] = ansichtProfilseite;
//        paramObj[urlParamId] = jsonBenutzer[paramId];
//        buildUrlQuery(paramObj);
//    });
//    
//    addBenachrichtigung("Dies ist eine alte Benachrichtigung ohne Funktion.", false, function(){});
    
}

/**
 * Zeigt Benutzername und Rolle
 * in der mypersonalbox an.
 */
function fillUserContainer()
{
    var vorname = jsonBenutzer["vorname"];
    var nachname = jsonBenutzer["nachname"];
    var nutzerstatus = jsonBenutzer["nutzerstatus"];
    $(".username").html(vorname+" "+nachname);
    $(".rolle").html(" "+nutzerstatus);
    
    // ProfilBild anzeigen
    $(".user_MyProfilBild").attr("src", jsonBenutzer[paramProfilBild]);
}

/**
 * Fügt eine Benachrichtung hinzu.
 * @param text Inhalt der angezeigt werden soll
 * @param isNeu Als neu markieren
 * @param onClickFkt CallBack-Function, die beim click aufgerufen werden soll
 */
var newBnCount = 0;
function addBenachrichtigung(text, isNeu, datumTxt, onClickFkt)
{
	// Jetzt existieren benachrichtigungen
	$("#keine_bn").slideUp("slow");
	
	var contentDiv = $("#bn_container");
	var divBn = $("<div></div>");
	divBn.addClass("bn");
	
	if(isNeu == true)
	{
		divBn.addClass("neu");
		if(newBnCount == 0)
			contentDiv.slideDown("slow");
		newBnCount++;
		$("#bn_anzahl").text("(" + newBnCount + " neu)");
	}
	else
		divBn.addClass("gelesen");
	
	var spanCntnt = $("<span></span>");
	spanCntnt.addClass("bn_text");
	spanCntnt.append(text);
	spanCntnt.append("<span class='bn_zeit' style='float: right;'>" + datumTxt + "</span>");
	
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