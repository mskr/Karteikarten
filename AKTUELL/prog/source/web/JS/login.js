/**
 * @author mk
 */

$(document).ready(function() {
    $("#login_submit").click(function() {
        var email = $("#login_email").val();
        var pass = $("#login_pass").val();
        alert("Email="+email+", PW="+pass); //Debug
        $.ajax({
            type: "POST",
            url: "BenutzerServlet",
            data: "action=login"+"&email="+email+"&pass="+pass,
            success: function(response) {
                var jsonObj = $.parseJSON(response);
                var errCode = jsonObj["error"];
                alert("errCode="+errCode); //Debug
                if(errCode == "noerror") {
                    window.location.href = "hauptseite.html";
                } else {
                    alert(buildMessage(errCode));
                }
            }
        });
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