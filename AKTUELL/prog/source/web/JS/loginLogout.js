/**
 * @author mk
 */

$(document).ready(function() {
    
    $("#login_form").submit(function(event) {
        var email = $("#login_email").val();
        var pass = $("#login_pass").val();
        pass = CryptoJS.MD5(pass);
        var params = {};
        params[paramEmail] = escape(email);
        params[paramPasswort] = escape(pass);
        ajaxCall(
             startseitenServlet,
             actionLogin,
             function() {
                 $.when(getBenutzer()).done(function(){
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
    
    $(".logout").click(function() {
        ajaxCall(
            startseitenServlet,
            actionLogout,
            function() {
                jsonBenutzer = undefined;
                showInfo("Sie haben sich erfolgreich abgemeldet");
                gotoStartseite();
            }
        )
    });
    
});