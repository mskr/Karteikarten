/**
 * @author Marius Kircher, Andreas Rottach
 */

// Hält das aktuelle Veranstaltungsobjekt der angezeigten Veranstaltung
var veranstaltungsObject;

$(document).ready(function() {
	// Handler für das Löschen der Veranstaltung
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
    	// Hole erste Kk id
    	id = $("#kk_all").children().first().attr("data-kkid");
    	// Falls diese existiert, lade Vorgänger
    	if(id != undefined)
    		loadPreKk(id);
    	// Andernfalls button ausblenden
    	else
			$(".kk_load_pre").addClass("animated zoomOut").fadeOut();
    });    
    
    $(".kk_load_after").click(function(){
    	// Hole letzte Kk id
    	id = $("#kk_all").children().last().attr("data-kkid");
    	// Falls diese existiert, lade Nachfolger
    	if(id != undefined)
			loadAfterKk(id);
    	// Andernfalls button ausblenden
    	else
			$(".kk_load_after").addClass("animated zoomOut").fadeOut();
    });
    
    // Popup erzeugen
	var popup = new PopupFenster($("#kk_export_popup_overlay"), 
			[$("#kk_export_cancel"),$("#kk_export_popup_close"),$("#kk_export_ok")], 
			// Close funktion. Alles wieder zurücksetzen
			function(){
	    		$(".kk_export_pdf_link").attr("href","");
	    		$(".kk_export_tex_link").attr("href","");
	    		$("#kk_export_popup").find(".kk_export_files").hide();
	    		$("#kk_export_popup").find("#kk_start_export").show();
	    		$("#kk_export_popup").find("#kk_start_export").html("PDF erstellen");
	    		$("#kk_export_popup").find(".load_kk_export").hide();
	    		$("#kk_export_popup").find("#kk_export_zurueck").show();	
	    		
	    		$('#kk_export_opt_notizen').prop('checked', false);
	    		$('#kk_export_opt_attrAnz').prop('checked', false);
	    		$('#kk_export_opt_Querverweise').prop('checked', false);
			}, 
			$("#kk_export_ok"),
			function(){
				return true;
			}, 
			undefined, 
			$("#kk_export_weiter"), 
			$("#kk_export_zurueck"));
	
    // Export handler für Karteikarten
    $("#kk_export").click(function(){
    	// Popup anzeigen
    	popup.show();
    });
    // Export der PDF starten
    $("#kk_start_export").one("click", function() {
		exportKkVonVn(veranstaltungsObject[paramId]);
	})
	
	
    // Inhaltsverzeichnis im Viewport halten
    stickyWrapperInhaltsverzeichniss = new Waypoint.Sticky({
        element: $("#kk_inhaltsverzeichnis"),
        wrapper: '<div class="inhaltsverzeichnis-sticky-wrapper" />'
    });
	
	// CopyLink Plugin zum Kopieren von Links in die Zwischenablage
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
/**
 * Startet den ExportVorgang
 * @param vnId Veranstaltung die exportiert werden soll
 * @returns Ajax Objekt
 */
