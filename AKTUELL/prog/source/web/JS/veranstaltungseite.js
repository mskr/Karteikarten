/**
 * @author mk
 */

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
	ajaxCall(veranstaltungServlet,
		actionGetVeranstaltung,
		function(response) 
		{
			veranstaltungsObject = response;
			
			if(veranstaltungsObject[paramAngemeldet] == false)
			{
				showError("Sie haben nicht die notwendingen Berechtigungen um diese Seite zu sehen!");
				gotoHauptseite();
				return d; // TODO Achtung
			}
			
			$.when(findStudiengaenge(Vid)).done(function() {
				$.when(findModeratorenVn(Vid)).done(function() {
					
					// Wenn alles geladen wurde
					titel = veranstaltungsObject[paramTitel];
					$(".vn_title").html(titel);
					$("#vn_ersteller").html(veranstaltungsObject[paramErsteller][paramVorname] + " " + veranstaltungsObject[paramErsteller][paramNachname]);
					$("#vn_semester").html(veranstaltungsObject[paramSemester]);
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
					
					
					
					// Deferred Objekt als abgeschlossen markieren.
					d.resolve();
				});
			});
			
		},
		params
	);
	
	return $.when(ajax1,ajax2,d);
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


