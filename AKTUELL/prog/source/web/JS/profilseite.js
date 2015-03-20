/**
 * @author mk
 */

function fillProfilseite() {
    $("#profil_email_input").val(jsonBenutzer["email"]);
    $("#profil_passwort_input").val("[geheim]");
    $("#profil_vorname_input").val(jsonBenutzer["vorname"]);
    $("#profil_nachname_input").val(jsonBenutzer["nachname"]);
    $("#profil_matnr_input").val(jsonBenutzer["matrikelnr"]);
    $("#profil_studiengang_input").val(jsonBenutzer["studiengang"]);
    $("#profil_rolle_input").val(jsonBenutzer["nutzerstatus"]);
    if(jsonBenutzer["nutzerstatus"] != "ADMIN") {
        $("#profil_matnr_input").prop("disabled", true);
        $("#profil_studiengang_input").prop("disabled", true);
        $("#profil_rolle_input").prop("disabled", true);
    }
    $("#profil_passwort_input").focus(function() {
        $(".profil_passwort_wdh").css("display","block");
        $(".profil_passwort_wdh").hide();
        $(".profil_passwort_wdh").slideDown();
    });
    
}