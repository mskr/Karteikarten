/**
 * @author mk
 */
var sampleJSONIDs = {};
sampleJSONIDs["k_1"] = {};
sampleJSONIDs["k_1"][paramIndex] = 7;
sampleJSONIDs["k_1"][paramId] = 24;
sampleJSONIDs["k_2"] = {};
sampleJSONIDs["k_2"][paramIndex] = 1;
sampleJSONIDs["k_2"][paramId] = 26;
sampleJSONIDs["k_3"] = {};
sampleJSONIDs["k_3"][paramIndex] = 3;
sampleJSONIDs["k_3"][paramId] = 22;
sampleJSONIDs["k_4"] = {};
sampleJSONIDs["k_4"][paramIndex] = 4;
sampleJSONIDs["k_4"][paramId] = 23;
sampleJSONIDs["k_5"] = {};
sampleJSONIDs["k_5"][paramIndex] = 8;
sampleJSONIDs["k_5"][paramId] = 20;
sampleJSONIDs["k_6"] = {};
sampleJSONIDs["k_6"][paramIndex] = 6;
sampleJSONIDs["k_6"][paramId] = 25;
sampleJSONIDs["k_7"] = {};
sampleJSONIDs["k_7"][paramIndex] = 2;
sampleJSONIDs["k_7"][paramId] = 21;
sampleJSONIDs["k_8"] = {};
sampleJSONIDs["k_8"][paramIndex] = 5;
sampleJSONIDs["k_8"][paramId] = 27;

var veranstaltungsObject;

$(document).ready(function() {
    // Code fuer das Attribute Tooltip
//    $("#attr_popup").popup({
//            type: 'tooltip',
//            vertical: 'topedge',
//            horizontal: 'leftedge'
//    });
//  CKEDITOR.config.irgendeinPlugin
	
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
	$("#vn_bearbeiten").click(function() {
		// TODO Dialog öffnen
	});
	
    // Einklappen der Kommentarboxen
//    $('.kk_kommtoggle').html('Einklappen');
//    var height = 0;
//    $('.kk_kommtoggle').click( function() {
//        var domelem = $(this).parent().get(0);
//        var jqueryobj = $(domelem);
//        if( jqueryobj.height()>50 ) {
//            height = jqueryobj.height();
//            jqueryobj.animate({
//                height: "15px"
//            }, 500 );
//            $(this).html('Ausklappen');
//        } else {
//            jqueryobj.animate({
//                height: ""+height+"px"
//            }, 500 );
//            $(this).html('Einklappen');
//        }
//    });
});

function fillVeranstaltungsSeite(Vid)
{
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
					
					// TODO hole eine liste mit den ersten 20, 30, ... karteikartenids vom server
					// hier noch mit samplejson sampleJSONIDs
					jsonKkIDs = sampleJSONIDs;
					
					
					json_length = Object.keys(jsonKkIDs).length; //anzahl der einträge im json
					newIdArray = sortiereKarteikartenIDs(jsonKkIDs);	//array in dem die ids in der gewünschten reihenfolge aufgelistet sind;
					
					ajaxArr = [];
					
					for(i=0;i<json_length;i++){			//startet für benötigte Karteikarten ajaxcalls und speichert diese in array
						ajaxArr[i] = getKarteikarteByID(newIdArray[i]);
					}
					
					$.when.apply($,ajaxArr).done(function() { //wenn alle fertig, werden diese erstellt und appended
						console.log("alle ajax calls für karteikarten fertig, array von karteikarten hier:")
						console.log(ajaxArr);
						for(i=0;i<json_length;i++){
							domElem = buildKarteikarte( jQuery.parseJSON(ajaxArr[i].responseText));
							$("#kk_all").append(domElem);
						}
					});
					
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
    
	return $.when(ajax1,ajax2,d);
}

function ladeKindKarteikarten(vaterId, vaterElem) {
    var params = {};
    params[paramId] = vaterId;
    // ersetze evntl bestehende Kindkarteikarten
    vaterElem.find("ul").remove();
    vaterElem = vaterElem.append("<ul></ul>").find("ul");
    return ajaxCall(
            karteikartenServlet,
            actionGetKarteikartenKinder,
            function(response) {
                // neu geladene Kindkarteikarten holen
                var arr = response[keyJsonArrResult];
                console.log(arr);
                // falls keine Kindkarteikarten vorhanden, tue nichts mehr
                if(arr.length == 0) {
                    console.log("hat keine kinder mehr");
                }
                // andernfalls DOM aufbauen
                else
                {
                    for(var i in arr)
                    {
                        var kkListItem = $("<li><a class='inhaltsvz_kk_knoten'>"+arr[i][paramTitel]+"</a></li>");
                        vaterElem.append(kkListItem);
                        // Lade bei Klick auf ein kkListItem dessen Kinder rekursiv
                        var f = function(arr, kkListItem, i) {
                            kkListItem.find("a").click(function(e) {
                                ladeKindKarteikarten(arr[i][paramId], kkListItem);
                                e.stopPropagation();
                            });
                        }
                        f(arr, kkListItem, i);
                        // Pseudo-Kind zum Hinzufuegen einer neuen Karteikarte
                        vaterElem.append("<li><a class='inhaltsvz_kk_erstellen'>Neu</a></li>");
                        //TODO Click Handler
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
	id_of_smallest_index =-1;
	smallest_index = -1;
	j_to_delete = -1;
	for(i=1; i<json_length+1;i++){
		for(j=1; j<json_length+1;j++){
			if(smallest_index == -1){
			//	console.log("set smallest index initial from -1 to:"+ jsonKkIDs["k_"+j].index);
				id_of_smallest_index = jsonKkIDs["k_"+j].id;
				smallest_index = jsonKkIDs["k_"+j].index;
				j_to_delete = j;
			}
			else if(jsonKkIDs["k_"+j].index < smallest_index){
			//	console.log("replace smallest index:"+ smallest_index + ",id: "+id_of_smallest_index);
				j_to_delete = j;
				id_of_smallest_index = jsonKkIDs["k_"+j].id;
				smallest_index = jsonKkIDs["k_"+j].index;
			//	console.log("with index: "+smallest_index+", id: "+ id_of_smallest_index);
			}
		}
	//	console.log("found smallest index: "+ smallest_index +"with id:"+ id_of_smallest_index);
		newIdArray[i-1]=id_of_smallest_index;
		jsonKkIDs["k_"+j_to_delete].index = 999999999;
		smallest_index = -1;
		id_of_smallest_index = -1;
	}
	return newIdArray;
}

