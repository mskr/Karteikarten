/**
 * @author mk
 */

function fillHauptseite() {
    
    fillVeranstaltungsliste();
    registerSuchEvent();
    
    // Code fuer das Veranstaltung erstellen Popup
    $('#vn_erstellen_popup').popup({
        openelement: '.vn_erstellen_bt',
        closeelement: '.vn_popup_close',
        focuselement: '#vn_titel_input',
        blur: false,
        transition: 'all 0.3s'
    });
}

/**
 * Holt alle Veranstaltungen vom Server,
 * ordnet sie in die Tabs ein und zeigt sie an.
 */
function fillVeranstaltungsliste() {
    // TODO Refractoring
    // TODO Laden der Veranstaltungen nach Bedarf 
    //      (wenn der Benutzer auf einen Tab klickt).
    //      Spaeter koennen ja theoretisch unendlich viele Veranstaltungen drin sein.
    // Alle Veranstaltungen
    var ajax1 = $.ajax({
        url: veranstaltungServlet,
        data: "action="+actionLeseVeranst + "&" + 
        leseVeranstMode +"=" + leseVeranstModeAlle,
        success: function(jsonObj) 
        {
            var errCode = jsonObj["error"];
            if(errCode == "noerror") 
            {
                var VeranstList = jsonObj[keyJsonArrResult];
                var divAlleVeranst = $("#vn_tabcontent_alle");
                for(var i = 0; i < VeranstList.length; i++)
                {
                    // Jede Veranstaltung erzeugen und funktion speichern in array
                    displayVeranstaltung(divAlleVeranst, VeranstList[i]);
                }
            }
            else
            {
                message(0, buildMessage(errCode));
            }
        }
    });
    // Meine Veranstaltungen
    var ajax2 = $.ajax({
         url: veranstaltungServlet,
         data: "action="+actionLeseVeranst + "&" + 
         leseVeranstMode +"=" + leseVeranstModeMeine,
         success: function(jsonObj) 
         {
             var errCode = jsonObj["error"];
             if(errCode == "noerror") 
             {
                var VeranstList = jsonObj[keyJsonArrResult];
                var divAlleVeranst = $("#vn_tabcontent_meine");
                for(var i = 0; i < VeranstList.length; i++)
                {
                    // Jede Veranstaltung erzeugen und funktion speichern in array
                    displayVeranstaltung(divAlleVeranst, VeranstList[i]);
                }
             }
             else
             {
                 message(0, buildMessage(errCode));
             }
         }
     });
    // Semester Veranstaltungen
    var ajax3 = $.ajax({
         url: veranstaltungServlet,
         data: "action="+actionLeseVeranst + "&" + 
         leseVeranstMode +"=" + leseVeranstModeSemester,
         success: function(jsonObj) 
         {
             var errCode = jsonObj["error"];
             if(errCode == "noerror") 
             {
                var VeranstList = jsonObj[keyJsonArrResult];
                var divAlleVeranst = $("#vn_tabcontent_semester");
                for(var i = 0; i < VeranstList.length; i++)
                {
                    // Jede Veranstaltung erzeugen und funktion speichern in array
                    displayVeranstaltung(divAlleVeranst, VeranstList[i]);
                }
             }
             else
             {
                 message(0, buildMessage(errCode));
             }
         }
     });
    // Studiengang Veranstaltungen
    var ajax4 = $.ajax({
         url: veranstaltungServlet,
         data: "action="+actionLeseVeranst + "&" + 
         leseVeranstMode +"=" + leseVeranstModeStudiengang,
         success: function(jsonObj) 
         {
             var errCode = jsonObj["error"];
             if(errCode == "noerror") 
             {
                var VeranstList = jsonObj[keyJsonArrResult];
                var divAlleVeranst = $("#vn_tabcontent_studiengang");
                for(var i = 0; i < VeranstList.length; i++)
                {
                    // Jede Veranstaltung erzeugen und funktion speichern in array
                    displayVeranstaltung(divAlleVeranst, VeranstList[i]);
                }
             }
             else
             {
                 message(0, buildMessage(errCode));
             }
         }
     });

    $.when(ajax1, ajax2, ajax3, ajax4).done(function() {
        // Folgendes erlaubt checked Radio-Buttons zu unchecken
        // @see http://stackoverflow.com/questions/17120576
        var checkedRadio; // DOM Object
        $(".vn_mehr_einbl_toggle").each(function(i) {
            // Achtung: $(this) holt das jQuery-, und this das DOM-Object.
            $(this).click(function() {
                // Hole die ID der zugehoerigen Veranstaltungen, um deren Farbe aendern zu koennen
                var vnIDaktuell = "vn_"+this.id.split("_")[1]+"_"+this.id.split("_")[2];
                if(checkedRadio != undefined)
                {
                    var vnIDdavor = "vn_"+checkedRadio.id.split("_")[1]+"_"+checkedRadio.id.split("_")[2];
                }
                if( checkedRadio == this )
                {
                    this.checked = false;
                    checkedRadio = undefined;
                    $("#"+vnIDaktuell).toggleClass("hovered");
                }
                else
                {
                    $("#"+vnIDdavor).toggleClass("hovered");
                    checkedRadio = this;
                    $("#"+vnIDaktuell).toggleClass("hovered");
                }
            });
        });
    });
    
}

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

		str +=		"<input id='"+id+"_radio' type='radio' class='vn_mehr_einbl_toggle' name='vn_"+container.attr("id").split("_")[2]+"' style='display:none'>" +
		            "<label for='"+id+"_radio' class='vn_mehr_einbl'>" +
		                "<span class='octicon'></span>" +
		            "</label>" +
		            "<span class='vn_titel'>" + jsonVeranstObj[paramTitel] + "</span>" +
					"<span class='vn_details'>" +
					    "<a class='vn_dozent'>" + jsonVeranstObj[paramErsteller][paramVorname]+ " " + jsonVeranstObj[paramErsteller][paramNachname] + "</a><br>" +
					    "<a class='vn_detail'>" + jsonVeranstObj[paramAnzTeilnehmer] + " Teilnehmer</a><br>" +
					    "<a class='vn_detail'>" + jsonVeranstObj[paramSemester] + "</a>" +
					"</span>" +
					"<div class='vn_mehr_wrapper'>" +
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
        var paramObj = {};
        paramObj[urlParamLocation] = ansichtProfilseite;
        paramObj[urlParamId] = jsonVeranstObj[paramErsteller][paramId];
        buildUrlQuery(paramObj);
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






