/**
 * @author mk
 */

/**
 * Diese Funktion zeigt einen roten oder gruenen Balken
 * am oberen Bildschirmrand an, der eine Fehler-
 * oder auch eine Erfolgsmeldung enthaelt.
 * @param isError ist 0, falls es sich um eine Fehler-
 * meldung handelt. 1 andernfalls.
 * @param text ist die anzuzeigende Meldung.
 */
function message(isError, text) {
	var elemClass;
    if(isError) {
    	elemClass = ".err";
    	$(".success").hide();
    } else {
    	elemClass = ".success";
    	$(".err").hide();
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

var messageQueue = [];
var messageTimer;
var laeuftGerade = false;

function bearbeiteMessageQueue()
{
	if(showNextMessage()){
		messageTimer = setTimeout(bearbeiteMessageQueue, 3000);
		laeuftGerade = true;
	}
	else{
		clearTimeout(messageTimer);
		laeuftGerade = false;
	}
}
/**
 * Beginnt die verarbeitung der Nachrichten. Tut nichts, wenn die verarbeitung schon läuft
 */
function startMessageQueue()
{
	if(laeuftGerade)
		return;
	bearbeiteMessageQueue();
}
/**
 * Leert die Queue und verarbeitet keine weiteren Nachrichten
 */
function clearMessageQueue()
{
	messageQueue = [];
	clearTimeout(messageTimer);
	laeuftGerade = false;
}

/**
 * Fügt eine neue Nachricht in die Queue ein. Schon vorhandene Nachrichten werden ignoriert.
 * @param errorTxt
 * @param type
 */
function addMessageToQueue(errorTxt, isError)
{
	obj = {}
	obj["txt"] = errorTxt;
	obj["type"] = isError;
	
	// Existiert diese meldung schon?
	for (i = 0; i < messageQueue.length; i++ )
    {
        if (messageQueue[i]["txt"] == errorTxt)
        {
            return;
        }
    }
	
	messageQueue.push(obj);
}
/**
 * Holt die nächste Nachricht aus der Queue und zeigt sie an
 * @returns {Boolean}
 */
function showNextMessage()
{
	if(messageQueue.lenght == 0)
		return false;
	
	obj = messageQueue.shift();
	
	if(obj == undefined)
		return false;
	
	// show message
	message(obj["type"],obj["txt"]);

	if(messageQueue.lenght == 0)
		return false;
	else
		return true;
}

/**
 * Wrapper für message(0,...)
 * @param errorTxt
 */
function showError(errorTxt) {
	addMessageToQueue(errorTxt,true);
	startMessageQueue();
	
}
/**
 * Wrapper für message(1,...)
 * @param intoTxt
 */
function showInfo(intoTxt) {
	addMessageToQueue(intoTxt,false);
	startMessageQueue();
}

function handleError(errorCode,msg) {
	switch (errorCode)
	{
		case "systemerror": 
			if(msg == undefined)
				showError("Ein interner Fehler ist aufgetreten. Versuchen Sie es erneut. Wenn der Fehler weiterhin besteht, wenden Sie sich an den Administrator.");
			else
				showError(msg);
			break;
		case "invalidparam": 
			if(msg == undefined)
				showError("Der Server hat ungueltige oder fehlende Parameter erhalten. Bitte machen Sie andere Eingaben!");
			else
				showError(msg);
			break;
		case "notloggedin": 
	
			if(getUrlParameterByName(urlParamLocation) != ansichtStartseite)
				showError("Bitte loggen Sie sich ein!");
			else if(msg != undefined)
					showError(msg);
	
			jsonBenutzer = undefined;			// Benutzer objekt ungültig
			gotoStartseite();
			break;
		case "loginfailed": 
			if(msg == undefined)
				showError("Dieser Eintrag wurde in der Datenbank nicht gefunden.");
			else
				showError(msg);
			break;
		case "registerfailed": 
			if(msg == undefined)
				showError("Ihre Daten konnten nicht registriert werden. Bitte versuchen Sie es erneut.");
			else
				showError(msg);
			break;
		case "emailalreadyinuse": 
			if(msg == undefined)
				showError("Entschuldigung, diese E-Mail Adresse ist bereits vergeben.");
			else
				showError(msg);
			break;
		case "pwresetfailed": 
			if(msg == undefined)
				showError("Ihr Passwort konnte nicht zurueckgesetzt werden. Bitte versuchen Sie es erneut.");
			else
				showError(msg);
			break;
		case "sessionexpired": 
			if(msg == undefined)
				showError("Ihre Login Session ist abgelaufen. Bitte loggen Sie sich erneut ein.");
			else
				showError(msg);
			break;
		case "notallowed":
			if(msg == undefined)
				showError("Diese Aktion ist nicht erlaubt. Sie haben womöglich nicht die nötigen Berechtigungen.");
			else
				showError(msg);
			break;
		default: 
			if(msg == undefined)
				showError("Ein unbekannter Fehler ist aufgetreten (Error Code nicht bekannt).");
			else
				showError(msg);
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
			handleError(jsonResponse["error"], jsonResponse[errorMsg]);	
		
		return false;
	}
	
	return true;
}


