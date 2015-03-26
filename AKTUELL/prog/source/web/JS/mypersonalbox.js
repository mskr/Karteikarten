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
        paramObj[urlParamBenutzerProfil] = jsonBenutzer[paramEmail];
        buildUrlQuery(paramObj);
    });
    
    $(".bn_header").click(function() {
		$(".bn_container").slideToggle("slow");
	});
    
    // TODO Benachrichtigungen laden
    // Hier ein paar Beispiele
    addBenachrichtigung("Hier werden Benachrichtungen angezeigt. Diese müssen noch geladen werden! Aber wir gehen jetzt mal zum Profil.", true, function() {
        var paramObj = {};
        paramObj[urlParamLocation] = ansichtProfilseite;
        paramObj[urlParamBenutzerProfil] = jsonBenutzer[paramEmail];
        buildUrlQuery(paramObj);
    });
    
    addBenachrichtigung("Dies ist eine alte Benachrichtigung ohne Funktion.", false, function(){});
    
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
function addBenachrichtigung(text, isNeu, onClickFkt)
{
	// Jetzt existieren benachrichtigungen
	$("#keine_bn").slideUp("slow");
	
	var contentDiv = $("#bn_container");
	var divBn = $("<div></div>");
	divBn.addClass("bn");
	
	if(isNeu == true)
	{
		divBn.addClass("neu");
		newBnCount++;
		$("#bn_anzahl").text("(" + newBnCount+ " neu)");
	}
	else
		divBn.addClass("gelesen");
	
	var spanCntnt = $("<span></span>");
	spanCntnt.addClass("bn_text");
	spanCntnt.append(text);
	
	divBn.append(spanCntnt);
	$(divBn).click(onClickFkt);
	divBn.hide();
	contentDiv.append(divBn);
	
	divBn.slideDown("slow");
	
	// TODO Div reordering (neu nach oben)
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
    if(getUrlParameterByName(urlParamLocation) != ansichtHauptseite)
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