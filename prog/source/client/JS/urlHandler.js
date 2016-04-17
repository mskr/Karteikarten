/*
 * Wird beim Neuladen der Seite automatisch aktiv:
 * 1. Liest die URL Parameter
 * 2. Sendet action getBenutzer an Server
 * 3. Interpretiert die URL Parameter
 * 4. Zeigt entsprechende Ansicht an
 * @author Marius, Andreas
 */

/**
 * Speichere global die URL mit der die Seite geladen wurde
 */
var initialURL="";

/**
 * Speichere global den aktuell eingeloggten Benutzer als JSON Objekt
 */
var jsonBenutzer;

$(document).ready(function() {
	
	initialURL = History.getState().url;
	
	// Initial alles unsichtbar machen
	$(".mainbox").hide();
	$(".mypersonalbox").hide();
	
	// Pruefe, ob ein Benutzer eingeloggt ist, und wenn ja welcher
    $.when(getBenutzer()).done(function(a1) {
    	var firstRunFinished = false;
    	// zu Beginn warten bis ALLE Ajax-Calls fertig sind
    	$( document ).ajaxStop(function() {
    		if(!firstRunFinished)
    		{
    		    // Wurde die Seite mit URL Parametern aufgerufen, entscheide was angezeigt werden soll
				var urlQuery = parseUrlQuery();
				interpreteUrlQuery(urlQuery);
				firstRunFinished = true;
    		}
    	});
    });
    
    // Auf zurueck und vorwaerts im Browser reagieren
    History.Adapter.bind(window, "statechange", function() {
        var urlQuery = parseUrlQuery();
        interpreteUrlQuery(urlQuery);
    });
});

/**
 * Packt alle Parameter aus der URL in ein Objekt.
 * @param qry URL Query String
 * @returns Assoziatives Objekt mit den URL Parametern
 */
function parseUrlQuery(qry) {
    var match,
    pl     = /\+/g,
    search = /([^&=]+)=?([^&]*)/g,
    decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
    query  = window.location.search.substring(1),
    urlParams = {};
    if(qry != undefined)
    	query = qry;
    while (match = search.exec(query))
    {
        urlParams[decode(match[1])] = decode(match[2]);
    }
    return urlParams;
}

/**
 * Haengt einen Parameter String an die URL an ohne die Seite neu zu laden.
 * Das erste Element in paramObj muss immer location sein
 * und einen Wert aus dem Array alleAnsichten enthalten.
 * Wird aufgerufen wenn die Ansicht gewechselt wird.
 * @param paramObj enthaelt alle Query Parameter
 */
function buildUrlQuery(paramObj) 
{
    var locationSearchTmp = "?";
    // Zaehler dienen dazu kein '&' nach dem letzten Paremeter zu schreiben
    var i = 0;
    var maxI = 0;
    for(var param in paramObj) 
    {
    	maxI++;
    }
    for(var param in paramObj) 
    {
        // Schreibe alle Parameter aus dem gegebenen Objekt in einen String
        locationSearchTmp += param + "=" + paramObj[param]
        if(i < maxI-1)
        	locationSearchTmp += "&";
        i++;
    }
    // Neuen Eintrag in den Browserverlauf pushen
    History.pushState(null, null, locationSearchTmp);
    // Pruefe, ob der String nun auch wirklich in der URL steht
//    var State = History.getState();
//    if(State.url.indexOf(locationSearchTmp) >= 0)
//    {
//        // Wenn ja dann starte die Anzeige der entprechenden Ansicht
//    	var urlQuery = parseUrlQuery(undefined);
//        interpreteUrlQuery(urlQuery);
//    }
}

/**
 * Interpretiert die aktuell in der URL stehenden Parameter.
 * Gibt der location-Parameter eine gueltige Ansicht an, wird diese gezeigt.
 * Dies entpricht dem Laden einer neuen HTML Seite bei klassischen Webseiten.
 * Wird aufgerufen wenn die getBenutzer Anfrage vom Server beantwortet wurde.
 * @param paramObj enthaehlt die Parameter als Key-Value Paare
 */
