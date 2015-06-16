/**
 * @author mk
 */

/**
 * Konfiguriert alle Ajax Calls.
 * Kommt vom Server keine Antwort,
 * wird ein Fehler ausgegeben.
 * Setzt einen Timeout.
 */

$.ajaxSetup({
    type: "POST",
	timeout: 10000,
	error: function(jqXHR, textStatus, errorThrown) { 

//		console.log("AJAX-ERROR: " + textStatus + " " + errorThrown);
		
		if(textStatus == "timeout")
		{
		    showError("Verbindung zum Server wurde unterbrochen. Wiederholen Sie den Vorngang, wenn möglich.");
		    if(!connectionCheckRunning)
		    	checkConnection();
		}
		else if(jqXHR.responseText == "")
		{
		    showError("Es konnte keine Verbindung zum Server hergestellt werden. Versuche Verbindung wieder herzustellen...");

		    if(!connectionCheckRunning)
		    	checkConnection();
		}
		else if (textStatus == "abort")
		{
			// ignorieren
		}
		else
			showError("Unbekannter Fehler. AjaxStatus: "+jqXHR.status+", AjaxTextStatus: "+textStatus+", AjaxErrorCode: "+errorThrown + ", resonseText: " + jqXHR.responseText);
	},
	complete: function() {
		if (typeof MathJax != 'undefined') {
			// Suche nach Formeln, die noch nicht gerendert wurden
			MathJax.Hub.Queue([ "Typeset", MathJax.Hub ]);
		}
	}
});

$(document).ready(function(){
	// Meldung zeigen, wenn Mathjax nicht geladen werden konnte
	if (typeof MathJax == 'undefined')
		showError("MathJax nicht geladen. Formeln können nicht konvertiert werden.");
});

var connectionCheckRunning = false;
function checkConnection() 
{
    connectionCheckRunning = true;
	$.ajax({
        url: startseitenServlet,
        data: "action="+actionPing,
        success: function(jsonResponse) {
		    showInfo("Verbindungsaufbau erfolgreich. Bitte warten Seite wird neu geladen...");
		    connectionCheckRunning = false;
		    setTimeout(function(){
		    	location.reload();
		    }, 3000);
        },
        error:function(){
        	setTimeout(function() { 
    		    showError("Verbindungsaufbau fehlgeschlagen. Versuche Verbindung wieder herzustellen...");
    		    setTimeout(function(){
        			checkConnection();
    		    }, 3000);
        	}, 8000);
        }
    });
}

$( document ).ajaxStart(function() {
	  $("#loadingDiv").show();
});

$( document ).ajaxStop(function() {
	  $("#loadingDiv").hide();
      Waypoint.refreshAll();  // Böser Hack
});
