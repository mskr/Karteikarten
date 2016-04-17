/**
 * @author Andreas, Marius
 */

$(document).ready(function() {
	
    // Setup der Suchfunktion
    registerSuchEvent();

    // Reagiere auf Aenderung der Studiengangauswahl im Alle-Tab
	$("#vn_alle_auswahl_studiengang").change(function() {
		leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
				   $("#vn_alle_auswahl_studiengang").val());
	});

    // Reagiere auf Aenderung der Semesterauswahl im Alle-Tab
	$("#vn_alle_auswahl_semester").change(function() {
		leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
				   $("#vn_alle_auswahl_studiengang").val());
	});
	
	// Registriere die Handler fuer den Veranstaltung Erstellen Dialog
	registerVeranstErzeugeHandler();
});

/**
 * Zentrale Funktion zum Anzeigen der Hauptseite.
 * Wird von urlHandler aufgerufen, wenn location == hauptseite.
 * @returns jQuery Deffered, das resolved, wenn alle Ajax Calls fuer das Laden der Hauptseite abgeschlossen
 */
function fillHauptseite() 
{
	document.title = "Veranstaltungen";
    
	// Studiengänge in Auswahlliste anzeigen
	var ajax1 = ajaxCall(startseitenServlet,
		actionGetStudiengaenge,
		function(response) 
		{
			var studgArr = response[keyJsonArrResult];

			fillSelectWithOptions($("#vn_alle_auswahl_studiengang"),studgArr,jsonBenutzer[paramStudiengang],true);
			fillSelectWithOptions($("#vn_erstellen_auswahl_studiengang"),studgArr,jsonBenutzer[paramStudiengang],true);
		}
	); 


	// Semester in Auswahlliste anzeigen
	var ajax2 =  ajaxCall(startseitenServlet,
		actionGetSemester,
		function(response) 
		{
			$("#vn_alle_auswahl_semester").empty();
			$("#vn_erstellen_auswahl_semester").empty();
			
			var studgArr = response[keyJsonArrResult];
			var aktSemesterString = response[paramAktSemester];
			var aktSemesterId = studgArr[0][paramId]; //default, falls kein match

			for(var i in studgArr) {
				$("#vn_alle_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
				$("#vn_erstellen_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
				if(aktSemesterString==studgArr[i][paramSemester]){
					aktSemesterId = studgArr[i][paramId];
				}
			}
			
			$("#vn_alle_auswahl_semester").find("option").sort(function(a,b) {
				return $(a).data('semesterid') > $(b).data('semesterid');
			}).appendTo('#vn_alle_auswahl_semester');
	
	
			$("#vn_erstellen_auswahl_semester").find("option").sort(function(a,b) {
				return $(a).data('semesterid') > $(b).data('semesterid');
			}).appendTo('#vn_erstellen_auswahl_semester');
			
			$("[data-semesterid='"+aktSemesterId+"']").prop('selected', true);
			$("#vn_erstellen_auswahl_semester option[value='"+ response[paramAktSemester] +"']").prop('selected', true);
	
		}
	);

	if(jsonBenutzer[paramNutzerstatus] == "ADMIN" || jsonBenutzer[paramNutzerstatus] == "DOZENT")
	{
		$("#vn_erstellen_bt").show();
	}
	else
	{
		$("#vn_erstellen_bt").hide();
	}

    return $.when(ajax1,ajax2).done(fillVeranstaltungsliste);
}

/**
 * Staret das Anzeigen der Veranstaltungen in den Tabs
 * @returns jQuery Deferred, das resolved, wenn die Veranstaltungen geladen wurden
 */
function fillVeranstaltungsliste() 
{
    return $.when(leseVeranstaltungenMeine(),leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
    												   $("#vn_alle_auswahl_studiengang").val()));
}

/**
 * Zeigt die Veranstaltung in die der aktuell eingeloggte Benutzer eingeschrieben ist im Tab 'Meine' an.
 * @returns jQuery Deferred, das resolved, wenn die Veranstaltungen geladen wurden
 */
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

/**
 * Zeigt die passenden Veranstaltungen zu einem gegebenen Semester sowie  Studiengang im Tab 'Alle' an.
 * @param semesterName String, z.B. 'SoSe2015'
 * @param studiengangName String, z.B. 'Informatik'
 * @returns jQuery Deferred, das resolved, wenn die Veranstaltungen geladen wurden
 */
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

/**
 * Zeigt eine Liste von Veranstaltungen in einem gegebenen Container Element an.
 * @param container DOM Objekt, das die Veranstaltungen enhalten soll
 * @param ajaxResult JSON Objekt mit allen Daten ueber die Veranstaltungen
 */
function displayVeranstaltungen(container, ajaxResult)
{
	if(verifyResponse(ajaxResult))
	{
		var veranstObjekte = ajaxResult[keyJsonArrResult];
		
		// Alle Veranstaltungen entfernen
		container.children().not(".vn_toolbar").remove();
		
		// Zeige spezielle Meldung, falls Liste leer ist
		if(veranstObjekte.length == 0)
		{
		    var str = "";
		    if(container.attr("id").split("_")[2] == "meine")
		    {
	            str += "<div class='vn_liste_leer_info'><span>Sie sind in keine Veranstaltungen eingeschrieben</span></div>";
		    }
		    else
		    {
                str += "<div class='vn_liste_leer_info'><span>Keine Veranstaltungen im gewählten Semester und Studiengang</span></div>";
		    }
		    container.append(str);
		}
		
		// Zeige entsprechende Veranstaltungen an und berechne die benoetigte Hoehe
		for(var i in veranstObjekte)
		{
			displayVeranstaltung(container, veranstObjekte[i]);
		}
	}
}

//Der Radio Button zur derzeit ausgeklappten Veranstaltung
var checkedRadio;

/**
 * Baut das DOM Element fuer eine einzelne Veranstaltung zusammen und
 * fuegt es in den gegeben Container ein.
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
		"	<div class='vn_beschreibung'>" + jsonVeranstObj[paramBeschr] + "</div>" +
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
		// in CSS mit versteckten Radiobuttons realisiert, sodass nur 1 Veranstaltung gleichzeitig aufgeklappt sein kann
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
		    $("#"+id+"_titel").off();
			$("#"+id+"_titel").click(function() {
				gotoVeranstaltung(jsonVeranstObj[paramId]);
			});
		}

		// Klick auf Erstellername fuehrt zu dessen Profil
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
    erstellerLink.off();
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
    button.off();
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
            if(jsonVeranstObj[paramKennwortGesetzt] && jsonBenutzer[paramNutzerstatus] != "ADMIN")
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

/**
 * Registriert den Handler fuer die Suche nach Personen und Veranstaltungen.
 */
function registerSuchEvent()
{
    var categoryClassMapping = {};
    categoryClassMapping[keyJsonObjKlasseBenutzer] = "BENUTZER";
    categoryClassMapping[keyJsonObjKlasseVeranst] = "VERANSTALTUNGEN";
    autoComplete(
            $("#suche_global_input"),
            {
                VERANSTALTUNGEN: function(jsonSuchErgebnis) {
                    // Gehe zum Semester und zum Studiengang der Veranstaltung
                    var id = jsonSuchErgebnis[paramId];
                    var semesterName = jsonSuchErgebnis[paramSemester];
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
                        },
                        params
                    );
                    $.when(studgAjax).done(function() {
                        // Setze korrektes Semester und Studiengang im Select
                        $("#vn_alle_auswahl_studiengang").val(studiengangName);
                        $("#vn_alle_auswahl_semester").val(semesterName);
                        var ajax = leseVeranstaltungenSemesterStudiengang(semesterName, studiengangName);
                        $.when(ajax).done(function() {
                            // Aktiviere den Alle-Tab
                            $("#vn_tab_alle").prop("checked",true);
                            // Klappe die entsprechende VN aus
                            $("#vn_alle_"+id+"_radio").trigger("click").prop("checked",true);
                        });
                    });
                },
                BENUTZER: function(jsonSuchErgebnis) {
                    gotoProfil(jsonSuchErgebnis[paramId]);
                }
            },
            categoryClassMapping,
            actionSucheBenVeranst
    );
}
