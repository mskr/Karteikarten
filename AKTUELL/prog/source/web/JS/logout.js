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
            success: function(response) 
            {
                if(verifyResponse(response))
                {
                    jsonBenutzer = undefined;
                    gotoStartseite();
                }
            }
        });
    });
});