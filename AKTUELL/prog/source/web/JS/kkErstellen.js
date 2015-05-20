$(document).ready(function() {
	Dropzone.autoDiscover = false;
	var UPLOAD_ID;
    $('#kk_erstellen_bt').click(function() {
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
            $("#kk_erstellen_ok"),
            function() {
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
                params[paramTitel] = escape(titel);
                params[paramSemester] = escape(semester);
                params[paramStudiengang] = "";
                
                for(var i in selectedStudiengaenge)
            	{
                	params[paramStudiengang] += selectedStudiengaenge[i][paramStudiengang] + ",";
            	}
                	
                params[paramBeschr] = escape(beschr);
                params[paramModeratorKkBearbeiten] = moderatorenKkBearbeiten;
                params[paramKommentareErlauben] = kommentareErlaubt;
                params[paramBewertungenErlauben] = bewertungenErlaubt;
                params[paramPasswort] = escape(passw);
                params[paramModeratoren] = moderatorenIDs;
                
                var ajax = ajaxCall(veranstaltungServlet,
                        actionErstelleVeranst,
                        function(response) {
                            showInfo("Veranstaltung \""+ titel +"\"wurde erfolgreich erzeugt.");
                            fillVeranstaltungsliste();  
                        },
                        params
                    );
                return true;
            },
            $("#vn_erstellen_titel_input"),
            $("#vn_erstellen_weiter"),
            $("#vn_erstellen_zurueck")
        );
        $("#cke_kk_erstellen_TA").show();
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
           		    	$("#cke_kk_erstellen_TA").hide(0);
               	    	console.log(data);
           		    }
           	    	uploadFile(file, successFkt, uploadAction, params, function(){}, function(){}) 
           	    });
           	    this.on("removedfile", function(file) {
           	    	$("#dz_info_message").show(0);
           	    	$("#cke_kk_erstellen_TA").show(0);
           	    });
           	  }
            }).get(0).dropzone;
        }
        catch(e){
        	myDropzone.removeAllFiles(true);
        }
    	
     	
     	
        
        
    });
    
});



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
