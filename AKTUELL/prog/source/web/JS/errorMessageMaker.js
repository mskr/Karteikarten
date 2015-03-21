/**
 * @author mk
 */

/**
 * Diese Funktion zeigt einen roten oder gruenen Balken
 * am oberen Bildschirmrand an, der eine Fehler-
 * oder auch eine Erfolgsmeldung enthaelt.
 * @param type ist 0, falls es sich um eine Fehler-
 * meldung handelt. 1 andernfalls.
 * @param text ist die anzuzeigende Meldung.
 */
function message(type, text) {
	var elemClass;
    if(type == 0) {
    	elemClass = ".err";
    } else {
    	elemClass = ".success";
    }
    $(elemClass).html(text);
    if( !$(elemClass).is(":visible") ) {
        $(elemClass).show().fadeOut(5000);
    } else {
        $(elemClass).stop();
        $(elemClass).css("opacity","1");
        $(elemClass).fadeOut(5000);
    }
}