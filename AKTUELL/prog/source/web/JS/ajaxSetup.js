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
    type: "GET",
	timeout: 3000,
	error: function(jqXHR, textStatus, errorThrown) { 
	    //status === 'timeout' if it took too long.
	    //handle that however you want.
	    showError("Ajax Call returned error. Debug Info: "+jqXHR.status+", "+textStatus+", "+errorThrown);
	}
});


$( document ).ajaxStart(function() {
	  $("#loadingDiv").fadeIn();
	});

$( document ).ajaxStop(function() {
	  $("#loadingDiv").fadeOut();
	});