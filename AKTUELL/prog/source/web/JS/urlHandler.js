/**
 * Wird beim Neuladen der Seite automatisch aktiv:
 * 1. Liest die URL Parameter
 * 2. Sendet action getBenutzer an Server
 * 3. Interpretiert die URL Parameter
 * 4. Zeigt entsprechende Ansicht an
 * @author mk
 */

$(document).ready(function() {
    var urlQuery = parseUrlQuery(undefined);
    console.log("[urlHandler] location="+urlQuery[urlParamLocation]);
    $.when(getBenutzer()).done(function(a1) {
        interpreteUrlQuery(urlQuery);
    });
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
 * Hängt den Query String an die URL an.
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
    // Test mit History
    History.pushState(null,"SopraProjekt Team23/17", locationSearchTmp);
    interpreteUrlQuery(parseUrlQuery(locationSearchTmp));
}

/**
 * Wird aufgerufen wenn die getBenutzer Anfrage vom Server beantwortet wurde.
 * Interpretiert den URL Query String.
 * Aktuell wird nur der location Parameter gelesen und
 * die entsprechende Seite angezeigt.
 * @param paramObj enthaehlt die Parameter als Map
 */
function interpreteUrlQuery(paramObj) {
    var ziel = paramObj[urlParamLocation];
    if(jsonBenutzer != undefined)
    { // Benutzer eingeloggt
        fillMyPersonalBox();
        if(ziel == undefined ||                         // Kein location Parameter
           $.inArray(ziel, alleAnsichten) == -1 ||      // Kein bekannter location Parameter
           ziel == ansichtStartseite ||                 // location ist Startseite
           ziel == ansichtHauptseite)                   // location ist Hauptseite
        {
            ziel = ansichtHauptseite;                   // Dann gehe zu Hauptseite
            fillHauptseite();
        } else if(ziel == ansichtProfilseite) 
        {
            fillProfilseite();
        }
        display(ziel);
    } 
    else 
    { // Benutzer nicht eingeloggt
        fillStartseite();
        display(ansichtStartseite);
    }
}

/**
 * Enthaelt den Ajax Call mit der getBenutzer action.
 * @returns when Ajax Call is finished
 */
function getBenutzer()
{
    return $.ajax({
        url: profilServlet,
        data: "action="+actionGetBenutzer,
        success: function(response) 
        {
            var jsonObj = response;
            var errCode = jsonObj["error"];
            if(errCode == "noerror") 
            {
                // Ein Benutzer ist eingeloggt
                jsonBenutzer = jsonObj;
            } 
            else 
            {
                // Niemand ist eingeloggt.
                jsonBenutzer = undefined;
                if(errCode == "notloggedin") 
                {
                    // TODO Koennte ein Session Timeout gewesen sein
                } 
                else 
                {
                    message(0, buildMessage(errCode));
                }
            }
        }
    });
}

/**
 * Blendet alle nicht benoetigten mainboxen aus und die richtige ein.
 */
function display(ansicht) 
{
    // mypersonalbox
    if(ansicht == ansichtStartseite)
    {
        $("#mypersonalbox_startseite").show();
        $("mypersonalbox_main").hide();
    }
    else
    {
        $("#mypersonalbox_main").show();
        $("mypersonalbox_startseite").hide();
    }
    // mainbox
    var isValid = false;
    for(var i=0; i<alleAnsichten.length; i++)
    {
        if(alleAnsichten[i] == ansicht) 
        {
            $("#mainbox_"+alleAnsichten[i]).show();
            isValid = true;
        } else {
            $("#mainbox_"+alleAnsichten[i]).hide();
        }
    }
    if(!isValid) 
    {
        console.log("[urlHandler] Ungueltige Ansicht: "+ansicht);
    }
}
