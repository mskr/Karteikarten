/**
 * @author Andreas, Marius
 */

$(document).ready(function() {
    
    $("#login_form").submit(function(event) {
        var email = $("#login_email").val();
        var pass = $("#login_pass").val();
        pass = CryptoJS.MD5(pass);
        var params = {};
        params[paramEmail] = email;
        params[paramPasswort] = escape(pass);
        ajaxCall(
             startseitenServlet,
             actionLogin,
             function() {
                 // Warte bis aktueller Benutzer als JSON geladen
                 $.when(getBenutzer()).done(function() {
                     // Falls URL nicht auf Startseite gesetzt, weiterleiten zu der location mit der
                     // die Seite aufgerufen wurde
                	 if(initialURL.indexOf("sopra.html")>=0 && initialURL.indexOf("startseite")<0)
                		 History.back();
                	 else
                		 gotoHauptseite();
                 });
             },
             params,
             undefined,
             function() {
                 $("#login_submit").val("Lädt...");
                 $("#login_submit").prop("disabled", true);
             },
             function() {
                 $("#login_submit").val("Login");
                 $("#login_submit").prop("disabled", false);
             }
        );
        // Verhindert das normale Absenden des Formulars
        event.preventDefault();
    });
    
    $("#logout").click(function() {
        ajaxCall(
            startseitenServlet,
            actionLogout,
            function() {
                jsonBenutzer = undefined;
                showInfo("Sie haben sich erfolgreich abgemeldet");
                clearBenachrichtigungen();
                gotoStartseite();
            }
        )
    });
    
});