/**
 * @author mk
 */

$(document).ready( function() {
    // Code fuer das Veranstaltung erstellen Popup
    $('#vn_erstellen_popup').popup({
        openelement: '.vn_erstellen_bt',
        closeelement: '.vn_popup_close',
        focuselement: '#vn_titel_input',
        blur: false,
        transition: 'all 0.3s'
    });
    // Code fuer das Ausklappen der Veranstaltungs-Listenelemente
    $('.vn_mehr_einbl').html('<span class="octicon octicon-three-bars"></span> Mehr');
    $('.vn_mehr_einbl').click( function() {
        var vnmehr_domelem = $(this).next().next().get(0);
        console.log('DOM Element class:'+vnmehr_domelem.getAttribute('class'));
        var vnmehr_jqueryobj = $(vnmehr_domelem);
        console.log('JQuery Object class:'+vnmehr_jqueryobj.attr('class'));
        if( vnmehr_jqueryobj.is(':hidden') ) {
            console.log('War hidden... Rufe jetzt slideDown() auf...');
            vnmehr_jqueryobj.slideDown();
            $(this).html('<span class="octicon octicon-three-bars"></span> Weniger');
        } else {
            console.log('War nicht hidde... Rufe jetzt slideUp() auf');
            vnmehr_jqueryobj.slideUp();
            $(this).html('<span class="octicon octicon-three-bars"></span> Mehr');
        }
    });
});