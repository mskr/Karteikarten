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
                    $("#mypersonalbox_hauptseite").hide();
                    $("#mainbox_hauptseite").hide();
                    $("#mypersonalbox_startseite").show();
                    $("#mainbox_startseite").show();
                    message(1, "Sie haben sich ausgeloggt.");
                } else {
                    message(0, buildMessage(errCode));
                }
            }
        });
    });
});