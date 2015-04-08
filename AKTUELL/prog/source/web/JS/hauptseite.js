/**
 * @author mk
 */
// Statische Handler einmal registrieren
$(document).ready(function() {
	
    registerSuchEvent();
    
    // Code fuer das Veranstaltung erstellen Popup
    $('#vn_erstellen_popup').popup({
    	openelement: '#vn_erstellen_bt',
    	closeelement: '#vn_popup_close',
    	focuselement: '#vn_titel_input',
    	blur: false,
    	transition: 'all 0.3s',
    	onclose : function() {
    		$("#vn_titel_input").val("");
    		// TODO
//  		$("#vn_erstellen_auswahl_semester [value='" + + "']").prop("selected", true);
    		$("#vn_erstellen_auswahl_studiengang [value='" + jsonBenutzer[paramStudiengang]+ "']").prop("selected", true);

    		$("#vn_mod_input").val("");
    		$("#vn_beschr_input").val("");
    		$("input[name=vn_bearbeitenMode_radiogb][value='Nur ich']").prop("checked", true);
    		$("#vn_komm_erlaubt").prop("checked", true);
    		$("#vn_bew_erlaubt").prop("checked", true);
    	}
    });


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
	var ajax1 =  $.ajax({
		url: benutzerServlet,
		data: "action="+actionGetStudiengaenge,
		success: function(response) 
		{
			if(verifyResponse(response))
			{
				$("#vn_alle_auswahl_studiengang").empty();
				$("#vn_erstellen_auswahl_studiengang").empty();

				var studgArr = response[keyJsonArrResult];
				for(var i in studgArr) 
				{
					$("#vn_alle_auswahl_studiengang").append("<option value='"+studgArr[i]+"'>"+studgArr[i]+"</option>");
					$("#vn_erstellen_auswahl_studiengang").append("<option value='"+studgArr[i]+"'>"+studgArr[i]+"</option>");
				}

				$("#vn_alle_auswahl_studiengang option[value="+ jsonBenutzer[paramStudiengang] +"]").prop('selected', true);
				$("#vn_erstellen_auswahl_studiengang option[value="+ jsonBenutzer[paramStudiengang] +"]").prop('selected', true);
			}
		}
	}); 
	

	// Semester in auswahlliste anzeigen
	var ajax2 =  $.ajax({
		url: benutzerServlet,
		data: "action="+actionGetSemester,
		success: function(response) 
		{
			if(verifyResponse(response))
			{
				$("#vn_alle_auswahl_semester").empty();
				$("#vn_erstellen_auswahl_semester").empty();

				var studgArr = response[keyJsonArrResult];

				for(var i in studgArr) {
					$("#vn_alle_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
					$("#vn_erstellen_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
				}

				$("#vn_alle_auswahl_semester option[value='"+ response[paramAktSemester] +"']").prop('selected', true);
				$("#vn_erstellen_auswahl_semester option[value='"+ response[paramAktSemester] +"']").prop('selected', true);

				$("#vn_alle_auswahl_semester").find("option").sort(function(a,b) {
					return $(a).data('semesterid') > $(b).data('semesterid');
				}).appendTo('#vn_alle_auswahl_semester');


				$("#vn_erstellen_auswahl_semester").find("option").sort(function(a,b) {
					return $(a).data('semesterid') > $(b).data('semesterid');
				}).appendTo('#vn_erstellen_auswahl_semester');
			}
		}
	});

	if(jsonBenutzer[paramNutzerstatus] == "ADMIN" || jsonBenutzer[paramNutzerstatus] == "DOZENT")
	{
		$("#vn_erstellen_bt").show();
	}
	else
	{
		$("#vn_erstellen_bt").hide();
	}

	
    $.when(ajax1,ajax2).then(fillVeranstaltungsliste);
}

function fillVeranstaltungsliste() 
{
    leseVeranstaltungenMeine();
    leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
    												   $("#vn_alle_auswahl_studiengang").val());
}

function leseVeranstaltungenMeine()
{
	// Meine Veranstaltungen
	return $.ajax({
		url: veranstaltungServlet,
		data: "action="+actionLeseVeranst + "&" + 
		leseVeranstMode +"=" + leseVeranstModeMeine,
		success: function(response) 
		{
			var divMeineVeranst = $("#vn_tabcontent_meine");
			displayVeranstaltungen(divMeineVeranst, response);
		}
	});
}

function leseVeranstaltungenSemesterStudiengang(semesterName, studiengangName)
{
	// Semester Veranstaltungen
	return $.ajax({
		url: veranstaltungServlet,
		data: "action="+actionLeseVeranst + "&" + 
		leseVeranstMode +"=" + leseVeranstModeStudiengangSemester + "&" +
		paramGewaehltesSemester + "=" + semesterName + "&" +
		paramGewaehltesStudiengang + "=" + studiengangName,
		success: function(response) 
		{
			var divSemesterVeranst = $("#vn_tabcontent_alle");
			displayVeranstaltungen(divSemesterVeranst, response);
		}
	});
}

//function leseVeranstaltungenStudiengang(studiengangName)
//{
//	// Studiengang Veranstaltungen
//	return $.ajax({
//		url: veranstaltungServlet,
//		data: "action="+actionLeseVeranst + "&" + 
//		leseVeranstMode +"=" + leseVeranstModeStudiengang + "&" +
//		paramGewaehltesStudiengang + "=" + studiengangName,
//		success: function(response) 
//		{
//			var divStudiengangVeranst = $("#vn_tabcontent_studiengang");
//			displayVeranstaltungen(divStudiengangVeranst, response);
//		}
//	});	
//}

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
            sindSieSicher((this), "", function() {
                $.ajax({
                    url: veranstaltungServlet,
                    data: "action="+actionAusschreiben + "&" + 
                          paramId +"=" + jsonVeranstObj[paramId],
                    success: function(response) 
                    {
                    	if(verifyResponse(response))
                            location.reload();	// TODO
                    }
                });
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
                    $.ajax({
                        url: veranstaltungServlet,
                        data: "action="+actionEinschreiben + "&" + 
                              paramId +"=" + jsonVeranstObj[paramId] + "&" +
                              paramPasswort + "=" + kennwort,                 // TODO
                        success: function(response) 
                        {
                        	var errorFkt = function(errorTxt) {
                        		if(errorTxt == "loginfailed") 
                                {
                                    showError("Ihr Zugangspasswort war falsch.");
                                    kennwortFeld.toggleClass("shake2");
                                    return true;
                                }
                        		return false;
							}
                        	if(verifyResponse(response,errorFkt))
                        	{
                        		kennwortForm.html("<div style='color:GreenYellow'><span class='octicon octicon-check'></span> Ok</div>");
                                location.reload();	// TODO
                        	}
                        }
                    });
                    event.preventDefault();
                });
            }
            else
            {
                // Einschreiben ohne Kennwort
                $.ajax({
                    url: veranstaltungServlet,
                    data: "action="+actionEinschreiben + "&" + 
                          paramId +"=" + jsonVeranstObj[paramId],
                    success: function(response) 
                    {
                    	if(verifyResponse(response))
                    	{
                            location.reload();	// TODO
                    	}
                    }
                });
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
    		var ajax = $.ajax({
    			url: suchfeldServlet,
    			data: "action=" + actionSucheBenVeranst + "&" +
    			paramSuchmuster + "=" + suchString,
    			success: function(response) {
    				var jsonObj = response;
    				var errCode = jsonObj["error"];
    				if(errCode == "noerror")
    				{
    					var arrSuchErgebnisse = jsonObj[keyJsonArrResult];
    					fillSuchergebnisse(arrSuchErgebnisse);
    					suchErgIterator = -1;
    					$("#sucherg_vn, #sucherg_benutzer").slideDown("fast");
    				}
    				else
    				{
    					message(0, buildMessage(errCode));
    				}
    			}
    		});
    		
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
    var klasse = jsonSuchErgebnis[keyJsonObjKlasse];
    if(klasse == keyJsonObjKlasseBenutzer) {
        $("#sucherg_benutzer_"+id).click(function() {
            gotoProfil(id);
        });
    }
    else if(klasse == keyJsonObjKlasseVeranst) {
        $("#sucherg_vn_"+id).click(function() {
            // Gehe zum Semester und zum Studiengang der VN
            var semesterName = jsonSuchErgebnis[paramSemester];
            var studiengangName = getFirstStudiengangOfVeranstaltung(id);
            console.log(jsonSuchErgebnis);
            console.log(semesterName);
            console.log(studiengangName);
            console.log("schicke ajax ab");
            var ajax = leseVeranstaltungenSemesterStudiengang(semesterName, studiengangName);
            $.when(ajax).done(function() {
                console.log("bin fertig");
                // Verberge die Suchergebnisse
                $("#sucherg_x").trigger("click");
                // Aktiviere den Alle-Tab
                $("#tab-2").prop("checked",true);
                // Klappe die entsprechende VN aus
                $("#vn_alle_"+id+"_radio").trigger("click").prop("checked",true);
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

function getFirstStudiengangOfVeranstaltung(id) {
    
}


function registerVeranstErzeugeHandler() {
	
	$("#vn_erzeugen_cancel").click(function() {
		$("#vn_erstellen_popup").popup("hide");
	});
	
	$("#vn_erzeugen_submit").click(function(event) {
		event.preventDefault();
		
		var popup = $("#vn_erstellen_popup");
		var titel = popup.find("#vn_titel_input").val(),
			semester = popup.find("#vn_erstellen_auswahl_semester").val(),
			studiengang = popup.find("#vn_erstellen_auswahl_studiengang").val(),
			beschr = popup.find("#vn_beschr_input").val(),
			moderatorenKkBearbeiten = popup.find("input[name=vn_bearbeitenMode_radiogb]:checked").val() != "Nur ich",
			passw = popup.find("#vn_pass_input").val(),
			kommentareErlaubt = popup.find("#vn_komm_erlaubt").is(':checked'),
			bewertungenErlaubt = popup.find("#vn_bew_erlaubt").is(':checked');
	
		var ajax = $.ajax({
			url: veranstaltungServlet,
			data: "action=" + actionErstelleVeranst + "&" + 
				  paramTitel + "=" + titel + "&" +
				  paramSemester + "=" + semester + "&" + 
				  paramStudiengang + "=" + studiengang + "&" +
				  paramBeschr + "=" + beschr + "&" + 
				  paramModeratorKkBearbeiten + "=" +moderatorenKkBearbeiten + "&"+
				  paramKommentareErlauben + "=" + kommentareErlaubt + "&" + 
				  paramBewertungenErlauben + "=" + bewertungenErlaubt + "&"+
				  paramPasswort + "=" + passw,
				  
			success: function(response) {
				if(verifyResponse(response))
				{
					popup.popup('hide');
					fillVeranstaltungsliste();	
				}	
			}
		});
	});
}
