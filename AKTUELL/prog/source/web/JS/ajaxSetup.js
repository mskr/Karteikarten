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
	    //status === 'timeout' if it took too long.
	    //handle that however you want.
		
		if(textStatus == "timeout")
		    showError("Verbindung zum Server wurde unterbrochen. Wiederholen Sie den Vorngang, wenn möglich.");
		else if(jqXHR.responseText == "")
		    showError("Bitte prüfen Sie Ihre Internetverbindung! Es konnte keine Verbindung zum Server hergestellt werden.");
		else
			showError("Ajax Call returned error. Debug Info: "+jqXHR.status+", "+textStatus+", "+errorThrown + "resonseText: " + jqXHR.responseText);
	}
});


$( document ).ajaxStart(function() {
	  $("#loadingDiv").fadeIn();
	});

$( document ).ajaxStop(function() {
	  $("#loadingDiv").fadeOut();
	});