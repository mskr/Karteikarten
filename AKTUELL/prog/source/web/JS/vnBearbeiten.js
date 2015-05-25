$(document).ready(function() {

	var categoryClassMapping = {};
	categoryClassMapping[keyJsonObjKlasseStudiengang] = "STUDIENGAENGE";
	autoComplete(
			$("#vn_bearbeiten_stg_input"),
			{
				STUDIENGAENGE: function(jsonSuchErgebnis) {
					addItemToList(selectedStudiengaenge, $("#vn_bearbeiten_stg_list"), 
							jsonSuchErgebnis[paramStudiengang], 
							jsonSuchErgebnis, undefined,undefined
					);
					$("#vn_bearbeiten_stg_list").focus(); //TODO geht nicht
				}
			},
			categoryClassMapping,
			actionSucheStudiengang
	);

	categoryClassMapping = {};
	categoryClassMapping[keyJsonObjKlasseBenutzer] = "MODERATORWAHL";
	autoComplete(
			$("#vn_bearbeiten_mod_input"),
			{
				MODERATORWAHL: function(jsonSuchErgebnis) {
					addItemToList(selectedModList, $("#vn_bearbeiten_mod_list"), 
							jsonSuchErgebnis[paramVorname] + " " + jsonSuchErgebnis[paramNachname], 
							jsonSuchErgebnis, undefined,undefined
					);
					$("#vn_bearbeiten_mod_list").focus(); //TODO geht nicht
				}
			},
			categoryClassMapping,
			actionSucheBenutzer
	);

    
	$('#vn_bearbeiten').click(function() {
		
		// Daten füllen
		var dialog = $("#vn_bearbeiten_popup");
		dialog.find("#vn_bearbeiten_titel_input").val(veranstaltungsObject[paramTitel]);
		dialog.find("#vn_bearbeiten_auswahl_semester").val(veranstaltungsObject[paramSemester]);

        for(var i in veranstaltungsObject[paramStudiengang])
    	{
        	var data = {};
        	data[paramStudiengang] = veranstaltungsObject[paramStudiengang][i];
        	
        	addItemToList(selectedStudiengaenge, $("#vn_bearbeiten_stg_list"), 
        			data[paramStudiengang], 
					data, undefined,undefined
			);
    	}
		
		dialog.find("#vn_bearbeiten_beschr_input").val(veranstaltungsObject[paramBeschr]);
		dialog.find("#vn_bearbeiten_passwort_set").prop("checked", veranstaltungsObject[paramKennwortGesetzt]);
		dialog.find("#vn_bearbeiten_pass_input").val();
		dialog.find("#vn_bearbeiten_komm_erlaubt").prop("checked", veranstaltungsObject[paramKommentareErlauben]);
		dialog.find("#vn_bearbeiten_bew_erlaubt").prop("checked", veranstaltungsObject[paramBewertungenErlauben]);
		
		if(veranstaltungsObject[paramModeratorKkBearbeiten] == true)
			dialog.find("#vn_bearbeiten_mod_bearb").prop("checked", true);
		else
			dialog.find("#vn_bearbeiten_keiner_bearb").prop("checked", true);
		
		selectedModList = {};
		// Moderatoren hinzufügen
		for(var m in veranstaltungsObject[paramModeratoren])
		{
			 addItemToList(selectedModList, $("#vn_bearbeiten_mod_list"), 
					 veranstaltungsObject[paramModeratoren][m][paramVorname]
			 + " " + veranstaltungsObject[paramModeratoren][m][paramNachname], 
			 veranstaltungsObject[paramModeratoren][m], undefined,undefined
             );
		}
		
		popupFenster(
				$("#vn_bearbeiten_popup_overlay"), 
				[$('#vn_bearbeiten_popup_close'),$("#vn_bearbeiten_cancel")],
				function() {
					var editor = $('#vn_bearbeiten_beschr_input').ckeditorGet();
					editor.destroy();

	            	var dialog = $("#vn_bearbeiten_popup");
	            	dialog.find("#vn_bearbeiten_titel_input").val("");
//	            	dialog.find("#vn_bearbeiten_auswahl_semester").val("");
	            	selectedStudiengaenge = {};
	            	$("#vn_bearbeiten_stg_list").empty();
	            	
	            	dialog.find("#vn_bearbeiten_beschr_input").val("");
	            	dialog.find("#vn_bearbeiten_pass_input").val("");
	            	dialog.find("#vn_bearbeiten_komm_erlaubt").prop("checked", true);
	            	dialog.find("#vn_bearbeiten_bew_erlaubt").prop("checked", true);

	            	dialog.find("#vn_bearbeiten_keiner_bearb").prop("checked", true);

	            	selectedModList = {};
	            	$("#vn_bearbeiten_mod_list").empty();
				},
				$("#vn_bearbeiten_ok"),
				function() {
					var dialog = $("#vn_bearbeiten_popup");
					var titel = dialog.find("#vn_bearbeiten_titel_input").val(),
					semester = dialog.find("#vn_bearbeiten_auswahl_semester").val(),
					beschr = dialog.find("#vn_bearbeiten_beschr_input").val(),
					moderatorenKkBearbeiten = dialog.find("input[name=vn_bearbeitenMode_radiogb]:checked").val() != "Nur ich",
					passw = dialog.find("#vn_bearbeiten_pass_input").val(),
					passChecked = dialog.find("#vn_bearbeiten_passwort_set").is(':checked'),
					kommentareErlaubt = dialog.find("#vn_bearbeiten_komm_erlaubt").is(':checked'),
					bewertungenErlaubt = dialog.find("#vn_bearbeiten_bew_erlaubt").is(':checked');

					var moderatorenIDs = [];

					for( var key in selectedModList)
					{
						moderatorenIDs.push(selectedModList[key][paramId]);
					}

					// Fehlerprüfung
	                if(titel == "" || beschr == "" || $.isEmptyObject(selectedStudiengaenge))
	                {
	                    showError("Bitte geben Sie mindestens einen Titel, eine Beschreibung und einen Studiengang an!");
	                    return false;
	                }
										
					var params = {};
					params[paramId] = veranstaltungsObject[paramId]
					params[paramTitel] = titel;
					params[paramSemester] = semester;
	                params[paramStudiengang] = "";
	                
	                for(var i in selectedStudiengaenge)
	            	{
	                	params[paramStudiengang] += selectedStudiengaenge[i][paramStudiengang] + ",";
	            	}
					params[paramBeschr] = beschr;
					params[paramModeratorKkBearbeiten] = moderatorenKkBearbeiten;
					params[paramKommentareErlauben] = kommentareErlaubt;
					params[paramBewertungenErlauben] = bewertungenErlaubt;
					if(passw != "" && passChecked)
						params[paramPasswort] = passw;
					else if(!passChecked)
						params[paramPasswort] = "";
					params[paramModeratoren] = moderatorenIDs;

					var ajax = ajaxCall(veranstaltungServlet,
							actionBearbeiteVeranst,
							function(response) {
						showInfo("Veranstaltung \""+ titel +"\"wurde erfolgreich bearbeitet.");
						gotoVeranstaltung(veranstaltungsObject[paramId]);  
					},
					params
					);
					return true;
				},
				$("#vn_bearbeiten_titel_input"),
				$("#vn_bearbeiten_weiter"),
				$("#vn_bearbeiten_zurueck")
		);

		$("#vn_bearbeiten_beschr_input").ckeditor(ckEditorVnErstellenConfig);

	});
});