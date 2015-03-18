/**
 * @author mk
 * Dieses Skript ist noetig, da nur eine HTML-Seite verwendet wird.
 * Das Skript sorgt dafuer, dass die richtigen <div> Container
 * (explizit mypersonalbox und mainbox)
 * fuer die jeweilige Situation angezeigt werden.
 */

var alleAnsichten = ["startseite","hauptseite","profilseite","veranstaltungseite"];
// Merke die letzte vom Nutzer besuchte Ansicht.
// Ein noch eingeloggter Benutzer wird dorthin geleitet.
// Initialer Wert: "hauptseite"
// Ein nicht eingeloggter Besucher wird zur Startseite geleitet.
var lastVisited = 1;

$(document).ready(function() {
	getBenutzer();
});

function getBenutzer() {
	$.ajax({
        type: "POST",
        url: "BenutzerServlet",
		data: "action=getBenutzer",
		success: function(response) {
            var jsonObj = $.parseJSON(response);
            var errCode = jsonObj["error"];
            if(errCode == "noerror") {
            	// Ein Benutzer ist eingeloggt
            	for(var i=0; i<alleAnsichten.length; i++) {
            		if(i != lastVisited) {
	                	$("#mypersonalbox_"+alleAnsichten[i]).hide();
	                	$("#mainbox_"+alleAnsichten[i]).hide();
            		}
            	}
            	$("#mypersonalbox_"+alleAnsichten[lastVisited]).show();
            	$("#mainbox_"+alleAnsichten[lastVisited]).show();
            } else {
            	message(0, buildMessage(errCode));
                $("#mypersonalbox_"+alleAnsichten[0]).show();
                $("#mainbox_"+alleAnsichten[0]).show();
                for(var i=1; i<alleAnsichten.length; i++) {
                	$("#mypersonalbox_"+alleAnsichten[i]).hide();
                	$("#mainbox_"+alleAnsichten[i]).hide();
                }
            }
		}
	});
}