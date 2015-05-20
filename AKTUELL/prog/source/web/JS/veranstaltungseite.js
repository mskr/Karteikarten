/**
 * @author mk
 */
var sampleJSONIDs = [];
sampleJSONIDs[0] = {};
sampleJSONIDs[0][paramIndex] = 7;
sampleJSONIDs[0][paramId] = 24;
sampleJSONIDs[1] = {};
sampleJSONIDs[1][paramIndex] = 1;
sampleJSONIDs[1][paramId] = 26;
sampleJSONIDs[2] = {};
sampleJSONIDs[2][paramIndex] = 3;
sampleJSONIDs[2][paramId] = 22;
sampleJSONIDs[3] = {};
sampleJSONIDs[3][paramIndex] = 4;
sampleJSONIDs[3][paramId] = 23;
sampleJSONIDs[4] = {};
sampleJSONIDs[4][paramIndex] = 8;
sampleJSONIDs[4][paramId] = 20;
sampleJSONIDs[5] = {};
sampleJSONIDs[5][paramIndex] = 6;
sampleJSONIDs[5][paramId] = 25;
sampleJSONIDs[6] = {};
sampleJSONIDs[6][paramIndex] = 2;
sampleJSONIDs[6][paramId] = 21;
//sampleJSONIDs[7] = {};
//sampleJSONIDs[7][paramIndex] = 5;
//sampleJSONIDs[7][paramId] = 27;

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
    	loadPreKk($("#kk_all").children().first().attr("data-kkid"))
    	
    });    
    $(".kk_load_after").click(function(){
		loadAfterKk($("#kk_all").children().last().attr("data-kkid"));
    });
});

function fillVeranstaltungsSeite(Vid)
{
	showPreAfterLoad();
	
	// Wir verwenden ein eigenes Deferred-Objekt um zurückzumelden, wenn alles geladen wurde.
	d = jQuery.Deferred();
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
				return d; // TODO Achtung
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
					
					loadAfterKk(veranstaltungsObject[paramErsteKarteikarte]);
					
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
	    var ajax4 = ladeKindKarteikarten(veranstaltungsObject[paramErsteKarteikarte], $("#kk_inhaltsverzeichnis"));
        // Inhaltsverzeichnis im Viewport halten
	    // warte bis mainbox visible
	    $.when(ajax1,ajax2,d,ajax4).done(function() {
	        var sticky = new Waypoint.Sticky({
	            element: $("#kk_inhaltsverzeichnis"),
	            wrapper: '<div class="inhaltsverzeichnis-sticky-wrapper" />'
	        });
	    });
        
	});
	
    // Elemente fuer kleine Bildschirme
    if (window.matchMedia("(max-width: 56em)").matches)
    {
        $(".r-suche_etwas_label").hide();
        $(".r-kk-inhaltsvz-toggle").show();
    }
    else
    {
        $(".r-suche_etwas_label").hide();
        $(".r-kk-inhaltsvz-toggle").hide();
    }
	return $.when(ajax1,ajax2,d);
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
                console.log("[LOG] arr hat die laenge "+arr.length);
                // falls keine Kindkarteikarten vorhanden, biete Neuerstellung an
                if(arr.length == 0) {
                    console.log("hat keine kinder mehr");
                    // Pseudo-Kind zum Hinzufuegen einer neuen Karteikarte
                    vaterElem.append("<li><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                }
                // andernfalls DOM aufbauen
                else
                {
                    // Pseudo-Kind zum Hinzufuegen einer neuen Karteikarte
                    vaterElem.append("<li><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                    for(var i in arr)
                    {
                        var kkListItem = $("<li><a data-kkID='"+arr[i][paramId]+"' class='inhaltsvz_kk_knoten'>"+arr[i][paramTitel]+"</a></li>");
                        vaterElem.append(kkListItem);
                        // Click Handler
                        var f = function(arr, kkListItem, i) {
                            kkListItem.find("a").click(function(e) {
                                // Falls noch nicht geschehen, lade Kindkarteikarten rekursiv
                                if($(e.target).siblings("ul").length == 0)
                                {
                                    console.log("[LOG] Ausklappen: Iteriere arr bei Index "+ i);
                                    ladeKindKarteikarten(arr[i][paramId], kkListItem);
                                }
                                // Andernfalls klappe Kindkarteikarten ein
                                else
                                {
                                    console.log("[LOG] Einklappen: Iteriere arr bei Index "+ i);
                                    $(e.target).siblings("ul").remove();
                                }
                                e.stopPropagation();
 
                                $("#kk_all").empty();
                                showPreAfterLoad();
//
            					var params2 ={};
            					params2[paramId] = arr[i][paramId];
    	    	        		ajax = ajaxCall(karteikartenServlet, actionGetKarteikarteByID, function(response){
    	    	        			domkk = buildKarteikarte(response);
    	    	        			domkk.hide();
    	    	        			$("#kk_all").append(domkk);
    	    	        			domkk.slideDown();
            					}, params2);
//    	    	        		
            					$.when(ajax).done(function(){
            						loadAfterKk(arr[i][paramId]);
            					});

                                console.log("[LOG] Array");
                                console.log(arr);
                                
                            });
                        }
                        f(arr, kkListItem, i);
                        // Pseudo-Kind zum Hinzufuegen einer neuen Karteikarte
                        vaterElem.append("<li><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                        //TODO Click Handler Karteikarte hinzu
                    }
                }
            },
            params
    );
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
function loadAfterKk(id)
{
	var params2 ={};
	params2[paramId] = id;
	return ajaxCall(karteikartenServlet,
			actionGetKarteikartenNachfolger, 
			function(response){
				data = response[keyJsonArrResult];
				if(data.length < 5)
				{
					$(".kk_load_after").slideUp();
				}
				for(j = 0; j <data.length;j++)
				{
					domkk = buildKarteikarte(data[j]);
		    		domkk.hide();
		    		$("#kk_all").append(domkk);
		    		domkk.slideDown();
				}
			}, params2)
}
function loadPreKk(id)
{
	var params ={};
	params[paramId] = id;
	return ajaxCall(karteikartenServlet, 
			actionGetKarteikartenVorgaenger, 
			function(response)
			{
				arr = response[keyJsonArrResult];
				if(arr.length < 5)
				{
					$(".kk_load_pre").slideUp();
				}
				for(i = 0; i < arr.length;i++)
				{
					domkk = buildKarteikarte(arr[i]);
	        		domkk.hide();
	        		$("#kk_all").prepend(domkk);
	        		domkk.slideDown();
				}
        		
			}, params);
}

function showPreAfterLoad()
{
	$(".kk_load_after").show();
	$(".kk_load_pre").show();
}
