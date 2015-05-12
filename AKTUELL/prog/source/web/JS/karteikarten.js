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
    var kkDom = 
        "<div id='kk_"+kkId+"_wrapper' class='kk_wrapper'>" +
            "<div class='kk_votes'>" +
                "<a class='kk_voteup'><span class='mega-octicon octicon-triangle-up'></span></a>" +
                "<div class='kk_votestat'></div>" +
                "<a class='kk_votedown'><span class='mega-octicon octicon-triangle-down'></span></a>" +
            "</div>" +
            "<div class='kk_optionen'>" +
                "<a class='option permalink tiptrigger tipabove' data-tooltip='Link kopieren'>" +
                    "<span class='octicon octicon-link'></span>" +
                "</a>" +
                "<a class='option loeschen tiptrigger tipabove' data-tooltip='Karteikarte löschen'>" +
                    "<span class='octicon octicon-trashcan'></span>" +
                "</a>" +
                "<a class='option einfuegen tiptrigger tipabove' data-tooltip='Karteikarte unter dieser Karteikarte einfügen'>" +
                    "<span class='octicon octicon-plus'></span>" +
                "</a>" +
                "<a class='option attribute attr_popup_open tiptrigger tipabove' data-tooltip='Attribute ändern'>" +
                    "<span class='octicon octicon-checklist'></span>" +
                "</a>" +
            "</div>" +
            "<div class='kk_notizen' contenteditable></div>";
    
    if(kkType == paramKkText)
    {
        kkDom +=
            "<div class='kk inhalt_text' contenteditable></div>";
    }
    else if(kkType == paramKkBild)
    {
        kkDom +=
            "<div class='kk inhalt_bild' contenteditable></div>";
    }
    else if(kkType == paramKkVideo)
    {
        kkDom +=
            "<div class='kk inhalt_video' contenteditable></div>";
    }
            
    kkDom +=
            "<div class='kk_kommbox'>" +
                "<div class='kk_kommheader'><span class='octicon octicon-comment-discussion'></span> Kommentare (<span class='kk_kommzaehler'></span>)</div>" +
                "<div class='kk_kommerstellen'>" +
                    "<textarea class='antw' placeholder='Neues Thema beginnen...'></textarea>"+
                	"<a class='komm_submit_bt' style='float:right'>Thema erstellen</a>"+
                "</div>" +
                "<div class='kk_kommList'></div>" +
            "</div>" +
        "</div>";

    kkDom =  $(kkDom);
    // Neues Thema Handler TODO
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
		
		// Antworten updaten
		// TODO 
	});

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
	
    fillKarteiKarte(kkDom,karteikarteJson);
    
    return kkDom;
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