function exportKkVonVn(vnId)
{
	// Gui updaten
	$("#kk_export_popup").find("#kk_start_export").html("Erstelle PDF....");
	$("#kk_export_popup").find(".load_kk_export").slideDown();
	$("#kk_export_popup").find("#kk_export_zurueck").hide();
	
	// Optionen für den Export in einem bool array speichern
	options = [];
	options.push($('#kk_export_opt_notizen').is(':checked'));
	options.push($('#kk_export_opt_attrAnz').is(':checked'));
	options.push($('#kk_export_opt_Querverweise').is(':checked'));
	
	// Parameter für ajax call zusammenbauen
	params = {};
	params[paramId] = vnId;
	params[paramExportOptions] = options;
	return ajaxCall(karteikartenServlet, actionExportSkript, function(response){
		// Wenn erfolgreich, dann Links für den Benutzer sichtbar machen
		$(".kk_export_pdf_link").attr("href",response[paramPDFFileName]);
		$(".kk_export_tex_link").attr("href",response[paramTexFileName]);
		$(".kk_export_pdf_link").show();
		$("#kk_export_popup").find("#kk_start_export").slideUp();
		$("#kk_export_popup").find(".kk_export_files").slideDown();
		$("#kk_export_error").hide();
		
	}, params, function(error, response) {
		// Bei einem Fehler, nur das TexDokument bereitstellen. Das sollte immer generiert werden können
		$("#kk_export_error").slideDown();
		$(".kk_export_pdf_link").hide();
		$(".kk_export_tex_link").attr("href",response[paramTexFileName]);
		$("#kk_export_popup").find("#kk_start_export").slideUp();
		$("#kk_export_popup").find(".kk_export_files").slideDown();
		
	}, undefined, function(){
		// Auf jedenfall immer den Click handler weider registrieren
		$("#kk_start_export").one("click", function() {
		    exportKkVonVn(veranstaltungsObject[paramId]);
		    });
	// Bis zu 20 Sek auf ajaxcall warten, da der export länger dauern könnte
	}, 20000);
}
/**
 * Füllt die Veranstaltungsseite.
 * @param Vid ID der Veranstaltung
 * @param kkId ID der Karteikarte die angezeigt werden soll.
 * Falls undefined wird Root Karteikarte angezeigt
 * @returns jQuery Deferred Ojekt, das resolved, wenn alles geladen
 */
