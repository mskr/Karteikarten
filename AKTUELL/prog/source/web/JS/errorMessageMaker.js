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
    if(type == 0) {
        $(".err").html(text);
        if( !$(".err").is(":visible") ) {
            $(".err").show().fadeOut(5000);
        } else {
            $(".err").stop();
            $(".err").css("opacity","1");
            $(".err").fadeOut(5000);
        }
    } else {
        $(".success").html(text);
        if( !$(".success").is(":visible") ) {
            $(".success").show().fadeOut(5000);
        } else {
            $(".success").stop();
            $(".success").css("opacity","1");
            $(".success").fadeOut(5000);
        }
    }
}