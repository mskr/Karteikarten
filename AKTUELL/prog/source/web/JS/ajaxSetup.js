/**
 * @author mk
 */

/**
 * Konfiguriert alle Ajax Calls.
 * Kommt vom Server keine Antwort,
 * wird ein Fehler ausgegeben.
 */
$.ajaxSetup({
    type: "GET",
	timeout: 1000,
	error: function(xhr, status, err) { 
	    //status === 'timeout' if it took too long.
	    //handle that however you want.
	    alert("Ajax Call returned error");
	}
});
