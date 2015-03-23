/**
 * @author mk
 */

    // Temporaere Variablen fuer die vom Server geladenen Daten.
    // TODO Evntl in defines.js auslagern
    var profilEmail;
    var profilVorname;
    var profilNachname;
    var profilMatrikelNr;
    var profilStudiengang;
    var profilNutzerstatus;
    var profilNotifyKommentare;
    var profilNotifyVeranstAenderung;
    var profilNotifyKarteikartenAenderung;

/**
 * Zeigt die Daten des Benutzers im Profil an
 * jsonBenutzer enthält immer das aktuelle BenutzerObjekt.
 */
function fillProfilseite() {
    
    // Der Benutzer dessen Profil angezeigt wird
    var profilBenutzerEmail = getUrlParameterByName(urlParamBenutzerProfil);
    
    if(profilBenutzerEmail == jsonBenutzer[paramEmail])
    {
        // Benutzer zeigt eigenes Profil an
        profilEmail = jsonBenutzer[paramEmail];
        profilVorname = jsonBenutzer[paramVorname];
        profilNachname = jsonBenutzer[paramNachname];
        profilMatrikelNr = jsonBenutzer[paramMatrikelNr];
        profilStudiengang = jsonBenutzer[paramStudiengang];
        profilNutzerstatus = jsonBenutzer[paramNutzerstatus];
        profilNotifyKommentare = jsonBenutzer[paramNotifyKommentare];
        profilNotifyVeranstAenderung = jsonBenutzer[paramNofityVeranstAenderung];
        profilNotifyKarteikartenAenderung = jsonBenutzer[paramNotifyKarteikartenAenderung];
        $(".profil_benutzer").text(profilVorname+" "+profilNachname);
        fillProfilDaten();
        fillProfilEinstellungen();
        registerProfilSpeichernEvents();
        registerAvatarAendernEvent();
    }
    else
    {
        // Benutzer zeigt anderes Profil an
        $.ajax({
            url: profilServlet,
            data: "action="+actionGetOtherBenutzer+
                "&"+paramEmail+"="+getUrlParameterByName(urlParamBenutzerProfil),
            success: function(response)
            {
                var jsonObj = response;
                var errCode = jsonObj["error"];
                if(errCode == "noerror")
                {
                    profilEmail = jsonObj[paramEmail];
                    profilVorname = jsonObj[paramVorname];
                    profilNachname = jsonObj[paramNachname];
                    profilMatrikelNr = jsonObj[paramMatrikelNr];
                    profilStudiengang = jsonObj[paramStudiengang];
                    profilNutzerstatus = jsonObj[paramNutzerstatus];
                    $(".profil_benutzer").text(profilVorname+" "+profilNachname);
                    fillProfilDaten();
                    if(jsonBenutzer[paramNutzerstatus] != "ADMIN")
                    {
                        // Eingeloggter normaler Benutzer zeigt anderes Profil an
                        $("#profil_email_input").prop("disabled", true);
                        $("#profil_vorname_input").prop("disabled", true);
                        $("#profil_nachname_input").prop("disabled", true);
                        $("#profil_einstellungen").hide();
                        $("#profil_passwort").hide();
                        $("#profil_daten_speichern").hide();
                    }
                    else
                    {
                        // Eingeloggter Admin zeigt anderes Profil an
                        profilNotifyKommentare = jsonObj[paramNotifyKommentare];
                        profilNotifyVeranstAenderung = jsonObj[paramNofityVeranstAenderung];
                        profilNotifyKarteikartenAenderung = jsonObj[paramNotifyKarteikartenAenderung];
                        fillProfilEinstellungen();
                        registerProfilSpeichernEvents();
                        // Ein eingeloggter Admin kann auf dem Profil eines anderen Benutzers das Passwort aendern
                        // OHNE das alte Passwort einzugeben.
                        $("#profil_passwort_alt").remove();
                    }
                }
                else
                {
                    message(0, buildMessage(errCode));
                    // Angefragter Benutzer existiert evntl nicht
                    // -> Server schickt Invalid Parameter Error
//                    location.search = ""; // Loesche alle URL Parameter und lade die Seite neu
                }
            }
        });
    }

    if(jsonBenutzer[paramNutzerstatus] != "ADMIN") 
    {
        // Eingeloggter Benutzer zeigt eigenes Profil an
        $("#profil_matnr_input").prop("disabled", true);
        $("#profil_studiengang_input").prop("disabled", true);
        $("#profil_rolle_input").prop("disabled", true);
    }
}

function fillProfilDaten() {
    $("#profil_email_input").val(profilEmail);
    $("#profil_vorname_input").val(profilVorname);
    $("#profil_nachname_input").val(profilNachname);
    $("#profil_matnr_input").val(profilMatrikelNr);
    fillProfilStudiengaenge();
    $("#profil_rolle_input").val(profilNutzerstatus);
}

function fillProfilStudiengaenge() {
    $.ajax({
        url: benutzerServlet,
        data: "action="+actionGetStudiengaenge,
        success: function(response) {
            var jsonObj = response;
            var errCode = jsonObj["error"];
            if(errCode == "noerror") {
                $("#profil_studiengang_input").empty();
                var studgArr = jsonObj[keyJsonArrResult];
                for(var studg of studgArr) {
                    $("#profil_studiengang_input").append("<option>"+studg+"</option>");
                }
                $("#profil_studiengang_input").val(profilStudiengang);
            } else {
                message(0, buildMessage(errCode));
            }
        }
    });
}

