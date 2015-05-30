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
			$(".kk_load_pre").addClass("animated zoomOut").fadeOut();
			displayKarteikarte(veranstaltungsObject[paramErsteKarteikarte], null, false);
    	}
    });    
    $(".kk_load_after").click(function(){
    	id = $("#kk_all").children().last().attr("data-kkid");
    	if(id != null)
			loadAfterKk(id);
    	else
			$(".kk_load_after").addClass("animated zoomOut").fadeOut();
    });
    
    
    
    $("#kk_export").click(function(){
    	popupFenster($("#kk_export_popup_overlay"), 
    			[$("#kk_export_cancel"),$("#kk_export_popup_close"),$("#kk_export_ok")], 
    			function(){
		    		$(".kk_export_pdf_link").attr("href","");
		    		$(".kk_export_tex_link").attr("href","");
		    		$("#kk_export_popup").find(".kk_export_files").hide();
		    		$("#kk_export_popup").find("#kk_start_export").show();
		    		$("#kk_export_popup").find("#kk_start_export").html("PDF erstellen");
		    		$("#kk_export_popup").find(".load_kk_export").hide();
		    		$("#kk_export_popup").find("#kk_export_zurueck").show();
    			}, 
    			$("#kk_export_ok"), 
    			function(){
    				return true;
    			}, 
    			undefined, 
    			$("#kk_export_weiter"), 
    			$("#kk_export_zurueck"))
    });
    
    $("#kk_start_export").click(function() {
		exportKkVonVn(veranstaltungsObject[paramId]);
	})
	
	    // CopyLink
    // -> Probleme mit CSS 
    var clip = new ZeroClipboard($("#link_copy_to_cb"));
    clip.on("ready", function() {
      this.on("aftercopy", function(event) {
    	  showInfo("URL wurde in die Zwischenablage eingefügt!");
      });
      
      clip.on( "copy", function (event) {
    	  var clipboard = event.clipboardData;
    	  clipboard.setData( "text/plain", $("#link_copy_data").text());
    	});
    });
});

