/**
 * @author mk
 */

$(document).ready(function() {
    $("#login_form").submit(function(event) {
        var email = $("#login_email").val();
        var pass = $("#login_pass").val();
        $.ajax({
            url: startseitenServlet,
            data: "action="+actionLogin+
            	"&"+paramEmail+"="+escape(email)+
            	"&"+paramPasswort+"="+escape(pass),
            beforeSend: function() {
                $("#login_submit").val("Lädt...");
                $("#login_submit").prop("disabled", true);
            },
            success: function(response) {
            	if(verifyResponse(response))
                {
            		$.when(getBenutzer()).done(gotoHauptseite);
                }
            },
            complete: function() {
                $("#login_submit").val("Login");
                $("#login_submit").prop("disabled", false);
            }
        });
        // Verhindert das normale Absenden des Formulars
        event.preventDefault();
    });
});

/**
 * Bleibt erstmal unbenutzt, da vielleicht unpraktisch.
 * Liefert einen String der Form
 * first-parameter=first-value&second-parameter=second-value,
 * der in einem Ajax Call mit der POST-Methode uebertragen werden kann.
 * @param paramArr ist ein Array der Form 
 * [first-parameter=first-value, second-parameter=second-value]
 */
function buildParameterString(paramArr) {
    var parameterString = "";
    for(var i=0; i<paramArr.length; i++) {
        parameterString += paramArr[i];
        if(i<paramArr.length-1) parameterString += "&";
    }
    return parameterString;
}