function fillVeranstaltungsSeite(Vid, kkId)
{
    // Wir verwenden ein eigenes Deferred-Objekt um zurückzumelden, wenn alles geladen wurde.
    d = jQuery.Deferred();
    
    // Inhaltsverzeichniss zurücksetzen
    // Seite nach oben scrollen und klasse stuck entfernen
    $("body,html").scrollTop(0);
    $(".stuck").removeClass("stuck");
    
    // Veranstaltungsobjekt laden
    var params = {};
    params[paramId] = Vid;
    var ajax1 = ajaxCall(veranstaltungServlet,
        actionGetVeranstaltung,
        function(response) 
        {
            veranstaltungsObject = response;
            
            if(!veranstaltungsObject[paramAngemeldet] && jsonBenutzer[paramNutzerstatus] != "ADMIN" )
            {
                showError("Sie haben nicht die notwendingen Berechtigungen um diese Seite zu sehen!");
                gotoHauptseite();

                // Deferred Objekt als abgeschlossen markieren, wenn der Benutzer keine Rechte hat
                d.resolve();
                return;
            }
            // Studiengänge und Moderatoren für Veranstaltung laden
            $.when(findStudiengaenge(Vid)).done(function() {
                $.when(findModeratorenVn(Vid)).done(function() {
                    
                    // Wenn alles geladen wurde, Dokumenten-Titel setzen
                    titel = veranstaltungsObject[paramTitel];
                    document.title = titel;
                    
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
                    
                    if(checkIfAllowedVn(veranstaltungsObject, true, false, false))
                        $("#vn_rechte_info").text("Als Ersteller haben Sie volle Berechtigungen für diese Veranstaltung.");
                    else if(checkIfAllowedVn(veranstaltungsObject, false, true, false))
                        $("#vn_rechte_info").text("Als Administrator haben Sie volle Berechtigungen für diese Veranstaltung.");
                    else if(checkIfAllowedVn(veranstaltungsObject, false, false, true))
                    {
                        if(veranstaltungsObject[paramModeratorKkBearbeiten])
                            $("#vn_rechte_info").text("Als Moderator haben Sie Berechtigungen betreffend Änderungen an Karteikarten.");
                        else
                            $("#vn_rechte_info").text("Als Moderator mit eingeschränkten Rechten haben Sie Berechtigungen betreffend Änderungen an Kommentaren.");
                    }
                    else
                        $("#vn_rechte_info").text("Als Student haben Sie keine besonderen Berechtigungen in dieser Veranstaltung.");
                    
                    // Admin oder nicht ?
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
                    // Ist eine Karteikarte übergeben die angezeigt werden soll?
                    // Wenn nein, dann Wurzel anzeigen
                    if(kkId == undefined)
                        displayKarteikarte(veranstaltungsObject[paramErsteKarteikarte], function(){
        					$(".kk_load_pre").addClass("animated zoomOut").fadeOut();
        					
                            // Deferred Objekt als abgeschlossen markieren.
                            d.resolve();
                        }, false);
                    
                    	
                    // Wenn ja, dann diese Karteikarte anzeigen
                    else
                        displayKarteikarte(kkId, function(){
                            // Deferred Objekt als abgeschlossen markieren.
                            d.resolve();
                        }, false);
                });
            });
        },
        params
    );
    // Alle CK-Editoren im Karteikarten container entfernen
    destroyCKeditors($("#kk_all"));
    // Karteikarten löschen
	$("#kk_all").empty();
	// Studiengänge in auswahlliste anzeigen
	var ajax2 = ajaxCall(startseitenServlet,
			actionGetStudiengaenge,
			function(response) 
			{
				var studgArr = response[keyJsonArrResult];
				fillSelectWithOptions($("#vn_bearbeiten_auswahl_studiengang"),studgArr,jsonBenutzer[paramStudiengang],true);
			}
	); 

	// Semester in auswahlliste anzeigen
	var ajax3 =  ajaxCall(startseitenServlet,
			actionGetSemester,
			function(response) 
			{
				$("#vn_bearbeiten_auswahl_semester").empty();
		
				var studgArr = response[keyJsonArrResult];
				var aktSemesterString = response[paramAktSemester];
				var aktSemesterId = studgArr[0][paramId]; //default, falls kein match
		
				for(var i in studgArr) {
					$("#vn_bearbeiten_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
					if(aktSemesterString==studgArr[i][paramSemester]){
						aktSemesterId = studgArr[i][paramId];
					}
				}
				$("#vn_bearbeiten_auswahl_semester").find("option").sort(function(a,b) {
					return $(a).data('semesterid') > $(b).data('semesterid');
				}).appendTo('#vn_bearbeiten_auswahl_semester');
		
				$("[data-semesterid='"+aktSemesterId+"']").prop('selected', true);
				$("#vn_bearbeiten_auswahl_semester option[value='"+ response[paramAktSemester] +"']").prop('selected', true);
			}
	);
	
	// Inhaltsverzeichnis aufbauen
	// warte bis VN Objekt geladen
	$.when(ajax1,d).done(function() {
	    var ajax4 = undefined;
	    if(!veranstaltungsObject[paramAngemeldet])
	        return;
	    
	    // Inhaltsverzeichnis erstellen
	    vnInhaltsverzeichnis = null;
	    vnInhaltsverzeichnis = new Inhaltsverzeichnis($("#kk_inhaltsverzeichnis"),
	    		veranstaltungsObject,
	    		"<span>Karteikarten in</span><strong> "+veranstaltungsObject[paramTitel]+"</strong>",
	    		true,undefined,false,true);
	    // Inhaltsverzeichnis initialiseren
	    ajax4 = vnInhaltsverzeichnis.init();
	    
	    // warte bis alles geladen
	    $.when(ajax1,ajax2,ajax3,d,ajax4).done(function() {
 
	    	if(stickyWrapperInhaltsverzeichniss != undefined)
	    		stickyWrapperInhaltsverzeichniss.destroy();
	    	
	    	stickyWrapperInhaltsverzeichniss = new Waypoint.Sticky({
	            element: $("#kk_inhaltsverzeichnis"),
	            wrapper: '<div class="inhaltsverzeichnis-sticky-wrapper" />'
	        });
	    	
	        var afterLoadWaypoint = new Waypoint({
	    		element: $(".kk_load_after"),
	    		enabled: false,
	        	handler: function(direction) {
	        		if(direction == "down")
	        			$(".kk_load_after").trigger("click");
	        	},
	        	offset: 'bottom-in-view'
	        });
	        afterLoadWaypoint.enable();
	    });
        
	});
	
	return $.when(ajax1,ajax2,ajax3,d);
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
	if(id == undefined)
	{
		console.log("Kk id bei displayKarteikarte ist undefined. Warscheinlich keine Berechtigung. Sollte nicht passieren.");
		gotoHauptseite();
	}
    // Karteikarte schon in der Liste?
    kkDiv = $("#kk_all").find("[data-kkid=" + id + "]");
    
    // Karteikarte geladen und schon angezeigt, dann einfach nur dort hin scrollen
    if(kkDiv.length&&!reload)
    {
    	$('body,html').animate({
            scrollTop: kkDiv.offset().top-40},
            'normal', callback);
    }
    // Andernfalls neu laden
    else
    {
    	// zuerst wieder alle editoren entfernen
    	destroyCKeditors($("#kk_all"));
    	// Alle Karteikarten ausblenden
    	$("#kk_all").children().fadeOut(200).promise().done(function(){
    		// Danach Karteikarten komplett entfernen und Lade-Buttons anzeigen
    		Waypoint.destroyAll();
    		$("#kk_all").empty();
    		
    		// Nach oben scrollen
    	    $("body,html").scrollTop(0);
    	    $(".stuck").removeClass("stuck");
    	    
    	    // Inhaltsverzeichniss neu initialisieren
    	    if(stickyWrapperInhaltsverzeichniss != undefined)
	    		stickyWrapperInhaltsverzeichniss.destroy();
	    	
	    	stickyWrapperInhaltsverzeichniss = new Waypoint.Sticky({
	            element: $("#kk_inhaltsverzeichnis"),
	            wrapper: '<div class="inhaltsverzeichnis-sticky-wrapper" />'
	        });
	    	
    	    // Lade buttons anzeigen
        	showPreAfterLoad();
        	
        	// karteikarten laden
        	var params2 ={};
            params2[paramKkId] = id;
            params2[paramVnId] = veranstaltungsObject[paramId];
        	ajax = ajaxCall(karteikartenServlet, actionGetKarteikarteByID, function(response){
        		domkk = buildKarteikarte(response);
                domkk.removeAttr("style"); // Zeige KK durch entfernen von display:none
				domkk.css("opacity", "0");
        		$("#kk_all").append(domkk);
				domkk.animate({opacity: 1}, 200);

	        	$.when(loadAfterKk(id)).done(function(){
	        		
		        	if(callback != undefined) 
		        		callback();
	        	});
	        	
        	}, params2);
    	});
    }
}
// Speichert den Ajaxcall der Karteikarten die aktuell geladen werden
kkLoadRequest = null;
// True, wenn aktuell Nachfolger geladen werden
displayingAfterKK = false;
/**
 * Lädt die Nachfolger einer Karteikarte und zeigt sie am Ende des kk_all Wrappers an.
 * @param id
 * @returns jQuery Deferred Objekt, das resolved, wenn Nachfolger geladen
 */
function loadAfterKk(id)
{
	if (kkLoadRequest != null){ 
		kkLoadRequest.abort();
		kkLoadRequest = null;
	}
	
	var params2 ={};
    params2[paramKkId] = id;
    params2[paramVnId] = veranstaltungsObject[paramId];
	
    var promise = $.Deferred();
	kkLoadRequest = ajaxCall(karteikartenServlet,
			actionGetKarteikartenNachfolger, 
			function(response){

				if(displayingAfterKK)
					return;
				displayingAfterKK = true;
				Waypoint.disableAll();
				
				data = response[keyJsonArrResult];
				if(data.length < 5)
				{
					$(".kk_load_after").addClass("animated zoomOut").fadeOut();
				}

				domms = [];
				function nextItem()
				{
						o = data.shift();
						if(o == undefined)
							return;
						domkk = buildKarteikarte(o);
						domkk.removeAttr("style"); // Zeige KK durch entfernen von display:none
						$("#kk_all").append(domkk);
						domms.push(domkk);
						nextItem();

				}
				nextItem();
				Waypoint.enableAll();
				displayingAfterKK = false;
				promise.resolve();
	        }, 
	        params2,
            undefined,
            function() { $(".kk_load_after").addClass("loading").children().hide(); },
            function() { $(".kk_load_after").removeClass("loading").children().show(); }
	);
	return promise;
}
// True, wenn Vorgänger Karteikarten geladen werden.
displayingPreKK = false;

/**
 * Lädt die Vorgänger einer Karteikarte und zeigt sie am Anfang des kk_all Wrappers an.
 * @param id
 * @returns jQuery Deferred Objekt, das resolved, wenn Vorgaenger geladen
 */
function loadPreKk(id)
{
	if (kkLoadRequest != null){ 
		kkLoadRequest.abort();
		kkLoadRequest = null;
	}
	var params ={};
    params[paramKkId] = id;
    params[paramVnId] = veranstaltungsObject[paramId];

    var promise = $.Deferred();
	kkLoadRequest = ajaxCall(karteikartenServlet, 
			actionGetKarteikartenVorgaenger, 
			function(response)
			{
				if(displayingPreKK)
					return;
				displayingPreKK = true;
				Waypoint.disableAll();
				
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
                    domkk.removeAttr("style"); // Zeige KK durch entfernen von display:none
					domkk.css("opacity", "0");
					$("#kk_all").prepend(domkk);
					domms.push(domkk);
					nextItem();
				}
				nextItem();
				Waypoint.enableAll();
				displayingPreKK = false;
				promise.resolve();
				
			}, 
			params,
			undefined,
			function() { $(".kk_load_pre").addClass("loading").children().hide(); },
			function() { $(".kk_load_pre").removeClass("loading").children().show(); }
	);
	return promise;
}

