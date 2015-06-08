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

function kkBearbeiten(kkJSON)
{
	$("#df_area_kk_b").find("#kk_b_BildPreviewTemplate").remove();
	$("#df_area_kk_b").find(".dz-message").show();
	appendDropzone();
	$("#kk_jetztNeuesKapitel ~ label").html("Diese Karteikarte als Überschrift verwenden.");
	$("#kk_jetztNeuesKapitel").off();
	$("#kk_jetztNeuesKapitel").change(function(e) {
    	if($("#kk_jetztNeuesKapitel").prop("checked")==true)
    	{
    		$("#kk_bearbeiten_content").hide();
//    		$("#kk_bearbeiten_text_area").hide();
//    		$("#df_area_kk_b").hide();
    		$("#kk_jetztNeuesKapitel ~ label").append(
    		        "<span style='color:yellow'><br><span class='octicon octicon-alert'></span> Inhalt geht verloren</span>");
    	}
    	else
    	{
    		$("#kk_bearbeiten_content").show();
//			$("#kk_bearbeiten_text_area").show();
//			$("#df_area_kk_b").show();
		    $("#kk_jetztNeuesKapitel ~ label").html("Diese Karteikarte als Überschrift verwenden.");
    	}
		
    });
	
	// Attribute anhaken
	$("#kkB_attributes input[type='checkbox']").each(function(i, elem) {
		if(kkJSON[paramAttribute][i])
		{
		    $(elem).prop("checked", true);
		}
	});
	
	$("#kk_bearbeiten_titel_input").val(kkJSON[paramTitel]);
	$("#kk_bearbeiten_TA").val(kkJSON[paramInhalt]);
	if(kkJSON[paramType]=="TEXT")
	{
		if(kkJSON[paramInhalt]==""){		//überschrift
			$("#kk_jetztNeuesKapitel").prop("checked",true);
//			$("#kk_bearbeiten_text_area").hide(0);
//			$("#df_area_kk_b").hide(0);
    		$("#kk_bearbeiten_content").hide();
		}
		else{							//textkarteikarte
			$("#kk_jetztNeuesKapitel").prop("checked",false);
//			$("#kk_bearbeiten_text_area").show(0);
//			$("#df_area_kk_b").show(0);
    		$("#kk_bearbeiten_content").show();
		}
	}
	else if(kkJSON[paramType]=="BILD")
	{
		var xhr = $.ajax({
			  type: "HEAD",
			  url: "files/images/"+kkJSON[paramId]+".png",
			  success: function(msg){
				$("#df_area_kk_b").find(".dz-message").hide(0);
			    size = String(xhr.getResponseHeader('Content-Length')/1000);
			    size = myRound(size,2);
			    domElem = $("#kk_b_BildPreviewTemplate").clone();
			    domElem.find(".dz-size").remove();
			    domElem.find(".dz-size").children().first().html("<strong>"+size+"</strong> KB");
			    domElem.css("display","block");
			    
			    name = kkJSON[paramId]+".png";
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
	else if(kkJSON[paramType]=="VIDEO")
	{
		var xhr = $.ajax({
			  type: "HEAD",
			  url: "files/videos/"+kkJSON[paramId]+".mp4",
			  success: function(msg){
				$("#df_area_kk_b").find(".dz-message").hide(0);
			    size = String(xhr.getResponseHeader('Content-Length')/1000);
			    size = myRound(size,2);
			    domElem = $("#kk_b_BildPreviewTemplate").clone();
			    domElem.find(".dz-size").remove();
			    domElem.find(".dz-size").children().first().html("<strong>"+size+"</strong> KB");
			    domElem.css("display","block");
			    
			    name = kkJSON[paramId]+".mp4";
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
	else
	{
		showError("Interner Fehler beim Laden der Karteikarte. Wenden sie sich an einen Administrator.")
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
    
	function submitFkt()
	{
        // Wenn die KK zu einer Ueberschrift gemacht wurde, einfach Inhaltstext = Leerstring
    	var text = $("#kk_jetztNeuesKapitel").prop("checked") ? "" : $("#kk_bearbeiten_TA").val().trim();
    	var titel = $("#kk_bearbeiten_titel_input").val().trim();
    	var attributes = getSelectedKkBAttributes();
    	var id = kkJSON[paramId];

    	if(titel=="")
    	{
    		showError("Bitte geben sie ihrer Karteikarte einen Titel.");
    		return false;
    	}
    	else if(text == "" && UPLOADIDSET == -1 && !$("#kk_jetztNeuesKapitel").prop("checked"))
    	{
            showError("Bitte füllen sie ihre Karteikarte mit einem Inhalt.");
    		return false;
    	}
    	else
    	{
    		processKKbearbeiten(id,text,titel,attributes,
    		        verweisVoraussetzungArr, verweisWeiterfuehrendArr, verweisUebungArr, verweisSonstigesArr);
    		return true;
    	}
    }
    
    function clearFkt()
    {
    	$("#kk_jetztNeuesKapitel").off();
    	$("#kk_bearbeiten_text_area").show();
		$("#df_area_kk_b").show();
        $("#kk_bearbeiten_popup input[type='checkbox']").prop("checked",false);
    	$("#kk_bearbeiten_titel_input").val("");
    	$("#kk_bearbeiten_TA").val("");
    	myDropzone.removeAllFiles(true);
        // Zerstoere Verweis Baeume mit allen Handlern
        $("#kk_bearbeiten_popup").find(".kk_verweise_baum").empty();
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
    
    function getSelectedKkBAttributes()
    {
    	var str = ""
    	$("#kkB_attributes input[type='checkbox']").each(function(i, elem) {
    		str = str + $(elem).prop("checked") + ",";
    	});
    	str = str.substring(0, str.length - 1);
    	return str;
    }
    
	try
	{
		var kk_ck_editor = $("#kk_bearbeiten_TA").ckeditor(ckEditorVnErstellenConfig);
	}
    catch(e)
    {
    	console.log(e);
    }
    
	
}

function appendDropzone(){
	try
	{
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
	       	    	if(ext=="png"||ext=="jpg"||ext=="jpeg"){
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
	       	    	$("#df_area_kk_b").find(".dz-message").hide();
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
	       		    	$("#cke_kk_erstellen_TA").hide();
	           	    	UPLOADIDSET = data.strResult;
	           	    	console.log("Zurückgegebene UploadID:"+UPLOADIDSET);
	       		    }
	       	    	uploadFile(file, successFkt, uploadAction, params) 
	       	    });
	       	    
	       	    this.on("removedfile", function(file) {
	       	    	$("#df_area_kk_b").find(".dz-message").show();
	       	    	$("#cke_kk_erstellen_TA").show();
	       	    	UPLOADTYPE = "";
	       	    	UPLOADIDSET = -1;
	       	    });
	       	    
       	  }
        }).get(0).dropzone;
		$( "#file-dropzone" ).droppable();
    }
    catch(e)
    {
    	myDropzone.removeAllFiles(true);
    }
}

function processKKbearbeiten(id,text,titel,attributes,
        verweisVoraussetzungArr, verweisWeiterfuehrendArr, verweisUebungArr, verweisSonstigesArr)
{
	var params = {};
	params[paramTitel] = titel;
	params[paramId] = id;
	params[paramInhalt] = text;
	params[paramAttribute] = attributes;
	params[paramType] = UPLOADTYPE;
	params[paramKkUploadID] = UPLOADIDSET;
	params[paramVeranstaltung] = veranstaltungsObject[paramId];
    params[paramVerweisVoraussetzung] = verweisVoraussetzungArr;
    params[paramVerweisWeiterfuehrend] = verweisWeiterfuehrendArr;
    params[paramVerweisUebung] = verweisUebungArr;
    params[paramVerweisSonstiges] = verweisSonstigesArr;
	submitEditKarteikarte(params)
}

function submitEditKarteikarte(params)
{
    console.log("Sende AJAX: Param Inhalt = " + params[paramInhalt]);
    var ajax = ajaxCall(karteikartenServlet,
            actionBearbeiteKarteikarte,
            function(response) {
                showInfo("Karteikarte \""+ params[paramTitel] +"\" wurde erfolgreich bearbeitet.");
                displayKarteikarte(params[paramId], null, true);
                initInhaltsverzeichnis();
            },
            params
        );
}