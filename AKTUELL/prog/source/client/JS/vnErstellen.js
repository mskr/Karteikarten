/**
 * @author Andreas, Marius
 * 
 */

/**
 * Erzeugt den Erstellen Dialog für das Erstellen der Veranstaltung
 */
$(document).ready(function() {

	// Popup dialog erstellen für Veranstaltung erstellen dialog.
	 var popup = new PopupFenster(
        $("#vn_erstellen_popup_overlay"), 
        [$('#vn_erstellen_popup_close'),$("#vn_erstellen_cancel")],
        undefined,
        $("#vn_erstellen_ok"),
        undefined,
        $("#vn_erstellen_titel_input"),
        $("#vn_erstellen_weiter"),
        $("#vn_erstellen_zurueck")
    );
	 // Schließen funktion
	 var closeFkt = function() {
		 var dialog = $("#vn_erstellen_popup");
		 dialog.find("#vn_erstellen_titel_input").val("");
		 selectedStudiengaenge = {};
		 $("#vn_erstellen_stg_list").empty();
		 $("#vn_erstellen_stg_input").val("");
		 dialog.find("#vn_erstellen_beschr_input").val("");
		 dialog.find("#vn_erstellen_pass_input").val("");
		 dialog.find("#vn_erstellen_komm_erlaubt").prop("checked", true);
		 dialog.find("#vn_erstellen_bew_erlaubt").prop("checked", true);
		 $("#vn_erstellen_mod_input").val("");
		 $("#vn_erstellen_mod_bearb").prop("checked", true);
		 selectedModList = {};
		 $("#vn_erstellen_mod_list").empty();
	 }
	 // Submit-Funktion
	 var submitFkt = function() {
		 var dialog = $("#vn_erstellen_popup");
		 var titel = dialog.find("#vn_erstellen_titel_input").val(),
		 semester = dialog.find("#vn_erstellen_auswahl_semester").val(),
		 beschr = dialog.find("#vn_erstellen_beschr_input").val(),
		 moderatorenKkBearbeiten = dialog.find("input[name=vn_bearbeitenMode_radiogb]:checked").val() != "Nur ich",
		 passw = dialog.find("#vn_erstellen_pass_input").val(),
		 kommentareErlaubt = dialog.find("#vn_erstellen_komm_erlaubt").is(':checked'),
		 bewertungenErlaubt = dialog.find("#vn_erstellen_bew_erlaubt").is(':checked');


		 // Fehlerprüfung
		 if(titel == "" || beschr == "" || $.isEmptyObject(selectedStudiengaenge))
		 {
			 showError("Bitte geben Sie mindestens einen Titel, eine Beschreibung und einen Studiengang an!");
			 return false;
		 }

		 var params = {};
		 params[paramTitel] = titel;
		 params[paramSemester] = semester;
		 params[paramStudiengang] = [];

		 for(var i in selectedStudiengaenge)
		 {
			 params[paramStudiengang].push(selectedStudiengaenge[i][paramStudiengang]);
		 }

		 params[paramBeschr] = beschr;
		 params[paramModeratorKkBearbeiten] = moderatorenKkBearbeiten;
		 params[paramKommentareErlauben] = kommentareErlaubt;
		 params[paramBewertungenErlauben] = bewertungenErlaubt;
		 params[paramPasswort] = passw;
		 params[paramModeratoren] = [];
		 for( var key in selectedModList)
		 {
			 params[paramModeratoren].push(selectedModList[key][paramId]);
		 }
		 
		 // Senden des Ajax-Calls zum Erstellen der Veranstaltung
		 ajaxCall(veranstaltungServlet,
				 actionErstelleVeranst,
				 function(response) {
			 showInfo("Veranstaltung \""+ titel +"\"wurde erfolgreich erzeugt.");
			 fillVeranstaltungsliste();  
		 },
		 params
		 );
		 return true;
	 }
	 // Funktionen setzen
	 popup.setCloseFkt(closeFkt);
	 popup.setSubmitFkt(submitFkt);
	 
	 // CK editor für die Veranstaltungsbeschreibung erstellen.
	 $("#vn_erstellen_beschr_input").ckeditor(ckEditorVnErstellenConfig);
	 // Popup anzeigen wenn benutzer auf erstellen klickt
	 $('#vn_erstellen_bt').click(function() {
		 popup.show();
	 });
	 
});

//Speichere die aktuell im Veranstaltung-erstellen-Dialog gewaehlten Studiengaenge
var selectedStudiengaenge = {};

// Speichere die aktuell im Veranstaltung-erstellen-Dialog gewaehlten Moderatoren
var selectedModList = {};

/**
 * Registrieren der Handler fuer Dialog zum Erstellen von Veranstaltungen
 */
function registerVeranstErzeugeHandler() {

    var categoryClassMapping = {};
    // Achtung: Kategoriename 'STUDIENGAENGE' muss dem Key in Categories entprechen!
    categoryClassMapping[keyJsonObjKlasseStudiengang] = "STUDIENGAENGE";
    autoComplete(
            $("#vn_erstellen_stg_input"),
            {
            	STUDIENGAENGE: function(jsonSuchErgebnis) {
                    addItemToList(selectedStudiengaenge, $("#vn_erstellen_stg_list"), 
                            jsonSuchErgebnis[paramStudiengang], 
                            jsonSuchErgebnis, undefined,undefined
                    );
                }
            },
            categoryClassMapping,
            actionSucheStudiengang
    );

    categoryClassMapping = {};
    // Achtung: Kategoriename 'Moderatorenwahl' muss dem Key in Categories entprechen!
    categoryClassMapping[keyJsonObjKlasseBenutzer] = "MODERATORWAHL";
    autoComplete(
            $("#vn_erstellen_mod_input"),
            {
                MODERATORWAHL: function(jsonSuchErgebnis) {
                    addItemToList(selectedModList, $("#vn_erstellen_mod_list"), 
                            jsonSuchErgebnis[paramVorname] + " " + jsonSuchErgebnis[paramNachname], 
                            jsonSuchErgebnis, undefined,undefined
                    );
                }
            },
            categoryClassMapping,
            actionSucheBenutzer
    );
}

