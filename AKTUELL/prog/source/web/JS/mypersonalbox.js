/**
 * @author mk
 */

/**
 * Fuellt die mypersonalbox mit den benoetigten Informationen.
 */
function fillMyPersonalBox()
{
    if(jsonBenutzer == undefined)
    {
        return;
    }
    fillUserContainer();
    handleReturnLink();
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
function fillUserContainer()
{
    var vorname = jsonBenutzer["vorname"];
    var nachname = jsonBenutzer["nachname"];
    var nutzerstatus = jsonBenutzer["nutzerstatus"];
    $(".username").html(vorname+" "+nachname);
    $(".rolle").html(" "+nutzerstatus);
    // ProfilBild anzeigen
    $(".user_MyProfilBild").attr("src", jsonBenutzer[paramProfilBild]);
}

/**
 * Blendet den Pfeil, ein der bei Klick zurueck auf die Hauptseite fuehrt,
 * falls der Benutzer sich nicht schon auf der Hauptseite befindet.
 */
function handleReturnLink() {
    if(getUrlParameterByName(urlParamLocation) != ansichtHauptseite)
    {
        $(".return").show();
        $(".return").click(function() {
            var paramObj = {};
            paramObj[urlParamLocation] = ansichtHauptseite;
            buildUrlQuery(paramObj);
        });
    }
    else
    {
        $(".return").hide();
        $(".return").off();
    }
}