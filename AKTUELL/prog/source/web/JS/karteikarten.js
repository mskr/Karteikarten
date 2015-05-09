/**
 * @author mk
 */

function buildKarteikarte(karteikarteJson)
{
    var kkId = karteikarteJson[paramId],
        kkTitel = karteikarteJson[paramTitel];
        kkType = karteikarteJson[paramType];
        kkVeranstaltung = karteikarteJson[paramVeranstaltung];
        kkInhalt = karteikarteJson[paramInhalt];
        kkBewertung = karteikarteJson[paramBewertung];
        kkAenderungsdatum = karteikarteJson[paramAenderungsdatum];
    var kkHtmlStr = 
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
        kkHtmlStr +=
            "<div class='kk inhalt_text' contenteditable></div>";
    }
    else if(kkType == paramKkBild)
    {
        kkHtmlStr +=
            "<div class='kk inhalt_bild' contenteditable></div>";
    }
    else if(kkType == paramKkVideo)
    {
        kkHtmlStr +=
            "<div class='kk inhalt_video' contenteditable></div>";
    }
            
    kkHtmlStr +=
            "<div class='kk_kommbox'>" +
                "<div class='kk_kommheader'><span class='octicon octicon-comment-discussion'></span> Kommentare (<span class='kk_kommzaehler'></span>)</div>" +
                "<div class='kk_kommerstellen'>" +
                    "<textarea placeholder='Neues Thema beginnen...'></textarea>" +
                "</div>" +
            "</div>" +
        "</div>";
    fillKarteiKarte($(kkHtmlStr),karteikarteJson);
}

function getKarteikarteByID (id){
	var params = {};
    params[paramId] = id;
    
	return ajaxCall(karteikartenServlet, actionGetKarteikarteByID, 
        function(response) {
			karteikarteJSON = response;
			console.log(karteikarteJSON);
			buildKarteikarte(karteikarteJSON); //just for test
			return karteikarteJSON;
		},
        params
    );
}

function showHauptKommentare(karteikarteContainer, kommentarArray)
{
	var kommbox = karteikarteContainer.find(".kk_kommbox");
	var kommCountElem = kommbox.find(".kk_kommzaehler");	
	kommCountElem.html(kommentarArray.length);
	
	
	for(var i in kommentarArray)
	{
		var kommObj = kommentarArray[i];
		var kommErsteller = kommObj[paramErsteller];
		var hauptKomm = $("#templateSuperKomm").clone();
		
		hauptKomm.removeAttr("id");
		hauptKomm.attr("data-komm-id",kommObj[paramId]);
		
		hauptKomm.find(".komm_votestat").html(kommObj[paramVotes]);
		
		if(kommObj[paramHatGevoted] == true)
		{
			// Oder löschen? TODO
			hauptKomm.find(".komm_voteup").hide();
			hauptKomm.find(".komm_down").hide();
		}
		else
		{
			// TODO Handler registrieren
		}
		
		if(kommErsteller[paramId] == jsonBenutzer[paramId] || 
				jsonBenutzer[paramNutzerstatus] == "ADMIN" )
		{
			// TODO Löschen handler
		}
		else
		{
			hauptKomm.find(".kk_komm_loeschen").hide();
		}
		
		hauptKomm.find(".kk_inhalt").html(kommObj[paramInhalt]);
		hautpKomm.find(".kommAuthor").html(kommErsteller[paramVorname] + " " +kommErsteller[paramNachname]);
		hautpKomm.find(".kommDatum").html(kommObj[paramErstellDatum]);
		hautpKomm.find(".komm_antw_bt").click(function(){
			// TODO Antworten handler
		});
		
		kommbox.append(hauptKomm);
		hauptKomm.show();		
	}
}
function showAntwortKommentare(hauptkommentar, kommentarArray)
{
	
}