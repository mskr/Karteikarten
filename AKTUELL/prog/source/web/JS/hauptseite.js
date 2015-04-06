/**
 * @author mk
 */
// Statische Handler einmal registrieren
$(document).ready(function() {
	
    registerSuchEvent();
    
    // Code fuer das Veranstaltung erstellen Popup
    $('#vn_erstellen_popup').popup({
    	openelement: '.vn_erstellen_bt',
    	closeelement: '.vn_popup_close',
    	focuselement: '#vn_titel_input',
    	blur: false,
    	transition: 'all 0.3s'
    });

	$("#vn_alle_auswahl_studiengang").change(function() {
		leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
				   $("#vn_alle_auswahl_studiengang").val());
	});
 
	$("#vn_alle_auswahl_semester").change(function() {
		leseVeranstaltungenSemesterStudiengang($("#vn_alle_auswahl_semester").val(),
				   $("#vn_alle_auswahl_studiengang").val());
	});
});

function fillHauptseite() 
{
	// Studiengänge in auswahlliste anzeigen
	var ajax1 =  $.ajax({
		url: benutzerServlet,
		data: "action="+actionGetStudiengaenge,
		success: function(jsonObj) 
		{
			var errCode = jsonObj["error"];
			if(errCode == "noerror") 
			{
				$("#vn_alle_auswahl_studiengang").empty();
				var studgArr = jsonObj[keyJsonArrResult];
				for(var i in studgArr) 
				{
					$("#vn_alle_auswahl_studiengang").append("<option value='"+studgArr[i]+"'>"+studgArr[i]+"</option>");
				}

				$("#vn_alle_auswahl_studiengang option[value="+ jsonBenutzer[paramStudiengang] +"]").prop('selected', true);
			}
			else
			{
				message(0, buildMessage(errCode));
			}
		}
	}); 
	

	// Semester in auswahlliste anzeigen
	var ajax2 =  $.ajax({
		url: benutzerServlet,
		data: "action="+actionGetSemester,
		success: function(jsonObj) 
		{
			var errCode = jsonObj["error"];
			if(errCode == "noerror") 
			{
				$("#vn_alle_auswahl_semester").empty();

				var studgArr = jsonObj[keyJsonArrResult];

				for(var i in studgArr) {
					$("#vn_alle_auswahl_semester").append("<option data-semesterid='"+ studgArr[i][paramId] +"' value='"+studgArr[i][paramSemester]+"'>"+studgArr[i][paramSemester]+"</option>");
				}

				$("#vn_alle_auswahl_semester option[value='"+ jsonObj[paramAktSemester] +"']").prop('selected', true);

				$("#vn_alle_auswahl_semester").find("option").sort(function(a,b) {
					return $(a).data('semesterid') > $(b).data('semesterid');
				}).appendTo('#vn_alle_auswahl_semester');
			}
			else
			{
				message(0, buildMessage(errCode));
			}
		}
	});
	
    $.when(ajax1,ajax2).then(fillVeranstaltungsliste);
}

