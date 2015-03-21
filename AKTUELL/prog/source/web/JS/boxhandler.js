/**
 * @author mk
 * Dieses Skript ist noetig, da nur eine HTML-Seite verwendet wird.
 * Das Skript sorgt dafuer, dass die richtigen <div> Container
 * (explizit mypersonalbox und mainbox)
 * fuer die jeweilige Situation angezeigt werden.
 */

$(document).ready(function() {
    handle();
});

// Der aktuell eingeloggte Benutzer als JSON Objekt.
var jsonBenutzer = "null";

/**
 * Fragt den aktuell eingeloggten Benutzer beim Servlet an.
 * Das Servlet schickt ein JSON Objekt zurueck.
 * Ist ein Benutzer eingeloggt, enthaelt das JSON Objekt die Benutzerdaten.
 * Ist kein Benutzer eingeloggt, enthaelt das JSON Objekt einen Fehlercode.
 * Zeigt dann die richtigen <div> Container an und delegiert weitere Aktionen.
 */
function handle() {
    console.log("lastVisited="+lastVisited);
    $.ajax({
        url: "ProfilServlet",
        data: "action="+actionGetBenutzer,
        success: function(response) {
            jsonObj = $.parseJSON(response);
            var errCode = jsonObj["error"];
            if(errCode == "noerror") {
                // Ein Benutzer ist eingeloggt
                jsonBenutzer = jsonObj;
                display(lastVisited);
                fillMyPersonalBox();
            } else {
                // Niemand ist eingeloggt.
                // Entweder hat der Besucher den Browser geoeffnet und die Seite erstmalig geladen
                // oder seine HTTPSession ist abgelaufen.
                // Zeige hierfuer unterschiedliche Fehlermeldungen an,
                // leite aber immer zur Startseite weiter:
               if(errCode == "notloggedin") {
                   message(1, "Bitte loggen Sie sich ein um fortzufahren.");
               } else {
                   message(0, buildMessage(errCode));
               }
               displayStartseite();
            }
        }
    });
}

// Merke die letzte vom Nutzer besuchte Ansicht.
// Ein eingeloggter Benutzer wird dorthin geleitet.
// Initialer Wert: "hauptseite"
// Ein nicht eingeloggter Besucher wird zur Startseite geleitet.
// TODO Das Merken funktioniert so nicht, denn beim Aktualisieren der Seite
// wird auch das Skript neu geladen und lastVisited auf 1 (Hauptseite) gesetzt.
// Das muss spaeter mit URL Rewriting irgendwie besser werden.
var alleAnsichten = ["startseite","hauptseite","profilseite","veranstaltungseite"];
var lastVisited = 1;

/**
 * Zeigt die Startseite an.
 */
function displayStartseite() {
    for(var i=1; i<alleAnsichten.length; i++) {
    	$("#mypersonalbox_"+alleAnsichten[i]).hide();
    	$("#mainbox_"+alleAnsichten[i]).hide();
    }
    $("#mypersonalbox_"+alleAnsichten[0]).show();
    $("#mainbox_"+alleAnsichten[0]).show();
    fillStartseite();
}

/**
 * Zeigt die durch den Arrayindex angegebene Seite an.
 */
function display(index) {
	for(var i=0; i<alleAnsichten.length; i++) {
		if(i != index) {
        	$("#mypersonalbox_"+alleAnsichten[i]).hide();
        	$("#mainbox_"+alleAnsichten[i]).hide();
		}
	}
	$("#mypersonalbox_"+alleAnsichten[index]).show();
	$("#mainbox_"+alleAnsichten[index]).show();
}
