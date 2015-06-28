/**
 * @author Julius, Andreas, Marius
 */

$(document).ready(function() {
    
    // Initialisiere Feld fuer File Uploads
	kkBearbeitenDropZone = $("#df_area_kk_b").dropzone(myDropzoneConfig).get(0).dropzone;
	
	// Initialisiere Karteikarte bearbeiten Popup
	kkBearbeitenPopup = new PopupFenster(
            $("#kk_bearbeiten_popup_overlay"), 
            [$('#kk_bearbeiten_popup_close'),$("#kk_bearbeiten_cancel")],
            undefined,
            $("#kk_bearbeiten_ok"),
            undefined,
            $("#kk_bearbeiten_titel_input"),
            $("#kk_bearbeiten_weiter"),
            $("#kk_bearbeiten_zurueck")
        );
	
});

/**
 * Laedt die Daten der zu bearbeitenden Karteikarte vom Server und startet das Anzeigen des Bearbeiten-Dialogs.
 * @param triggerElem Bearbeiten Button einer Karteikarte
 * @returns Ajax Objekt
 */
function processKK_editClick(triggerElem){
	id = triggerElem.parents(".kk_wrapper").data("kkid");
	var params = {};
    params[paramKkId] = id;
    params[paramVnId] = veranstaltungsObject[paramId];
    return ajaxCall(karteikartenServlet, actionGetKarteikarteByID, 
        function(response) {
			kkBearbeiten(response);
		},
        params
    );
}

/**
 * Startet den Vorgang zum Bearbeiten der gegebenen Karteikarte.
 * @param kkJSON vom Server empfangene Daten einer Karteikarte als JSON Objekt
 */
