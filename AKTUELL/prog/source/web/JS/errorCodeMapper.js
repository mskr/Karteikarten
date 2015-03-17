/**
 * @author mk
 * @see Servlet
*/

/*
 * Zentrale Funktion,
 * die vom Server empfangene Error-Codes
 * in eine Fehlermeldung fuer den Benutzer uebersetzt.
*/
function buildMessage(errCode) {
    switch(errCode) {
        case "notloggedin": return "Bitte melden Sie sich erneut an.";
        case "loginfailed": return "Dieser Eintrag wurde in der Datenbank nicht gefunden oder ein Serverfehler ist aufgetreten.";
        case "logoutfailed": return "Ihr Logout ist fehlgeschlagen.";
        case "registerfailed": return "Ihre Daten konnten nicht registriert werden. Bitte versuchen Sie es erneut.";
        case "pwresetfailed": return "Ihr Passwort konnte nicht zurueckgesetzt werden. Bitte versuchen Sie es erneut.";
        case "invalidparam": return "Der Server hat ungueltige oder fehlende Parameter erhalten.";
    }
}