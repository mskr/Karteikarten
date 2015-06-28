/**
 * @author Julius, Andreas, Marius
 */

$(document).ready(function() {
    
    // Initialisiere Feld fuer File Uploads
	kkErstellenDropZone = $("#df_area_kk").dropzone(myDropzoneConfig).get(0).dropzone;
	
	// Initialisiere Karteikarte bearbeiten Popup
    kkErstellenPopup = new PopupFenster(
            $("#kk_erstellen_popup_overlay"), 
            [$('#kk_erstellen_popup_close'),$("#kk_erstellen_cancel")],
            undefined,
            $("#kk_erstellen_ok"),
            undefined,
            $("#kk_erstellen_titel_input"),
            $("#kk_erstellen_weiter"),
            $("#kk_erstellen_zurueck")
        );
});

/**
 * Startet den Vorgang zum erstellen einer neuen Karteikarte mit gegebener Vater- und Bruderkarteikarte.
 * @param vater
 * @param bruder
 */
function newKarteikarte(vater, bruder) {
    
	// Spezialfall ganz oben im Baum
	if(vater == undefined)
		vater = veranstaltungsObject[paramErsteKarteikarte];
	if(bruder == undefined) //no brother before it, then -1
		bruder = -1;
		
	// Initialisiere ckEditor
	try {
	    $("#kk_erstellen_TA").ckeditor(ckEditorVnErstellenConfig);
	}
    catch(e){
    	console.log(e);
    }
    
    // Soll eine neues Kapitel angelegt werden sind die Popup-Seiten 
    // fuer Attribut und Verweise nicht noetig
    $("#kk_neuesKapitel").change(function(e) {
    	if($("#kk_neuesKapitel").prop("checked"))
    	{
    		kkErstellenPopup.disablePage(1);
    		kkErstellenPopup.disablePage(2);
    		$("#kk_erstellen_content").hide();
    	}
    	else
    	{
    		kkErstellenPopup.enableAllPages();
    		$("#kk_erstellen_content").show();
    	}
    });
    
    //== Code fuer die Verweise START ==
    
    var verweisVoraussetzungArr = [];
    var verweisWeiterfuehrendArr = [];
    var verweisUebungArr = [];
    var verweisSonstigesArr = [];
    
    // Sammle Ajax Objekte aller Verweis Baeume
    var verwBaeumeAjaxCalls = [];
    
    $(".kk_erstellen_verweise_baum").each(function(i,v) {
    	var inh = new Inhaltsverzeichnis($(this),
    			veranstaltungsObject,
    			undefined,
    			false,
    			function(arr, kkListItem, i, e, ajax, klappeAus) {
                    if(klappeAus)
                    {
                        $.when(ajax).done(function() {
                            // Checkboxen mit den Arrays synchronisieren
                            kkListItem.find("> ul input[type='checkbox']").each(function(index, elem) {
                                var verwBaum = $(elem).parents(".kk_verweise_baum");
                                var verwTyp = verwBaum.attr("id").split("_")[4];
                                syncCheckboxWithArray(verwTyp, $(elem).data("kkid"), $(elem));
                            });
                            // Change Handler fuer die Checkboxes der bei Klick geladenen Kinder
                            kkListItem.find("> ul input[type='checkbox']").change(function(e) {
                                var verwBaum = $(e.target).parents(".kk_verweise_baum");
                                var verwTyp = verwBaum.attr("id").split("_")[4];
                                var isHinzu = $(e.target).prop("checked");
                                var zielKkId = $(e.target).data("kkid");
                                verweiseVonBenutzerGeaendert(verwTyp, isHinzu, zielKkId);
                            });
                        });
                    }
                },
                true,false);

        verwBaeumeAjaxCalls.push(inh.init());
    });
    
    // Warte darauf, dass die Wurzel-Ebene aller Verweise Baeume geladen wurde
    $.when.apply(null, verwBaeumeAjaxCalls).done(function() {
        // Change Handler fuer die Checkboxes der Wurzel-Ebene
        $(".kk_erstellen_verweise_baum input[type='checkbox']").change(function(e) {
            var verwBaum = $(e.target).parents(".kk_verweise_baum");
            var verwTyp = verwBaum.attr("id").split("_")[4];
            var isHinzu = $(e.target).prop("checked");
            var zielKkId = $(e.target).data("kkid");
            verweiseVonBenutzerGeaendert(verwTyp, isHinzu, zielKkId);
        });
    });
    
    /**
     * Setzt die gegebene Checkbox falls der gegebene Verweis im Array vorhanden ist.
     * @param verweisTyp String
     * @param zielKkId Number
     * @param checkbox DOM Object
     */
    function syncCheckboxWithArray(verweisTyp, zielKkId, checkbox)
    {
        switch(verweisTyp)
        {
            case "voraussetzung":
                if(jQuery.inArray(zielKkId, verweisVoraussetzungArr) != -1)
                    checkbox.prop("checked", true);
                break;
            case "weiterfuehrend":
                if(jQuery.inArray(zielKkId, verweisWeiterfuehrendArr) != -1)
                    checkbox.prop("checked", true);
                break;
            case "uebung":
                if(jQuery.inArray(zielKkId, verweisUebungArr) != -1)
                    checkbox.prop("checked", true);
                break;
            case "sonstige":
                if(jQuery.inArray(zielKkId, verweisSonstigesArr) != -1)
                    checkbox.prop("checked", true);
        }
    }
    
    /**
     * Durch Setzen oder Loeschen von Checkboxen geaenderte Verweise auch in den Array hinzufuegen oder loeschen.
     * @param verweisTyp String
     * @param isHinzu Boolean
     * @param zielKkId Number
     */
    function verweiseVonBenutzerGeaendert(verweisTyp, isHinzu, zielKkId)
    {
        switch(verweisTyp)
        {
            case "voraussetzung":
                if(isHinzu)
                    verweisVoraussetzungArr.push(zielKkId);
                else
                    verweisVoraussetzungArr = jQuery.grep(verweisVoraussetzungArr, function(elem) {
                        return elem != zielKkId;
                    });
                break;
            case "weiterfuehrend":
                if(isHinzu)
                    verweisWeiterfuehrendArr.push(zielKkId);
                else
                    verweisWeiterfuehrendArr = jQuery.grep(verweisWeiterfuehrendArr, function(elem) {
                        return elem != zielKkId;
                    });
                break;
            case "uebung":
                if(isHinzu)
                    verweisUebungArr.push(zielKkId);
                else
                    verweisUebungArr = jQuery.grep(verweisUebungArr, function(elem) {
                        return elem != zielKkId;
                    });
                break;
            case "sonstige":
                if(isHinzu)
                    verweisSonstigesArr.push(zielKkId);
                else
                    verweisSonstigesArr = jQuery.grep(verweisSonstigesArr, function(elem) {
                        return elem != zielKkId;
                    });
        }
        
    }

    //== Code fuer die Verweise ENDE ==
    
    /**
     * Startet eine Pruefung der Eingaben und ggf. das Absenden der Daten.
     * @returns true, falls Eingaben fehlerfrei und Popup bereit zum Schliessen.
     */
    submitFkt = function() {
    	var text = $("#kk_neuesKapitel").prop("checked") ? "" : $("#kk_erstellen_TA").val().trim();
    	var titel = $("#kk_erstellen_titel_input").val().trim();
    	var attributes = getSelectedKkAttributes();
    	if(titel=="")
    	{
    		showError("Bitte geben Sie ihrer Karteikarte einen Titel.");
    		return false;
    	}
    	else if(text == "" && kkErstellenDropZone.files.length == 0 && !$("#kk_neuesKapitel").prop("checked"))
    	{
    		showError("Bitte füllen sie ihre Karteikarte mit einem Inhalt.");
    		return false;
    	}
    	else if(uploadInProgress)
		{
    		showError("Bitte warten. Datei wird hochgeladen.");
    		return false;
		}
    	else
    	{
    		processKKerstellen(text,titel,attributes, bruder, vater, 
    				verweisVoraussetzungArr, verweisWeiterfuehrendArr, verweisUebungArr, verweisSonstigesArr);
    		return true;
    	}
    }
    
    /**
     * Raeumt das Popup beim Schliessen auf.
     */
    clearFkt = function(){
    	$("#kk_neuesKapitel").off();
    	$("#kk_erstellen_text_area").show();
		$("#df_area_kk").show();
    	$("#kk_erstellen_popup input[type='checkbox']").prop("checked",false);
		$("#kk_erstellen_content").show();
    	$("#kk_erstellen_titel_input").val("");
    	$("#kk_erstellen_TA").val("");
    	kkErstellenDropZone.removeAllFiles(true);
    	$("#kk_erstellen_popup .drop_file_areas").removeClass("dropped");
    	// Zerstoere Verweis Baeume mit allen Handlern
    	$("#kk_erstellen_popup").find(".kk_verweise_baum").empty();
    }
    
    // Popup konfigurieren und anzeigen
    kkErstellenPopup.setCloseFkt(clearFkt);
    kkErstellenPopup.setSubmitFkt(submitFkt);
    kkErstellenPopup.show();
    
    /**
     * Erstellt ein requestfertiges Objekt aller noetigen Parameter zum Erstellen einer neuen Karteikarte
     * @param text
     * @param titel
     * @param attributes
     * @param bruder
     * @param vater
     * @param verweisVoraussetzungArr
     * @param verweisWeiterfuehrendArr
     * @param verweisUebungArr
     * @param verweisSonstigesArr
     */
	function processKKerstellen(text,titel,attributes, bruder, vater, 
	        verweisVoraussetzungArr, verweisWeiterfuehrendArr, verweisUebungArr, verweisSonstigesArr)
	{
		var params = {};
		params[paramTitel] = titel;
		params[paramVeranstaltung] = veranstaltungsObject[paramId];
		params[paramInhalt] = text;
		params[paramAttribute] = attributes;
		params[paramType] = UPLOADTYPE;
		params[paramVaterKK] = vater;
		params[paramBruderKK] = bruder;
		params[paramKkUploadID] = UPLOADIDSET;
		params[paramVerweisVoraussetzung] = verweisVoraussetzungArr;
        params[paramVerweisWeiterfuehrend] = verweisWeiterfuehrendArr;
        params[paramVerweisUebung] = verweisUebungArr;
        params[paramVerweisSonstiges] = verweisSonstigesArr;
		submitNewKarteikarte(params)
	}
	
	/**
     * @returns true falls Attribute gewaehlt wurden
     */
    function isAnyAttrSelected()
    {
        var isTrue = false;
        $("#kkE_attributes input[type='checkbox']").each(function(i, elem) {
            if($(elem).prop("checked")){
                isTrue = true;
                return false; // break loop
            }
        });
        return isTrue;
    }
    
    /**
     * Wandelt gewaehlte Attribute in einen String um.
     * Der String ist eine kommaseparierte Liste aus 'true' und 'false'.
     * Ein solcher String wird vom Server erwartet.
     * @returns String
     */
    function getSelectedKkAttributes()
    {
        var str = ""
            $("#kkE_attributes input[type='checkbox']").each(function(i, elem) {
                str = str + $(elem).prop("checked") + ",";
            });
            str = str.substring(0, str.length - 1);
            return str;
    }
    
    /**
     * Sendet den Ajax Call zum Erstellen einer Karteikarte ab.
     * @params
     */
    function submitNewKarteikarte(params)
    {
    	var ajax = ajaxCall(karteikartenServlet,
    			actionErstelleKarteikarte,
                function(response) {
                    showInfo("Karteikarte \""+ params[paramTitel] +"\" wurde erfolgreich erzeugt.");
                    displayKarteikarte(response[paramId]);
                    $.when(vnInhaltsverzeichnis.init()).done(function(){
                        vnInhaltsverzeichnis.showEintrag(response[paramId]);
                    });
                },
                params
            );
    }
    
   
}

/**
 * Testet, ob eine Datei ein akzeptiertes Bild ist.
 * @param filename
 * @returns true, wenn akzeptiertes Format
 */
function isImage(filename) 
{
    var ext = getExtension(filename);
    switch (ext.toLowerCase()) {
        case 'jpg':
        case 'png':
            //etc
            return true;
    }
    return false;
}

/**
 * Testet, ob eine Datei ein akzeptiertes Video ist.
 * @param filename
 * @returns true, wenn akzeptiertes Format
 */
function isVideo(filename) 
{
    var ext = getExtension(filename);
    switch (ext.toLowerCase()) {
        case 'mp4':
        case 'ogg':
            // etc
            return true;
    }
    return false;
}