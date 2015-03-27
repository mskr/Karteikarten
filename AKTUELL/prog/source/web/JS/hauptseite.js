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
    var resLoadAllVeranst= $.ajax({
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
            		displayVeranstaltung(divAlleVeranst, VeranstList[i]);
            	}
            }
            else
            {
                message(0, buildMessage(errCode));
            }
        }
    });
    // TODO Erweitern mit dem Laden aller anderen Veranstaltungen
    $.when(resLoadAllVeranst).done(function() {
    	// Alle Einblende Felder aktivieren
    	$(".vn_mehr_einbl").click(function() 
    	    	{
    	    		var parent = $(this).parent();
    	    		var wrapper = parent.find(".vn_mehr_wrapper");
    	    		wrapper.slideToggle("slow");
    	    	});
	});
    
//    // Code fuer das Ausklappen der Veranstaltungs-Listenelemente
//    $('.vn_mehr_einbl').html('');
//    $('.vn_mehr_einbl').click( function() {
//        var vnmehr_domelem = $(this).next().next().get(0);
//        console.log('DOM Element class:'+vnmehr_domelem.getAttribute('class'));
//        var vnmehr_jqueryobj = $(vnmehr_domelem);
//        console.log('JQuery Object class:'+vnmehr_jqueryobj.attr('class'));
//        if( vnmehr_jqueryobj.is(':hidden') ) {
//            console.log('War hidden... Rufe jetzt slideDown() auf...');
//            vnmehr_jqueryobj.slideDown();
//            $(this).html('<span class="octicon octicon-three-bars"></span> Weniger');
//        } else {
//            console.log('War nicht hidde... Rufe jetzt slideUp() auf');
//            vnmehr_jqueryobj.slideUp();
//            $(this).html('<span class="octicon octicon-three-bars"></span> Mehr');
//        }
//    });
}

/**
 * Zeigt eine Veranstaltung im angegeben Container an.
 * @param container Übergeordneter Container
 * @param jsonVeranstObj Objekt, dass die Veranstaltung enthält
 */
function displayVeranstaltung(container, jsonVeranstObj)
{	
	var result = $.ajax({
        url: profilServlet,
        data: "action="+actionGetOtherBenutzer + "&" + 
        paramId +"=" + jsonVeranstObj[paramErsteller],
        success: function(jsonObj) 
        {
            var errCode = jsonObj["error"];
            if(errCode == "noerror") 
            {
            	container.append(
    			"<div class='vn'>" +
    					"<span class='vn_titel'>" + jsonVeranstObj[paramTitel] + "</span>" +
    					"<span class='vn_details'>" +
    						"<span class='vn_detail'>" + jsonObj[paramVorname] + " " + jsonObj[paramNachname] + "</span><br>" +
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
    			"</div>");
            }
            else
            {
                message(0, buildMessage(errCode));
            }
        }
    });
	
	
}