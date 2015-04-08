/**
 * @author mk
 */

$(document).ready(function() {
    $("#registrieren_form").submit(function(event) {
        // Textfelder auslesen
        var email = $("#login_email").val();
        if(validateEmail(email))
        {
            var pass = $("#login_pass").val();
            var passwdh = $("#reg_pass").val();
            // Passwortwiederholung ueberpruefen
            if(pass == passwdh)
            {
                var vorname = $("#reg_vorname").val();
                var nachname = $("#reg_nachname").val();
                var matnr = $("#reg_matnr").val();
                var studiengang = $("#reg_studiengang").val();
                $.ajax({
                   url: benutzerServlet,
                   data: "action="+actionRegister
                       +"&"+paramEmail+"="+email
                       +"&"+paramPasswort+"="+pass
                       +"&"+paramVorname+"="+vorname
                       +"&"+paramNachname+"="+nachname
                       +"&"+paramStudiengang+"="+studiengang
                       +"&"+paramMatrikelNr+"="+matnr,
                   success: function(response) {
                	   
                       if(verifyResponse(response))
                       {
                    	   showInfo("Ihre Daten wurde erfolgreich eingetragen. Sie können sich nun einloggen.")
                       }
                   }
                });
            }
            else
            {
                // Falsche Passwortwiederholung
                $("#reg_pass").focus();
                $("#reg_pass").val("");
                $("#reg_pass").css("border","4px solid IndianRed");
                message(0, "Bitte wiederholen Sie Ihr Passwort erneut.");
            }
        }
        else
        {
            $("#login_email").focus();
            $("#login_email").css("border","4px solid IndianRed");
            message(0, "Bitte prüfen Sie Ihre Email-Adresse.");
        }
        // Verhindert das normale Absenden des Formulars
        event.preventDefault();
    });
});

/**
 * Akzeptiert Email Adressen, die "mindestens" die Form a@b.de haben.
 * @see http://stackoverflow.com/questions/46155
 * @param email die validiert werden soll.
 * @returns true wenn Email valide, false andernfalls.
 */
function validateEmail(email) {
    var re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}