function exportKkVonVn(vnId)
{
	$("#kk_export_popup").find("#kk_start_export").html("Erstelle PDF....");
	$("#kk_export_popup").find(".load_kk_export").slideDown();
	$("#kk_export_popup").find("#kk_export_zurueck").hide();
	
	options = [];
	options.push($('#kk_export_opt_notizen').is(':checked'));
	options.push($('#kk_export_opt_komm').is(':checked'));
	options.push($('#kk_export_opt_attrAnz').is(':checked'));
	options.push($('#kk_export_opt_Querverweise').is(':checked'));
	
	params = {};
	params[paramId] = vnId;
	params[paramExportOptions] = options;
	return ajaxCall(karteikartenServlet, actionExportSkript, function(response){
		$(".kk_export_pdf_link").attr("href",response[paramPDFFileName]);
		$(".kk_export_tex_link").attr("href",response[paramTexFileName]);
		$("#kk_export_popup").find("#kk_start_export").slideUp();
		$("#kk_export_popup").find(".kk_export_files").slideDown();
	}, params, function() {
		$("#kk_export_ok").trigger("click");
	}, undefined, undefined, 20000);
}

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
						displayKarteikarte(veranstaltungsObject[paramErsteKarteikarte], null, false);
					else
						displayKarteikarte(kkId, null, false);
					
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
	    var ajax4 = initInhaltsverzeichnis();
	    // warte bis mainbox visible
	    $.when(ajax1,ajax2,d,ajax4).done(function() {
	        // Inhaltsverzeichnis im Viewport halten
	        var sticky = new Waypoint.Sticky({
	            element: $("#kk_inhaltsverzeichnis"),
	            wrapper: '<div class="inhaltsverzeichnis-sticky-wrapper" />'
	        });
	        
	        registerErstellKkHandler($(".inhaltsvz_kk_erstellen"));
	       
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

function initInhaltsverzeichnis(){
    $("#kk_inhaltsverzeichnis_titel").html("<span>Karteikarten in</span><strong> "+veranstaltungsObject[paramTitel]+"</strong>");
	return ladeInhaltsverzeichnisKinder(veranstaltungsObject[paramErsteKarteikarte], $("#kk_inhaltsverzeichnis"))
}

function ladeInhaltsverzeichnisKinder(vaterId, vaterElem) {
    return ladeKindKarteikarten(vaterId, vaterElem, true, function(arr, kkListItem, i, e, ajax, klappeAus) {
        // Bei Klick auf Knoten zur Kk scrollen und im Inhaltsvz hervorheben
        displayKarteikarte(arr[i][paramId], function() {
            inhaltsverzeichnisUnhighlightAll();
            inhaltsverzeichnisHighlightKnoten($(e.target));
        });
    }, false);
}

/**
 * Generische Methode, die alle direkten Kindkarteikarten zu einer gegebenen Vater-ID
 * vom Server laedt und in eine Unordered List einfuegt. Es wird ein Click Handler registriert,
 * der beim Klick auf ein ListItem rekursiv dessen direkte Kinder laedt usw.
 * @param vaterId ID der Vaterkarteikarte
 * @param vaterElem jQuery Objekt. Container, in den die Unordered List eingefuegt wird.
 * @param zeigeErstellButtons Boolean. Gibt an ob die Buttons zum Erstellen neuer KKs angezeigt werden.
 * @param extraClickFunction Funktion, die <i>zusaetzlich</i> zum Ein-/Ausklappen ausgefuehrt werden soll.
 * Diese Funktion erhaelt folgende Parameter:
 * <ul>
 * <li>arr = Array der vom Server empfangenen Kindkarteikarten (als JSON) auf Ebene des angeklickten Elements.</li>
 * <li>kkListItem = JQuery Object. Listitem, das die angeklickte Karteikarte wrappt.</li>
 * <li>i = Index der angeklickten Karteikarte im Array arr. Folglich liefert arr[i] die angeklickte Karteikarte als JSON.</li>
 * <li>e = Click-Event</li>
 * <li>ajax = JQuery Deferred Object. Resolved, wenn Kinder der angeklickten Karteikarte geladen.</li>
 * <li>klappeAus = Boolean. True, wenn Kinder durch den Klick ausgeklappt werden. False, wenn sie eingeklappt werden.</li>
 * </ul>
 * @param zeigeCheckboxes Boolean. Gibt an ob vor jedem Karteikarten-Knoten eine Checkbox platziert werden soll.
 * Diese Checkbox wird ein Attribut "data-kkid" mit der Karteikarten-ID haben.
 * @returns Ajax Objekt
 */
function ladeKindKarteikarten(vaterId, vaterElem, zeigeErstellButtons, extraClickFunction, zeigeCheckboxes) {
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
                // Erstell Button, falls keine Kinder vorhanden
                if(arr.length == 0)
                {
                    if(!zeigeErstellButtons)
                    {
                        elem = $("<li style='display:none class='kk_baum_keine_kinder'>Nichts anzuzeigen</li>");
                    }
                    else
                    {
                        elem = $("<li style='display:none'><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                        registerErstellKkHandler(elem);
                    }
                    elem.appendTo(vaterElem).slideDown();
                }
                // andernfalls DOM aufbauen
                else
                {
                    // Erstell Button vor Kk
                    if(zeigeErstellButtons)
                    {
                        elem = $("<li style='display:none'><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                        elem.appendTo(vaterElem).slideDown();
            	        registerErstellKkHandler(elem);
                    }
                    for(var i in arr)
                    {
                        var kkListItem = "<li style='display:none'>";
                        if(zeigeCheckboxes)
                        {
                            // Kind Karteikarte mit Checkbox
                            kkListItem += "<input type='checkbox' data-kkid='"+arr[i][paramId]+"'>";
                        }
                        // Kind Karteikarte ohne Checkbox
                        kkListItem += "<a data-kkid='"+arr[i][paramId]+"' class='inhaltsvz_kk_knoten'>"+arr[i][paramTitel]+"</a></li>";
                        kkListItem = $(kkListItem);
                        kkListItem.appendTo(vaterElem).slideDown();
                        
                        // Click Handler fuer Kk Knoten
                        var f = function(arr, kkListItem, i) {
                            kkListItem.find("a").click(function(e) {
                                var klappeAus;
                                var ajax;
                                // Lade Kindkarteikarten rekursiv, falls noch nicht geschehen
                                if($(e.target).siblings("ul").length == 0)
                                {
                                    ajax = ladeKindKarteikarten(arr[i][paramId], kkListItem, zeigeErstellButtons, extraClickFunction, zeigeCheckboxes);
                                    klappeAus = true;
                                }
                                // Andernfalls Kindkarteikarten einklappen und aushaengen
                                else
                                {
                                    $(e.target).siblings("ul").slideUp("normal", function() { $(this).remove() });
                                    klappeAus = false;
                                }
                                // Vom Aufrufer definierte Funktion ausfuehren
                                extraClickFunction(arr, kkListItem, i, e, ajax, klappeAus);
                                // Ausbreitung des Events verhindern
                                e.stopPropagation();
                            });
                        }
                        f(arr, kkListItem, i);
                        // Erstell Button nach Kk
                        if(zeigeErstellButtons)
                        {
                            elem = $("<li style='display:none'><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                            elem.appendTo(vaterElem).slideDown();
                	        registerErstellKkHandler(elem);
                        }
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

function displayKarteikarte(id, callback, reload){
	//reload = true, wenn neu geladen werden soll
    // Karteikarte schon in der Liste?
    kkDiv = $("#kk_all").find("[data-kkid=" + id + "]");
    if(kkDiv.length&&!reload)
    {
    	$('html,body').animate({
            scrollTop: kkDiv.offset().top},
            'normal', callback);
    }
    else
    {
    	if(reload)
    	{
    		kkDiv.remove();
    	}
    	destroyCKeditors($("#kk_all"));
    	$("#kk_all").children().fadeOut(200).promise().done(function(){
    		$("#kk_all").empty();
        	showPreAfterLoad();

        	var params2 ={};
        	params2[paramId] = id;
        	ajax = ajaxCall(karteikartenServlet, actionGetKarteikarteByID, function(response){
        		domkk = buildKarteikarte(response);
				domkk.show();
				domkk.css("opacity", "0");
        		$("#kk_all").append(domkk);
				domkk.animate({opacity: 1}, 200);
        	}, params2);

        	$.when(ajax).done(function(){
        		loadAfterKk(id);
        		if(callback != undefined) callback();
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
					$(".kk_load_after").addClass("animated zoomOut").fadeOut();
				}

				domms = [];
				function nextItem()
				{
						o = data.shift();
						if(o == undefined){
							$.each(domms,function(i, obj){
								domms[i].animate({opacity: 1}, 200);
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
				
	        }, 
	        params2,
            undefined,
            function() { $(".kk_load_after").addClass("loading").children().hide(); },
            function() { $(".kk_load_after").removeClass("loading").children().show(); }
	);
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
					$(".kk_load_pre").addClass("animated zoomOut").fadeOut();
				}
				
				domms = [];

				function nextItem()
				{
					o = data.shift();

					if(o == undefined){
						$.each(domms,function(i, obj){
							domms[i].animate({opacity: 1}, 200);
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
				
			}, 
			params,
			undefined,
			function() { $(".kk_load_pre").addClass("loading").children().hide(); },
			function() { $(".kk_load_pre").removeClass("loading").children().show(); }
	);
	return kkLoadRequest;
}

function showPreAfterLoad()
{
	$(".kk_load_after").show();
	$(".kk_load_pre").show();
}