/**
 * Zeigt den Vorgänger/Nachfolger-LadeButton an.
 */
function showPreAfterLoad()
{
	$(".kk_load_after").removeClass("animated").removeClass("zoomOut").show();
	$(".kk_load_pre").removeClass("animated").removeClass("zoomOut").show();
}

/**
 * Klassendefinition für Inhaltsverzeichnis
 * @param kkDivMain Main Container des Inhaltsverzeichnisses
 * @param htmlTitel Titel des Inhaltsverzeichnises
 * @param vnObjekt Veranstaltungsobjekt, das zum Inhaltsverzeichnis gehört.
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
 * @param extraGotoButton Boolean. Wenn true, dann werden kleine Buttons hinter jedem Eintrag angezeigt, mit dessen Hilfe kann man die aktuelle Karteikarte anzeigen.
 */
function Inhaltsverzeichnis(kkDivMain, vnObjekt, htmlTitel, zeigeErstellButtons, extraClickFunction, zeigeCheckboxes,extraGotoButton)
{
	this.kkDivMain = kkDivMain;
	this.htmlTitel = htmlTitel;
	this.vnObjekt = vnObjekt;
	this.zeigeErstellButtons = zeigeErstellButtons;
	if(extraClickFunction == undefined)
		extraClickFunction = function(){};
	this.extraClickFunction = extraClickFunction;
	this.zeigeCheckboxes = zeigeCheckboxes;
	this.extraGotoButton = extraGotoButton;
	this.highlightedKnoten = undefined;
	this.gotoIsClicked = false;
}
/**
 * Initialisiert das Inhaltsverzeichnis
 */
