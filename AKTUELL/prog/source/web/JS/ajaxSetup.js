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
	timeout: 6000,
	error: function(jqXHR, textStatus, errorThrown) { 
		if(textStatus == "timeout")
		{
		    showError("Verbindung zum Server wurde unterbrochen. Wiederholen Sie den Vorngang, wenn möglich.");
		    if(!connectionCheckRunning)
		    	checkConnection();
		}
		else if(jqXHR.responseText == "")
		{
		    showError("Bitte prüfen Sie Ihre Internetverbindung! Es konnte keine Verbindung zum Server hergestellt werden. Versuche Verbindung wieder herzustellen...");

		    if(!connectionCheckRunning)
		    	checkConnection();
		}
		else
			showError("Unbekannter Fehler. AjaxStatus: "+jqXHR.status+", AjaxTextStatus: "+textStatus+", AjaxErrorCode: "+errorThrown + ", resonseText: " + jqXHR.responseText);
	}
});

var connectionCheckRunning = false;
function checkConnection() 
{
    connectionCheckRunning = true;
	$.ajax({
        url: startseitenServlet,
        data: "action="+actionPing,
        success: function(jsonResponse) {
		    showInfo("Verbindungsaufbau erfolgreich. Bitte warten...");
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
	  $("#loadingDiv").fadeIn();
	});

$( document ).ajaxStop(function() {
	  $("#loadingDiv").fadeOut();
	});