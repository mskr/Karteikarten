/**
 * @author Andreas, Marius
 */

/**
 * Diese Funktion zeigt einen roten oder gruenen Balken
 * am oberen Bildschirmrand an, der eine Fehler-
 * oder auch eine Erfolgsmeldung enthaelt.
 * @param isError true falls es sich um eine Fehlermeldung handelt, false andernfalls
 * @param text anzuzeigende Meldung
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
//        $(elemClass).show().fadeOut(5000);
        $(elemClass).slideDown("fast", function() {
            setTimeout(function() {
                $(elemClass).slideUp("slow");
            }, 3000);
        });
    } else {
        $(elemClass).stop();
        $(elemClass).css("opacity","1");
        $(elemClass).fadeOut(5000);
    }
}

// Speichert mehrere Meldungen in einer Warteschlange
var messageQueue = [];
// Dauer in Millisekunden, die eine Meldung angezeigt werden soll
var messageTimer;
// Speichert, ob aktuell eine Meldung angezeigt wird
var laeuftGerade = false;

/**
 * Ruft sich in regelmaessigen Zeitabstaenden selbst auf, um die naechste Meldung anzuzeigen.
 */
function bearbeiteMessageQueue()
{
	if(showNextMessage())
	{
		messageTimer = setTimeout(bearbeiteMessageQueue, 3000);
		laeuftGerade = true;
	}
	else
	{
		clearTimeout(messageTimer);
		laeuftGerade = false;
	}
}
/**
 * Beginnt die Verarbeitung der Meldungen. Tut nichts, wenn die Verarbeitung schon laeuft.
 */
function startMessageQueue()
{
	if(laeuftGerade)
		return;
	bearbeiteMessageQueue();
}
/**
 * Leert die Queue und verarbeitet keine weiteren Meldungen.
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
 * Holt die nächste Nachricht aus der Queue und zeigt sie an.
 * @returns {Boolean} true falls weitere Meldungen warten
 */
function showNextMessage()
{
	if(messageQueue.length == 0)
		return false;
	
	obj = messageQueue.shift();
	
	if(obj == undefined)
		return false;
	
	// Meldung anzeigen
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
	console.log("[ERROR] " + errorTxt);
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

/**
 * Uebersetzt Error Codes in fuer Benutzer angemessene Fehlermeldungen und zeigt diese an.
 * In einigen Faellen sendet der Server eine solche Fehlermeldung zusaetzlich zum Error Code mit.
 * In diesen Faellen wird diese Fehlermeldung angezeigt. 
 * Andernfalls wird eine hier als String gespeicherte Fehlermeldung angezeigt. 
 * Bei 'notloggedin' leitet diese Funktion den Benutzer zur Startseite weiter.
 * Bei 'notallowed' leitet diese Funktion den Benutzer zur Hauptseite weiter.
 * @param errorCode Error Code
 * @param msg Fehlermeldung
 */
function handleError(errorCode,msg) {
	switch (errorCode)
	{
		case "systemerror": 
			if(msg == undefined)
				showError("Ein interner Serverfehler ist aufgetreten. " +
						"Versuchen Sie es erneut. " +
						"Wenn der Fehler weiterhin besteht, wenden Sie sich an den Administrator.");
			else
				showError(msg);
			break;
		case "invalidparam": 
			if(msg == undefined)
				showError("Der Server hat unerwartete Eingaben erhalten, die er nicht verarbeiten kann. " +
						"Bitte machen Sie andere Eingaben.");
			else
				showError(msg);
			break;
		case "notloggedin": 
            if(msg == undefined)
                showError("Sie sind nicht eingeloggt.");
            else
                showError(msg);
            // Benutzer Objekt loeschen
			jsonBenutzer = undefined;
			gotoStartseite();
			break;
		case "loginfailed": 
			if(msg == undefined)
				showError("Dieser Benutzer wurde in der Datenbank nicht gefunden.");
			else
				showError(msg);
			break;
		case "registerfailed": 
			if(msg == undefined)
				showError("Die angegebenen Daten konnten nicht registriert werden. Bitte geben Sie andere Daten ein.");
			else
				showError(msg);
			break;
		case "emailalreadyinuse": 
			if(msg == undefined)
				showError("Diese E-Mail Adresse ist bereits vergeben.");
			else
				showError(msg);
			break;
		case "pwresetfailed": 
			if(msg == undefined)
				showError("Ihr Passwort konnte nicht zurueckgesetzt werden.");
			else
				showError(msg);
			break;
		case "sessionexpired": 
			if(msg == undefined)
				showError("Sie wurden automatisch ausgeloggt, da sie zu lange inaktiv waren.");
			else
				showError(msg);
			break;
		case "notallowed":
			if(msg == undefined)
				showError("Zugriff verweigert. Sie haben nicht die erforderlichen Rechte.");
			else
				showError(msg);
			
			gotoHauptseite();
			break;
		default: 
			if(msg == undefined)
				showError("Unbekannter Fehler. Error Code = "+errorCode);
			else
				showError(msg);
		break;
	}
}

/**
 * Prueft ob eine gegebene Serverantwort fehlerfrei ist.
 * @param jsonResponse Antwortobjekt (JSON)
 * @param specialErrorHandlingFkt Spezialbehandlung fuer einen gegebenen Errorcode.
 * Dieser Callback muss true zurueckgeben, falls der Fehler nicht mehr anderweitig 
 * bearbeitet werden soll (insb. durch eine Fehlermeldung).
 * @returns true, falls fehlerfrei
 */
function verifyResponse(jsonResponse, specialErrorHandlingFkt) 
{
	if(jsonResponse["error"] != "noerror")
	{
		var processed = false;
		
		if(specialErrorHandlingFkt != undefined)
			processed = specialErrorHandlingFkt(jsonResponse["error"], jsonResponse);
		
		if(!processed)
			handleError(jsonResponse["error"], jsonResponse[errorMsg]);	
		
		return false;
	}
	
	return true;
}