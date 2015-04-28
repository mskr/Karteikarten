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
function fillVeranstaltungsSeite(Vid){
	var params = {};
	params[paramId] = Vid;
	var ajax1 = ajaxCall(veranstaltungServlet,
		actionGetVeranstaltung,
		function(response) 
		{
			veranstaltungsObject = response;
			$.when(findStudiengaenge(Vid)).done(function() {
				titel = response[paramTitel];
				$("#vn_title").html(titel);
				document.title = titel;
				findModeratorenVn(Vid);
			});
			
		},
		params
	);
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