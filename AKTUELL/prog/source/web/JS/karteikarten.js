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
    kkDom.removeAttr("id");
    kkDom.attr("data-kkid", kkId);
    kkDom.show();

    fillKarteiKarte(kkDom,karteikarteJson);
    
    if(kkInhalt == "")
    	return kkDom;
    
    // Info
    kkDom.find(".kk_info_body_wrapper").hide();
    kkDom.find(".kk_info_head").click(function(){
    	kkDom.find(".kk_info_body_wrapper").slideToggle();
    });
    
    // Notiz
    kkDom.find(".kk_notizen_body_wrapper").hide();
    kkDom.find(".kk_notizen_head").click(function(){
    	kkDom.find(".kk_notizen_body_wrapper").slideToggle();
    });
    
    kkDom.find(".kk_notizen_body").ckeditor(function() {

    	// TODO Karteikarte setzen und DANACH change handler registireren
    	// Funktioniert irgendwie nicht. Handler wird gesetzt bevor Inhalt gesetzt wurde
    	var f = function(that){
    		that.on('change', function() {
            	kkDom.find(".kk_notizen_foot").slideDown();
            });
    	};
    	var f2 = function(that) {
    		$.when(setNotiz(kkDom, kkId)).done(function(){
        		f(that);
        	});
		};
    	f2(this);
    	
	}, ckEditorNotizConfig);
    // Voting
    if(karteikarteJson[paramHatGevoted] == true)
    {
    	kkDom.find(".kk_voteup").css("opacity","0.1");
    	kkDom.find(".kk_votedown").css("opacity","0.1");
    	kkDom.find(".kk_voteup").css("cursor","default");
    	kkDom.find(".kk_votedown").css("opacity","default");
    }
    else
    {
    	kkDom.find(".kk_voteup").click(function() {
			$.when(voteKkUp(karteikarteJson[paramId])).done(function()
				{
					doVoteKkGUI(kkDom, parseInt(kkDom.find(".kk_votestat").html())+1);
				});
		});
    	kkDom.find(".kk_votedown").click(function() {
			$.when(voteKkDown(karteikarteJson[paramId])).done(function()
				{
					doVoteKkGUI(kkDom, parseInt(kkDom.find(".kk_votestat").html())-1);
				});
		});
    }
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
	        
	// set Rating
	domElem.find(".kk_votestat").html(json[paramBewertung]);
	domElem.find(".kk_titel").text(json[paramTitel]);
	// Attribute setzen
	domElem.find(".kk_info_attr").text(createAttrStr(json[paramAttribute]));
	
	
	// detect type and add content
	switch (json[paramType]) {
    case paramKkText:
    	domElem.find(".kk_inhalt").addClass("inhalt_text");
    	if(json[paramInhalt].trim() == "")
    	{
    		domElem.find("div").hide();
    	}
    	else
    	{
    		domElem.find(".inhalt_text").html(json[paramInhalt]);
    	}
    	break;
    case paramKkBild:
    	domElem.find(".kk_inhalt").addClass("inhalt_bild");
    	image = $(document.createElement("img"));
    	image.attr("src","files/images/"+json[paramId]+".png");
    	image.attr("onerror","this.src='files/general/default.png'");
    	domElem.find(".inhalt_bild").html(image);
    	break;
    case paramKkVideo:
    	domElem.find(".kk_inhalt").addClass("inhalt_video");
    	video = $(document.createElement("video"));
    	video.css("width","100%");
    	video.attr("autobuffer","");
//    	video.attr("autoplay",""); 
    	video.attr("controls","");
    	video.append("<source src='files/videos/"+json[paramId]+".mp4' type='video/mp4'></source>");
    	video.append("Your browser does not support the video tag.");
    	domElem.find(".inhalt_video").html(video);
    	break;
	}
}
function createAttrStr(arrAttr){
	str="";
	strArr = [];
	if(arrAttr[0] == true)
		strArr.push("Satz");
	if(arrAttr[1] == true)
		strArr.push("Lemma");
	if(arrAttr[2] == true)
		strArr.push("Beweis");
	if(arrAttr[3] == true)
		strArr.push("Definition");
	if(arrAttr[4] == true)
		strArr.push("Wichtig");
	if(arrAttr[5] == true)
		strArr.push("Grundlage");
	if(arrAttr[6] == true)
		strArr.push("Zusatzinfo");
	if(arrAttr[7] == true)
		strArr.push("Exkurs");
	if(arrAttr[8] == true)
		strArr.push("Beispiel");
	if(arrAttr[9] == true)
		strArr.push("Übung");
	
	if(strArr.length == 0)
		strArr.push("Keine Attribute angegeben.");
	
	for(i in strArr)
	{
		str += strArr[i];
		if(i < strArr-1)
			str +=",";
	}
	return str;
}
function getKarteikarteByID(id){
	var params = {};
    params[paramId] = id;
    karteikarteJSON = {};
    return ajaxCall(karteikartenServlet, actionGetKarteikarteByID, 
        function(response) {
			karteikarteJSON = response;
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
	    	if(response[paramInhalt].trim() != "")
	    	{
	    		kkDom.find(".kk_notizen_head").append(" (!)");
	    	}
	    	else
    		{
//	    		kkDom.find(".kk_notizen_head").append("( gesetzt )");
    		}
	    },
	    params
	);
}
//Funktion für GUI der Votes
function doVoteKkGUI(domKomm, vote){
	domKomm.find(".kk_voteup").css("opacity","0.1");
	domKomm.find(".kk_votedown").css("opacity","0.1");
	domKomm.find(".kk_voteup").css("cursor","default");
	domKomm.find(".kk_votedown").css("cursor","default");
	domKomm.find(".kk_voteup").off("click");
	domKomm.find(".kk_votedown").off("click");
	domKomm.find(".kk_votestat").html(vote);
}
function voteKkUp(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(karteikartenServlet, actionVoteKarteikarteUp, 
	        function(response) {},
	        params
	    );
}
function voteKkDown(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(karteikartenServlet, actionVoteKarteikarteDown, 
	        function(response) {},
	        params
	    );
}