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
	timeout: 3000,
	error: function(jqXHR, textStatus, errorThrown) { 
		if(textStatus == "timeout")
		{
		    showError("Verbindung zum Server wurde unterbrochen. Wiederholen Sie den Vorngang, wenn möglich.");
			checkConnection();
		}
		else if(jqXHR.responseText == "")
		{
		    showError("Bitte prüfen Sie Ihre Internetverbindung! Es konnte keine Verbindung zum Server hergestellt werden. Versuche Verbindung wieder herzustellen...");
			checkConnection();
		}
		else
			showError("Unbekannter Fehler. AjaxStatus: "+jqXHR.status+", AjaxTextStatus: "+textStatus+", AjaxErrorCode: "+errorThrown + ", resonseText: " + jqXHR.responseText);
	}
});

function checkConnection() 
{
	$.ajax({
        url: startseitenServlet,
        data: "action="+actionGetSemester,
        success: function(jsonResponse) {
		    showInfo("Verbindungsaufbau erfolgreich. Bitte warten...");
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
        	}, 5000);
        }
    });
}

$( document ).ajaxStart(function() {
	  $("#loadingDiv").fadeIn();
	});

$( document ).ajaxStop(function() {
	  $("#loadingDiv").fadeOut();
	});