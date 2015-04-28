/**
 * @author mk
 */

$(document).ready(function() {
    // Code fuer das Attribute Tooltip
//    $("#attr_popup").popup({
//            type: 'tooltip',
//            vertical: 'topedge',
//            horizontal: 'leftedge'
//    });
//  CKEDITOR.config.irgendeinPlugin
    // Einklappen der Kommentarboxen
    $('.kk_kommtoggle').html('Einklappen');
    var height = 0;
    $('.kk_kommtoggle').click( function() {
        var domelem = $(this).parent().get(0);
        var jqueryobj = $(domelem);
        if( jqueryobj.height()>50 ) {
            height = jqueryobj.height();
            jqueryobj.animate({
                height: "15px"
            }, 500 );
            $(this).html('Ausklappen');
        } else {
            jqueryobj.animate({
                height: ""+height+"px"
            }, 500 );
            $(this).html('Einklappen');
        }
    });
});
function fillVeranstaltungsSeite(Vid)
{
	// Wir verwenden ein eigenes Deferred-Objekt um zurückzumelden, wenn alles geladen wurde.
	d = jQuery.Deferred();
	
	var params = {};
	params[paramId] = Vid;
	ajaxCall(veranstaltungServlet,
		actionGetVeranstaltung,
		function(response) 
		{
			veranstaltungsObject = response;
			
			$.when(findStudiengaenge(Vid)).done(function() {
				$.when(findModeratorenVn(Vid)).done(function() {
					
					// Wenn alles geladen wurde
					titel = veranstaltungsObject[paramTitel];
					$("#vn_title").html(titel);
					$("#vn_ersteller").html(veranstaltungsObject[paramErsteller][paramVorname] + " " + veranstaltungsObject[paramErsteller][paramNachname]);
					$("#vn_semester").html(veranstaltungsObject[paramSemester]);
					document.title = titel;
					
					// Deferred Objekt als abgeschlossen markieren.
					d.resolve();
				});
			});
			
		},
		params
	);
	
	return d;
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