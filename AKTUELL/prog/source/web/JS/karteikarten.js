/**
 * @author mk
 */

function buildKarteikarte(karteikarteJson)
{
    var kkId = karteikarteJson[paramId],
        kkTitel = karteikarteJson[paramTitel],
        kkType = karteikarteJson[paramType],
        kkVeranstaltung = karteikarteJson[paramVeranstaltung],
        kkInhalt = karteikarteJson[paramInhalt],
        kkBewertung = karteikarteJson[paramBewertung],
        kkAenderungsdatum = karteikarteJson[paramAenderungsdatum];
    
    var kkDom = $("#templatekarteikarte").clone();
    kkDom.attr("id", "kk_" +kkId+"_wrapper");
    kkDom.show();
    
    if(kkType == paramKkText)
        kkDom.find(".kk_inhalt").addClass("inhalt_text");
    else if(kkType == paramKkBild)
        kkDom.find(".kk_inhalt").addClass("inhalt_bild");
    else if(kkType == paramKkVideo)
        kkDom.find(".kk_inhalt").addClass("inhalt_video");

    fillKarteiKarte(kkDom,karteikarteJson);
    
    // Notiz
    kkDom.find(".kk_notizen_body").ckeditor(function() {
    	// TODO FadeIn des Speichern buttons wenn sich der inhalt geändert hat.
    	setNotiz(kkDom, kkId)
	}, ckEditorNotizConfig);
    
    kkDom.find(".kk_notizen_foot").find(".mybutton").click(function(){
    	var params = {};
    	text = kkDom.find(".kk_notizen_body").val();
	    params[paramId] = kkId;
	    params[paramInhalt] = text;
		ajaxCall(
		    notizServlet,
		    actionSpeichereNotiz,
		    function(response) {
		    	showInfo("Notizen wurden gespeichert.");
		    },
		    params
		);
    });
    
    // Kommentare aufklappen
    kkDom.find(".kk_kommheader").click(function(){
    	kkDom.find(".kk_kommbody").slideToggle("slow");
    });
    
    // Neues Thema Handler
    kkDom.find(".antw").ckeditor(ckEditorKommentarConfig);
    
    kkDom.find(".komm_submit_bt").click(function(){
		if(kkDom.find(".antw").val().trim() == "")
		{
			showError("Der Text darf nicht leer sein!");
			return;
		}
		$.when(erstelleNeuesThemaKk(karteikarteJson[paramId], kkDom.find(".antw").val())).done(function(){
			kkDom.find(".antw").val("");
			 var params = {};
			    params[paramId] = kkId;
				ajaxCall(
				    kommentarServlet,
				    actionLeseThemaKommentar,
				    function(response) {
				    	arr = response[keyJsonArrResult];
				        showHauptKommentare(kkDom,arr);
				    },
				    params
				);
		});
	});
    
    
    // Kommentare laden und anzeigen
    var params = {};
    params[paramId] = kkId;
	ajaxCall(
	    kommentarServlet,
	    actionLeseThemaKommentar,
	    function(response) {
	    	arr = response[keyJsonArrResult];
	        showHauptKommentare(kkDom,arr);
	    },
	    params
	);
    return kkDom;
}

function fillKarteiKarte(domElem, json){
	//set Rating
	domElem.find(".kk_votestat").html(json[paramBewertung]);
	
	// detect type and add content
	switch (json[paramType]) {
    case "TEXT":
    	domElem.find(".inhalt_text").html(json[paramInhalt]);
    	break;
    case "BILD":
    	image = $(document.createElement("img"));
    	image.attr("src","files/images/"+json[paramId]+".png");
    	image.attr("onerror","this.src='files/general/default.png'");
    	domElem.find(".inhalt_bild").html(image);
    	break;
    case "VIDEO":
    	video = $(document.createElement("video"));
    	video.css("flex-shrink"," 0");
    	video.attr("autobuffer","");
//    	video.attr("autoplay",""); 
    	video.attr("controls","");
    	video.append("<source src='files/videos/"+json[paramId]+".mp4' type='video/mp4'></source>");
    	video.append("Your browser does not support the video tag.");
    	domElem.find(".inhalt_video").html(video);
    	break;
	}
}

function getKarteikarteByID(id){
	var params = {};
    params[paramId] = id;
    karteikarteJSON = {};
    return ajaxCall(karteikartenServlet, actionGetKarteikarteByID, 
        function(response) {
			karteikarteJSON = response;
//			console.log("getKarteikarte bekam folgenden json:");
//			console.log(karteikarteJSON);
//			buildKarteikarte(karteikarteJSON);
//			console.log(karteikarteJSON);
//			buildKarteikarte(karteikarteJSON); //just for test
		},
        params
    );
}

function setNotiz(kkDom, kkId)
{
	var params = {};
	text = kkDom.find(".kk_notizen_body").val();
    params[paramId] = kkId;
	return ajaxCall(
	    notizServlet,
	    actionLeseNotiz,
	    function(response) {
	    	kkDom.find(".kk_notizen_body").val(response[paramInhalt]);
	    },
	    params
	);
}