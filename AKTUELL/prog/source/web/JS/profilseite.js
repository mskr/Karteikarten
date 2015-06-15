/**
 * @author mk
 */

var currentProfilID;

// Statische Handler einmal registrieren
$(document).ready(function() {
    
	$("#profil_loeschen_bt").click(function() {
		sindSieSicher($("#profil_loeschen_bt"), "Wollen sie das Profil wirklich löschen?",function(){
		    var params = {};
		    params[paramId] = currentProfilID;
			ajaxCall(
			    profilServlet,
			    actionDeleteBenutzer,
			    function() {
			        showInfo("Profil wurde erfolgreich gelöscht.");
			        // Hat sich der Benutzer selbst geloescht oder war das
			        // ein Admin, der jmd anders geloescht hat.
			        if(currentProfilID == jsonBenutzer[paramId])
			        {
			            jsonBenutzer = undefined;
	                    gotoStartseite();
			        }
			        else
			        {
			            gotoHauptseite();
			        }
			    },
			    params
			);
		}, "bottom","left");
	});

    // Reagiere auf die Auswahl einer Datei
    $('#profil_avatar_aendern_file').change(function() {
        var filenameFull = $('#profil_avatar_aendern_file').val();
        var fileName = filenameFull.split(/(\\|\/)/g).pop();
        $("#profil_avatar_aendern_file_name").html(fileName);
        $("#profil_avatar_aendern_file_name").css("color","yellow");
        if(fileName != "")
        {
            $("#profil_avatar_submit").show("scale");
            $("#profil_avatar_aendern_file_name").show("scale");
        }
        else
        {
            $("#profil_avatar_submit").hide("scale");
            $("#profil_avatar_aendern_file_name").hide("scale");
        }
    });
    $("#profil_avatar_loeschen_bt").click(function(){
    	sindSieSicher($("#profil_avatar_loeschen_bt"), "Wollen sie das Bild wirklich löschen?",function(){
		    var params = {};
		    params[paramId] = currentProfilID;
			ajaxCall(
			    profilServlet,
			    actionDeleteBenutzerBild,
			    function() {
			        showInfo("Ihr Profilbild wurde erfolgreich gelöscht!");
			        $(".profil_avatar_img").attr("src","files/profilBilder/default.png");
			        $(".user_MyProfilBild").attr("src","files/profilBilder/default.png");
			        $("#profil_avatar_loeschen_bt").hide();
			    },
			    params
			);
		}, "bottom","left");
    });
    
    getProfilStudiengaenge();
    registerProfilSpeichernEvents();
    registerAvatarAendernEvent();
    
    // Farbschema aendern Handler
    $("#farbschema_toggle_checkbox").change(function() {
        if($(this).prop("checked"))
        {
			if (jsonBenutzer[paramId] == currentProfilID) {
				changeCSS("CSS/sopra_light.css", 0);
				changeCSS("CSS/mybuttonstyle_light.css", 1);
			}

			$("#farbschema_toggle_label").text("TAG");
            
            var params = {};
            params[paramId] = currentProfilID;
            params[paramTheme] = "DAY";
            ajaxCall(profilServlet, actionSetTheme, function(){
            	showInfo("Farbschema geändert.")
            	jsonBenutzer[paramTheme] = "DAY";
            }, params);
            
        }
        else
        {
        	if (jsonBenutzer[paramId] == currentProfilID) {
        		changeCSS("CSS/sopra.css", 0);
        		changeCSS("CSS/mybuttonstyle.css", 1);
        	}
        	$("#farbschema_toggle_label").text("NACHT");

            var params = {};
            params[paramId] = currentProfilID;
            params[paramTheme] = "NIGHT";
            ajaxCall(profilServlet, actionSetTheme, function(){
            	showInfo("Farbschema geändert.")
            	jsonBenutzer[paramTheme] = "NIGHT";
            }, params);
        }
    });
    
});
/**
 * Zeigt die Daten des Benutzers im Profil an
 * jsonBenutzer enthält immer das aktuelle BenutzerObjekt.
 */
