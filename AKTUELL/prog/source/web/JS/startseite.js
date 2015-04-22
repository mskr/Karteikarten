/**
 * @author mk
 */

// Statische Handler einmal registrieren
$(document).ready(function() {
    $("#pwreset").click(function() {
		if($("#login_email").val() == "")
		{
    		showError("Bitte geben Sie Ihre eMail-Adresse an!");
		}
		else
		{
			 $.ajax({
				 url: startseitenServlet,
				 data: "action="+actionResetPasswort + "&" +
				 paramEmail + "=" + $("#login_email").val(),
				 success: function(response) {
					 var errorFkt = function(errorTxt) {
						 if(errorTxt == "loginfailed") 
						 {
							 showError("Diese eMail-Adresse existiert nicht im System.");
							 return true;
						 }
						 return false;
					 }
					 if(verifyResponse(response,errorFkt))
					 { 
						 showInfo("Ihr Passwort wurde erfolgreich zurückgesetzt. " +
						 		"Sie haben eine eMail mit ihrem neuen Passwort erhalten.");
					 }
				 }
			 });
		}
		
	});
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
    return $.ajax({
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