function kkBearbeiten(kkJSON)
{
    // Initialisiere Dialog
	$("#df_area_kk_b").find("#kk_b_BildPreviewTemplate").remove();
	$("#df_area_kk_b").find(".dz-message").show();
	$("#kk_jetztNeuesKapitel_b ~ label").html("Diese Karteikarte als Überschrift verwenden.");
	$("#kk_jetztNeuesKapitel_b").off();
	
	// Wenn Haken fuer Erstellen einer Ueberschrift gesetzt wurde
	// deaktiviere Popup Seiten mit Attributen und Verweisen, da diese bei Ueberschriften nicht angezeigt werden
	$("#kk_jetztNeuesKapitel_b").change(function(e) {
    	if($("#kk_jetztNeuesKapitel_b").prop("checked")==true)
    	{
    		$("#kk_bearbeiten_content").hide();
			kkBearbeitenPopup.disablePage(1);
			kkBearbeitenPopup.disablePage(2);
    		$("#kk_jetztNeuesKapitel_b ~ label").append(
    		        "<span style='color:yellow'><br><span class='octicon octicon-alert'></span> Inhalt geht verloren</span>");
    	}
    	else
    	{
    		$("#kk_bearbeiten_content").show();
    		kkBearbeitenPopup.enableAllPages();
		    $("#kk_jetztNeuesKapitel_b ~ label").html("Diese Karteikarte als Überschrift verwenden.");
    	}
    });
	
	// Aktuell gesetzte Attribute anhaken
	$("#kkB_attributes input[type='checkbox']").each(function(i, elem) {
		if(kkJSON[paramAttribute][i])
		{
		    $(elem).prop("checked", true);
		}
	});
	
	// Aktuellen Titel setzen
	$("#kk_bearbeiten_titel_input").val(kkJSON[paramTitel]);
	
	// Aktuellen Inhalt setzen
	$("#kk_bearbeiten_TA").val(kkJSON[paramInhalt]);
	
	// Dialog fuer verschiedene Typen von Karteikarten anpassen
	if(kkJSON[paramType]=="TEXT")
	{
	    // Wenn Inhalt Leerstring ist handelt es sich um eine Ueberschrift.
	    // Deaktiviere daher Popup Seiten fuer Attribute und Verweise
		if(kkJSON[paramInhalt]==""){
			$("#kk_jetztNeuesKapitel_b").prop("checked",true);
    		$("#kk_bearbeiten_content").hide();
			kkBearbeitenPopup.disablePage(1);
			kkBearbeitenPopup.disablePage(2);
		}
		else
		{
			$("#kk_jetztNeuesKapitel_b").prop("checked",false);
    		$("#kk_bearbeiten_content").show();
    		kkBearbeitenPopup.enableAllPages();
		}
	}
	else if(kkJSON[paramType]=="BILD")
	{
	    // Zeige aktuell gewaehltes Bild im Upload Feld an
		var xhr = $.ajax({
			  type: "HEAD",
			  url: "files/images/"+kkJSON[paramId]+".png",
			  success: function(msg){
				  var mockFile = { 
						  name: kkJSON[paramId]+".png", 
						  size: xhr.getResponseHeader('Content-Length'),
						  accepted: true,
						  fromServer: true					// Wird verwendet um zu prüfen, ob file neu ist oder nicht
						  };
				  mockFile.upload = {bytesSent: xhr.getResponseHeader('Content-Length')};
				  mockFile.kind = "file";
				  
				  // Feuere Added File Event
				  kkBearbeitenDropZone.emit("addedfile", mockFile);
				  kkBearbeitenDropZone.files.push(mockFile);
				  // Zeige Thumbnail
				  kkBearbeitenDropZone.emit("thumbnail", mockFile, "files/images/"+kkJSON[paramId]+".png");
				  $("#df_area_kk_b").addClass("dz-max-files-reached");
			  }
		});
	}
	else if(kkJSON[paramType]=="VIDEO")
	{
	    // Zeige aktuell gewaehltes Video im Upload Feld an
		var xhr = $.ajax({
			  type: "HEAD",
			  url: "files/videos/"+kkJSON[paramId]+".mp4",
			  success: function(msg){
				  var mockFile = { 
						  name: kkJSON[paramId]+".mp4", 
						  size: xhr.getResponseHeader('Content-Length'),
						  accepted: true,
						  fromServer: true					// Wird verwendet um zu prüfen, ob file neu ist oder nichht
						  };
				  mockFile.upload = {bytesSent: xhr.getResponseHeader('Content-Length')};
				  mockFile.kind = "file";
				  
				  // Feuere Added File Event
				  kkBearbeitenDropZone.emit("addedfile", mockFile);
				  kkBearbeitenDropZone.files.push(mockFile);
				  // Zeige Thumbnail
				  kkBearbeitenDropZone.emit("thumbnail", mockFile, "files/videos/"+kkJSON[paramId]+".mp4");
			  }
			});
	}
	else
	{
		showError("Ein interner Fehler beim Laden der Karteikarte ist aufgetreten.")
	}
    
    //== Code fuer die Verweise START ==
    
    var verweisVoraussetzungArr = [];
    var verweisWeiterfuehrendArr = [];
    var verweisUebungArr = [];
    var verweisSonstigesArr = [];
    
    // Arrays mit aktuellen Verweisen fuellen
    for(var index in kkJSON[paramVerweise])
    {
        switch(kkJSON[paramVerweise][index][paramType].toLowerCase())
        {
            case paramVerweisVoraussetzung.toLowerCase():
                verweisVoraussetzungArr.push(kkJSON[paramVerweise][index][paramId]);
                break;
            case paramVerweisWeiterfuehrend.toLowerCase():
                verweisWeiterfuehrendArr.push(kkJSON[paramVerweise][index][paramId]);
                break;
            case paramVerweisUebung.toLowerCase():
                verweisUebungArr.push(kkJSON[paramVerweise][index][paramId]);
                break;
            case paramVerweisSonstiges.toLowerCase():
                verweisSonstigesArr.push(kkJSON[paramVerweise][index][paramId]);
        }
    }
    
    // Sammle Ajax Objekte aller Verweis Baeume
    var verwBaeumeAjaxCalls = [];
    
    $(".kk_bearbeiten_verweise_baum").each(function() {
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
        $(".kk_bearbeiten_verweise_baum input[type='checkbox']").each(function(index, elem) {
            var verwBaum = $(elem).parents(".kk_verweise_baum");
            var verwTyp = verwBaum.attr("id").split("_")[4];
            syncCheckboxWithArray(verwTyp, $(elem).data("kkid"), $(elem));
        });
        // Change Handler fuer die Checkboxes der Wurzel-Ebene
        $(".kk_bearbeiten_verweise_baum input[type='checkbox']").change(function(e) {
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
	function submitFkt()
	{
        // Wenn die KK zu einer Ueberschrift gemacht wurde, einfach Inhaltstext = Leerstring
    	var text = $("#kk_jetztNeuesKapitel_b").prop("checked") ? "" : $("#kk_bearbeiten_TA").val().trim();
    	var titel = $("#kk_bearbeiten_titel_input").val().trim();
    	var attributes = getSelectedKkBAttributes();
    	var id = kkJSON[paramId];
    	// Fehler abfangen
    	if(titel=="")
    	{
    		showError("Bitte geben sie ihrer Karteikarte einen Titel.");
    		return false;
    	}
    	else if(text == "" &&  kkBearbeitenDropZone.files.length == 0 && !$("#kk_jetztNeuesKapitel_b").prop("checked"))
    	{
            showError("Bitte füllen sie ihre Karteikarte mit einem Inhalt.");
    		return false;
    	}
    	else if(uploadInProgress)
		{
    		showError("Bitte warten Sie bis Ihre Datei hochgeladen wurde.");
    		return false;
		}
    	else
    	{
    		processKKbearbeiten(id,text,titel,attributes,
    		        verweisVoraussetzungArr, verweisWeiterfuehrendArr, verweisUebungArr, verweisSonstigesArr);
    		return true;
    	}
    }
    
	/**
	 * Raeumt das Popup beim Schliessen auf.
	 */
    function clearFkt()
    {
    	$("#kk_jetztNeuesKapitel_b").off();
		$("#kk_jetztNeuesKapitel_b").prop("checked",false);
    	$("#kk_bearbeiten_text_area").show();
		$("#df_area_kk_b").show();
        $("#kk_bearbeiten_popup input[type='checkbox']").prop("checked",false);
		$("#kk_bearbeiten_content").show();
    	$("#kk_bearbeiten_titel_input").val("");
    	$("#kk_bearbeiten_TA").val("");
    	kkBearbeitenDropZone.removeAllFiles(true);
        $("#kk_bearbeiten_popup .drop_file_areas").removeClass("dropped");
        // Zerstoere Verweis Baeume mit allen Handlern
        $("#kk_bearbeiten_popup").find(".kk_verweise_baum").empty();
        
    }

    // Popup konfigurieren und anzeigen
    kkBearbeitenPopup.setCloseFkt(clearFkt);
    kkBearbeitenPopup.setSubmitFkt(submitFkt);
    kkBearbeitenPopup.show();
    
    /**
     * @returns true falls Attribute gewaehlt wurden
     */
    function isAnyAttrSelected()
    {
    	var isTrue = false;
    	$("#kkB_attributes input[type='checkbox']").each(function(i, elem) {
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
    function getSelectedKkBAttributes()
    {
    	var str = ""
    	$("#kkB_attributes input[type='checkbox']").each(function(i, elem) {
    		str = str + $(elem).prop("checked") + ",";
    	});
    	str = str.substring(0, str.length - 1);
    	return str;
    }
    
    // ckEditor initialisieren
	try
	{
		$("#kk_bearbeiten_TA").ckeditor(ckEditorVnErstellenConfig);
	}
    catch(e)
    {
    	console.log(e);
    }
}


/**
 * Wird aufgerufen wenn der Karteikarte bearbeiten Dialog mit fehlerfreien Eingaben abgesendet wird.
 * Als Inhalt der Karteikarte wird entweder der uebergebene Text genommen oder (wenn Leerstring),
 * die im Upload Feld ausgewaehlte Datei.
 * Startet das Senden der Daten an den Server.
 * @param id
 * @param text
 * @param titel
 * @param attributes
 * @param verweisVoraussetzungArr
 * @param verweisWeiterfuehrendArr
 * @param verweisUebungArr
 * @param verweisSonstigesArr
 */
function processKKbearbeiten(id,text,titel,attributes,
        verweisVoraussetzungArr, verweisWeiterfuehrendArr, verweisUebungArr, verweisSonstigesArr)
{
	var params = {};
	params[paramTitel] = titel;
	params[paramId] = id;
	params[paramInhalt] = text;
	params[paramAttribute] = attributes;
	params[paramVeranstaltung] = veranstaltungsObject[paramId];
    params[paramVerweisVoraussetzung] = verweisVoraussetzungArr;
    params[paramVerweisWeiterfuehrend] = verweisWeiterfuehrendArr;
    params[paramVerweisUebung] = verweisUebungArr;
    params[paramVerweisSonstiges] = verweisSonstigesArr;
    if(kkBearbeitenDropZone.files.length==0 || $("#kk_jetztNeuesKapitel_b").prop("checked")){
		params[paramType] = "";
		params[paramKkUploadID] = -1;
    }
    else if(kkBearbeitenDropZone.files[0].fromServer)
    {
		params[paramType] = "doNothing";
		params[paramKkUploadID] = -1;
	}
    else
	{
		params[paramType] = UPLOADTYPE;
		params[paramKkUploadID] = UPLOADIDSET;
	}
	submitEditKarteikarte(params)
}

/**
 * Sendet den Ajax Call zum Karteikarte Bearbeiten ab
 * @param params
 */
function submitEditKarteikarte(params)
{
    var ajax = ajaxCall(karteikartenServlet,
            actionBearbeiteKarteikarte,
            function(response) {
                showInfo("Karteikarte \""+ params[paramTitel] +"\" wurde erfolgreich bearbeitet.");
                displayKarteikarte(params[paramId], undefined, true);
                $.when(vnInhaltsverzeichnis.init()).done(function(){
                    vnInhaltsverzeichnis.showEintrag(response[paramId]);
                });
            },
            params
        );
}