function fillProfilseite() 
{
    currentProfilID = getUrlParameterByName(urlParamId);
    
    if(currentProfilID == jsonBenutzer[paramId])
    {
    	UserName = jsonBenutzer[paramVorname]+" "+jsonBenutzer[paramNachname];
    	document.title = UserName;
    	fillMyProfil(jsonBenutzer[paramNutzerstatus] == "ADMIN");
    }
    else
    {
        // Benutzer zeigt anderes Profil an
    	var params = {};
    	params[paramId] = currentProfilID;
        return ajaxCall(profilServlet,
            			actionGetOtherBenutzer,
            function(response)
            {
        		fillOtherProfil(response,jsonBenutzer[paramNutzerstatus] == "ADMIN");
            },
            params,
            function(erroTxt)
            {
                // Angefragter Benutzer existiert evntl nicht
                gotoHauptseite();
                
                return false;
            }
        );
    }
}

/**
 * Holt die Studiengaenge und traegt sie in das select Element ein.
 */
function getProfilStudiengaenge() {
    return ajaxCall(startseitenServlet,
    				actionGetStudiengaenge,
    				function(response) 
    				{
				    	$("#profil_studiengang_input").empty();
				    	var studgArr = response[keyJsonArrResult];
				    	for(var i in studgArr) {
				    		$("#profil_studiengang_input").append("<option>"+studgArr[i]+"</option>");
				    	}
				    	console.log("Studiengaenge geladen");
			    	}
		    	);
}

function fillMyProfil(isAdmin)
{
    $("#profil_email_input").attr("disabled",false);
    $("#profil_vorname_input").attr("disabled",false);
    $("#profil_nachname_input").attr("disabled",false);
    
	$("#profil_avatar_aendern_file_name").show();
	$(".profil_avatar_overlay").show();
	$(".profil_loeschen").show();
	$("#profil_einstellungen").show();
	$("#profil_passwort ").show();
	$("#profil_pw_alt_div").show();
	$("#profil_passwort_alt_input").prop("required",true);
	$("#profil_daten_speichern").show();
    $(".profil_farbschema_auswahl").show();
	
	$(".profil_benutzername").text(jsonBenutzer[paramVorname] +" "+jsonBenutzer[paramNachname]);
	$(".profil_avatar_img").attr("src", jsonBenutzer[paramProfilBild]);
	console.log(jsonBenutzer[paramProfilBild]);
	if(jsonBenutzer[paramProfilBild].indexOf("default")==-1){
		$("#profil_avatar_loeschen_bt").show();
	}
	else
	{
		$("#profil_avatar_loeschen_bt").hide();
	}
    $(".profil_loeschen").show();
    $("#profil_email").show();
    $("#profil_matrikelnummer").show();
    $("#profil_email_input").val(jsonBenutzer[paramEmail]);
    $("#profil_vorname_input").val(jsonBenutzer[paramVorname]);
    $("#profil_nachname_input").val(jsonBenutzer[paramNachname]);
    $("#profil_matnr_input").val(jsonBenutzer[paramMatrikelNr]);
    $("#profil_rolle_input").val(jsonBenutzer[paramNutzerstatus]);
	$("#profil_studiengang_input").val(jsonBenutzer[paramStudiengang]);
	console.log("Setzte studiengang auf " + jsonBenutzer[paramStudiengang]);
    
    switch(jsonBenutzer[paramNotifyKommentare])
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

    if(jsonBenutzer[paramTheme] == "DAY")
    {
    	$("#farbschema_toggle_checkbox").prop("checked",true);
        $("#farbschema_toggle_label").text("TAG");
    }
    else
    {
    	$("#farbschema_toggle_checkbox").prop("checked",false);
        $("#farbschema_toggle_label").text("NACHT");
    }
    
    $("#notifyVeranstAenderung_input").prop("checked",jsonBenutzer[paramNofityVeranstAenderung]);
    $("#notifyKarteikartenAenderung_input").prop("checked",jsonBenutzer[paramNotifyKarteikartenAenderung]);
	
	if(!isAdmin)
	{
		$("#profil_matnr_input").prop("disabled", true);
		$("#profil_studiengang_input").prop("disabled", true);
		$("#profil_rolle_input").prop("disabled", true);
	}
	else
	{
		$("#profil_matnr_input").prop("disabled", false);
		$("#profil_studiengang_input").prop("disabled", false);
		$("#profil_rolle_input").prop("disabled", false);
	}
}

