/**
 * @author mk
 */
/***
 * Pacth for dialog-fix ckeditor problem [ by ticket #4727 ]
 * 	http://dev.jqueryui.com/ticket/4727
 */

// Statische Handler einmal registrieren
$(document).ready(function() {
	
    registerSuchEvent();
    
//    $.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
//        _title: function(title) {
//            if (!this.options.title ) {
//                title.html("&#160;");
//            } else {
//                title.html(this.options.title);
//            }
//        }
//    }));
    
    // Code fuer das Veranstaltung erstellen Popup
    $('#vn_erstellen_bt').click(function() {
        // jQuery-UI auskommentiert
//    	$('#vn_erstellen_popup').dialog({
//    	    show: "fold",   //  the animation on show
//    	    hide: "fold",    // the animation on close
//    	    resizable: false,   // prevents user from resizing
//    	    closeOnEscape: true,    //  esc key will close dlg
//    	    modal: true,
//    	    width: '40em',
//    	    title: "<div class='popup_fenster_titel'>" + 
//		"<span class='octicon octicon-podium'></span>" +
//		"<span class='popup_fenster_ueberschrift'> Veranstaltung erstellen</span>" +
//		"</div>", // dlg title in ttl bar
//    	    buttons: {},
//    	    open: function(event, ui) { 
//    	        //hide close button.
//    	        $(this).parent().children().children('.ui-dialog-titlebar-close').hide();
//    	    },
//    	    close: function(e) {
//    	    	if(jsonBenutzer == undefined)
//    	    		return;
//
//    	    	$("#vn_titel_input").val("");
//    	    	// TODO
////  	    	$("#vn_erstellen_auswahl_semester [value='" + + "']").prop("selected", true);
//    	    	$("#vn_erstellen_auswahl_studiengang [value='" + jsonBenutzer[paramStudiengang]+ "']").prop("selected", true);
//
//    	    	$("#vn_pass_input").val("");
//    	    	$("#vn_beschr_input").val("");
//    	    	$("input[name=vn_bearbeitenMode_radiogb][value='Nur ich']").prop("checked", true);
//    	    	$("#vn_komm_erlaubt").prop("checked", true);
//    	    	$("#vn_bew_erlaubt").prop("checked", true);
//    	    	$("#vn_mod_list").children().remove();
//    	    	$("#vn_mod_input").val("");
//    	    	$("#vn_mod_vorschlag").slideUp(100);
//    	    	selectedModList = {};
//    	    }
//    	});
        popupFenster($("#vn_erstellen_popup_overlay"), "open", $("#vn_titel_input"));
        // Aktiviert den CK-Editor
        $("#vn_beschr_input").ckeditor();
        
	})
	
	$('#vn_popup_close').click(function() {
        popupFenster($("#vn_erstellen_popup_overlay"), "close");
		var editor = $('#vn_beschr_input').ckeditorGet();
        editor.destroy();
	});
    
	
//    $('#vn_erstellen_popup').popup({
//    	openelement: '#vn_erstellen_bt',
//    	closeelement: '#vn_popup_close',
//    	focuselement: '#vn_titel_input',
//        blur: false,
//    	transition: 'all 0.3s',
//    	onclose : function() {
//    		if(jsonBenutzer == undefined)
//    			return;
//    		
//    		$("#vn_titel_input").val("");
//    		// TODO
////  		$("#vn_erstellen_auswahl_semester [value='" + + "']").prop("selected", true);
//    		$("#vn_erstellen_auswahl_studiengang [value='" + jsonBenutzer[paramStudiengang]+ "']").prop("selected", true);
//    		
//    		$("#vn_pass_input").val("");
//    		$("#vn_beschr_input").val("");
//    		$("input[name=vn_bearbeitenMode_radiogb][value='Nur ich']").prop("checked", true);
//    		$("#vn_komm_erlaubt").prop("checked", true);
//    		$("#vn_bew_erlaubt").prop("checked", true);
//    		$("#vn_mod_list").children().remove();
//			$("#vn_mod_input").val("");
//			$("#vn_mod_vorschlag").slideUp(100);
//    		selectedModList = {};
//    	}
//    });


	$("#vn_alle_auswahl_studiengang").change(function() {
		leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
				   $("#vn_alle_auswahl_studiengang").val());
	});
 
	$("#vn_alle_auswahl_semester").change(function() {
		leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
				   $("#vn_alle_auswahl_studiengang").val());
	});

    // Globaler Handler fuer das x zum Schliessen des Suchergebnis-Containers
    $("#sucherg_x").click(function() {
        $("#suche_global_input").val("");
    });
	
	registerVeranstErzeugeHandler();
});