function interpreteUrlQuery(paramObj)
{	
    // location lesen
    var ziel = paramObj[urlParamLocation];
	// Pruefe ob ein Benutzer eingeloggt ist
    if(jsonBenutzer != undefined) // Ein Benutzer ist eingeloggt
    {
        // Lade zuerst die mypersonalbox wie in mypersonalbox.js definiert
        var ajax1 = fillMyPersonalBox()
        var ajax2;
        // Warten bis mypersonalbox fertig geladen
        $.when(ajax1).done(function(){
            // Pruefe ob der Benutzer immernoch eingeloggt ist
        	if(jsonBenutzer == undefined) // Der Benutzer ist nicht mehr eingeloggt
    		{
        	    // Lade die Startseite und gehe dort hin, um erneuten Login zu ermoeglichen
                $.when(fillStartseite()).done(function() {
                	display(ansichtStartseite);
        		});
                return;
    		}
        	else if(ziel == undefined ||                    // Wenn kein location Parameter oder
	           $.inArray(ziel, alleAnsichten) == -1 ||      // ein bekannter location Parameter oder
	           ziel == ansichtStartseite ||                 // location ist Startseite oder
	           ziel == ansichtHauptseite)                   // location ist Hauptseite
	        {
	            ziel = ansichtHauptseite;                   // Dann gehe zu Hauptseite
	            ajax2 = fillHauptseite();
	        }
	        else if(ziel == ansichtProfilseite) 
	        {
	            // Stosse das Laden der Profilseite an
	        	ajax2 = fillProfilseite();
	        }
	        else if(ziel == ansichtVeranstaltungsseite) {
	            // Bei der Veranstaltungsseite muss zum Laden die vnID gegeben sein
	            // und es kann eine kkID gegeben sein, falls zu einer bestimmen KK gesprungen werden soll
	        	var vid = paramObj[paramId];
	        	var kkId = paramObj[paramURLKkID];
	        	ajax2 = fillVeranstaltungsSeite(vid, kkId);
	        	// Wenn Veranstaltungsseite komplett geladen, aktiviere einfach alle Waypoints
	        	// (es werden momentan sowieso alle Waypoints dort erstellt)
	        	ajax2.done(function(){
	        		Waypoint.enableAll();
	        	});
	        }
	        $.when(ajax2).done(function() {
	        	display(ziel);
			});
        });
        // Individuell eingestelltes Theme setzen
        if(jsonBenutzer[paramTheme] == "DAY")
        {
            changeCSS("CSS/sopra_light.css", 0);
            changeCSS("CSS/mybuttonstyle_light.css", 1);
        }
        else
        {
            changeCSS("CSS/sopra.css", 0);
            changeCSS("CSS/mybuttonstyle.css", 1);
        }
    } 
    // Benutzer nicht eingeloggt
    else 
    {
        $.when(fillStartseite()).done(function() {
        	display(ansichtStartseite);
		});
    }
    // Alle Waypoints loeschen, da diese in der Veranstaltungsseite neu erstellt werden
    Waypoint.destroyAll();
    // Check ob in der Mobile-Ansicht andere Buttons angezeigt werden muessen
    onResizeHandler();
}

/**
 * Blendet alle nicht benoetigten mainboxen aus und die richtige ein.
 * @param ansicht als String gemaess den defines, die angezeigt werden soll
 */
function display(ansicht) 
{
    console.log("[GOTO] "+ansicht);
    
    // mypersonalbox
    if(ansicht == ansichtStartseite)
    {
        $("#mypersonalbox_main").hide();
        $("#mypersonalbox_startseite").show();
    }
    else
    {
        $("#mypersonalbox_startseite").hide();
        $("#mypersonalbox_main").show();
        
    }
    
    // mainbox
    var ansichtIdx = alleAnsichten.indexOf(ansicht);
    
    if(ansichtIdx != -1)
    {
    	// Alles ausser gegebene ansicht ausblenden
        $(".mainbox").not("#mainbox_"+alleAnsichten[ansichtIdx]).hide();
        
        return $("#mainbox_"+alleAnsichten[ansichtIdx]).show();
    }
    else 
    {
        console.log("[urlHandler] Ungueltige Ansicht: "+ansicht);
    }
    
    return $.Deferred().resolve();
}

/**
 * Diese Funktion setzt die URL und wechselt zum angegebenen Profil
 * @param benutzerID
 */