Inhaltsverzeichnis.prototype.init = function()
{
	if(this.htmlTitel != undefined)
		this.kkDivMain.find("#kk_inhaltsverzeichnis_titel").html(this.htmlTitel);
    
	return this.ladeKinder(this.vnObjekt[paramErsteKarteikarte], this.kkDivMain, true);
}
/**
 * Registriert den Erstellen-Handler zum gegebenen dom element.
 * @param domErstellenLink
 * @param vater
 * @param bruder
 */
Inhaltsverzeichnis.prototype.registerErstellKkHandler = function(domErstellenLink, vater,bruder){
	// Prüfen ob erlaubt
	if(checkIfAllowedVn(veranstaltungsObject, true, true, false) ||
			(checkIfAllowedVn(veranstaltungsObject, false, false, true) &&
					veranstaltungsObject[paramModeratorKkBearbeiten]))
	{
		domErstellenLink.find(".inhaltsvz_kk_erstellen").click(function(){
			newKarteikarte(vater,bruder);
		});
	}
	else
	{
		domErstellenLink.find(".inhaltsvz_kk_erstellen").attr("style","visibility: hidden");
	}
}

/**
 * Generische Methode, die alle direkten Kindkarteikarten zu einer gegebenen Vater-ID
 * vom Server laedt und in eine Unordered List einfuegt. Es wird ein Click Handler registriert,
 * der beim Klick auf ein ListItem rekursiv dessen direkte Kinder laedt usw.
 * @param vaterId ID der Vaterkarteikarte
 * @param vaterElem jQuery Objekt. Container, in den die Unordered List eingefuegt wird.
 * @param initialVisible True, wenn die Kinder direkt angezeigt werden sollen.
 * @returns Ajax Objekt
 */
