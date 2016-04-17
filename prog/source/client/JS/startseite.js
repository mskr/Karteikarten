/**
 * @author Andreas, Marius
 */

// Statische Handler einmal registrieren
$(document).ready(function() {
    $("#pwreset").click(function() {
		if($("#login_email").val() == "")
		{
    		showError("Bitte geben Sie Ihre E-Mail-Adresse an.");
		}
		else
		{
			 var params = {};
			 params[paramEmail] = $("#login_email").val();
			 ajaxCall(
			     startseitenServlet,
			     actionResetPasswort,
			     function(response) {
			         showInfo("Ihr Passwort wurde erfolgreich zurückgesetzt. " +
                              "Sie haben eine E-Mail mit Ihrem neuen Passwort erhalten.");
			     },
			     params,
			     function(errCode) {
			         if(errCode == "loginfailed") 
                     {
                         showError("Diese E-Mail-Adresse existiert nicht.");
                         return true;
                     }
			         else
			         {
			             return false;
			         }
			     }
			 );
		}
		
	});
});

/**
 * Wird von urlHandler.js aufgerufen,
 * nachdem die div-Container fuer die Startseite angezeigt wurden.
 * Holt die benoetigten Daten per Ajax Call vom Server
 * und traegt sie in die richtigen HTML Elemente ein.
 * Im Falle der Startseite werden die verfuegbaren Studiengaenge
 * geholt und in das select-Element eingeordnet.
 */
function fillStartseite() 
{
	document.title = "Sopra Login";
    
    return ajaxCall(
        startseitenServlet,
        actionGetStudiengaenge,
        function(response) {
            $("#reg_studiengang").empty();
            var studgArr = response[keyJsonArrResult];
            fillSelectWithOptions($("#reg_studiengang"), studgArr, "", true, "Studiengang");
        }
    );
}
