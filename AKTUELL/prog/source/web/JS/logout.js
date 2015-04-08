/**
 * @author mk
 */

$(document).ready(function() {
    /**
     * Beim Ausloggen wird das Logout Kommando an den Server gesendet.
     * Antwortet dieser mit einem No Error wird einfach die Seite
     * neu geladen. Das darauf folgende getBenutzer erkennt,
     * dass niemand eingeloggt ist und leitet zur Startseite weiter.
     */
    $(".logout").click(function() {
        $.ajax({
            url: benutzerServlet,
            data: "action="+actionLogout,
            beforeSend: function() {
                $("#logout").off();
                $("#logout").text("Lädt...");
                $("#logout").css({"color":"gray","font-size":".75em"});
            },
            success: function(response) 
            {
                if(verifyResponse(response))
                {
                    location.search = "";
                }
            }
        });
    });
});