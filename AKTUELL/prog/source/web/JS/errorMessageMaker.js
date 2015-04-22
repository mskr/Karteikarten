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
/**
 * Wrapper für message(0,...)
 * @param errorTxt
 */
function showError(errorTxt) {
	message(0,errorTxt);
}
/**
 * Wrapper für message(1,...)
 * @param intoTxt
 */
function showInfo(intoTxt) {
	message(1,intoTxt);
}

function handleError(errorCode) {
	switch (errorCode)
	{
		case "systemerror": 
			showError("Ein interner Fehler ist aufgetreten. Versuchen Sie es erneut. Wenn der Fehler weiterhin besteht, wenden Sie sich an den Administrator.");
			break;
		case "invalidparam": 
			showError("Der Server hat ungueltige oder fehlende Parameter erhalten. Bitte machen Sie andere Eingaben!");
			break;
		case "notloggedin": 
			
			if(getUrlParameterByName(urlParamLocation) != ansichtStartseite)
				showError("Bitte loggen Sie sich ein!");
			
			jsonBenutzer = undefined;			// Benutzer objekt ungültig
			gotoStartseite();
			break;
		case "loginfailed": 
			showError("Dieser Eintrag wurde in der Datenbank nicht gefunden.");
			break;
		case "registerfailed": 
			showError("Ihre Daten konnten nicht registriert werden. Bitte versuchen Sie es erneut.");
			break;
		case "emailalreadyinuse": 
			showError("Entschuldigung, diese E-Mail Adresse ist bereits vergeben.");
			break;
		case "pwresetfailed": 
			showError("Ihr Passwort konnte nicht zurueckgesetzt werden. Bitte versuchen Sie es erneut.");
			break;
		case "sessionexpired": 
			showError("Ihre Login Session ist abgelaufen. Bitte loggen Sie sich erneut ein.");
			break;
		case "notallowed":
			showError("Diese Aktion ist nicht erlaubt. Sie haben womöglich nicht die nötigen Berechtigungen.");
			break;
		default: 
			showError("Ein unbekannter Fehler ist aufgetreten (Error Code nicht bekannt).");
		break;
	}
}

/**
 * 
 * @param jsonResponse
 * @param specialErrorHandlingFkt
 * @returns true, falls kein Error, false andernfalls
 */
function verifyResponse(jsonResponse, specialErrorHandlingFkt) 
{
	if(jsonResponse["error"] != "noerror")
	{
		var processed = false;
		
		if(specialErrorHandlingFkt != undefined)
			processed = specialErrorHandlingFkt(jsonResponse["error"]);
		
		if(!processed)
			handleError(jsonResponse["error"]);	
		
		return false;
	}
	
	return true;
}


