/**
 * @author mk
 */

$(document).ready(function() {
    var urlQuery = parseUrlQuery();
    // Hier in when einen Ajax Call hinzufuegen, der die (auf dem Server gespeicherte) Ansicht holt,
    // die der Benutzer zuletzt angesehen hat.
    // So kann man nach einem Session Expired schoener reagieren.
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
function parseUrlQuery() {
    var match,
    pl     = /\+/g,  // Regex for replacing addition symbol with a space
    search = /([^&=]+)=?([^&]*)/g,
    decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
    query  = window.location.search.substring(1),
    urlParams = {};
    while (match = search.exec(query)) {
        urlParams[decode(match[1])] = decode(match[2]);
    }
    return urlParams;
}

/**
 * Wird aufgerufen wenn die Ansicht gewechselt wird.
 * Das erste Element in paramObj muss immer location sein.
 * Dieses gibt den Index der Ansicht im Array alleAnsichten an.
 * Hängt den Query String an die URL an.
 * Danach wird die Seite automatisch neu geladen.
 * @param paramObj enthaelt alle Query Parameter
 */
function buildUrlQuery(paramObj) {
    var locationSearchTmp = "?";
    for(var param in paramObj) {
        locationSearchTmp += param + "=" + paramObj[param] + "&";
    }
    location.search = locationSearchTmp; // Dies laedt auch die Seite neu
}

/**
 * Wird aufgerufen wenn die getBenutzer Abfrage vom Server
 * beantwortet wurde.
 * Interpretiert den URL Query String und
 * zeigt die entsprechende Seite an.
 * @param paramObj enthaehlt die Parameter als Map
 */
function interpreteUrlQuery(paramObj) {
    var ziel = paramObj[urlParamLocation];
    if(jsonBenutzer != undefined)
    { // Benutzer eingeloggt
        fillMyPersonalBox();
        if(ziel == undefined || 
           $.inArray(ziel, alleAnsichten) == -1 ||			// Keine Gültige Ansicht?
           ziel == ansichtStartseite ||
           ziel == ansichtHauptseite) 
        {
            ziel = ansichtHauptseite;
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
            jsonObj = $.parseJSON(response);
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
                   message(1, "Bitte loggen Sie sich ein um fortzufahren.");
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
 * Zeigt die durch den angegebene Seite an.
 */
function display(ansicht) 
{
    var isValid = false;
    for(var i=0; i<alleAnsichten.length; i++)
    {
    	// Richtige ansicht suchen und einblenden
        if(alleAnsichten[i] == ansicht) 
        {
            $("#mypersonalbox_"+alleAnsichten[i]).show();
            $("#mainbox_"+alleAnsichten[i]).show();
            isValid = true;
        } else {
            $("#mypersonalbox_"+alleAnsichten[i]).hide();
            $("#mainbox_"+alleAnsichten[i]).hide();
        }
    }
    if(!isValid) 
    {
        console.log("Ungueltige Ansicht: "+ansicht);
    }
}
