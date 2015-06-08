function showHauptKommentare(karteikarteContainer, kommentarArray)
{
	var kommlist = karteikarteContainer.find(".kk_kommList");
	var kommCountElem = karteikarteContainer.find(".kk_kommzaehler");	
	kommCountElem.html(kommentarArray.length > 0 ? kommentarArray.length : "");
	kommlist.empty();
	for(var i in kommentarArray)
	{
		hauptKomm = createAndFillKommentar(kommentarArray[i], false, karteikarteContainer);
		kommlist.append(hauptKomm);
	}

	// Sortieren
	orderKommentareById(kommlist);
}

function showAntwortKommentare(hauptkommentar, kommentarArray)
{
	var kommbox = hauptkommentar.find(".subkommentare");
	kommbox.empty();
	for(var i in kommentarArray)
	{
		subKomm = createAndFillKommentar(kommentarArray[i],true,hauptkommentar);
		kommbox.append(subKomm);
	}
	// Sortieren
	orderKommentareById(kommbox);
}

function fillHauptKommentar(domKomm, kommObj, domKK)
{
	// Voting	
	domKomm.find(".komm_votestat").html(kommObj[paramBewertung]);
	if(kommObj[paramBewertung] < 0)
	{
		domKomm.css("border-color","rgb(251,51,11)");
	}
	else if(kommObj[paramBewertung] > 0)
	{
		domKomm.css("border-color","rgb(14,248,101)");
	}
	
	if(kommObj[paramHatGevoted] == true)
	{
		domKomm.find(".komm_voteup").css("opacity"," 0");
		domKomm.find(".komm_votedown").css("opacity"," 0");
	}
	else
	{
		// Vote-Handler registrieren
		domKomm.find(".komm_voteup").click(function() {
			$.when(voteKommentarUp(kommObj[paramId])).done(function()
				{
					doVoteGUI(domKomm, kommObj[paramBewertung]+1);
				});
		});
		domKomm.find(".komm_votedown").click(function() {
			$.when(voteKommentarDown(kommObj[paramId])).done(function()
				{
					doVoteGUI(domKomm, kommObj[paramBewertung]-1);
				});
		});
	}

	var kommErsteller = kommObj[paramErsteller];
	if(kommErsteller[paramId] == jsonBenutzer[paramId] || checkIfAllowedVn(veranstaltungsObject,false))
	{
		// Löschen handler
		domKomm.find(".kk_komm_loeschen").click(function() {
			sindSieSicher(domKomm.find(".kk_komm_loeschen"), "Wollen Sie das gesamte Thema wirklich löschen?", 
			function() {
				$.when(loescheKommentar(kommObj[paramId])).done(function()
				{
					domKomm.slideUp();
					count = parseInt(domKK.find(".kk_kommzaehler").html());	
					domKK.find(".kk_kommzaehler").html(count-1 > 0 ? count-1 : "");
				});
			});
		});
	}
	
	setupAntwAnz(domKomm, kommObj[paramAntwortCount]);

	// Antworten anzeigen
	domKomm.find(".antwAnzeigen").click(function(){
		// Aufgeklappt ?
		isAufgeklappt = domKomm.find(".subkommentare").is(':visible') ;
		if (isAufgeklappt)
		{
		    domKomm.find(".kk_kommantwort").slideUp(function() {
	            // Zerstoere ckEditor
	            domKomm.find(".antw").ckeditorGet().destroy();
		    });
			hideAntworten(domKomm);
		}
		// Zugeklappt ?
		else if (!isAufgeklappt) 
		{
            domKomm.find(".kk_kommantwort").slideDown(function() {
                // Erzeuge ckEditor
                domKomm.find(".antw").ckeditor(ckEditorKommentarConfig);
            });
			showAntworten(domKomm,kommObj[paramId]);
		}
	});
	
	// Antwort absenden Handler
	domKomm.find(".komm_submit_bt").click(function(){
		if(domKomm.find(".antw").ckeditorGet().getData().trim() == "")
		{
			showError("Bitte geben Sie etwas in das Antwortfeld ein.");
			return;
		}

		$.when(sendeAntwortKomm(kommObj[paramId], domKomm.find(".antw").ckeditorGet().getData())).done(function() {
		    domKomm.find(".antw").ckeditorGet().setData("");
			showAntworten(domKomm,kommObj[paramId]);
		});

	});
}

function fillAntKomm(domKomm, kommObj, domVaterKomm)
{
	var kommErsteller = kommObj[paramErsteller];
	
	if(kommErsteller[paramId] == jsonBenutzer[paramId] || checkIfAllowedVn(veranstaltungsObject,false) )
	{
		domKomm.find(".kk_komm_loeschen").off("click");
		domKomm.find(".kk_komm_loeschen").click(function() {
			sindSieSicher(domKomm.find(".kk_komm_loeschen"), "Wollen Sie den Kommentar wirklich löschen?", 
				function() {
					$.when(loescheKommentar(kommObj[paramId])).done(function()
					{
						domKomm.slideUp();
						i = parseInt(domVaterKomm.find(".subKommCount").html());
						if(!setupAntwAnz(domVaterKomm, i-1))
						{
							hideAntworten(domVaterKomm);
						}							
					});
				});
		});
	}
}

