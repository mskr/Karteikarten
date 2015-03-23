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

/**
 * Ajax Call Ladeanmation
 * @see http://stackoverflow.com/questions/1964839
 */
$(document).ready(function() {
    $('#loading').bind('ajaxStart', function(){
        $(this).show();
    }).bind('ajaxStop', function(){
        $(this).hide();
    });
//    $(document).on({
//        ajaxStart: function() { $body.addClass("loading");    },
//        ajaxStop:  function() { $body.removeClass("loading"); }    
//    });
});