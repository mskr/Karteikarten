var myDropzone;
var UPLOADIDSET = -1;
var UPLOADTYPE ="";
$(document).ready(function(){
	// Nicht hier sondern beim erstellen. Neue Karteikarten werden sonst nicht beachtet !
//	$(".KKbearbeiten").click(function(){
//		processKK_editClick($(this));
//	});
});

function processKK_editClick(triggerElem){
	id = triggerElem.parent().parent().parent().parent().data("kkid");
	var params = {};
    params[paramId] = id;
    karteikarteJSON = {};
    return ajaxCall(karteikartenServlet, actionGetKarteikarteByID, 
        function(response) {
			karteikarteJSON = response;
			fillKKbearbeitenTemplateWith(karteikarteJSON);
		},
        params
    );
}
function fillKKbearbeitenTemplateWith(json){
	$("#df_area_kk_b").find("#kk_b_BildPreviewTemplate").remove();
	$("#df_area_kk_b").find(".dz-message").show(0);
	appendDropzone();
	$("#kk_jetztNeuesKapitel").unbind("change");
	$("#kk_jetztNeuesKapitel").change(function(e){
    	if($("#kk_jetztNeuesKapitel").prop("checked")==true){
    		$("#kk_bearbeiten_text_area").slideUp(300);
    		$("#df_area_kk_b").fadeOut(300);
    	}
    	else{
			$("#kk_bearbeiten_text_area").slideDown(300);
			$("#df_area_kk_b").fadeIn(300);
    	}
		
    });
	counter = 0;
	$("#kkB_attributes").children().each(function(){			//fill with attributes...
		if(isEven(counter)){
//			console.log("even");
//			console.log($(this).children().first());
			if(json[paramAttribute][counter/2]===true){
				$(this).children().first().prop("checked",true);
			}
			else{
				$(this).children().first().prop("checked",false);
			}
		}
		counter++;
		
	});
	
	console.log(json);
	$("#kk_bearbeiten_titel_input").val(json[paramTitel]);
	if(json[paramType]=="TEXT"){
		if(json[paramInhalt]==""){		//überschrift
			$("#kk_jetztNeuesKapitel").prop("checked",true);
			$("#kk_bearbeiten_text_area").hide(0);
			$("#df_area_kk_b").hide(0);
		}
		else{							//textkarteikarte
			$("#kk_jetztNeuesKapitel").prop("checked",false);
			$("#kk_bearbeiten_text_area").show(0);
			$("#df_area_kk_b").show(0);
			$("#kk_bearbeiten_TA").val(json[paramInhalt]);
		}
	}
	else if(json[paramType]=="BILD"){		//bildkarteikarte
		var xhr = $.ajax({
			  type: "HEAD",
			  url: "files/images/"+json[paramId]+".png",
			  success: function(msg){
				$("#df_area_kk_b").find(".dz-message").hide(0);
			    size = String(xhr.getResponseHeader('Content-Length')/1000);
			    size = myRound(size,2);
			    domElem = $("#kk_b_BildPreviewTemplate").clone();
			    domElem.find(".dz-size").children().first().html("<strong>"+size+"</strong> KB");
			    domElem.css("display","block");
			    
			    name = json[paramId]+".png";
			    domElem.find(".dz-filename").children().first().html(name);
			    domElem.find(".dz-image").show();
			    domElem.find(".dz-image").children().first().attr("src","files/images/"+name).css({height:"120px",width:"120px"});
			    
			    
			    if($("#df_area_kk_b").find("#kk_b_BildPreviewTemplate").length===0){
			    	$("#df_area_kk_b").append(domElem);
			    }
			    else{
			    	$("#kk_b_BildPreviewTemplate").replaceWith(domElem);
			    }
			    
			    $("#KKbearbeitenRemoveLink").one("click",function(){
			    	$("#kk_b_BildPreviewTemplate").remove();
			    	$("#kk_bearbeiten_text_area").slideDown(300);
			    	$("#df_area_kk_b").find(".dz-message").fadeIn(200);
			    });
			  }
			});
	}
	else if(json[paramType]=="VIDEO"){
		var xhr = $.ajax({
			  type: "HEAD",
			  url: "files/videos/"+json[paramId]+".mp4",
			  success: function(msg){
				$("#df_area_kk_b").find(".dz-message").hide(0);
			    size = String(xhr.getResponseHeader('Content-Length')/1000);
			    size = myRound(size,2);
			    domElem = $("#kk_b_BildPreviewTemplate").clone();
			    domElem.find(".dz-size").children().first().html("<strong>"+size+"</strong> KB");
			    domElem.css("display","block");
			    
			    name = json[paramId]+".mp4";
			    domElem.find(".dz-filename").children().first().html(name);
			    
			    domElem.find(".dz-image").hide();
			    
			    
			    if($("#df_area_kk_b").find("#kk_b_BildPreviewTemplate").length===0){
			    	$("#df_area_kk_b").append(domElem);
			    }
			    else{
			    	$("#kk_b_BildPreviewTemplate").replaceWith(domElem);
			    }
			    
			    $("#KKbearbeitenRemoveLink").one("click",function(){
			    	$("#kk_b_BildPreviewTemplate").remove();
			    	$("#kk_bearbeiten_text_area").slideDown(300);
			    	$("#df_area_kk_b").find(".dz-message").fadeIn(200);
			    });
			  }
			});
	}
	else{
		showError("Interner Fehler beim Laden der Karteikarte. Wenden sie sich an einen Administrator.")
	}
    
    //== Code fuer die Verweise START ==
    
    var verweisVoraussetzungArr = [];
    var verweisWeiterfuehrendArr = [];
    var verweisUebungArr = [];
    var verweisSonstigesArr = [];
    
    // Arrays mit aktuellen Verweisen fuellen
    for(var index in json[paramVerweise])
    {
        switch(json[paramVerweise][index][paramType].toLowerCase())
        {
            case paramVerweisVoraussetzung.toLowerCase():
                verweisVoraussetzungArr.push(json[paramVerweise][index][paramId]);
                break;
            case paramVerweisWeiterfuehrend.toLowerCase():
                verweisWeiterfuehrendArr.push(json[paramVerweise][index][paramId]);
                break;
            case paramVerweisUebung.toLowerCase():
                verweisUebungArr.push(json[paramVerweise][index][paramId]);
                break;
            case paramVerweisSonstiges.toLowerCase():
                verweisSonstigesArr.push(json[paramVerweise][index][paramId]);
        }
    }
    
    // Sammle Ajax Objekte aller Verweis Baeume
    var verwBaeumeAjaxCalls = [];
    
    $(".kk_bearbeiten_verweise_baum").each(function() {
        // Verweis Baeume initialisieren
        baumWurzelAjax = ladeKindKarteikarten(
                veranstaltungsObject[paramErsteKarteikarte], 
                $(this), 
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
                true
        );
        verwBaeumeAjaxCalls.push(baumWurzelAjax);
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
    
	displayKKtoEdit(json);
}
function displayKKtoEdit(KKjson){
	
	submitFkt = function(){
    	var text = $("#kk_bearbeiten_TA").val().trim();
    	var titel = $("#kk_bearbeiten_titel_input").val().trim();
    	var attributes = getSelectedKkBAttributes();
    	var id = KKjson[paramId];

    	if(titel==""){
    		showError("Bitte geben sie ihrer Karteikarte einen Titel.");
    		return false;
    	}
    	else if($("#kk_jetztNeuesKapitel").prop("checked")){
    		var params = {};
    		params[paramTitel] = titel;
    		params[paramId] = KKjson[paramId];
    		params[paramAttribute] = attributes;
    		params[paramType] = "TEXT";
    		params[paramVeranstaltung] = veranstaltungsObject[paramId];
    		submitEditUeberschriftKarteikarte(params);
    		return true;
    	}
    	else if(text == "" && UPLOADIDSET == -1){
    		showError("Bitte füllen sie ihre Karteikarte mit einem Text aus oder laden sie ein Bild/Video hoch.");
    		return false;
    	}
    	else if(isAnyAttrSelected() == false){
    		sindSieSicher($("#kk_erstellen_ok"), "Wollen sie eine Karteikarte ohne Attribute abspeichern?",  function(){
        		processKKbearbeiten(id,text,titel,attributes);
        		return true;
    		}, 0, 0);
    		return false;
    	}
    	else{
    		processKKbearbeiten(id,text,titel,attributes);
    		return true;
    	}
    }
    
    clearFkt = function(){
    	$("#kk_jetztNeuesKapitel").unbind("change");
    	$("#kk_jetztNeuesKapitel").prop("checked",false);
    	$("#cke_kk_b_erstellen_TA").fadeIn(0);
		$("#df_area_kk_b").fadeIn(0);
    	$(".checkboxes").prop("checked",false);
    	$("#kk_bearbeiten_titel_input").val("");
    	$("#kk_bearbeiten_TA").val("");
        // Zerstoere Verweis Baeume mit allen Handlern
        $("#kk_bearbeiten_popup").find(".kk_verweise_baum").remove();
    }
	
	popupFenster(
            $("#kk_bearbeiten_popup_overlay"), 
            [$('#kk_bearbeiten_popup_close'),$("#kk_bearbeiten_cancel")],
            clearFkt,
            $("#kk_bearbeiten_ok"),
            submitFkt,
            $("#kk_bearbeiten_titel_input"),
            $("#kk_bearbeiten_weiter"),
            $("#kk_bearbeiten_zurueck")
        );
	
	
	$(".checkbox_labels").unbind("click");
    $(".checkbox_labels").click(function(){
    	if($(this).siblings().first().prop("checked")==true ){
    		$(this).siblings().first().prop("checked",false)
    	}
    	else{
    		$(this).siblings().first().prop("checked",true);
    	}
    	
    });
    
    function isAnyAttrSelected(){
    	var isTrue = false;
    	$("#kkB_attributes").children().filter("span").children().filter("input").each(function(i){
    		if($(this).prop("checked")==true){
    			isTrue = true;
    			return false;
    		}
    	});
    	return isTrue;
    }
    function getSelectedKkBAttributes(){
    	var str = ""
    	$("#kkB_attributes").children().filter("span").children().filter("input").each(function(i){
    		str = str + $(this).prop("checked")+",";
    	});
    	str =str.substring(0, str.length - 1);
    	return str;
    }
	try{
		var kk_ck_editor = $("#kk_bearbeiten_TA").ckeditor(ckEditorVnErstellenConfig);
	}
    catch(e){
    	console.log(e);
    }
    
	
}
function appendDropzone(){
	try{
		myDropzone = $("#df_area_kk_b").dropzone({
        url: actionUploadKKBild,
        maxFilesize: 1,
        acceptedFiles: ".jpeg,.jpg,.png,.mp4",
        autoDiscover :false,
        autoProcessQueue:false,
        createImageThumbnails: true,
        uploadMultiple: false,
        addRemoveLinks: true,
            init: function() {
            	myDropzone = this;
           	 	$(".dz-error-message").remove();
       	    this.on("addedfile", function(file) {
       	    	var ext = file.name.substr(file.name.lastIndexOf('.') + 1);
       	    	console.log(ext);
       	    	uploadAction = false;
       	    	if(ext=="png"||ext=="jpg"){
       	    		uploadAction = actionUploadKKBild;
       	    	}
       	    	else if(ext=="mp4"){
       	    		uploadAction = actionUploadKKVideo;
       	    	}
       	    	else{
       	    		showError("Bitte verwenden sie für Bilder .png oder .jpg und für Videos .mp4 als Format!");
       	         	myDropzone.removeAllFiles(true);
       	    		return false;
       	    	}
       	    	var params = {};
       	    	$("#df_area_kk_b").find(".dz-message").hide(0);
       	    	$(".dz-preview").addClass("center");
       	    	$(".dz-error-message").remove();
       	    	$(".dz-error-mark").remove();
       	    	$(".dz-success-mark").remove();
       	    	$(".dz-preview").css("margin","40px");
       	    	clone = $(".dz-size").first();
       	    	$(".dz-size").remove();
       	    	$(".dz-details").append(clone);
       		    function successFkt(data){
       		    	UPLOADTYPE = ext;
       		    	$("#cke_kk_erstellen_TA").hide(0);
           	    	UPLOADIDSET = data.strResult;
           	    	console.log("Zurückgegebene UploadID:"+UPLOADIDSET);
       		    }
       	    	uploadFile(file, successFkt, uploadAction, params, function(){}, function(){}) 
       	    });
       	    this.on("removedfile", function(file) {
       	    	$("#df_area_kk_b").find(".dz-message").show(0);
       	    	$("#cke_kk_erstellen_TA").show(0);
       	    	UPLOADTYPE = "";
       	    	UPLOADIDSET = -1;
       	    });
       	  }
        }).get(0).dropzone;
		$( "#file-dropzone" ).droppable();
    }
    catch(e){
    	myDropzone.removeAllFiles(true);
    }


}
function submitEditKarteikarte(params){
	var ajax = ajaxCall(karteikartenServlet,
			actionBearbeiteKarteikarte,
            function(response) {
                showInfo("Karteikarte \""+ params[paramTitel] +"\" wurde erfolgreich bearbeitet.");
                $("#kk_bearbeiten_popup_overlay").fadeOut(110);
                // Wir bekommen die eingefügte id zurück
                displayKarteikarte(params[paramId], null, true);
                initInhaltsverzeichnis();
            },
            params
        );
}
function processKKbearbeiten(id,text,titel,attributes){
	var params = {};
	params[paramTitel] = titel;
	params[paramId] = id;
	params[paramInhalt] = text;
	params[paramAttribute] = attributes;
	params[paramType] = UPLOADTYPE;
	params[paramKkUploadID] = UPLOADIDSET;
	params[paramVeranstaltung] = veranstaltungsObject[paramId];

	submitEditKarteikarte(params)
}

function submitEditUeberschriftKarteikarte(params){
	var ajax = ajaxCall(karteikartenServlet,
			actionBearbeiteUeberschrift,
            function(response) {
                showInfo("Karteikarte \""+ params[paramTitel] +"\" wurde erfolgreich bearbeitet.");
                $("#kk_bearbeiten_popup_overlay").fadeOut(110);
                // Wir bekommen die eingefügte id zurück
                displayKarteikarte(params[paramId], null, true);
                initInhaltsverzeichnis();
            },
            params
        );
}