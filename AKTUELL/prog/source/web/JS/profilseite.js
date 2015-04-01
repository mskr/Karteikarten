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
    var profilBenutzerId = getUrlParameterByName(urlParamId);
    
    $("#profil_avatar_aendern_file_name").hide();
    $(".profil_avatar_overlay").hide();
    $(".profil_loeschen").hide();
    
    if(profilBenutzerId == jsonBenutzer[paramId])
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
        $(".profil_benutzername").text(profilVorname+" "+profilNachname);
        $(".profil_avatar_img").attr("src", jsonBenutzer[paramProfilBild]);
        
        fillProfilHead();
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
                "&"+paramId+"="+getUrlParameterByName(urlParamId),
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
                    $(".profil_benutzername").text(profilVorname+" "+profilNachname);
                    $(".profil_avatar_img").attr("src", jsonObj[paramProfilBild]);
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
                        $("#profil_avatar_aendern").hide();
                    }
                    else
                    {
                        // Eingeloggter Admin zeigt anderes Profil an
                        profilNotifyKommentare = jsonObj[paramNotifyKommentare];
                        profilNotifyVeranstAenderung = jsonObj[paramNofityVeranstAenderung];
                        profilNotifyKarteikartenAenderung = jsonObj[paramNotifyKarteikartenAenderung];
                        fillProfilHead();
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
                    var paramObj = {};
                    paramObj[urlParamLocation] = ansichtHauptseite;
                    buildUrlQuery(paramObj);
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

/**
 * Zeigt Elemente im Profilkopf an.
 * Dazu gehoeren aktuell:
 * - Avatar aendern Trigger
 * - Konto loeschen Button
 */
function fillProfilHead() {
    // Zeige Avatar aendern Trigger
    $("#profil_avatar_aendern_file_name").show();
    $(".profil_avatar_overlay").show();
    // Reagiere auf die Auswahl einer Datei
    $('#profil_avatar_aendern_file').change(function() {
        var filenameFull = $('#profil_avatar_aendern_file').val();
        var fileName = filenameFull.split(/(\\|\/)/g).pop();
        $("#profil_avatar_aendern_file_name").html(fileName);
        $("#profil_avatar_aendern_file_name").css("color","yellow");
        if(fileName != "")
            $("#profil_avatar_submit").fadeIn();
        else
            $("#profil_avatar_submit").fadeOut();
    });
    
    $(".profil_loeschen").show();
    // TODO Konto loeschen Funktion implementieren
}

/**
 * Zeigt die Stammdaten des Benutzers an.
 */
function fillProfilDaten() {
    $("#profil_email_input").val(profilEmail);
    $("#profil_vorname_input").val(profilVorname);
    $("#profil_nachname_input").val(profilNachname);
    $("#profil_matnr_input").val(profilMatrikelNr);
    fillProfilStudiengaenge();
    $("#profil_rolle_input").val(profilNutzerstatus);
}

/**
 * Holt die Studiengaenge und traegt sie in das select Element ein.
 */
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
                for(var i in studgArr) {
                    $("#profil_studiengang_input").append("<option>"+studgArr[i]+"</option>");
                }
                $("#profil_studiengang_input").val(profilStudiengang);
            } else {
                message(0, buildMessage(errCode));
            }
        }
    });
}

/**
 * Zeigt die persoenlichen Einstellungen des Benutzers an.
 */
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

/**
 * Liest die Daten aus den Feldern aus und uebertraegt Sie zum Server.
 */
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
         +"&"+paramId+"="+getUrlParameterByName(urlParamId)
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

/**
 * Laedt ein neues Profilbild hoch.
 */
function registerAvatarAendernEvent() {
    $("#profil_avatar_aendern_form").submit(function(event) {
        event.preventDefault();
    	var file = $('#profil_avatar_aendern_file')[0].files[0];
    	var filenameFull = $('#profil_avatar_aendern_file').val();
    	var fileName = filenameFull.split(/(\\|\/)/g).pop();
    	if(fileName.toLowerCase().indexOf(".jpg") < 0 && 
    			fileName.toLowerCase().indexOf(".jpeg") < 0 &&
    			fileName.toLowerCase().indexOf(".bmp") < 0 &&
    			fileName.toLowerCase().indexOf(".png") < 0)
    	{
    		message(0, "Leider werden nur die Formate jpg/jpeg, bmp oder png unterstützt.");
    	}
    	else
    	{
    		uploadFile(file, function(response) {
    			var jsonObj = response;
    			var errCode = jsonObj["error"];
    			if(errCode == "noerror") 
    			{
    				message(1, "Gespeichert.");
    				location.reload();
    			} 
    			else if(errCode == "invalidparam")
    			{
    				message(0, "Bitte geben Sie eine gültige Datei an.");
    			}
    			else 
    			{
    				message(0, buildMessage(errCode));
    			}
    			console.log("uploadFile meldet: "+response);
    		},
    		actionUploadProfilBild,
    		function() {
    		    // beforeSend
    		    $("#profil_avatar_submit").val("Lädt...");
    		    $("#profil_avatar_submit").prop("disabled", true);
    		},
    		function() {
    		    // complete
    		    $("#profil_avatar_submit").val("Avatar ändern");
                $("#profil_avatar_submit").prop("disabled", false);
    		});
    	}
        event.preventDefault();
    });
}