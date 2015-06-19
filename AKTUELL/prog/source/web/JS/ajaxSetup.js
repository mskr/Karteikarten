/**
 * @author Andreas, Marius
 * 
 */

/**
 * Konfiguriert global alle Ajax Calls (asynchrone HTTP Requests).
 * Kommt vom Server keine Antwort, wird ein Fehler ausgegeben.
 * Setzt einen Timeout.
 */
$.ajaxSetup({
    type: "POST",
	timeout: 10000,
	error: function(jqXHR, textStatus, errorThrown) {
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
			showError("Unbekannter Fehler. AjaxStatus: "+jqXHR.status+", AjaxTextStatus: "+textStatus+", AjaxErrorCode: "+errorThrown + ", responseText: " + jqXHR.responseText);
	},
	complete: function() {
		if (typeof MathJax != 'undefined') {
			// Suche nach Formeln, die noch nicht gerendert wurden
			MathJax.Hub.Queue([ "Typeset", MathJax.Hub ]);
		}
	}
});

// Speichert, ob aktuell auf Beantwortung eines Ping-Requests gewartet wird
var connectionCheckRunning = false;

/**
 * Fuehrt bei Timeouts einen Verbindungstest durch,
 * indem wiederholt 'Ping' an den Server gesendet wird.
 * Sobald eine Antwort zurueck kommt wird automatisch die Seite neu geladen.
 */
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


// Zeige waehrend Ajax Calls eine Ladeanimation auf der GUI an

$( document ).ajaxStart(function() {
	  $("#loadingDiv").show();
});

$( document ).ajaxStop(function() {
	  $("#loadingDiv").hide();
});
