var UPLOADIDSET = -1;
var UPLOADTYPE ="";


	
	Dropzone.autoDiscover = false;
	var UPLOAD_ID;
    function newKarteikarte(triggerElem){
    	var vater = findVater(triggerElem);
    	var bruder = findBruder(triggerElem);
    	try{
    		var kk_ck_editor = $("#kk_erstellen_TA").ckeditor(ckEditorVnErstellenConfig);
    	}
        catch(e){
        	console.log(e);
        }

        popupFenster(
            $("#kk_erstellen_popup_overlay"), 
            [$('#kk_erstellen_popup_close'),$("#kk_erstellen_cancel")],
            function() {},
            $(""),
            function() {
            },
            $(""),
            $(""),
            $("")
        );
        $("#cke_kk_erstellen_TA").show();
        $("#kk_erstellen_weiter").click(function(){
        	$("#popup_fenster_body1").hide(0);
        	$("#popup_fenster_body2").show(0);
        });
        $("#kk_erstellen_zurueck").click(function(){
        	$("#popup_fenster_body1").show(0);
        	$("#popup_fenster_body2").hide(0);
        });
        $(".checkbox_labels").click(function(){
        	if($(this).siblings().first().prop("checked")==true ){
        		$(this).siblings().first().prop("checked",false)
        	}
        	else{
        		$(this).siblings().first().prop("checked",true);
        	}
        	
        });
        $("#kk_erstellen_ok").click(function(){
        	var text = $("#kk_erstellen_TA").val().trim();
        	var titel = $("#kk_erstellen_titel_input").val().trim();
        	var attributes = getSelectedKkAttributes();
        	console.log(getSelectedKkAttributes());
        	if(text == ""&& UPLOADIDSET == -1){
        		showError("Bitte füllen sie ihre Karteikarte mit Text oder laden sie ein Bild/Video hoch.");
        		return false;
        	}
        	if(titel==""){
        		showError("Bitte geben sie ihrer Karteikarte einen Titel.");
        		return false;
        	}
        	if(isAnyAttrSelected() == false){
        		sindSieSicher($("#kk_erstellen_ok"), "Wollen sie eine Karteikarte ohne Attribute erstellen?",  processKKerstellen, 0, 0)
        	}
        	else{
        		processKKerstellen(text,titel,attributes, bruder, vater);
        	}
        	return false;
   
        });
        

    	function processKKerstellen(text,titel,attributes, bruder, vater){
    		var params = {};
    		params[paramTitel] = escape(titel);
    		params[paramVeranstaltung] = veranstaltungsObject[paramId];
    		params[paramInhalt] = escape(text);
    		params[paramAttribute] = escape(attributes);
    		params[paramType] = escape(UPLOADTYPE);
    		params[paramVaterKK] = vater;
    		params[paramBruderKK] = bruder;
    		params[paramKkUploadID] = escape(UPLOADIDSET);	
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
                        showInfo("Karteikarte \""+ titel +"\"wurde erfolgreich erzeugt.");
                     //   fillVeranstaltungsliste();  
                    },
                    params
                );
        }
        
        
        
        
        
        
        
        
        
        
        try{
    		myDropzone = $("#file-dropzone").dropzone({
            url: actionUploadKKBild,
            acceptedFiles: "image/jpeg,image/png,image/gif,video/mp4",
            clickable: true,
            maxFilesize: 1,
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
               	    	console.log(data);
               	    	UPLOADIDSET = data;
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
	elem.parent().prev().children().first().data("kkid");
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