function fillOtherProfil(benutzer, isAdmin)
{
	//aktualisiere Titel
	UserName = benutzer[paramVorname]+" "+benutzer[paramNachname];
	document.title = UserName;

	$("#profil_avatar_loeschen_bt").hide();
	$(".profil_benutzername").text(benutzer[paramVorname] +" "+benutzer[paramNachname]);
	$(".profil_avatar_img").attr("src", benutzer[paramProfilBild]);

    $("#profil_vorname_input").val(benutzer[paramVorname]);
    $("#profil_nachname_input").val(benutzer[paramNachname]);
    $("#profil_rolle_input").val(benutzer[paramNutzerstatus]);
	$("#profil_studiengang_input").val(benutzer[paramStudiengang]);
    
    switch(benutzer[paramNotifyKommentare])
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
    
    $("#notifyVeranstAenderung_input").prop("checked",benutzer[paramNofityVeranstAenderung]);
    $("#notifyKarteikartenAenderung_input").prop("checked",benutzer[paramNotifyKarteikartenAenderung]);
	
	if(!isAdmin)
	{
	    $("#profil_email").hide();
	    $("#profil_matrikelnummer").hide();
		$("#profil_avatar_aendern_file_name").hide();
		$(".profil_avatar_overlay").hide();
		$(".profil_loeschen").hide();
		$("#profil_einstellungen").hide();
		$("#profil_pw_alt_div").show();
		$("#profil_passwort_alt_input").prop("required",true);
		$("#profil_passwort").hide();
		$("#profil_daten_speichern").hide();
        $(".profil_farbschema_auswahl").hide();

	    $("#profil_email_input").val("");
	    $("#profil_matnr_input").val("");
	    
		$("#profil_email_input").prop("disabled", true);
		$("#profil_vorname_input").prop("disabled", true);
		$("#profil_nachname_input").prop("disabled", true);
		$("#profil_matnr_input").prop("disabled", true);
		$("#profil_studiengang_input").prop("disabled", true);
		$("#profil_rolle_input").prop("disabled", true);
	}
	else
	{
	    $("#profil_email").show();
	    $("#profil_matrikelnummer").show();
		$("#profil_avatar_aendern_file_name").show();
		$(".profil_avatar_overlay").show();
		$(".profil_loeschen").show();
		$("#profil_einstellungen").show();
		$("#profil_passwort").show();
		$("#profil_passwort_alt_input").prop("required",false);
		$("#profil_pw_alt_div").hide();
		$("#profil_daten_speichern").show();
        $(".profil_farbschema_auswahl").show();

	    $("#profil_email_input").val(benutzer[paramEmail]);
	    $("#profil_matnr_input").val(benutzer[paramMatrikelNr]);
	    
		$("#profil_email_input").prop("disabled", false);
		$("#profil_vorname_input").prop("disabled", false);
		$("#profil_nachname_input").prop("disabled", false);
		$("#profil_matnr_input").prop("disabled", false);
		$("#profil_studiengang_input").prop("disabled", false);
		$("#profil_rolle_input").prop("disabled", false);


	    if(benutzer[paramTheme] == "DAY")
	    {
	    	$("#farbschema_toggle_checkbox").prop("checked",true);
	        $("#farbschema_toggle_label").text("TAG");
	    }
	    else
	    {
	    	$("#farbschema_toggle_checkbox").prop("checked",false);
	        $("#farbschema_toggle_label").text("NACHT");
	    }
	}
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
        var params = {};
        params[paramId] = getUrlParameterByName(urlParamId);
        params[paramVorname] = vorname;
        params[paramNachname] = nachname;
        params[paramNofityVeranstAenderung] = notifyVeranst;
        params[paramNotifyKarteikartenAenderung] = notifyKarteikarten;
        params[paramNotifyKommentare] = notifyKommentare;
        params[paramEmailNew] = email;
        if(jsonBenutzer[paramNutzerstatus] == "ADMIN")
        {
            params[paramNutzerstatus] = rolle;
            params[paramStudiengang] = studiengang;
            params[paramMatrikelNr] = matnr;
        }
        ajaxCall(
            profilServlet,
            actionAendereProfil,
            function(response) {
                showInfo("Änderungen gespeichert.");
                $.when(getBenutzer()).done(function() {
                    fillProfilseite();
                    fillMyPersonalBox();
                });
            },
            params,
            undefined,
            function() {
                $("#profil_daten_speichern").val("Lädt...");
                $("#profil_daten_speichern").prop("disabled", true);
            },
            function() {
                $("#profil_daten_speichern").val("Speichern");
                $("#profil_daten_speichern").prop('disabled', false);
            }
        );
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
            showError("Bitte prüfen Sie Ihre Eingaben.");
        }
        else
        {
        	pwNeu = CryptoJS.MD5(pwNeu);
        	pwAlt = CryptoJS.MD5(pwAlt);
        	
            var params = {};
            params[paramPasswortNew] = escape(pwNeu);
            if(jsonBenutzer[paramNutzerstatus] != "ADMIN")
            	params[paramPasswort] = escape(pwAlt);
            params[paramId] = currentProfilID;
            ajaxCall(
                profilServlet,
                actionAenderePasswort,
                function(response) {
                    showInfo("Änderungen gespeichert.");
                    $("#profil_passwort_alt_input").removeAttr("style");
                    $("#profil_passwort_wdh_input").removeAttr("style");
                    $("#profil_passwort_input").removeAttr("style");
                    fillProfilseite();
                },
                params,
                function(errCode) {
                    if(errCode == "loginfailed") 
                    {
                        $("#profil_passwort_alt_input").css("border","4px solid IndianRed");
                        $("#profil_passwort_alt_input").val("");
                        $("#profil_passwort_alt_input").focus();
                        showError("Bitte prüfen Sie Ihre Eingaben.");
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                },
                function() {
                    $("#profil_passwort_speichern").val("Lädt...");
                    $("#profil_passwort_speichern").prop('disabled', true);
                },
                function() {
                    $("#profil_passwort_speichern").val("Speichern");
                    $("#profil_passwort_speichern").prop('disabled', false);
                }
            );
            $("#profil_passwort_input").val("");
            $("#profil_passwort_wdh_input").val("");
            $("#profil_passwort_alt_input").val("");
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
    		showError("Leider werden nur die Formate jpg/jpeg, bmp oder png unterstützt.");
            $("#profil_avatar_submit").hide("scale");
            $("#profil_avatar_aendern_file_name").hide("scale");
    	}
    	else
    	{
    		var params = {};
    		params[paramId] = currentProfilID;
    		uploadFile(file, function(response) {
    			

            	var errFkt = function(errCode) 
            	{
            		if(errCode == "invalidparam") 
                    {
                        showError("Bitte geben Sie eine gültige Datei an.");
                        return true;
                    }
            		return false;
				};
				
    			if(verifyResponse(response,errFkt))
    			{
            		showInfo("Änerungen gespeichert.");
            		$.when(getBenutzer()).done(function() {
                 		fillProfilseite();
                 		fillMyPersonalBox();
					});
    			}
    		},
    		actionUploadProfilBild,
    		params,
    		function() {
    		    // beforeSend
    		    $("#profil_avatar_submit").val("Lädt...");
    		    $("#profil_avatar_submit").prop("disabled", true);
    		},
    		function() {
    		    // complete
    		    $("#profil_avatar_submit").val("Avatar ändern");
                $("#profil_avatar_submit").prop("disabled", false);
                $("#profil_avatar_submit").hide("scale");
                $("#profil_avatar_aendern_file_name").hide("scale");
    		});
    	}
        event.preventDefault();
    });
}