function createAndFillKommentar(kommObj, isAntwortKommentar, domVater)
{
	komID = isAntwortKommentar? "#templateSubKomm": "#templateSuperKomm";

	var kommErsteller = kommObj[paramErsteller];
	var domKomm = $(komID).clone();

	domKomm.removeAttr("id");
	domKomm.attr("data-komm-id",kommObj[paramId]);
	domKomm.show();
	
	// Infos setzen
	domKomm.find(".kommInhalt").html(kommObj[paramInhalt]);
	domKomm.find(".kommAuthor").html(kommErsteller[paramVorname] + " " + kommErsteller[paramNachname]);
	domKomm.find(".kommAuthor").click(function(){
		gotoProfil(kommErsteller[paramId]);
	});
	domKomm.find(".kommDatum").html(kommObj[paramErstellDatum]);
	domKomm.find(".kk_komm_profilBild").attr("src",kommErsteller[paramProfilBild]);
	
	// TODO Was ist mit Moderatoren?
	if(kommErsteller[paramId] == jsonBenutzer[paramId] || checkIfAllowedVn(veranstaltungsObject,false))
	{
	}
	else
	{
		domKomm.find(".kk_komm_loeschen").fadeTo("fast", 0);
	}
	
	if(isAntwortKommentar)
	{
		fillAntKomm(domKomm, kommObj, domVater);
	}
	else
		fillHauptKommentar(domKomm, kommObj, domVater);
		
	return domKomm;
}

//Funktion für GUI der Votes
function doVoteGUI(domKomm, vote){
	domKomm.find(".komm_voteup").fadeTo("fast",0);
	domKomm.find(".komm_votedown").fadeTo("fast",0);
	domKomm.find(".komm_voteup").off("click");
	domKomm.find(".komm_votedown").off("click");
	domKomm.find(".komm_votestat").html(vote);
	if(vote < 0)
	{
		domKomm.css("border-color","rgb(251,51,11)");
	}
	else if(vote > 0)
	{
		domKomm.css("border-color","rgb(14,248,101)");
	}
	else
	{
		domKomm.removeAttr("style");
	}
}

function setupAntwAnz(domKomm, antwCnt)
{
	if(antwCnt>0)
	{
		domKomm.find(".antwAnzeigen").show();
		domKomm.find(".subKommCount").text(antwCnt);
		return true;
	}
	else
	{
//		domKomm.find(".antwAnzeigen").hide();
        domKomm.find(".antwAnzeigen").show();
        domKomm.find(".subKommCount").text("Keine.");
        domKomm.find(".subKommCount").addClass("keineAntworten");
		return false;
	}
}

function showAntworten(domKomm, id)
{
	var params = {};
	params[paramId] = id;
	ajaxCall(
			kommentarServlet,
			actionLeseAntwortKommentar,
			function(response) {
				arr = response[keyJsonArrResult];
				setupAntwAnz(domKomm,arr.length);
				domKomm.find(".subkommentare").empty();
				
				showAntwortKommentare(domKomm,arr);

				domKomm.find(".AntwPfeil").toggleClass("hidden");
				domKomm.find(".subkommentare").slideDown();
			},
			params
	);
}

function hideAntworten(domKomm)
{
	domKomm.find(".subkommentare").slideUp(function(){
		domKomm.find(".subkommentare").empty();
		domKomm.find(".AntwPfeil").toggleClass("hidden");
	});
}


function orderKommentareById(kommentareContainer)
{
	// Neu Sortieren
	var elem = kommentareContainer.find('[data-komm-id]').sort(function(a,b){
		return $(b).attr("data-komm-id") - $(a).attr("data-komm-id");
	});
	kommentareContainer.append(elem);
}

function voteKommentarUp(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(kommentarServlet, actionVoteKommentarUp, 
	        function(response) {},
	        params
	    );
}
function voteKommentarDown(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(kommentarServlet, actionVoteKommentarDown, 
	        function(response) {},
	        params
	    );
}
function loescheKommentar(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(kommentarServlet, actionDeleteKommentar, 
	        function(response) {},
	        params
	    );
}
function sendeAntwortKomm(hautpKommId, text)
{
	var params = {};
    params[paramId] = hautpKommId;
    params[paramInhalt] = text;
	return ajaxCall(kommentarServlet, 
	        actionErstelleAntwortKommentar, 
	        function(response) {},
	        params
	);
}
function erstelleNeuesThemaKk(kkId, text)
{
	var params = {};
    params[paramId] = kkId;
    params[paramInhalt] = text;
	return ajaxCall(kommentarServlet, actionErstelleThemaKommentar, 
	        function(response) {},
	        params
	    );
}