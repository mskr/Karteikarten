/**
 * @author mk
 */

$(document).ready(function() {
    $("#logout").click(function() {
        $.ajax({
            type: "POST",
            url: "BenutzerServlet",
            data: "action="+actionLogout,
            success: function(response) {
                var jsonObj = $.parseJSON(response);
                var errCode = jsonObj["error"];
                if(errCode == "noerror") {
                    displayStartseite();
                    message(1, "Sie haben sich ausgeloggt.");
                } else {
                    message(0, buildMessage(errCode));
                }
            }
        });
    });
});