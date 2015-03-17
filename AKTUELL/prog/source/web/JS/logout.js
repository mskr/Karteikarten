/**
 * @author mk
 */

$(document).ready(function() {
    $("#logout").click(function() {
        $.ajax({
            type: "POST",
            url: "BenutzerServlet",
            data: "action=logout",
            success: function(response) {
                var jsonObj = $.parseJSON(response);
                var errCode = jsonObj["error"];
                if(errCode == "noerror") {
                    window.location.href = "startseite.html";
                } else {
                    $(".err").html(buildMessage(errCode));
                }
            }
        });
    });
});