/**
 * Triggert einen Sind-Sicher-Dialog.
 * @param anchorElem ist das Triggerelement (jQuery- oder DOM-Element).
 * @param message ist die Nachricht, die der Benutzer bestaetigen soll.
 * @param doCriticalThing ist eine Funktion, die nach Bestaetigung mit Ok ausgefuehrt wird.
 */
function sindSieSicher(anchorElem, message, doCriticalThing)
{
    $("#dialog_sicher").popup({
        type: 'tooltip',
        vertical: 'top',
        horizontal: 'center',
        tooltipanchor: anchorElem,
        transition: 'none'
    });
    $("#dialog_sicher").popup("show");
    $(".dialog_sicher_frage").text(message);
    $(".dialog_sicher_ja").click(function() {
        doCriticalThing();
    });
}







function registerSuchEvent()
{
    // TODO Bei jedem keyup-event 1 sec warten, ob noch ein event kommt.
    // Reagiere dann nur auf das event, was zuletzt aufgetreten ist.
    $("#suche_global_input").keyup(function(event) {
        console.log("keycode="+event.keyCode);
        if(event.keyCode == 40 || 
           event.keyCode == 38 || 
           event.keyCode == 13 || 
           event.keyCode == 27)
        {
            // 40 = Pfeil runter, 38 = Pfeil hoch, 13 = ENTER, 27 = ESC
            // Sende bei diesen Eingaben keinen Ajax Call
            // sondern navigiere in den Suchergebnissen
            handlePfeiltastenEvents(event.keyCode);
            return;
        }
        if($("#suche_ergebnisse").is(":hidden"))
        {
            $("#suche_ergebnisse").css("display","flex"); // Anstatt  von show()
        }
        $("#sucherg_vn").empty();
        $("#sucherg_benutzer").empty();
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
    });
}

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
        } else if(klasse == "Veranstaltung") {
            $("#sucherg_vn").append(
                    "<div id='sucherg_vn_"+id+"' class='sucherg_vn_item'><span class='octicon octicon-podium'></span>" + text + " (#"+id+")</div>");
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
            var paramObj = {};
            paramObj[urlParamLocation] = ansichtProfilseite;
            paramObj[urlParamId] = benutzerId;
            buildUrlQuery(paramObj);
        });
    });
    $(".sucherg_vn_item").each(function(index) {
        $(this).click(function() {
            var vnId = $(this).attr("id").split("_")[2];
            var paramObj = {};
            paramObj[urlParamLocation] = ansichtVeranstaltungseite;
            paramObj[urlParamId] = vnId;
            buildUrlQuery(paramObj);
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