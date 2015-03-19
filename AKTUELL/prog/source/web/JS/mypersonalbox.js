/**
 * @author mk
 */

/**
 * Fuellt die mypersonalbox mit den benoetigten Informationen.
 */
function fillMyPersonalBox() {
    if(jsonBenutzer != "null") {
        fillUserContainer();
    }
    if(lastVisited != 1) {
        // Derzeit wird nicht die Hauptseite angezeigt,
        // also erhaelt der Zurueckpfeil eine Click Function
        $(".return").click(function() {
            lastVisited = 1;
            handle();
        });
    }
}

/**
 * Zeigt Benutzername und Rolle an
 * in der mypersonalbox an.
 */
function fillUserContainer() {
    var vorname = jsonBenutzer["vorname"];
    var nachname = jsonBenutzer["nachname"];
    var nutzerstatus = jsonBenutzer["nutzerstatus"];
    $(".username").html(vorname+" "+nachname);
    $(".rolle").html(" "+nutzerstatus);
}