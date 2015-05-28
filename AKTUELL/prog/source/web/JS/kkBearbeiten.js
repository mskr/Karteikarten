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
		UPLOADTYPE = "png";
		UPLOADIDSET = json[paramId];
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
		UPLOADTYPE = "mp4";
		UPLOADIDSET = json[paramId];
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
    		params[paramId] = kkjson[paramId];
    		params[paramAttribute] = attributes;
    		params[paramBruderKK] = bruder;
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
       	    	clone = $(".dz-size");
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
                displayKarteikarte(params[paramId]);
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
	submitEditKarteikarte(params)
}

function submitEditUeberschriftKarteikarte(params){
	var ajax = ajaxCall(karteikartenServlet,
			actionBearbeiteUeberschrift,
            function(response) {
                showInfo("Karteikarte \""+ params[paramTitel] +"\" wurde erfolgreich bearbeitet.");
                $("#kk_bearbeiten_popup_overlay").fadeOut(110);
                // Wir bekommen die eingefügte id zurück
                displayKarteikarte(params[paramId]);
                initInhaltsverzeichnis();
            },
            params
        );
}