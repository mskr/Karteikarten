/**
 * Wird beim Neuladen der Seite automatisch aktiv:
 * 1. Liest die URL Parameter
 * 2. Sendet action getBenutzer an Server
 * 3. Interpretiert die URL Parameter
 * 4. Zeigt entsprechende Ansicht an
 * @author mk
 */
var initialURL="";
$(document).ready(function() {
	
	initialURL = History.getState().url;
	
	// Initial alles unsichtbar machen
	$(".mainbox").hide();
	$(".mypersonalbox").hide();
	
    // TODO So wird das Benutzerobjekt nur einmal initial zu beginn geladen
    // Was passiert aber, wenn sich das profil geÃ¤ndert hat?
    $.when(getBenutzer()).done(function(a1) {
        var urlQuery = parseUrlQuery(undefined);    
        interpreteUrlQuery(urlQuery);
    });
    
    // Auf zurÃ¼ck und vorwÃ¤rts im browser reagieren
    History.Adapter.bind(window,'statechange',function(){ // Note: We are using statechange instead of popstate
        var urlQuery = parseUrlQuery(undefined);
        interpreteUrlQuery(urlQuery);
    });
});

var onResizeHandler = function(){
	// Elemente fuer kleine Bildschirme
    if (window.matchMedia("(max-width: 56em)").matches)
    {
    	if(window.location.href.indexOf("hauptseite")>=0){
    		$(".r-suche_etwas_label").show();
    		$(".r-kk-inhaltsvz-toggle").hide();
    	}
    	else if(window.location.href.indexOf("veranstaltungsseite")>=0){
    		$(".r-kk-inhaltsvz-toggle").show();
    		$(".r-suche_etwas_label").hide();
    	}
    	else
        {
            $(".r-suche_etwas_label").hide();
            $(".r-kk-inhaltsvz-toggle").hide();
        }
    }
    else
    {
        $(".r-suche_etwas_label").hide();
        $(".r-kk-inhaltsvz-toggle").hide();
    }
}

$( window ).resize(function() {    
	onResizeHandler();
});
//Der aktuell eingeloggte Benutzer als JSON Objekt.
var jsonBenutzer;

/**
 * Packt alle Parameter aus der URL in ein Objekt.
 * @returns 
 */
function parseUrlQuery(qry) {
    var match,
    pl     = /\+/g,  // Regex for replacing addition symbol with a space
    search = /([^&=]+)=?([^&]*)/g,
    decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
    query  = window.location.search.substring(1),
    urlParams = {};
    
    if(qry != undefined)
    	query = qry;
    
    while (match = search.exec(query)) {
        urlParams[decode(match[1])] = decode(match[2]);
    }
    return urlParams;
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
 * Wird aufgerufen wenn die Ansicht gewechselt wird.
 * Das erste Element in paramObj muss immer location sein.
 * Dieses gibt den Index der Ansicht im Array alleAnsichten an.
 * HÃ¤ngt den Query String an die URL an.
 * Danach wird die Seite automatisch neu geladen.
 * @param paramObj enthaelt alle Query Parameter
 */
function buildUrlQuery(paramObj) 
{
    var locationSearchTmp = "?";
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
    
    // TODO
    //location.search = locationSearchTmp; // Dies laedt auch die Seite neu
    var State =  History.getState();
    History.pushState(null,null, locationSearchTmp);
    
    // Beim Seitenwechsel alle Nachrichten verwerfen
    // TODO Ist das sinvoll?
    //clearMessageQueue();

    // WORKAROUND Wenn zeilseite = aktuelle seite, dann wird die Change Funktion nicht getriggert
    if(State.url.indexOf(locationSearchTmp) >= 0)
    {
    	var urlQuery = parseUrlQuery(undefined);
        interpreteUrlQuery(urlQuery);
    }
}

/**
 * Wird aufgerufen wenn die getBenutzer Anfrage vom Server beantwortet wurde.
 * Interpretiert den URL Query String.
 * Aktuell wird nur der location Parameter gelesen und
 * die entsprechende Seite angezeigt.
 * @param paramObj enthaehlt die Parameter als Map
 */
function interpreteUrlQuery(paramObj) 
{	
	
	onResizeHandler();
	
    var ziel = paramObj[urlParamLocation];
	// Benutzer eingeloggt
    if(jsonBenutzer != undefined)
    { 
        var ajax1 = fillMyPersonalBox();
        var ajax2;
        if(ziel == undefined ||                         // Kein location Parameter
           $.inArray(ziel, alleAnsichten) == -1 ||      // Kein bekannter location Parameter
           ziel == ansichtStartseite ||                 // location ist Startseite
           ziel == ansichtHauptseite)                   // location ist Hauptseite
        {
            ziel = ansichtHauptseite;                   // Dann gehe zu Hauptseite
            ajax2 = fillHauptseite();
        }
        else if(ziel == ansichtProfilseite) 
        {
        	ajax2 = fillProfilseite();
        }
        else if(ziel == ansichtVeranstaltungsseite){
        	vid = paramObj[paramId];
        	kkId = paramObj[paramURLKkID];
        	ajax2 = fillVeranstaltungsSeite(vid, kkId);
        }
        
        $.when(ajax1, ajax2).done(function() {
        	display(ziel);
		});
    } 
    // Benutzer nicht eingeloggt
    else 
    {
        $.when(fillStartseite()).done(function() {
        	display(ansichtStartseite);
		});
    }
}

/**
 * Enthaelt den Ajax Call mit der getBenutzer action.
 * @returns when Ajax Call is finished
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

/**
 * Blendet alle nicht benoetigten mainboxen aus und die richtige ein.
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
    	// Alles auÃŸer das neue Ausblenden
        $(".mainbox").not("#mainbox_"+alleAnsichten[ansichtIdx]).hide();
        
        $("#mainbox_"+alleAnsichten[ansichtIdx]).show();
        
        if(ansicht == ansichtVeranstaltungsseite)
        {
        	$("#vn_kk_ueberscht_box").show();
        }
    }
    else 
    {
        console.log("[urlHandler] Ungueltige Ansicht: "+ansicht);
    }
    $(document).scrollTop(0);
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
