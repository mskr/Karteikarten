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
    	url: startseitenServlet,
    	data: "action="+actionGetStudiengaenge,
    	success: function(response) {
            if(verifyResponse(response))
            { 
            	$("#reg_studiengang").empty();
            	var studgArr = response[keyJsonArrResult];
            	for(var i in studgArr) {
            		$("#reg_studiengang").append("<option>"+studgArr[i]+"</option>");
            	}
            }
    	}
    });
}