Inhaltsverzeichnis.prototype.ladeKinder = function(vaterId, vaterElem, initialVisible) {
    var params = {};
    params[paramKkId] = vaterId;
    params[paramVnId] = this.vnObjekt[paramId];
    // Evntl bestehende Kindkarteikarten aushaengen
    vaterElem.find("ul").remove();
    // Neue Liste aufbauen
    vaterElem = vaterElem.append("<ul></ul>").find("ul");
    vaterElem.css("display","none");
    var that = this;
    return ajaxCall(
            karteikartenServlet,
            actionGetKarteikartenKinder,
            function(response) {
                // neu geladene Kindkarteikarten holen
                var arr = response[keyJsonArrResult];
                // Erstell Button, falls keine Kinder vorhanden
                if(arr.length == 0)
                {
                    if(!that.zeigeErstellButtons)
                    {
                        elem = $("<li class='kk_baum_keine_kinder'>Nichts anzuzeigen</li>");
                    }
                    else
                    {
                        elem = $("<li ><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                        that.registerErstellKkHandler(elem,vaterId,-1);
                    }
                    elem.appendTo(vaterElem);//.slideDown();
                }
                // andernfalls DOM aufbauen
                else
                {
                    // Erstell Button vor Kk
                    if(that.zeigeErstellButtons)
                    {
                        elem = $("<li><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                        elem.appendTo(vaterElem).slideDown();
                        that.registerErstellKkHandler(elem,vaterId,-1);
                    }
                    for(var i in arr)
                    {
                        var kkListItem = "<li data-ausgeklappt='false' data-kinder-geladen='false' data-kkid='"+arr[i][paramId]+"'><span class='li_header'>";
                        if(that.zeigeCheckboxes)
                        {
                            // Kind Karteikarte mit Checkbox
                            kkListItem += "<input type='checkbox' data-kkid='"+arr[i][paramId]+"'>";
                        }
                        // Kind Karteikarte ohne Checkbox
                        kkListItem += "<a data-kkid='"+arr[i][paramId]+"' class='inhaltsvz_kk_knoten'>"+arr[i][paramTitel] + "</a>";
                        
                        if(that.extraGotoButton)
                        	kkListItem +=" <a class='gotoKk tiptrigger tipabove' data-tooltip='Zu Karteikarte springen'><span class='octicon octicon-move-right'></span></a>";
                        
                        kkListItem += "</span></li>";
                        
                        kkListItem = $(kkListItem);
                        kkListItem.appendTo(vaterElem);//.slideDown();
                        // Click Handler fuer Kk Knoten
                        var f = function(arr, kkListItem, i) {
                        	kkListItem.find(".gotoKk").click(function(e){
                        		if(!that.gotoIsClicked)
                        		{
                        			that.gotoIsClicked = true;
                        			displayKarteikarte(arr[i][paramId], function() {
                        				that.highlightKnoten(kkListItem);
                        				that.gotoIsClicked = false;
                        			});
                        		}
                        	});
                        	
                            kkListItem.find(".inhaltsvz_kk_knoten").click(function(e) {
                                var ajax;
                                var istAusgeklappt = kkListItem.attr("data-ausgeklappt")=='true';
                                var kinderGeladen = kkListItem.attr("data-kinder-geladen")=='true';

                                istAusgeklappt = !istAusgeklappt;
                                // Toggeln
                                ajax = that.kkAufEinklappen(kkListItem,istAusgeklappt);
                                
                                // Vom Aufrufer definierte Funktion ausfuehren
                                that.extraClickFunction(arr, kkListItem, i, e, ajax, istAusgeklappt);
                                // Ausbreitung des Events verhindern
                                e.stopPropagation();
                            });
                        }
                        f(arr, kkListItem, i);
                        // Erstell Button nach Kk
                        if(that.zeigeErstellButtons)
                        {
                            elem = $("<li><a class='inhaltsvz_kk_erstellen'>Erstellen</a></li>");
                            elem.appendTo(vaterElem);//.slideDown();
                            that.registerErstellKkHandler(elem,vaterId,arr[i][paramId]);
                        }
                    }
                }
                if(initialVisible)
                {
                	vaterElem.slideDown(); 
                	vaterElem.parent().attr("data-ausgeklappt", "true");
                	vaterElem.parent().attr("data-kinder-geladen", "true");
                }
                else
                {
                	vaterElem.slideUp(); 
                	vaterElem.parent().attr("data-ausgeklappt", "false");
                	vaterElem.parent().attr("data-kinder-geladen", "true");
                }
            },
            params
    );
}
/**
 * Highlighted den gegebenen Knoten und alle seiner Eltern
 * @param kkKnoten Knoten, der gehighlited werden soll.
 */
Inhaltsverzeichnis.prototype.highlightKnoten = function (kkKnoten) {
	if(this.highlightedKnoten != undefined)
		this.unhighlightKnoten();
	
    kkKnoten.children(".li_header").css("font-weight","bold");
    kkKnoten.children(".li_header").css("color","white");
    kkKnoten.css("border-left","2px solid white");
    kkKnoten.parents("li").css("border-left","2px solid white");
    this.highlightedKnoten = kkKnoten;
}
/**
 * Unhighlighted alle Knoten des Inhaltsverzeichnisses
 */
Inhaltsverzeichnis.prototype.unhighlightKnoten = function () {
	this.kkDivMain.find("li").removeAttr("style");
	this.kkDivMain.find(".li_header").removeAttr("style");
	this.highlightedKnoten = undefined;
}
/**
 * Klappt alle Knoten des Inhaltsverzeichnisses ein
 */
Inhaltsverzeichnis.prototype.alleEinklappen = function () {
	this.kkAufEinklappen(this.kkDivMain,false);
}
/**
 * Klappt die Kinder des Übergebenen Knotens ein bzw. auf.
 * @param liVater
 * @param aufklappen
 * @returns deferred
 */
Inhaltsverzeichnis.prototype.kkAufEinklappen = function (liVater, aufklappen)
{
    var istAusgeklappt = liVater.attr("data-ausgeklappt")=='true';
    var kinderGeladen = liVater.attr("data-kinder-geladen")=='true';
    var kkId = liVater.attr("data-kkid");
    var that = this;
    var result = $.Deferred();
    
    if(kkId == undefined)
    	return result.resolve();
    
    // etwas zu tun?
    if(istAusgeklappt != aufklappen)
    {
        // Lade Kindkarteikarten rekursiv, falls noch nicht geschehen
    	if(!kinderGeladen)
	    {
	        ajax = this.ladeKinder(kkId, liVater, true);
	        $.when(ajax).done(function(){
	        	liVater.attr("data-kinder-geladen","true");
	        	liVater.attr("data-ausgeklappt", "true");
	        });
	    	istAusgeklappt = true;
	    	kinderGeladen = true;
	    	return ajax;
	    }
    	// andernfalls auf oder zuklappen
    	else
    	{
    		if(aufklappen){
    			liVater.children("ul").slideDown(); 
	        	liVater.attr("data-ausgeklappt", "true");
    		}
    		else
    		{
    			liVater.children("ul").slideUp();
	        	liVater.attr("data-ausgeklappt", "false");
	        	
    			// Rekursiv alles einklappen
    			$.each(liVater.children("ul").children(),function(i,v){
    				that.kkAufEinklappen($(v), false);
    			});
    		}
    		return result.resolve();
    	}
    }    
}
/**
 * Klappt einen gegebenen Eintrag inklusive alle seiner Eltern auf
 * @param kkID KkID des Eintrags
 */
Inhaltsverzeichnis.prototype.showEintrag = function (kkID)
{
	this.showEintragInternal(this.kkDivMain,kkID);
}
/**
 * Durchsucht rekursiv den Baum und klappt jeden Vater der gesuchten Karteikarte auf. Diese funktion lädt die Kinder vom Server, falls notwendig.
 * @params startElem Start Div ab dem die Suche beginnt.
 * @params kkID gesuchte Karteikarten ID
 */
Inhaltsverzeichnis.prototype.showEintragInternal = function (startElem, kkID)
{
    var that = this;
	var f = function(){
	    // Hole Liste mit Kindern der naechsten Ebene, die keine "Erstellen"-buttons sind
	    var kkArr = startElem.children("ul").children("[data-kkid]");
	    if(kkArr.length == 0) 
	    	return;
	    
	    var isFound = false;
	    // Suche in der aktuellen Ebene nach der ID
	    kkArr.each(function(i, elem) {
	    	elem = $(elem);
	        var currentKkID = elem.attr("data-kkid");
	        if(currentKkID == kkID)
	        {
	        	// Wenn gefunden Knoten hervorheben
	        	that.highlightKnoten(elem);
	        	
	        	
	        	$.each(elem.parents("li[data-kkid]").siblings(), function(i,v){
	        		that.kkAufEinklappen($(v),false);
	        	});
	        	$.each(elem.siblings(), function(i,v){
	        		that.kkAufEinklappen($(v),false);
	        	});
	        	$.each(elem.children("ul").children("li[data-kkid]"), function(i,v){
	        		that.kkAufEinklappen(elem,false);
	        	});
	        	
	            // Aktuellen Knoten und eltern aufklappen
	        	$.each(elem.parents("li[data-kkid]"), function(i,v){
	        		that.kkAufEinklappen($(v),true);
	        	});
	        	isFound = true;
	        	return false; // break loop
	        }
	    });
	    if(!isFound)
	    {
	    	kkArr.each(function(i, elem) {
		    	elem = $(elem);
		    	
		    	var istAusgeklappt = elem.attr("data-ausgeklappt")=='true';
	        	var kinderGeladen = elem.attr("data-kinder-geladen")=='true';
		        var currentKkID = elem.attr("data-kkid");
	
	        	// Ist naechste Ebene bereits ausgeklappt starte sofort rekursiven Aufruf
	        	if(kinderGeladen)
	        	{
	        		that.showEintragInternal(elem, kkID);
	        	}
	        	// Andernfalls lade naechste Ebene und starte danach rekursiven Aufruf
	        	else
	        	{
	        		// Alle unsichtbar laden
	        		$.when( that.ladeKinder(currentKkID, elem, false )).done(function() {
	        			that.showEintragInternal(elem, kkID);
	        		});
	        	}
	    	});
	    }
	};
	f();
	
}
