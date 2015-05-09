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
		erstelleNeuesThemaKk(karteikarteJson[paramId], kkDom.find(".antw").val());
		
		kkDom.find(".antw").val("");
		// Antworten updaten
		// TODO 
	});
    
    arr = [];
    sampleKommentar[paramErsteller] = jsonBenutzer;
    sampleKommentar2[paramErsteller] = jsonBenutzer;
    arr.push(sampleKommentar);
    arr.push(sampleKommentar2);
    arr.push(sampleKommentar);
    showHauptKommentare(kkDom,arr);
    fillKarteiKarte(kkDom,karteikarteJson);
    
    return kkDom;
}

function getKarteikarteByID (id){
	var params = {};
    params[paramId] = id;
    
	return ajaxCall(karteikartenServlet, actionGetKarteikarteByID, 
        function(response) {
			karteikarteJSON = response;
			console.log(karteikarteJSON);
//			buildKarteikarte(karteikarteJSON); //just for test
			return karteikarteJSON;
		},
        params
    );
}

var sampleKommentar = {};
sampleKommentar[paramId] = 5;
sampleKommentar[paramBewertung] = -6;
sampleKommentar[paramHatGevoted] = false;
sampleKommentar[paramInhalt] = "Dies ist ein beispiel kommentar dj kadjks fsdjkfsdkjfsdk jsdkjkj sdkjsdk jdsfsdkj fdsfkj dsfkj dskj dskj dsf kjdskj sdkj sdfkj dsfkj dskj ds kjsd kjdsfkj sdf kjdsf kjldsf kjlsdf kjldsf kjlds zu einer Karteikarte";
sampleKommentar[paramErstellDatum] = "20.05.2015 12:00 Uhr";
sampleKommentar[paramAntwortCount] = 1;


var sampleKommentar2 = {};
sampleKommentar2[paramId] = 6;
sampleKommentar2[paramBewertung] = 23;
sampleKommentar2[paramHatGevoted] = true;
sampleKommentar2[paramInhalt] = "Dies ist ein beispiel kommentar dj kadjks fsdjkfsdkjfsdk jsdkjkj sdkjsdk jdsfsdkj fdsfkj dsfkj dskj dskj dsf kjdskj sdkj sdfkj dsfkj dskj ds kjsd kjdsfkj sdf kjdsf kjldsf kjlsdf kjldsf kjlds zu einer Karteikarte";
sampleKommentar2[paramErstellDatum] = "20.05.2015 15:00 Uhr";
sampleKommentar2[paramAntwortCount] = 6;

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
				// TODO Vote-Handler registrieren
				hauptKomm.find(".komm_voteup").click(function() {
					voteKommentarUp(kommObj[paramId]);
					// TODO 
					hauptKomm.find(".komm_voteup").fadeTo("fast",0);
					hauptKomm.find(".komm_votedown").fadeTo("fast",0);
					hauptKomm.find(".komm_voteup").off("click");
					hauptKomm.find(".komm_votedown").off("click");
				});
				hauptKomm.find(".komm_votedown").click(function() {
					voteKommentarDown(kommObj[paramId]);
					// TODO 
					hauptKomm.find(".komm_voteup").fadeTo("fast",0);
					hauptKomm.find(".komm_votedown").fadeTo("fast",0);
					hauptKomm.find(".komm_voteup").off("click");
					hauptKomm.find(".komm_votedown").off("click");
				});
			}

			if(kommErsteller[paramId] == jsonBenutzer[paramId] || 
					jsonBenutzer[paramNutzerstatus] == "ADMIN" )
			{
				// TODO Löschen handler
				hauptKomm.find(".kk_komm_loeschen").click(function() {
					sindSieSicher(hauptKomm.find(".kk_komm_loeschen"), "Wollen Sie das gesamte Thema wirklich löschen?", 
							function() {
								loescheKommentar(kommObj[paramId]);
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

			// TODO Antworten handler
			hauptKomm.find(".komm_submit_bt").click(function(){
				if(hauptKomm.find(".antw").val().trim() == "")
				{
					showError("Ein Kommentar darf nicht leer sein!");
					return;
				}
				
				sendeAntwortKomm(kommObj[paramId], hauptKomm.find(".antw").val());
				hauptKomm.find(".antw").val("");
				// Antworten updaten
				arr = [];
				sampleKommentar[paramErsteller] = jsonBenutzer;
				sampleKommentar2[paramErsteller] = jsonBenutzer;
				arr.push(sampleKommentar);
				arr.push(sampleKommentar2);
				arr.push(sampleKommentar);
				showAntwortKommentare(hauptKomm,arr);
				
				hauptKomm.find(".AntwPfeil").addClass("octicon-triangle-up");
				hauptKomm.find(".AntwPfeil").removeClass("octicon-triangle-down");
				hauptKomm.find(".subkommentare").slideDown("slow");
			});

			if(kommObj[paramAntwortCount] > 0)
			{
				hauptKomm.find(".subKommCount").text(kommObj[paramAntwortCount]);
				hauptKomm.find(".AntwPfeil").addClass("octicon octicon-triangle-down");
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
						arr = [];
						sampleKommentar[paramErsteller] = jsonBenutzer;
						sampleKommentar2[paramErsteller] = jsonBenutzer;
						arr.push(sampleKommentar);
						arr.push(sampleKommentar2);
						arr.push(sampleKommentar);
						showAntwortKommentare(hauptKomm,arr);
						
						hauptKomm.find(".AntwPfeil").addClass("octicon-triangle-up");
						hauptKomm.find(".AntwPfeil").removeClass("octicon-triangle-down");
						hauptKomm.find(".subkommentare").slideDown("slow");
						
					}
				});
			}
			else
			{
				hauptKomm.find(".antwAnzeigen").hide();
			}


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
							loescheKommentar(kommObj[paramId]);
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
	console.log("Vote Kommentar " + kommId + " nach oben");
	showInfo("Kommentar positiv bewertet.");
}
function voteKommentarDown(kommId)
{
	console.log("Vote Kommentar " + kommId + " nach unten");
	showInfo("Kommentar negativ bewertet.");
}
function loescheKommentar(kommId)
{
	console.log("loesche Kommentar " + kommId);
	showInfo("Kommentar gelöscht.");
}
function sendeAntwortKomm(hautpKommId, text)
{
	console.log("Antworte auf Kommentar " + hautpKommId + " mit: " + text);
	showInfo("Antwort gespeichert.");
}
function erstelleNeuesThemaKk(kkId, text)
{
	console.log("Neues Thema zur Karteikarte " + kkId + " mit: " + text);
	showInfo("Thema erstellt gespeichert.");
}
