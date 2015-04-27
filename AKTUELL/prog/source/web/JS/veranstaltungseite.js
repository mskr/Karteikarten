/**
 * @author mk
 */

$(document).ready(function() {
    // Code fuer das Attribute Tooltip
    $("#attr_popup").popup({
            type: 'tooltip',
            vertical: 'topedge',
            horizontal: 'leftedge'
    });
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
//todo übergebe Informationen über Veranstaltung
function fillVeranstaltungsSeite(){
	//aktualisiere title
}