var globalContainerVeranstCount = 0;
var globalContainerVeranstArray = [];

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
		success: function(jsonObj) 
		{
			var divMeineVeranst = $("#vn_tabcontent_meine");
			displayVeranstaltungen(divMeineVeranst, jsonObj);
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
		success: function(jsonObj) 
		{
			var divSemesterVeranst = $("#vn_tabcontent_alle");
			displayVeranstaltungen(divSemesterVeranst, jsonObj);
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
//		success: function(jsonObj) 
//		{
//			var divStudiengangVeranst = $("#vn_tabcontent_studiengang");
//			displayVeranstaltungen(divStudiengangVeranst, jsonObj);
//		}
//	});	
//}

function displayVeranstaltungen(container, ajaxResult)
{
	var errCode = ajaxResult["error"];
	if(errCode == "noerror") 
	{
		var veranstObjekte = ajaxResult[keyJsonArrResult];
		// Alle Veranstaltungen entfernen
		container.children().not(".vn_toolbar").remove();
		globalContainerVeranstArray = [];
		globalContainerVeranstCount = 0;
		
		for(var i in veranstObjekte)
		{
			var newV = true;
			for(var j in globalContainerVeranstArray)
			{
				// Veranstaltung schon gepeichert
				if(globalContainerVeranstArray[j][paramId] == veranstObjekte[i][paramId])
				{
					newV = false;
					break;
				}
			}
			if(newV)
			{
				// Veranstaltung speichern
				globalContainerVeranstArray[globalContainerVeranstCount++] = veranstObjekte[i];
			}
			displayVeranstaltung(container, veranstObjekte[i]);
		}
	}
	else
	{
		message(0, buildMessage(errCode));
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
	var errCode = jsonVeranstObj["error"];
	if(errCode == "noerror") 
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
	else
	{
		message(0, buildMessage(errCode));
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
                    success: function(jsonObj) 
                    {
                        var errCode = jsonObj["error"];
                        if(errCode == "noerror") 
                        {
                            location.reload();
                        }
                        else
                        {
                            message(0, buildMessage(errCode));
                        }
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
                        success: function(jsonObj) 
                        {
                            var errCode = jsonObj["error"];
                            if(errCode == "noerror") 
                            {
                                kennwortForm.html("<div style='color:GreenYellow'><span class='octicon octicon-check'></span> Ok</div>");
                                location.reload();
                            }
                            else if(errCode == "loginfailed") 
                            {
                                message(0, "Ihr Zugangspasswort war falsch.");
                                kennwortFeld.toggleClass("shake2");
                            }
                            else
                            {
                                message(0, buildMessage(errCode));
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
                    success: function(jsonObj) 
                    {
                        var errCode = jsonObj["error"];
                        if(errCode == "noerror") 
                        {
                            location.reload();
                        }
                        else
                        {
                            message(0, buildMessage(errCode));
                        }
                    }
                });
            }
        });
    }
}




function registerSuchEvent()
{
    // TODO Bei jedem keyup-event 1 sec warten, ob noch ein event kommt.
    // Reagiere dann nur auf das event, was zuletzt aufgetreten ist,
    // Um Datenbankabfragen zu reduzieren. Wie macht man das am besten?
    // TODO Wenn man sehr schnell tippt werden mehr als 5 Ergebnisse angezeigt.
    $("#suche_global_input").keydown(function(event) {
        console.log("keycode="+event.keyCode);
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
    	timer = setInterval(function(){
    		
    		$("#sucherg_vn").empty();
    		$("#sucherg_benutzer").empty();
    		var suchString = $("#suche_global_input").val() + String.fromCharCode(event.keyCode);
    		var ajax = $.ajax({
    			url: suchfeldServlet,
    			data: "action=" + actionSucheBenVeranst + "&" +
    			paramSuchmuster + "=" + suchString,
    			success: function(response) {
    				var jsonObj = response;
    				var errCode = jsonObj["error"];
    				if(errCode == "noerror")
    				{
    					var arrSuchErgebnisse = jsonObj[keyJsonArrSuchfeldResult];
    					fillSuchergebnisse(arrSuchErgebnisse);
    					suchErgIterator = -1;
    				}
    				else
    				{
    					message(0, buildMessage(errCode));
    				}
    			}
    		});
    		
        },1000);
    }
    that.reset = function(){
        clearInterval(timer);
    }
    return that;
}();

function fillSuchergebnisse(arrSuchErgebnisse)
{
    for(var i in arrSuchErgebnisse)
    {
        var jsonSuchErgebnis = arrSuchErgebnisse[i];
        var text = jsonSuchErgebnis[keyJsonSuchfeldErgText];
        var klasse = jsonSuchErgebnis[keyJsonSuchfeldErgKlasse];
        var id = jsonSuchErgebnis[keyJsonSuchfeldErgId];
        if(klasse == "Benutzer") {
            $("#sucherg_benutzer").append(
                    "<div id='sucherg_benutzer_"+id+"' class='sucherg_benutzer_item'><span class='octicon octicon-person'></span>" + text + " (#"+id+")</div>");
            $("#sucherg_benutzer_"+id).slideDown();
        } else if(klasse == "Veranstaltung") {
            $("#sucherg_vn").append(
                    "<div id='sucherg_vn_"+id+"' class='sucherg_vn_item'><span class='octicon octicon-podium'></span>" + text + " (#"+id+")</div>");
            $("#sucherg_vn_"+id).slideDown();
        }
    }
    registerSucheClickEvent(id);
}

function registerSucheClickEvent(id)
{
    // 1) Alle Elemente mit class "sucherg_..._item" selektieren
    // 2) Mit for each durch die Elemente iterieren und id abfragen.
    // 3) Fuer jede id eine URL zusammenbauen
    $(".sucherg_benutzer_item").each(function(index) {
        $(this).click(function() {
            var benutzerId = $(this).attr("id").split("_")[2];
            gotoProfil(benutzerId);
        });
    });
    $(".sucherg_vn_item").each(function(index) {
        $(this).click(function() {
            var vnId = $(this).attr("id").split("_")[2];
            gotoVeranstaltung(vnId)
        });
    });
    // Das kleine x zum schliessen
    $("#sucherg_x").click(function() {
        $("#suche_global_input").val("");
    });
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