function fillProfilEinstellungen() {
    switch(profilNotifyKommentare)
    {
        case paramNotifyKommentareValVeranst:
            $("#notifyKommentare_input_teilgenommen").prop("checked",true);
            break;
        case paramNotifyKommentareValDiskussion:
            $("#notifyKommentare_input_diskussionen").prop("checked",true);
            break;
        case paramNotifyKommentareValKeine:
            $("#notifyKommentare_input_nie").prop("checked",true);
    }
    $("#notifyVeranstAenderung_input").prop("checked",profilNotifyVeranstAenderung);
    $("#notifyKarteikartenAenderung_input").prop("checked",profilNotifyKarteikartenAenderung);
    
}

function registerProfilSpeichernEvents() {
    
    $("#profil_daten_form").submit(function(event) 
    {
        // Felder auslesen
        var email = $("#profil_email_input").val();
        var vorname = $("#profil_vorname_input").val();
        var nachname = $("#profil_nachname_input").val();
        var notifyVeranst = $("#notifyVeranstAenderung_input").prop("checked");
        var notifyKarteikarten = $("#notifyKarteikartenAenderung_input").prop("checked");
        var notifyKommentare =  $("input[name=profil_notifyKommentareRb]:checked").val();
        if(jsonBenutzer[paramNutzerstatus] == "ADMIN")
        {
            var matnr = $("#profil_matnr_input").val();
            var studiengang = $("#profil_studiengang_input").val();
            var rolle = $("#profil_rolle_input").val();
        }
         // Datenstring zusammenbauen
         var dataStr = "action="+actionAendereProfil
         +"&"+paramEmail+"="+getUrlParameterByName(urlParamBenutzerProfil)
         +"&"+paramVorname+"="+vorname
         +"&"+paramNachname+"="+nachname
         +"&"+paramNofityVeranstAenderung+"="+notifyVeranst
         +"&"+paramNotifyKarteikartenAenderung+"="+notifyKarteikarten
         +"&"+paramNotifyKommentare+"="+notifyKommentare
         +"&"+paramEmailNew+"="+email;
         if(jsonBenutzer[paramNutzerstatus] == "ADMIN")
         {
             dataStr+= "&"+paramNutzerstatus+"="+rolle
                        +"&"+paramStudiengang+"="+studiengang
                        +"&"+paramMatrikelNr+"="+matnr;
         }
         // Daten via Ajax an Server senden
         $.ajax({
             url: profilServlet,
             data: dataStr,
             beforeSend: function() {
                 $("#profil_daten_speichern").val("Lädt...");
                 $("#profil_daten_speichern").prop("disabled", true);
             },
             success: function(response) {
                 var jsonObj = response;
                 var errCode = jsonObj["error"];
                 if(errCode == "noerror") {
                     message(1, "Gespeichert.");
                     location.reload();
                 } else {
                     message(0, buildMessage(errCode));
                 }
             },
             complete: function() {
                 $("#profil_daten_speichern").val("Speichern");
                 $("#profil_daten_speichern").prop('disabled', false);
             }
          }); 
         // Verhindert das normale Absenden des Formulars
         event.preventDefault();         
    });
            
    $("#profil_passwort_form").submit(function(event) 
    {
        // Felder auslesen
        var pwNeu = $("#profil_passwort_input").val();
        var pwNeuWdh = $("#profil_passwort_wdh_input").val();
        var pwAlt = $("#profil_passwort_alt_input").val();
        // Fehleingaben abfangen
        if(pwNeu != pwNeuWdh)
        {
            // Wdh-Feld leeren
            $("#profil_passwort_wdh_input").val("");
            $("#profil_passwort_wdh_input").focus();
            $("#profil_passwort_wdh_input").css("border","4px solid IndianRed");
            message(0, "Bitte prüfen Sie Ihre Eingaben.");
        }
        else
        {
            // Datenstring zusammenbauen
            var dataStr = "action="+actionAenderePasswort
                                    +"&"+paramPasswortNew+"="+pwNeu
                                    +"&"+paramPasswort+"="+pwAlt
                                    +"&"+paramEmail+"="+profilEmail;
            // Daten via Ajax an Server senden
            $.ajax({
                url: profilServlet,
                data: dataStr,
                beforeSend: function() {
                    $("#profil_passwort_speichern").val("Lädt...");
                    $("#profil_passwort_speichern").prop('disabled', true);
                },
                success: function(response) {
                    var jsonObj = response;
                    var errCode = jsonObj["error"];
                    if(errCode == "noerror") 
                    {
                        message(1, "Gespeichert.");
                        location.reload();
                    } 
                    else if(errCode = "loginfailed") 
                    {
                        $("#profil_passwort_alt_input").css("border","4px solid IndianRed");
                        $("#profil_passwort_alt_input").val("");
                        $("#profil_passwort_alt_input").focus();
                        message(0, "Bitte prüfen Sie Ihre Eingaben.");
                    } else {
                        message(0, buildMessage(errCode));
                    }
                },
                complete: function() {
                    $("#profil_passwort_speichern").val("Speichern");
                    $("#profil_passwort_speichern").prop('disabled', false);
                }
            });
        }
        // Verhindert das normale Absenden des Formulars
        event.preventDefault();
    });
}

function registerAvatarAendernEvent() {
    $("#profil_avatar_aendern").submit(function(event) {
        var formData = new FormData();
        formData.append('file', $('#profil_avatar_aendern_file')[0].files[0]);
        formData.append('action', actionUploadProfilBild);
        $.ajax({
            type: "POST",
            url: fileUploadServlet,
            enctype: "multipart/form-data",
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                console.log(response);
            }
        });
        event.preventDefault();
    });
}
