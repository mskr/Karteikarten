/**
 * @author mk
 */

$(document).ready(function() {
    $("#login_form").submit(function(event) {
        var email = $("#login_email").val();
        var pass = $("#login_pass").val();
        $.ajax({
            type: "POST",
            url: "BenutzerServlet",
            data: "action=login"+"&email="+email+"&pass="+pass,
            success: function(response) {
                var jsonObj = $.parseJSON(response);
                var errCode = jsonObj["error"];
                if(errCode == "noerror") {
                    window.location.href = "hauptseite.html";
                } else {
                    $(".err").html(buildMessage(errCode));
                    if( !$(".err").is(":visible") ) {
                        $(".err").show().fadeOut(5000);
                    } else {
                        $(".err").stop();
                        $(".err").css("opacity","1");
                        $(".err").fadeOut(5000);
                    }
                }
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