function fillHauptseite() 
{
	// Studiengänge in auswahlliste anzeigen
	var ajax1 = ajaxCall(startseitenServlet,
		actionGetStudiengaenge,
		function(response) 
		{
			var studgArr = response[keyJsonArrResult];
			fillSelectWithOptions($("#vn_alle_auswahl_studiengang"),studgArr,jsonBenutzer[paramStudiengang],true);
			fillSelectWithOptions($("#vn_erstellen_auswahl_studiengang"),studgArr,jsonBenutzer[paramStudiengang],true);
		}
	); 


	// Semester in auswahlliste anzeigen
	var ajax2 =  ajaxCall(startseitenServlet,
		actionGetSemester,
		function(response) 
		{
			$("#vn_alle_auswahl_semester").empty();
			$("#vn_erstellen_auswahl_semester").empty();
			
			var studgArr = response[keyJsonArrResult];
			var aktSemesterString = response[paramAktSemester];
			var aktSemesterDI = 1; //default, falls kein match
			
			for(var i in studgArr) {
				$("#vn_alle_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
				$("#vn_erstellen_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
				if(aktSemesterString==studgArr[i][paramSemester]){
					aktSemesterDI = Number(i)+1;
				}
			}
			
			$("#vn_alle_auswahl_semester").find("option").sort(function(a,b) {
				return $(a).data('semesterid') > $(b).data('semesterid');
			}).appendTo('#vn_alle_auswahl_semester');
	
	
			$("#vn_erstellen_auswahl_semester").find("option").sort(function(a,b) {
				return $(a).data('semesterid') > $(b).data('semesterid');
			}).appendTo('#vn_erstellen_auswahl_semester');
			
			$("[data-semesterid='"+aktSemesterDI+"']").prop('selected', true);
			$("#vn_erstellen_auswahl_semester option[value='"+ response[paramAktSemester] +"']").prop('selected', true);
	
		}
	);

	if(jsonBenutzer[paramNutzerstatus] == "ADMIN" || jsonBenutzer[paramNutzerstatus] == "DOZENT")
	{
		$("#vn_erstellen_bt").show();
	}
	else
	{
		$("#vn_erstellen_bt").hide(); // TODO Hier vielleicht besser remove(), dann kann ein Hacker ihn nicht mehr einblenden
	}

    return $.when(ajax1,ajax2).done(fillVeranstaltungsliste);
}

function fillVeranstaltungsliste() 
{
    return $.when(leseVeranstaltungenMeine(),leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
    												   $("#vn_alle_auswahl_studiengang").val()));
}

function leseVeranstaltungenMeine()
{
	// Meine Veranstaltungen
	var params = {};
	params[leseVeranstMode] = leseVeranstModeMeine;
	return ajaxCall(veranstaltungServlet,
		actionLeseVeranst,
		function(response) 
		{
			var divMeineVeranst = $("#vn_tabcontent_meine");
			displayVeranstaltungen(divMeineVeranst, response);
		},
		params
	);
}

function leseVeranstaltungenSemesterStudiengang(semesterName, studiengangName)
{
	// Semester Veranstaltungen
	var params = {};
	params[leseVeranstMode] = leseVeranstModeStudiengangSemester;
	params[paramGewaehltesSemester] = semesterName;
	params[paramGewaehltesStudiengang] = studiengangName;
	
	return ajaxCall(veranstaltungServlet,
		actionLeseVeranst,
		function(response) 
		{
			var divSemesterVeranst = $("#vn_tabcontent_alle");
			displayVeranstaltungen(divSemesterVeranst, response);
		},
		params
	);
}
function displayVeranstaltungen(container, ajaxResult)
{
	if(verifyResponse(ajaxResult))
	{
		var veranstObjekte = ajaxResult[keyJsonArrResult];
		// Alle Veranstaltungen entfernen
		container.children().not(".vn_toolbar").remove();

		for(var i in veranstObjekte)
		{
			displayVeranstaltung(container, veranstObjekte[i]);
		}
	}
}

var checkedRadio; // Der Radio Button zur derzeit ausgeklappten Veranstaltung

/**
 * Zeigt eine Veranstaltung im angegeben Container an.
 * @param container Übergeordneter Container
 * @param jsonVeranstObj Objekt, dass die Veranstaltung enthält
 */
function displayVeranstaltung(container, jsonVeranstObj)
{
	if(verifyResponse(jsonVeranstObj))
	{
		// Baue zuerst eine eindeutige id fuer die jetzt einzutragende Veranstaltung
		// id hat die Form "vn_<tab>_<DatenbankID>"
		var id = "vn_"+container.attr("id").split("_")[2]+"_"+jsonVeranstObj[paramId];
		var str = "";

		if(jsonVeranstObj[paramAngemeldet] == true)
			str += "<div id='"+id+"' class='vn vn_eingeschrieben'>";
		else
			str += "<div id='"+id+"' class='vn'>";


		str +=		"<input id='"+id+"_radio' type='radio' class='vn_mehr_einbl_toggle' name='vn' style='display:none'>" +
		"<label for='"+id+"_radio' class='vn_mehr_einbl'>" +
		"<span class='octicon octicon-triangle-down'></span>" +
		"</label>";

		if(jsonVeranstObj[paramAngemeldet] == true)
			str +=    "<a id='"+id+"_titel' class='vn_titel'>" + jsonVeranstObj[paramTitel] + "</a>";
		else
			str += "<span id='"+id+"_titel' class='vn_titel'>" + jsonVeranstObj[paramTitel] + "</span>";


		str +=		"<span class='vn_details'>" +
		"<a class='vn_dozent'>" + jsonVeranstObj[paramErsteller][paramVorname]+ " " + jsonVeranstObj[paramErsteller][paramNachname] + "</a><br>" +
		"<a class='vn_detail'>" + jsonVeranstObj[paramAnzTeilnehmer] + " Teilnehmer</a><br>" +
		"<a class='vn_detail'>" + jsonVeranstObj[paramSemester] + "</a>" +
		"</span>" +
		"<div id='"+id+"_mehr_wrapper' class='vn_mehr_wrapper'>" +
		"	<span class='vn_beschreibung'>" + jsonVeranstObj[paramBeschr] + "</span>" +
		"	<div class='vn_optionen'>";


		if(jsonVeranstObj[paramAngemeldet] == true)
			str += "<a class='vn_einausschreiben '><span class='octicon octicon-x'></span> Ausschreiben</a>";
		else
		{
			str += "<a class='vn_einausschreiben'><span class='octicon octicon-rocket'></span> Einschreiben</a>";
			if(jsonVeranstObj[paramKennwortGesetzt] == true)
			{
				str += "<form class='vn_zugangspasswort_form' style='display:none'><input class='input_std vn_zugangspasswort_input' type='password' placeholder='Zugangspasswort' required><input style='display:none' type='submit'></form>";
			}
		}

		str +=		"	</div>" +
		"</div>" +
		"</div>";

		str = $(str);
		container.append(str);

		// Ein-/Ausklappen
		// (jetzt in CSS mit versteckten Radiobuttons realisiert, sodass nur 1 Veranstaltung gleichzeitig aufgeklappt sein kann)
		$("#"+id+"_radio").click(function() {
			var vnIDaktuell = "vn_"+this.id.split("_")[1]+"_"+this.id.split("_")[2];
			var vnIDdavor = "";
			if(checkedRadio != undefined)
				vnIDdavor = "vn_"+checkedRadio.id.split("_")[1]+"_"+checkedRadio.id.split("_")[2];
			if( checkedRadio == this )
			{
				this.checked = false;
				checkedRadio = undefined;
				$("#"+vnIDaktuell).toggleClass("focused");
				$("#"+vnIDaktuell+"_mehr_wrapper").slideUp();
			}
			else
			{
				$("#"+vnIDdavor+"_mehr_wrapper").slideUp();
				$("#"+vnIDdavor).toggleClass("focused");
				checkedRadio = this;
				$("#"+vnIDaktuell).toggleClass("focused");
				$("#"+vnIDaktuell+"_mehr_wrapper").slideDown();
			}
		});

		// Titel Click Handler
		if(jsonVeranstObj[paramAngemeldet] == true)
		{
			$("#"+id+"_titel").click(function() {
				gotoVeranstaltung(jsonVeranstObj[paramId]);
			});
		}

		registerErstellerClickFunction(str,jsonVeranstObj);
		registerEinAusschreibenClickEvent(str, jsonVeranstObj);
	}
}

/**
 * Versieht den Namen des Veranstaltungserstellers mit einer Weiterleitung an dessen Profil.
 * @param vnHtmlString String mit dem HTML Knoten, der eine Veranstaltung darstellt
 * @param jsonVeranstObj Objekt, dass die Veranstaltung enthält
 */
function registerErstellerClickFunction(vnHtmlString, jsonVeranstObj) {
    var erstellerLink = vnHtmlString.find(".vn_dozent");
    erstellerLink.click(function() {
        gotoProfil(jsonVeranstObj[paramErsteller][paramId]);
    });
}

/**
 * Versieht den Link bzw. Button zum Ein- oder Ausschreiben mit Funktionalitaet.
 * @param vnHtmlString String mit dem HTML Knoten, der eine Veranstaltung darstellt
 * @param jsonVeranstObj Objekt, dass die Veranstaltung enthält
 */
function registerEinAusschreibenClickEvent(vnHtmlString, jsonVeranstObj) {
    var button = vnHtmlString.find(".vn_einausschreiben");
    if(jsonVeranstObj[paramAngemeldet] == true)
    {
        // AUSSCHREIBEN
        button.click(function() {
            sindSieSicher($(this), "", function() {
            	var params = {};
            	params[paramId] = jsonVeranstObj[paramId];
            	
            	ajaxCall(veranstaltungServlet,
                    actionAusschreiben, 
                    function(response) 
                    {
	            		showInfo("Sie haben sich abgemeldet von der Veranstaltung \"" + jsonVeranstObj[paramTitel] + "\".");
	            		fillVeranstaltungsliste();
                    },
                    params
                );
            });
        });
    }
    else
    {
        // EINSCHREIBEN
        button.click(function() {
            if(jsonVeranstObj[paramKennwortGesetzt])
            {
                // Einschreiben mit Kennwort
                var kennwortForm = vnHtmlString.find(".vn_zugangspasswort_form");
                var kennwortFeld = kennwortForm.find(".vn_zugangspasswort_input");
                var kennwort = "";
                button.hide();
                kennwortForm.show();
                kennwortFeld.focus();
                kennwortForm.submit(function(event) {
                    kennwort = kennwortFeld.val();
                    
                    var errorFkt = function(errorTxt) {
                		if(errorTxt == "loginfailed") 
                        {
                            showError("Ihr Zugangspasswort war falsch.");
                            kennwortFeld.toggleClass("shake2");
                            return true;
                        }
                		return false;
					}
                    var params = {};
                    params[paramId] = jsonVeranstObj[paramId];
                    params[paramPasswort] = escape(kennwort);
                    
                    ajaxCall(veranstaltungServlet,
                        actionEinschreiben, 
                        function(response) 
                        {
//                        		kennwortForm.html("<div style='color:GreenYellow'><span class='octicon octicon-check'></span> Ok</div>");
                        		showInfo("Sie sind nun eingeschrieben in der Veranstaltung \"" + jsonVeranstObj[paramTitel] + "\".");
                        		
                        		fillVeranstaltungsliste(function()
                        		{
                            		// Aktiviere den Alle-Tab
                                    $("#tab-2").prop("checked",true);
                                    // Klappe die entsprechende VN aus
                                    $("#vn_alle_"+jsonVeranstObj[paramId]+"_radio").trigger("click").prop("checked",true);
								});

                        },
                        params,
                        errorFkt
                    );
                    event.preventDefault();
                });
            }
            else
            {
                // Einschreiben ohne Kennwort
            	var params = {};
            	params[paramId] = jsonVeranstObj[paramId];
                ajaxCall(veranstaltungServlet,
                    actionEinschreiben, 
                    function(response) 
                    {
                		showInfo("Sie sind nun eingeschrieben in der Veranstaltung \"" + jsonVeranstObj[paramTitel] + "\".");
                		fillVeranstaltungsliste(function() {
                    		// Aktiviere den Alle-Tab
                            $("#tab-2").prop("checked",true);
                            // Klappe die entsprechende VN aus
                            $("#vn_alle_"+jsonVeranstObj[paramId]+"_radio").trigger("click").prop("checked",true);
						});
                    },
                    params
                );
            }
        });
    }
}

function registerSuchEvent()
{
    $("#suche_global_input").keydown(function(event) {
        if(event.keyCode == 40 || // Pfeil runter
           event.keyCode == 38 || // Pfeil hoch
           event.keyCode == 13 || // ENTER
           event.keyCode == 27)   // ESC
        {
            // Sende bei diesen Eingaben keinen Ajax Call
            // sondern navigiere in den Suchergebnissen
            handlePfeiltastenEvents(event.keyCode);
            event.preventDefault();
            return;
        }
        if($("#suche_ergebnisse").is(":hidden"))
        {
            $("#suche_ergebnisse").show();
        }
        suchTimer.reset();
        suchTimer.set();
    });
}

var suchTimer = function(){
    var that = this,
    time = 15,
    timer;
    that.set = function() {
    	timer = setTimeout(function(){

            $("#sucherg_vn").slideUp("fast").empty();
            $("#sucherg_benutzer").slideUp("fast").empty();
    		var suchString = $("#suche_global_input").val();
    		var params = {};
    		params[paramSuchmuster] = suchString;
    		ajaxCall(suchfeldServlet,
    			actionSucheBenVeranst,
    			function(response) {
					var arrSuchErgebnisse = response[keyJsonArrResult];
					fillSuchergebnisse(arrSuchErgebnisse);
					suchErgIterator = -1;
					$("#sucherg_vn, #sucherg_benutzer").slideDown("fast");
    			},
    			params
    		);
    		
        },400);
    }
    that.reset = function(){
        clearTimeout(timer);
    }
    return that;
}();

function fillSuchergebnisse(arrSuchErgebnisse)
{
    var isBenutzerLeer = true;
    var isVeranstLeer = true;
    for(var i in arrSuchErgebnisse)
    {
        var jsonSuchErgebnis = arrSuchErgebnisse[i];
        var klasse = jsonSuchErgebnis[keyJsonObjKlasse];
        var id = jsonSuchErgebnis[paramId];
        if(klasse == keyJsonObjKlasseBenutzer) {
            $("#sucherg_benutzer").append(
                    "<div id='sucherg_benutzer_"+id+"' class='sucherg_benutzer_item'><span class='octicon octicon-person'></span>" + jsonSuchErgebnis[paramVorname] + " " + jsonSuchErgebnis[paramNachname] + "</div>");
            isBenutzerLeer = false;
        } else if(klasse == keyJsonObjKlasseVeranst) {
            $("#sucherg_vn").append(
                    "<div id='sucherg_vn_"+id+"' class='sucherg_vn_item'><span class='octicon octicon-podium'></span>" + jsonSuchErgebnis[paramTitel] + "</div>");
            isVeranstLeer = false;
        }
        registerSucheClickEvent(jsonSuchErgebnis);
    }
    if(isBenutzerLeer)
    {
        $("#sucherg_benutzer").append("<div class='sucherg_vn_leer'>Keine Benutzer gefunden.</div>");
    }
    if(isVeranstLeer)
    {
        $("#sucherg_vn").append("<div class='sucherg_vn_leer'>Keine Veranstaltungen gefunden.</div>");
    }
}

function registerSucheClickEvent(jsonSuchErgebnis)
{
    var id = jsonSuchErgebnis[paramId];
    // klasse gibt an, ob es sich um einen Benutzer oder eine Veranstaltung handelt
    var klasse = jsonSuchErgebnis[keyJsonObjKlasse];
    if(klasse == keyJsonObjKlasseBenutzer)
    {
        // Bei Klick auf einen Benutzer, gehe zum Profil
        $("#sucherg_benutzer_"+id).click(function() {
            // Verberge die Suchergebnisse
            $("#sucherg_x").trigger("click");
            gotoProfil(id);
        });
    }
    else if(klasse == keyJsonObjKlasseVeranst)
    {
        // Bei Klick auf Veranstaltung, klappe entsprechende Veranstaltung in der Liste aus
        $("#sucherg_vn_"+id).click(function() {
            // Verberge die Suchergebnisse
            $("#sucherg_x").trigger("click");
            // Gehe zum Semester und zum Studiengang der Veranstaltung
            var semesterName = jsonSuchErgebnis[paramSemester];
            //TODO
            // 1. Hole die zu dieser Veranstaltung gehoerenden Studiengaenge
            // 2. Schaue, ob der Studiengang des angemeldeten Benutzers dabei ist
            // 3. Wenn ja, waehle diesen in der select Liste im Alle Tab und klappe die Veranstaltung aus
            // 4. Wenn nein, nehme den ersten Studiengang und waehle diesen in der select Liste
            var studiengangName;
            var params = {};
            params[paramId] = id;
            var studgAjax = ajaxCall(
                veranstaltungServlet, 
                actionGetStudgVn, 
                function(response) {
                    var studgArr = response[keyJsonArrResult];
                    studiengangName = studgArr[0];
                    for(var i in studgArr)
                    {
                        if(studgArr[i] == jsonBenutzer[paramStudiengang])
                        {
                            studiengangName = studgArr[i];
                        }
                    }
                    console.log("Waehle folgenden Studiengang "+studiengangName);
                },
                params
            );
            $.when(studgAjax).done(function() {
                // Waehle korrektes Semester und Studiengang
                $("#vn_alle_auswahl_studiengang").val(studiengangName);
                $("#vn_alle_auswahl_semester").val(semesterName);
                var ajax = leseVeranstaltungenSemesterStudiengang(semesterName, studiengangName);
                $.when(ajax).done(function() {
                    // Aktiviere den Alle-Tab
                    $("#tab-2").prop("checked",true);
                    
                    // Klappe die entsprechende VN aus
                    $("#vn_alle_"+id+"_radio").trigger("click").prop("checked",true);
                });
            });
        });
    }
}

var suchErgIterator = -1;

function handlePfeiltastenEvents(pressedKey) {
    var arr = $("#suche_ergebnisse").find(".sucherg_benutzer_item, .sucherg_vn_item");
    if(pressedKey == 40) // Pfeil runter
    {
        $(arr[suchErgIterator]).css({"background":"none", "color":"#4a4a4a"});
        if(suchErgIterator+1 < arr.length)
            suchErgIterator++;
    }
    else if(pressedKey == 38) // Pfeil hoch
    {
        $(arr[suchErgIterator]).css({"background":"none", "color":"#4a4a4a"});
        if(suchErgIterator-1 >= 0)
            suchErgIterator--;
    }
    else if(pressedKey == 13) // ENTER
    {
        $(arr[suchErgIterator]).trigger("click");
    }
    else if(pressedKey == 27) // ESC
    {
        $("#sucherg_x").trigger("click");
    }
    $(arr[suchErgIterator]).css({"background":"#4a4a4a", "color":"white"});
}


var selectedModList = {};
var modSuchTimer;
function registerVeranstErzeugeHandler() {
	
	$("#vn_erzeugen_cancel").click(function() {
	    popupFenster($("#vn_erstellen_popup_overlay"), "close");
	});
	
	$("#vn_erzeugen_ok").click(function(event) {
		var dialog = $("#vn_erstellen_popup");
		var titel = dialog.find("#vn_titel_input").val(),
			semester = dialog.find("#vn_erstellen_auswahl_semester").val(),
			studiengang = dialog.find("#vn_erstellen_auswahl_studiengang").val(),
			beschr = dialog.find("#vn_beschr_input").val(),
			moderatorenKkBearbeiten = dialog.find("input[name=vn_bearbeitenMode_radiogb]:checked").val() != "Nur ich",
			passw = dialog.find("#vn_pass_input").val(),
			kommentareErlaubt = dialog.find("#vn_komm_erlaubt").is(':checked'),
			bewertungenErlaubt = dialog.find("#vn_bew_erlaubt").is(':checked');
		
		var moderatorenIDs = [];
		
		for( var key in selectedModList)
		{
			moderatorenIDs.push(selectedModList[key][paramId]);
		}
	
		var params = {};
		params[paramTitel] = escape(titel);
		params[paramSemester] = escape(semester);
		params[paramStudiengang] = escape(studiengang);
		params[paramBeschr] = escape(beschr);
		params[paramModeratorKkBearbeiten] = moderatorenKkBearbeiten;
		params[paramKommentareErlauben] = kommentareErlaubt;
		params[paramBewertungenErlauben] = bewertungenErlaubt;
		params[paramPasswort] = escape(passw);
		params[paramModeratoren] = moderatorenIDs;
		
		var ajax = ajaxCall(veranstaltungServlet,
				actionErstelleVeranst,
				function(response) {
					showInfo("Veranstaltung \""+ titel +"\"wurde erfolgreich erzeugt.");
					dialog.dialog('close');
					fillVeranstaltungsliste();	
				},
				params
			);
	});
	
	// Triggert das eigene Enter-Event wenn key 13 gedrückt wurde
	$("#vn_mod_input").keyup(function(e)
	{
		if(e.keyCode == 37) // Pfeil links
		{
			// TODO 
		}
		else if(e.keyCode == 38) // Pfeil hoch
		{
			// TODO
		}
		else if(e.keyCode == 39) // Pfeil rechts
		{
			// TODO
		}
		else if(e.keyCode == 40) // Pfeil runter
		{
			// TODO
		}
		else if(e.keyCode == 13) // ENTER
		{
			// TODO
			$("#vn_mod_vorschlag").children().first().trigger("click");
		}
		else if(e.keyCode == 27) // ESC
		{
			$("#vn_mod_input").val("");
			$("#vn_mod_input").trigger("keyup");
		}
		// Falls etwas eingegeben wurde suchen, sonst Feld leeren
		else if($("#vn_mod_input").val() != "")
		{
			clearTimeout(modSuchTimer);
			modSuchTimer = setTimeout(function(){
				var params = {};
				params[paramSuchmuster] = $("#vn_mod_input").val();
				ajaxCall(suchfeldServlet,
					actionSucheBenutzer,
					function(response) 
					{
						// Alle Suchergebnisse entfernen
						$("#vn_mod_vorschlag").children().remove();
						// Neue Suchergebnisse holen
						var res = response[keyJsonArrResult];
						// Falls welche existieren
						if(res.length>0)
						{
							for(var i in res)
							{
								var x = $("<a>" + res[i][paramVorname] + " " + res[i][paramNachname] + "</a>");
	
								(function(benutzer) 
										{
									// Click handler für den Sucheintrag
									x.click(function()
											{
										addItemToList(selectedModList, $("#vn_mod_list"), 
												benutzer[paramVorname] + " " + benutzer[paramNachname], 
												benutzer, undefined,undefined);
	
										$("#vn_mod_input").val("");
										$("#vn_mod_vorschlag").slideUp(100);
	
											});
										})(res[i]);
	
	
								$("#vn_mod_vorschlag").append(x);
							}
							$("#vn_mod_vorschlag").slideDown(100);
						}
						else
							$("#vn_mod_vorschlag").slideUp(100);

					},
					params
				);

			},400);
		}
		else
			$("#vn_mod_vorschlag").fadeOut();
	});
}

