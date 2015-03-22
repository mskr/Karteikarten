/**
 * @author mk
 */

/**
 * Zeigt die Daten des Benutzers im Profil an
 * jsonBenutzer enthält immer das aktuelle BenutzerObjekt.
 */
// TODO Funktion um Benutzer-Parameter erweitern, um belibige Profile anzuzeigen
// TODO 2. Prameter um Passwort/Einstellungen- feld an oder auszuschalten.
function fillProfilseite() {
	//TODO Durch variablen ersetzen
    $("#profil_email_input").val(jsonBenutzer[paramEmail]);
    //$("#profil_passwort_input").val("[geheim]");
    $("#profil_vorname_input").val(jsonBenutzer[paramVorname]);
    $("#profil_nachname_input").val(jsonBenutzer[paramNachname]);
    $("#profil_matnr_input").val(jsonBenutzer[paramMatrikelNr]);
    $("#profil_studiengang_input").val(jsonBenutzer[paramStudiengang]);
    $("#profil_rolle_input").val(jsonBenutzer[paramNutzerstatus]);
    if(jsonBenutzer[paramNutzerstatus] != "ADMIN") 
    {
        $("#profil_matnr_input").prop("disabled", true);
        $("#profil_studiengang_input").prop("disabled", true);
        $("#profil_rolle_input").prop("disabled", true);
    }
    // Zwar existent, aber unsichtbar
	$(".profil_passwort_wdh, .profil_passwort_alt").css("display","block");
	$(".profil_passwort_wdh, .profil_passwort_alt").hide();
	
    $("#profil_passwort_input").focus(function() 
    {
    	// ausklappen
    	$(".profil_passwort_wdh, .profil_passwort_alt").slideDown("slow");
    });
    $("#profil_passwort_input").focusout(function() 
    {
        // Wieder einklappen wenn Eingabefelder leer sind
	    if($("#profil_passwort_wdh_input").val() == "" && 
	       $("#profil_passwort_alt_input").val() == "" && 
	       $("#profil_passwort_input").val() == "" )
	    {
	    	$(".profil_passwort_wdh, .profil_passwort_alt").slideUp("slow");
	    }
    });

    // Einstellungen setzen
    // TODO Auf false setzen vorher notwendig ?!
    if(jsonBenutzer[paramNotifyKommentare] == paramNotifyKommentareValVeranst)
        $("#notifyKommentare_input_teilgenommen").prop("checked",true);
    if(jsonBenutzer[paramNotifyKommentare] == paramNotifyKommentareValDiskussion)
        $("#notifyKommentare_input_diskussionen").prop("checked",true);
    if(jsonBenutzer[paramNotifyKommentare] == paramNotifyKommentareValKeine)
        $("#notifyKommentare_input_nie").prop("checked",true);
    

    $("#notifyVeranstAenderung_input").prop("checked",jsonBenutzer[paramNofityVeranstAenderung]);
    $("#notifyKarteikartenAenderung_input").prop("checked",jsonBenutzer[paramNotifyKarteikartenAenderung]);
    
    
    // Buttons
    $("#profil_daten_speichern").click(function() 
    {
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
         
         // Prüfen ob alles ausgefüllt und bei Fehler einzeln hervorheben!
         var validInput = true;
         if(email == "")
         {
             $("#profil_email_input").css("border","4px solid IndianRed");
        	 validInput = false;
         }
         else
             $("#profil_email_input").css("border","none");
         
         if(vorname == "")
         {
             $("#profil_vorname_input").css("border","4px solid IndianRed");
        	 validInput = false;
         }
         else
             $("#profil_vorname_input").css("border","none");
         if(nachname == "")
         {
             $("#profil_nachname_input").css("border","4px solid IndianRed");
        	 validInput = false;
         }
         else
             $("#profil_nachname_input").css("border","none");
         if(jsonBenutzer[paramNutzerstatus] == "ADMIN")
    	 {
             if(matnr == "")
             {
                 $("#profil_matnr_input").css("border","4px solid IndianRed");
            	 validInput = false;
             }
             else
                 $("#profil_matnr_input").css("border","none");
             if(studiengang == "")
             {
                 $("#profil_studiengang_input").css("border","4px solid IndianRed");
            	 validInput = false;
             }
             else
                 $("#profil_studiengang_input").css("border","none");
             if(rolle == "")
             {
                 $("#profil_rolle_input").css("border","4px solid IndianRed");
            	 validInput = false;
             }
             else
                 $("#profil_rolle_input").css("border","none");
    	 }
         
         if(!validInput)
         {
         	message(0, "Bitte alle Felder ausfüllen!");
         }
         else
    	 {
        	 // Datenstring zusammenbauen
        	 var dataStr = "action="+actionAendereProfil
             +"&"+paramEmail+"="+email
             +"&"+paramVorname+"="+vorname
             +"&"+paramNachname+"="+nachname
             +"&"+paramNofityVeranstAenderung+"="+notifyVeranst
             +"&"+paramNotifyKarteikartenAenderung+"="+notifyKarteikarten
             +"&"+paramNotifyKommentare+"="+notifyKommentare;

             if(jsonBenutzer[paramNutzerstatus] == "ADMIN")
        	 {
            	 dataStr+= "&"+paramNutzerstatus+"="+rolle
                     		+"&"+paramStudiengang+"="+studiengang
                     		+"&"+paramMatrikelNr+"="+matnr
        	 }
             
        	 $.ajax({
                 url: profilServlet,
                 data: dataStr,
                 success: function(response) {
                     var jsonObj = $.parseJSON(response);
                     var errCode = jsonObj["error"];
                     if(errCode == "noerror") {
                    	 var paramObj = {};
                    	 paramObj[urlParamLocation] = ansichtProfilseite;
                    	 buildUrlQuery(paramObj);
                     } else {
                         message(0, buildMessage(errCode));
                     }
                 }
              });
    	 }              
	});
    
    $("#profil_passwort_speichern").click(function() 
	{
    	var pwNeu = $("#profil_passwort_input").val();
    	var pwNeuWdh = $("#profil_passwort_wdh_input").val();
    	var pwAlt = $("#profil_passwort_alt_input").val();

    	var validInput = true;
        if(pwNeu == "")
        {
            $("#profil_passwort_input").css("border","4px solid IndianRed");
       	 	validInput = false;
        }
        else
            $("#profil_passwort_input").css("border","none");
        if(pwNeuWdh == "")
        {
            $("#profil_passwort_wdh_input").css("border","4px solid IndianRed");
       	 	validInput = false;
        }
        else
            $("#profil_passwort_wdh_input").css("border","none");
        if(pwAlt == "")
        {
            $("#profil_passwort_alt_input").css("border","4px solid IndianRed");
       	 	validInput = false;
        }
        else
            $("#profil_passwort_alt_input").css("border","none");

        if(!validInput)
        {
        	message(0, "Bitte alle Felder ausfüllen!.");
        	// ausklappen
        	$(".profil_passwort_wdh, .profil_passwort_alt").slideDown("slow");
        }
        else if(pwNeu != pwNeuWdh)
    	{
        	// Wdh-Feld leeren
        	$("#profil_passwort_wdh_input").val("");
            $("#profil_passwort_wdh_input").css("border","4px solid IndianRed");
            message(0, "Passwortwiederholung falsch! Bitte prüfen Sie Ihre Eingaben!");
        	// ausklappen
        	$(".profil_passwort_wdh, .profil_passwort_alt").slideDown("slow");
    	}
        else
        { 
        	var dataStr = "action="+actionAenderePasswort
						            +"&"+paramPasswortNew+"="+pwNeu
						            +"&"+paramPasswort+"="+pwAlt
        	$.ajax({
        		url: profilServlet,
        		data: dataStr,
        		success: function(response) {
        			var jsonObj = $.parseJSON(response);
        			var errCode = jsonObj["error"];
        			if(errCode == "noerror") 
        			{
        				var paramObj = {};
        				paramObj[urlParamLocation] = ansichtProfilseite;
        				buildUrlQuery(paramObj);
        			} 
        			else if(errCode = "loginfailed") 
        			{
        	            $("#profil_passwort_alt_input").css("border","4px solid IndianRed");
        				message(0, "Altes Passwort ist falsch!");
        			} else {
        				message(0, buildMessage(errCode));
        			}
        		}
        	});
    	}
    	
	});
    
}
