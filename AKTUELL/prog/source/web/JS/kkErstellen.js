var UPLOADIDSET = -1;
var UPLOADTYPE ="";


	
Dropzone.autoDiscover = false;
var UPLOAD_ID;
function newKarteikarte(triggerElem){
	var vater = findVater(triggerElem);
	// Spezialfall ganz oben im Baum
	if(vater == undefined)
		vater = veranstaltungsObject[paramErsteKarteikarte];
	
	var bruder = findBruder(triggerElem);
	try{
		var kk_ck_editor = $("#kk_erstellen_TA").ckeditor(ckEditorVnErstellenConfig);
	}
    catch(e){
    	console.log(e);
    }
    
    $("#kk_neuesKapitel").change(function(e){
    	if($("#kk_neuesKapitel").prop("checked")==true){
    		$("#cke_kk_erstellen_TA").slideUp(300);
    		$("#df_area_kk").fadeOut(300);
    	}
    	else{
			//TODO CKeditor bleibt immer weg
    		// Prüfen ob == statt === das problem behoben hat.
    		if($(".dz-filename").length==0){
    			$("#cke_kk_erstellen_TA").slideDown(300);
        		$("#df_area_kk").fadeIn(300);
    		}
    		else{
    			$("#df_area_kk").fadeIn(300);
    		}
    		
    	}
		
    });
    
    
    // KK Verweis Baeume initialisieren
    $(".kk_erstellen_verweise_baum").each(function() {
        ladeKindKarteikarten(
                veranstaltungsObject[paramErsteKarteikarte], 
                $(this), 
                false, 
                function() {
                    // kk knoten clicked
                }, 
                true
        );
    });
    
    submitFkt = function() {
    	var text = $("#kk_erstellen_TA").val().trim();
    	var titel = $("#kk_erstellen_titel_input").val().trim();
    	var attributes = getSelectedKkAttributes();

    	if(titel==""){
    		showError("Bitte geben sie ihrer Karteikarte einen Titel.");
    		return false;
    	}
    	else if($("#kk_neuesKapitel").prop("checked")){
    		var params = {};
    		params[paramTitel] = titel;
    		params[paramVeranstaltung] = veranstaltungsObject[paramId];
    		params[paramVaterKK] = vater;
    		params[paramAttribute] = attributes;
    		params[paramBruderKK] = bruder;
    		submitNewUeberschriftKarteikarte(params);
    		return true;
    	}
    	else if(text == "" && UPLOADIDSET == -1){
    		showError("Bitte füllen sie ihre Karteikarte mit einem Text aus oder laden sie ein Bild/Video hoch.");
    		return false;
    	}
    	else if(isAnyAttrSelected() == false){
    		sindSieSicher($("#kk_erstellen_ok"), "Wollen sie eine Karteikarte ohne Attribute erstellen?",  function(){
        		processKKerstellen(text,titel,attributes, bruder, vater);
        		return true;
    		}, 0, 0);
    		return false;
    	}
    	else{
    		processKKerstellen(text,titel,attributes, bruder, vater);
    		return true;
    	}
    }
    
    clearFkt = function(){
    	$("#kk_neuesKapitel").unbind("change");
    	$("#kk_neuesKapitel").prop("checked",false);
    	$("#cke_kk_erstellen_TA").fadeIn(0);
		$("#df_area_kk").fadeIn(0);
    	$(".checkboxes").prop("checked",false);
    	$("#kk_erstellen_titel_input").val("");
    	$("#kk_erstellen_TA").val("");
    }
    
    popupFenster(
        $("#kk_erstellen_popup_overlay"), 
        [$('#kk_erstellen_popup_close'),$("#kk_erstellen_cancel")],
        clearFkt,
        $("#kk_erstellen_ok"),
        submitFkt,
        $("#kk_erstellen_titel_input"),
        $("#kk_erstellen_weiter"),
        $("#kk_erstellen_zurueck")
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
    
	function processKKerstellen(text,titel,attributes, bruder, vater){
		var params = {};
		params[paramTitel] = titel;
		params[paramVeranstaltung] = veranstaltungsObject[paramId];
		params[paramInhalt] = text;
		params[paramAttribute] = attributes;
		params[paramType] = UPLOADTYPE;
		params[paramVaterKK] = vater;
		params[paramBruderKK] = bruder;
		params[paramKkUploadID] = UPLOADIDSET;
		submitNewKarteikarte(params)
	}
    function isAnyAttrSelected(){
    	var isTrue = false;
    	$("#kkE_attributes").children().filter("span").children().filter("input").each(function(i){
    		if($(this).prop("checked")==true){
    			isTrue = true;
    			return false;
    		}
    	});
    	return isTrue;
    }
    function getSelectedKkAttributes(){
    	var str = ""
    	$("#kkE_attributes").children().filter("span").children().filter("input").each(function(i){
    		str = str + $(this).prop("checked")+",";
    	});
    	str =str.substring(0, str.length - 1);
    	return str;
    }
    
    function submitNewKarteikarte(params){
    	var ajax = ajaxCall(karteikartenServlet,
    			actionErstelleKarteikarte,
                function(response) {
                    showInfo("Karteikarte \""+ params[paramTitel] +"\" wurde erfolgreich erzeugt.");
                    $("#kk_erstellen_popup_overlay").fadeOut(110);
                    // Wir bekommen die eingefügte id zurück
                    displayKarteikarte(response[paramId]);
                    initInhaltsverzeichnis();
                },
                params
            );
    }
    function submitNewUeberschriftKarteikarte(params){
    	var ajax = ajaxCall(karteikartenServlet,
    			actionErstelleUeberschrift,
                function(response) {
                    showInfo("Übergeordnetes Kapitel \""+ params[paramTitel] +"\" wurde erfolgreich erzeugt.");
                    // Wir bekommen die eingefügte id zurück
                    displayKarteikarte(response[paramId]);
                    initInhaltsverzeichnis();
                },
                params
            );
    }
    
    
    try{
		myDropzone = $("#df_area_kk").dropzone({
        url: actionUploadKKBild,
        maxFilesize: 1,
        acceptedFiles: ".jpeg,.jpg,.png,.mp4",
        autoDiscover :false,
        autoProcessQueue:false,
        createImageThumbnails: true,
        uploadMultiple: false,
        addRemoveLinks: true,
            init: function() {
            	var myDropzone = this;
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
       	    	$("#dz_info_message").hide(0);
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
       	    	$("#dz_info_message").show(0);
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

function findVater(elem){
	maybeNode = elem.parent().parent().parent().attr("id")
	if(maybeNode === "kk_inhaltsverzeichnis"){
		console.log("Vater ist VeranstaltungsKK (erste):"+veranstaltungsObject[paramErsteKarteikarte]);
		return veranstaltungsObject[paramErsteKarteikarte];
	}
	else{
		maybeNode = elem.parent().parent().prev().data("kkid");
		console.log("Vater ist eine andere Karteikarte:"+maybeNode);
		return maybeNode;
	}
	
}

function findBruder(elem){
	elemBefore = elem.parent().prev();
	if(elemBefore.length < 1){ //no brother before it, then -1
		console.log("Kein Bruder gefunden. Daher -1");
		return -1;
	}
	else{
		id = elemBefore.children().first().data("kkid");
		console.log("Vorangehender Bruder gefunden: "+id);
		return id
	}
	//2.:
//	elem.parent().prev().children().first().data("kkid");
}

function isImage(filename) {
    var ext = getExtension(filename);
    switch (ext.toLowerCase()) {
    case 'jpg':
    case 'png':
        //etc
        return true;
    }
    return false;
}

function isVideo(filename) {
    var ext = getExtension(filename);
    switch (ext.toLowerCase()) {
    case 'mp4':
        // etc
        return true;
    }
    return false;
}
