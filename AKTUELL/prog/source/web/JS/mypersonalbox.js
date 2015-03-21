/**
 * @author mk
 */

/**
 * Fuellt die mypersonalbox mit den benoetigten Informationen.
 */
function fillMyPersonalBox() {
    if(jsonBenutzer != undefined) {
        fillUserContainer();
    }
    $(".return").click(function() {
        var paramObj = {};
        paramObj[urlParamLocation] = ansichtHauptseite;
        buildUrlQuery(paramObj);
    });
    $(".username").click(function() {
        var paramObj = {};
        paramObj[urlParamLocation] = ansichtProfilseite;
        buildUrlQuery(paramObj);
    });
    // TODO Was passiert wenn man aus der Profilseite heraus auf den Usernamen klickt??
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
