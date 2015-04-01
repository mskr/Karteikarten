/**
 * @author mk
 */

// Statische Handler einmal registrieren
$(document).ready(function() {
    
});

/**
 * Wird von urlHandler.js aufgerufen,
 * nachdem die <div> Container fuer die Startseite angezeigt wurden.
 * Holt die benoetigten Daten per Ajax Call vom Server
 * und traegt sie in die richtigen HTML Elemente ein.
 * Im Falle der Startseite werden die verfuegbaren Studiengaenge
 * geholt und in das <select> Element eingeordnet.
 */
function fillStartseite() {
    $.ajax({
    	url: benutzerServlet,
    	data: "action="+actionGetStudiengaenge,
    	success: function(response) {
            var jsonObj = response;
            var errCode = jsonObj["error"];
            if(errCode == "noerror") {
                $("#reg_studiengang").empty();
                var studgArr = jsonObj[keyJsonArrResult];
            	for(var i in studgArr) {
            		$("#reg_studiengang").append("<option>"+studgArr[i]+"</option>");
            	}
            } else {
            	message(0, buildMessage(errCode));
            }
    	}
    });
}
