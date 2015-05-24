/**
 * @author mk
 */

var veranstaltungsObject;

$(document).ready(function() {
	$("#vn_kk_ueberscht_box").hide();
	
	$("#vn_loeschen").click(function() {
		
		sindSieSicher($("#vn_loeschen"), "Soll die Veranstaltung wirklich gelöscht werden?", function() {
			var params = {};
			params[paramId] = getUrlParameterByName(paramId);
			ajaxCall(veranstaltungServlet,
				actionDeleteVn,
				function(response) 
				{
					gotoHauptseite();
				},
				params
			);
		})
	});
	
	// LadeHandler
    $(".kk_load_pre").click(function(){
    	id = $("#kk_all").children().first().attr("data-kkid");
    	if(id != null)
    		loadPreKk(id);
    	else{
			$(".kk_load_pre").slideUp();
			displayKarteikarte(veranstaltungsObject[paramErsteKarteikarte]);
    	}
    });    
    $(".kk_load_after").click(function(){
    	id = $("#kk_all").children().last().attr("data-kkid");
    	if(id != null)
			loadAfterKk(id);
    	else
			$(".kk_load_after").slideUp();
    });
    
});

function fillVeranstaltungsSeite(Vid, kkId)
{
	showPreAfterLoad();
	
	// Wir verwenden ein eigenes Deferred-Objekt um zurückzumelden, wenn alles geladen wurde.
	d = jQuery.Deferred();
    destroyCKeditors($("#kk_all"));
	$("#kk_all").empty();
	// Studiengänge in auswahlliste anzeigen
	var ajax1 = ajaxCall(startseitenServlet,
			actionGetStudiengaenge,
			function(response) 
			{
				var studgArr = response[keyJsonArrResult];
				fillSelectWithOptions($("#vn_bearbeiten_auswahl_studiengang"),studgArr,jsonBenutzer[paramStudiengang],true);
			}
	); 

	// Semester in auswahlliste anzeigen
	var ajax2 =  ajaxCall(startseitenServlet,
			actionGetSemester,
			function(response) 
			{
				$("#vn_bearbeiten_auswahl_semester").empty();
		
				var studgArr = response[keyJsonArrResult];
				var aktSemesterString = response[paramAktSemester];
				var aktSemesterDI = 1; //default, falls kein match
		
				for(var i in studgArr) {
					$("#vn_bearbeiten_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
					if(aktSemesterString==studgArr[i][paramSemester]){
						aktSemesterDI = Number(i)+1;
					}
				}
				$("#vn_bearbeiten_auswahl_semester").find("option").sort(function(a,b) {
					return $(a).data('semesterid') > $(b).data('semesterid');
				}).appendTo('#vn_bearbeiten_auswahl_semester');
		
				$("[data-semesterid='"+aktSemesterDI+"']").prop('selected', true);
				$("#vn_bearbeiten_auswahl_semester option[value='"+ response[paramAktSemester] +"']").prop('selected', true);
			}
	);
	
	var params = {};
	params[paramId] = Vid;
	var ajax3 = ajaxCall(veranstaltungServlet,
		actionGetVeranstaltung,
		function(response) 
		{
			veranstaltungsObject = response;
			
			if(veranstaltungsObject[paramAngemeldet] == false && jsonBenutzer[paramNutzerstatus] != "ADMIN" )
			{
				showError("Sie haben nicht die notwendingen Berechtigungen um diese Seite zu sehen!");
				gotoHauptseite();

				// Deferred Objekt als abgeschlossen markieren.
				d.resolve();
				return;
			}
			
			$.when(findStudiengaenge(Vid)).done(function() {
				$.when(findModeratorenVn(Vid)).done(function() {
					
					// Wenn alles geladen wurde
					titel = veranstaltungsObject[paramTitel];
					console.log(veranstaltungsObject);
					// Details der VN in DOM einfuegen
					$("#vn_title").text(titel);
					$("#vn_attr_semester").text(veranstaltungsObject[paramSemester]);
					var vnStudiengaenge = "";
					for(var i = 0; i<veranstaltungsObject[paramStudiengang].length; i++)
					{
					    vnStudiengaenge += veranstaltungsObject[paramStudiengang][i];
	                    if(i < veranstaltungsObject[paramStudiengang].length-1)
	                        vnStudiengaenge += ", ";
					}
					$("#vn_attr_studgang").text(vnStudiengaenge);
                    $("#vn_attr_ersteller").text(veranstaltungsObject[paramErsteller][paramVorname] + " " + veranstaltungsObject[paramErsteller][paramNachname]);
                    var vnModeratoren = "";
                    if(veranstaltungsObject[paramModeratoren].length > 0)
                    {
                        for(var i = 0; i<veranstaltungsObject[paramModeratoren].length; i++)
                        {
                            console.log(veranstaltungsObject[paramModeratoren][i]);
                            vnModeratoren += veranstaltungsObject[paramModeratoren][i][paramVorname] +
                                             " " + veranstaltungsObject[paramModeratoren][i][paramNachname];
                            if(i < veranstaltungsObject[paramModeratoren].length-1)
                                vnModeratoren += ", ";
                        }
                    }
                    else
                    {
                        vnModeratoren += "-";
                    }
                    $("#vn_attr_moderatoren").text(vnModeratoren);
                    $("#vn_attr_bewertungen_erlaubt").text(veranstaltungsObject[paramBewertungenErlauben] ? "ja" : "nein");
                    $("#vn_attr_kommentare_erlaubt").text(veranstaltungsObject[paramKommentareErlauben] ? "ja" : "nein");
                    $("#vn_attr_modbearb_erlaubt").text(veranstaltungsObject[paramModeratorKkBearbeiten] ? "ja" : "nein");
                    
                    document.title = titel;
					
					
					if(veranstaltungsObject[paramErsteller][paramId] == jsonBenutzer[paramId] || jsonBenutzer[paramNutzerstatus] == "ADMIN")
					{
						$("#vn_loeschen").show();
						$("#vn_bearbeiten").show();
						$("#kk_erstellen").show();
					}
					else
					{
						$("#vn_loeschen").hide();
						$("#vn_bearbeiten").hide();
						$("#kk_erstellen").hide();
					}
					
					if(kkId == undefined)
						displayKarteikarte(veranstaltungsObject[paramErsteKarteikarte]);
					else
						displayKarteikarte(kkId);
					
					// Deferred Objekt als abgeschlossen markieren.
					d.resolve();
				});
			});
		},
		params
	);
	
	// Inhaltsverzeichnis aufbauen
	// warte bis VN Objekt geladen
	$.when(ajax3).done(function() {
	    var ajax4 = initInhaltsverzeichniss();
        // Inhaltsverzeichnis im Viewport halten
	    // warte bis mainbox visible
	    $.when(ajax1,ajax2,d,ajax4).done(function() {
	        var sticky = new Waypoint.Sticky({
	            element: $("#kk_inhaltsverzeichnis"),
	            wrapper: '<div class="inhaltsverzeichnis-sticky-wrapper" />'
	        });
	        
	        registerErstellKkHandler($(".inhaltsvz_kk_erstellen"));
	       
	    });
        
	});
	return $.when(ajax1,ajax2,d);
}

function initInhaltsverzeichniss(){
    console.log(veranstaltungsObject);
	return ladeKindKarteikarten(veranstaltungsObject[paramErsteKarteikarte], $("#kk_inhaltsverzeichnis"))
}

/**
 * Generische Methode, die alle direkten Kindkarteikarten zu einer gegebenen Vater-ID
 * vom Server laedt und in eine Unordered List einfuegt. Es wird ein Click Handler registriert,
 * der beim Klick auf ein ListItem rekursiv dessen direkte Kinder laedt usw.
 * @param vaterId ID der Vaterkarteikarte
 * @param vaterElem jQuery Objekt. Container, in den die Unordered List eingefuegt wird.
 * @returns Ajax Objekt
 */
function ladeKindKarteikarten(vaterId, vaterElem) {
    var params = {};
    params[paramId] = vaterId;
    // Evntl bestehende Kindkarteikarten aushaengen
    vaterElem.find("ul").remove();
    // Neue Liste aufbauen
    vaterElem = vaterElem.append("<ul></ul>").find("ul");
    return ajaxCall(
            karteikartenServlet,
            actionGetKarteikartenKinder,
            function(response) {
                // neu geladene Kindkarteikarten holen
                var arr = response[keyJsonArrResult];
                // falls keine Kindkarteikarten vorhanden, biete Neuerstellung an
                if(arr.length == 0) {
                	elem = $("<li><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                    vaterElem.append(elem);
        	        registerErstellKkHandler(elem);
                }
                // andernfalls DOM aufbauen
                else
                {
                    // Pseudo-Kind zum Hinzufuegen einer neuen Karteikarte
                	elem = $("<li><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                    vaterElem.append(elem);
        	        registerErstellKkHandler(elem);
                    for(var i in arr)
                    {
                        var kkListItem = $("<li><a data-kkid='"+arr[i][paramId]+"' class='inhaltsvz_kk_knoten'>"+arr[i][paramTitel]+"</a></li>");
                        vaterElem.append(kkListItem);
                        
                        // Click Handler
                        var f = function(arr, kkListItem, i) {
                            kkListItem.find("a").click(function(e) {
                                // Falls noch nicht geschehen, lade Kindkarteikarten rekursiv
                                if($(e.target).siblings("ul").length == 0)
                                {
                                    ladeKindKarteikarten(arr[i][paramId], kkListItem);
                                }
                                // Andernfalls klappe Kindkarteikarten ein
                                else
                                {
                                    $(e.target).siblings("ul").remove();
                                }
                                displayKarteikarte(arr[i][paramId]);
                                e.stopPropagation();
                            });
                        }
                        f(arr, kkListItem, i);
                        // Pseudo-Kind zum Hinzufuegen einer neuen Karteikarte                        
                    	elem = $("<li><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                        vaterElem.append(elem);
            	        registerErstellKkHandler(elem);
                    }
                }
            },
            params
    );
}
function registerErstellKkHandler(domErstellenLink){
	// Prüfen ob erlaubt
    if(jsonBenutzer[paramId] == veranstaltungsObject[paramErsteller][paramId] || 
    		jsonBenutzer[paramNutzerstatus] == "ADMIN")
	{
    	domErstellenLink.find(".inhaltsvz_kk_erstellen").click(function(){
	    	newKarteikarte($(this));
        });
	}
    else
    {
    	domErstellenLink.find(".inhaltsvz_kk_erstellen").attr("style","visibility: hidden");
    }
}


//sucht Studiengänge, die zur Veranstaltung gehören
function findStudiengaenge(id){
	var params = {};
    params[paramId] = id;
    
	return ajaxCall(
        veranstaltungServlet, 
        actionGetStudgVn, 
        function(response) {
            studgArr = response[keyJsonArrResult];
            veranstaltungsObject[paramStudiengang] = studgArr;
        },
        params
    );
}
//sucht Moderatoren, die zur Veranstaltung gehören
function findModeratorenVn(id){
	var params = {};
    params[paramId] = id;
    
	return ajaxCall(
        veranstaltungServlet, 
        actionGetModVn, 
        function(response) {
            ModArr = response[keyJsonArrResult];
            console.log(ModArr);
            veranstaltungsObject[paramModeratoren] = ModArr;
        },
        params
    );
}

function sortiereKarteikartenIDs(jsonKkIDs){
	newIdArray = [];
	jsonKkIDs.sort(function(a,b){
		return a[paramIndex] - b[paramIndex];
	});
	for(var i in jsonKkIDs)
		newIdArray.push(jsonKkIDs[i][paramId]);
	return newIdArray;
}

function displayKarteikarte(id){
    // Karteikarte schon in der Liste?
    kkDiv = $("#kk_all").find("[data-kkid=" + id + "]");
    // Existiert ?
    if(kkDiv.length)
    {
    	$('html,body').animate({
            scrollTop: kkDiv.offset().top},
            'slow');
    }
    else
    {
    	destroyCKeditors($("#kk_all"));
    	$("#kk_all").children().fadeOut("slow").promise().done(function(){
    		$("#kk_all").empty();
        	showPreAfterLoad();

        	var params2 ={};
        	params2[paramId] = id;
        	ajax = ajaxCall(karteikartenServlet, actionGetKarteikarteByID, function(response){
        		domkk = buildKarteikarte(response);
				domkk.show();
				domkk.css("opacity", "0");
        		$("#kk_all").append(domkk);
				domkk.animate({opacity: 1}, 2000);
        	}, params2);

        	$.when(ajax).done(function(){
        		loadAfterKk(id);
        	});
    	});
    	
    }
}
displayingAfterKK = false;
var kkLoadRequest;
function loadAfterKk(id)
{
	if (kkLoadRequest != null){ 
		kkLoadRequest.abort();
		kkLoadRequest = null;
	}
	
	var params2 ={};
	params2[paramId] = id;
	kkLoadRequest = ajaxCall(karteikartenServlet,
			actionGetKarteikartenNachfolger, 
			function(response){

				if(displayingAfterKK)
					return;
				displayingAfterKK = true;
				
				data = response[keyJsonArrResult];
				if(data.length < 5)
				{
					$(".kk_load_after").slideUp();
				}

				domms = [];
				function nextItem()
				{
						o = data.shift();
						if(o == undefined){
							$.each(domms,function(i, obj){
								domms[i].animate({opacity: 1}, 2000);
							})
							return;
						}
						
						domkk = buildKarteikarte(o);
						domkk.show();
						domkk.css("opacity", "0");
						$("#kk_all").append(domkk);
						domms.push(domkk);
						nextItem();
				}
				nextItem();
				displayingAfterKK = false;
				
	}, params2);
	return kkLoadRequest;
}
displayingPreKK = false;
function loadPreKk(id)
{
	if (kkLoadRequest != null){ 
		kkLoadRequest.abort();
		kkLoadRequest = null;
	}
	var params ={};
	params[paramId] = id;
	kkLoadRequest = ajaxCall(karteikartenServlet, 
			actionGetKarteikartenVorgaenger, 
			function(response)
			{
				if(displayingPreKK)
					return;
				displayingPreKK = true;
				
				data = response[keyJsonArrResult];
				if(data.length < 5)
				{
					$(".kk_load_pre").slideUp();
				}
				
				domms = [];

				function nextItem()
				{
					o = data.shift();

					if(o == undefined){
						$.each(domms,function(i, obj){
							domms[i].animate({opacity: 1}, 2000);
						})
						return;
					}
					domkk = buildKarteikarte(o);
					domkk.show();
					domkk.css("opacity", "0");
					$("#kk_all").prepend(domkk);
					domms.push(domkk);
					nextItem();
				}
				nextItem();
				
				displayingPreKK = false;
        		
			}, params);
	return kkLoadRequest;
}

function showPreAfterLoad()
{
	$(".kk_load_after").show();
	$(".kk_load_pre").show();
}
