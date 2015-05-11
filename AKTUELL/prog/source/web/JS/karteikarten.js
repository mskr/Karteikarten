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

function getKarteikarteByID (id){
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

function showHauptKommentare(karteikarteContainer, kommentarArray)
{
	var kommbox = karteikarteContainer.find(".kk_kommbox");
	var kommCountElem = kommbox.find(".kk_kommzaehler");	
	kommCountElem.html(kommentarArray.length);
	kommbox.find(".kk_kommList").empty();
	
	for(var i in kommentarArray)
	{

		var Obj = kommentarArray[i];
		var f = function(kommObj)
		{
			var kommErsteller = kommObj[paramErsteller];
			var hauptKomm = $("#templateSuperKomm").clone();

			hauptKomm.removeAttr("id");
			hauptKomm.attr("data-komm-id",kommObj[paramId]);
			hauptKomm.show();

			hauptKomm.find(".komm_votestat").html(kommObj[paramBewertung]);

			if(kommObj[paramHatGevoted] == true)
			{
				// Oder löschen? TODO
				hauptKomm.find(".komm_voteup").css("opacity"," 0");
				hauptKomm.find(".komm_votedown").css("opacity"," 0");
			}
			else
			{
				// Vote-Handler registrieren
				hauptKomm.find(".komm_voteup").click(function() {
					$.when(voteKommentarUp(kommObj[paramId])).done(function()
						{
							hauptKomm.find(".komm_voteup").fadeTo("fast",0);
							hauptKomm.find(".komm_votedown").fadeTo("fast",0);
							hauptKomm.find(".komm_voteup").off("click");
							hauptKomm.find(".komm_votedown").off("click");
							hauptKomm.find(".komm_votestat").html(kommObj[paramBewertung]+1);
						});
				});
				hauptKomm.find(".komm_votedown").click(function() {
					$.when(voteKommentarDown(kommObj[paramId])).done(function()
						{
							hauptKomm.find(".komm_voteup").fadeTo("fast",0);
							hauptKomm.find(".komm_votedown").fadeTo("fast",0);
							hauptKomm.find(".komm_voteup").off("click");
							hauptKomm.find(".komm_votedown").off("click");
							hauptKomm.find(".komm_votestat").html(kommObj[paramBewertung]-1);
						});
				});
			}

			if(kommErsteller[paramId] == jsonBenutzer[paramId] || 
					jsonBenutzer[paramNutzerstatus] == "ADMIN" )
			{
				// Löschen handler
				hauptKomm.find(".kk_komm_loeschen").click(function() {
					sindSieSicher(hauptKomm.find(".kk_komm_loeschen"), "Wollen Sie das gesamte Thema wirklich löschen?", 
					function() {
						$.when(loescheKommentar(kommObj[paramId])).done(function()
						{
							hauptKomm.slideUp("slow");
						});
					});
				});
			}
			else
			{
				hauptKomm.find(".kk_komm_loeschen").hide();
			}

			hauptKomm.find(".kk_inhalt").html(kommObj[paramInhalt]);
			hauptKomm.find(".kommAuthor").html(kommErsteller[paramVorname] + " " + kommErsteller[paramNachname]);
			hauptKomm.find(".kommAuthor").click(function(){
				gotoProfil(kommErsteller[paramId]);
			});
			
			hauptKomm.find(".kommDatum").html(kommObj[paramErstellDatum]);

			// Antworten handler
			hauptKomm.find(".komm_submit_bt").click(function(){
				if(hauptKomm.find(".antw").val().trim() == "")
				{
					showError("Ein Kommentar darf nicht leer sein!");
					return;
				}
				
				$.when(sendeAntwortKomm(kommObj[paramId], hauptKomm.find(".antw").val())).done(function() {
					hauptKomm.find(".antw").val("");
					
					// Antworten updaten				
					var params = {};
					params[paramId] = kommObj[paramId];
					ajaxCall(
							kommentarServlet,
							actionLeseAntwortKommentar,
							function(response) {
								arr = response[keyJsonArrResult];
								showAntwortKommentare(hauptKomm,arr);
								if(arr.length > 0)
								{
									hauptKomm.find(".antwAnzeigen").show();
									hauptKomm.find(".subKommCount").text(arr.length);
									hauptKomm.find(".AntwPfeil").addClass("octicon octicon-triangle-down");
								}
								else
								{
									hauptKomm.find(".antwAnzeigen").hide();
								}
								
								hauptKomm.find(".subKommCount").text(arr.length);
								hauptKomm.find(".AntwPfeil").addClass("octicon-triangle-up");
								hauptKomm.find(".AntwPfeil").removeClass("octicon-triangle-down");
								hauptKomm.find(".subkommentare").slideDown("slow");
							},
							params
					);
				});
				
			});

			if(kommObj[paramAntwortCount] > 0)
			{
				hauptKomm.find(".antwAnzeigen").show();
				hauptKomm.find(".subKommCount").text(kommObj[paramAntwortCount]);
				hauptKomm.find(".AntwPfeil").addClass("octicon octicon-triangle-down");
			}
			else
			{
				hauptKomm.find(".antwAnzeigen").hide();
			}
			
			hauptKomm.find(".antwAnzeigen").click(function(){
				// Aufgeklappt ?
				if ( hauptKomm.find(".subkommentare").is(':visible') ) 
				{
					hauptKomm.find(".subkommentare").slideUp("slow",function(){
						hauptKomm.find(".subkommentare").empty();
						hauptKomm.find(".AntwPfeil").addClass("octicon-triangle-down");
						hauptKomm.find(".AntwPfeil").removeClass("octicon-triangle-up");
					});
				}
				// Zugeklappt ?
				else if ( hauptKomm.find(".subkommentare").is(':hidden') ) 
				{
					// TODO ClickHandler
					var params = {};
					params[paramId] = kommObj[paramId];
					ajaxCall(
							kommentarServlet,
							actionLeseAntwortKommentar,
							function(response) {
								arr = response[keyJsonArrResult];
								showAntwortKommentare(hauptKomm,arr);
								
								hauptKomm.find(".AntwPfeil").addClass("octicon-triangle-up");
								hauptKomm.find(".AntwPfeil").removeClass("octicon-triangle-down");
								hauptKomm.find(".subkommentare").slideDown("slow");
							},
							params
					);

				}
			});

			kommbox.find(".kk_kommList").append(hauptKomm);
		};
		f(Obj);
	}
	// Sortieren
	orderKommentareById(kommbox.find(".kk_kommList"));
}

function showAntwortKommentare(hauptkommentar, kommentarArray)
{
	var kommbox = hauptkommentar.find(".subkommentare");
	kommbox.empty();
	for(var i in kommentarArray)
	{

		var Obj = kommentarArray[i];
		var f = function(kommObj)
		{
			var kommErsteller = kommObj[paramErsteller];
			var subKomm = $("#templateSubKomm").clone();

			subKomm.removeAttr("id");
			subKomm.attr("data-komm-id",kommObj[paramId]);
			subKomm.css("display","block");

			if(kommErsteller[paramId] == jsonBenutzer[paramId] || 
					jsonBenutzer[paramNutzerstatus] == "ADMIN" )
			{
				// TODO Löschen handler
				subKomm.find(".kk_komm_loeschen").click(function() {
					sindSieSicher(subKomm.find(".kk_komm_loeschen"), "Wollen Sie den Kommentar wirklich löschen?", 
						function() {
							$.when(loescheKommentar(kommObj[paramId])).done(function()
							{
								subKomm.slideUp("slow");
								i = (Number)hauptkommentar.find(".subKommCount").html();
								
								hauptkommentar.find(".subKommCount").html(i);
																
							});
						});
				});
			}
			else
			{
				subKomm.find(".kk_komm_loeschen").hide();
			}

			subKomm.find(".kk_inhalt").html(kommObj[paramInhalt]);
			subKomm.find(".kommAuthor").html(kommErsteller[paramVorname] + " " +kommErsteller[paramNachname]);
			subKomm.find(".kommAuthor").click(function(){
				gotoProfil(kommErsteller[paramId]);
			});
			subKomm.find(".kommDatum").html(kommObj[paramErstellDatum]);

			kommbox.append(subKomm);
		};
		f(Obj);
	}
	// Sortieren
	orderKommentareById(kommbox);
}

function orderKommentareById(kommentareContainer)
{
	// Neu Sortieren
	var elem = kommentareContainer.find('[data-komm-id]').sort(sortDivByKommentarId);
	kommentareContainer.append(elem);
}
function sortDivByKommentarId(a,b)
{
	return $(b).attr("data-komm-id") - $(a).attr("data-komm-id");
}

function voteKommentarUp(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(kommentarServlet, actionVoteKommentarUp, 
	        function(response) {
				console.log("Vote Kommentar " + kommId + " nach oben");
				showInfo("Kommentar positiv bewertet.");
			},
	        params
	    );
}
function voteKommentarDown(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(kommentarServlet, actionVoteKommentarDown, 
	        function(response) {
				console.log("Vote Kommentar " + kommId + " nach unten");
				showInfo("Kommentar negativ bewertet.");
			},
	        params
	    );
}
function loescheKommentar(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(kommentarServlet, actionDeleteKommentar, 
	        function(response) {
				console.log("loesche Kommentar " + kommId);
				showInfo("Kommentar gelöscht.");
			},
	        params
	    );
}
function sendeAntwortKomm(hautpKommId, text)
{
	var params = {};
    params[paramId] = hautpKommId;
    params[paramInhalt] = text;
	return ajaxCall(kommentarServlet, actionErstelleAntwortKommentar, 
	        function(response) {
				console.log("Antworte auf Kommentar " + hautpKommId + " mit: " + text);
				showInfo("Antwort gespeichert.");
			},
	        params
	    );
}
function erstelleNeuesThemaKk(kkId, text)
{
	var params = {};
    params[paramId] = kkId;
    params[paramInhalt] = text;
	return ajaxCall(kommentarServlet, actionErstelleThemaKommentar, 
	        function(response) {
				console.log("Neues Thema zur Karteikarte " + kkId + " mit: " + text);
				showInfo("Thema erstellt gespeichert.");
			},
	        params
	    );
}