function gotoProfil(benutzerID)
{
	var paramObj = {};
	paramObj[urlParamLocation] = ansichtProfilseite;
	paramObj[urlParamId] = benutzerID;
	buildUrlQuery(paramObj);
}

/**
 * Diese Funktion setzt die URL und wechselt zur angegebenen Veranstaltung
 * @param veranstId
 */
function gotoVeranstaltung(veranstId, kkId)
{
	var paramObj = {};
	paramObj[urlParamLocation] = ansichtVeranstaltungsseite;
	paramObj[urlParamId] = veranstId;
	if(kkId != undefined)
		paramObj[paramURLKkID] = kkId;
	buildUrlQuery(paramObj);
}

/**
 * Diese Funktion setzt die URL und wechselt zur Hautseite
 */
function gotoHauptseite()
{
	var paramObj = {};
	paramObj[urlParamLocation] = ansichtHauptseite;
	buildUrlQuery(paramObj);
}

/**
 * Diese Funktion wechselt zur Startseite
 */
function gotoStartseite()
{
	var paramObj = {};
	paramObj[urlParamLocation] = ansichtStartseite;
	buildUrlQuery(paramObj);
}

/**
 * Liefert den Wert zu einem gegebenen Parameternamen.
 * @see http://stackoverflow.com/questions/901115
 * @param name ist der Parametername
 * @returns Wert des Parameters
 * oder leerer String bei nicht vorhandenem Parameter
 */
function getUrlParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

/**
 * Holt den aktuell eingloggten Benutzer vom Server und speichert ihn in jsonBenutzer
 * @returns Ajax Objekt
 */
function getBenutzer()
{
    return ajaxCall(
        profilServlet,
        actionGetBenutzer,
        function(response) {
            jsonBenutzer = response;
        },
        undefined,
        function(errCode) {
            jsonBenutzer = undefined;
        }
    );
}

$( window ).resize(function() {    
    onResizeHandler();
});

/**
 * Wird bei Groessenaenderung des Fensters sowie bei Seitenwechseln aufgerufen.
 * Blendet kontextsensitive Buttons in der Ansicht fuer kleine Bildschirme ein bzw. aus.
 */
function onResizeHandler() 
{
    // Elemente fuer kleine Bildschirme
    if (window.matchMedia("(max-width: 56em)").matches)
    {
        if(window.location.href.indexOf("hauptseite")>=0){
            $(".r-suche_etwas_label").show();
            $(".r-kk-inhaltsvz-toggle").hide();
            $(".r-kk-inhaltsvz-toggle-screensize-m").hide();
        }
        else if(window.location.href.indexOf("veranstaltungsseite")>=0){
            $(".r-kk-inhaltsvz-toggle-screensize-m").removeAttr("style");
            $(".r-kk-inhaltsvz-toggle").removeAttr("style");
            $(".r-suche_etwas_label").hide();
        }
        else
        {
            $(".r-suche_etwas_label").hide();
            $(".r-kk-inhaltsvz-toggle").hide();
            $(".r-kk-inhaltsvz-toggle-screensize-m").hide();
        }
    }
    else if (window.matchMedia("(max-width: 80em)").matches)
    {
        if(window.location.href.indexOf("hauptseite")>=0){
            $(".r-suche_etwas_label").hide();
            $(".r-kk-inhaltsvz-toggle").hide();
            $(".r-kk-inhaltsvz-toggle-screensize-m").hide();
        }
        else if(window.location.href.indexOf("veranstaltungsseite")>=0){
            $(".r-kk-inhaltsvz-toggle-screensize-m").removeAttr("style");
            $(".r-kk-inhaltsvz-toggle").hide();
            $(".r-suche_etwas_label").hide();
        }
        else
        {
            $(".r-suche_etwas_label").hide();
            $(".r-kk-inhaltsvz-toggle").hide();
            $(".r-kk-inhaltsvz-toggle-screensize-m").hide();
        }
    }
    else
    {
        $(".r-suche_etwas_label").hide();
        $(".r-kk-inhaltsvz-toggle").hide();
        $(".r-kk-inhaltsvz-toggle-screensize-m").hide();
    }
}