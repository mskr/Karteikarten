/**
 * @author mk
 */

function fillHauptseite() {
    // Code fuer das Veranstaltung erstellen Popup
    $('#vn_erstellen_popup').popup({
        openelement: '.vn_erstellen_bt',
        closeelement: '.vn_popup_close',
        focuselement: '#vn_titel_input',
        blur: false,
        transition: 'all 0.3s'
    });
    
    
    // Alle Veranstaltungen holen und anzeigen
    // TODO Refractoring
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

	// Alle Einblende Felder aktivieren
    $.when(ajax1,ajax2,ajax3,ajax4).done(function() {
    	$(".vn_mehr_einbl").click(function() 
    			{
    		var parent = $(this).parent();
    		var wrapper = parent.find(".vn_mehr_wrapper");
    		wrapper.slideToggle("slow");
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
		var str = "<div class='vn'>" +
					"<span class='vn_titel'>" + jsonVeranstObj[paramTitel] + "</span>" +
					"<span class='vn_details'>" +
					"<span class='vn_detail'><a >"  + jsonVeranstObj[paramErsteller][paramVorname]+ " " + jsonVeranstObj[paramErsteller][paramNachname] + "</a></span><br>" +
					"<span class='vn_detail'>" + jsonVeranstObj[paramAnzTeilnehmer] + " Teilnehmer</span><br>" +
					"<span class='vn_detail'>" + jsonVeranstObj[paramSemester] + "</span>" +
					"</span>" +
					"<span class='vn_mehr_einbl'>" +
					"<span class='octicon octicon-three-bars'> Mehr anzeigen</span> " +
					"</span><br>" +
					"<div class='vn_mehr_wrapper'>" +
					"	<span class='vn_mehr'>" + jsonVeranstObj[paramBeschr] + "</span>" +
					"	<div class='vn_optionen'>" +
					// TODO ! Muss unterschiedlich dargestellt werden, wenn Benutzer an Veranstaltung teilnimmt
					"		<span class='mybutton dark'><span class='octicon octicon-sign-out'></span> Ausschreiben</span>" +
					"	</div>" +
					"</div>" +
					"</div>";
		
		var erstellerLink = $(str).find("a");
		erstellerLink.click(function() {
	        var paramObj = {};
	        paramObj[urlParamLocation] = ansichtProfilseite;
	        paramObj[urlParamId] = jsonVeranstObj[paramErsteller];
	        buildUrlQuery(paramObj);
		});
		container.append(str);
	}
	else
	{
		message(0, buildMessage(errCode));
	}
}