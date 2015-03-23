/**
 * @author mk
 */

/**
 * Fuellt die mypersonalbox mit den benoetigten Informationen.
 */
function fillMyPersonalBox() {
    fillUserContainer();
    $(".return").click(function() {
        var paramObj = {};
        paramObj[urlParamLocation] = ansichtHauptseite;
        buildUrlQuery(paramObj);
    });
    if(jsonBenutzer == undefined) {
        return;
    }
    $(".username").click(function() {
        var paramObj = {};
        paramObj[urlParamLocation] = ansichtProfilseite;
        paramObj[urlParamBenutzerProfil] = jsonBenutzer[paramEmail];
        buildUrlQuery(paramObj);
    });
}

/**
 * Zeigt Benutzername und Rolle
 * in der mypersonalbox an.
 */
function fillUserContainer() {
    if(jsonBenutzer == undefined) {
        return;
    }
    var vorname = jsonBenutzer["vorname"];
    var nachname = jsonBenutzer["nachname"];
    var nutzerstatus = jsonBenutzer["nutzerstatus"];
    $(".username").html(vorname+" "+nachname);
    $(".rolle").html(" "+nutzerstatus);
}
