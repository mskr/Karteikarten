/**
 * @author mk
 */

$(document).ready(function() {
    $("#registrieren_form").submit(function(event) {
        // Textfelder auslesen
        var email = $("#login_email").val();
        var pass = $("#login_pass").val();
        var passwdh = $("#reg_pass").val();
        var vorname = $("#reg_vorname").val();
        var nachname = $("#reg_nachname").val();
        var matnr = $("#reg_matnr").val();
        var studiengang = $("#reg_studiengang").val();
        // Passwortwiederholung ueberpruefen
        if(pass == passwdh) {
            $.ajax({
               url: "BenutzerServlet",
               data: "action="+actionRegister
                   +"&"+paramEmail+"="+email
                   +"&"+paramPasswort+"="+pass
                   +"&"+paramVorname+"="+vorname
                   +"&"+paramNachname+"="+nachname
                   +"&"+paramStudiengang+"="+studiengang
                   +"&"+paramMatrikelNr+"="+matnr,
               success: function(response) {
                   var jsonObj = $.parseJSON(response);
                   var errCode = jsonObj["error"];
                   if(errCode == "noerror") {
                       message(1, "Ihre Daten wurde erfolgreich eingetragen. Sie können sich nun einloggen.");
                   } else {
                       message(0, buildMessage(errCode));
                   }
               }
            });
        } else {
            // Falsche Passwortwiederholung
            $("#reg_pass").focus();
            $("#reg_pass").select();
            $("#reg_pass").css("border","4px solid IndianRed");
            message(0, "Ihre Passwortbestaetigung stimmt nicht mit dem Original ueberein. Bitte bestaetigen Sie es erneut.");
        }
        // Verhindert das normale Absenden des Formulars
        event.preventDefault();
    });
});