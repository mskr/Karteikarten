/**
 * @author mk
 */

var adminServlet = "AdminServlet";
var benutzerServlet = "BenutzerServlet";
var karteikartenServlet = "KarteikartenServlet";
var kommentarServlet = "KommentarServlet";
var notizServlet = "NotizServlet";
var profilServlet = "ProfilServlet";
var veranstaltungServlet = "VeranstaltungServlet";

var actionLogin = "login";
var actionLogout = "logout";
var actionRegister = "registrieren";
var actionResetPasswort = "resetPasswort";
var actionGetBenutzer = "getBenutzer";
var actionGetStudiengaenge = "getStudiengaenge";
var actionGetOtherBenutzer = "getOtherBenutzer";
var actionGetStudiengaenge = "getStudiengaenge";
var actionAenderePasswort = "aenderePasswort";
var actionAendereProfil = "aendereProfil";

// GET/POST-Parameter fuer die Servlets
// Werden auch als Schluesselnamen fuer JSON Objekte verwendet
var paramEmail = "email";
var paramPasswort = "pass";
var paramPasswortNew = "passNew";
var paramVorname = "vorname";
var paramNachname = "nachname";
var paramMatrikelNr = "matrikelnr";
var paramStudiengang = "studiengang";
var paramNutzerstatus = "nutzerstatus";
var paramNofityVeranstAenderung = "notifyVeranstAenderung";
var paramNotifyKarteikartenAenderung = "notifyKarteikartenAenderung";
var paramNotifyKommentare = "notifyKommentare";
var paramNotifyKommentareValKeine = "KEINE";
var paramNotifyKommentareValVeranst = "VERANSTALTUNG_TEILGENOMMEN";
var paramNotifyKommentareValDiskussion = "DISKUSSION_TEILGENOMMEN";

// Schluesselnamen fuer JSON Objekte
var keyJsonArrResult = "jsonArrResult";

// GET-Parameter fuer den urlHandler.js
var urlParamLocation = "location";
var urlParamBenutzerProfil = "user";

var ansichtStartseite = "startseite";
var ansichtHauptseite = "hauptseite";
var ansichtProfilseite = "profilseite";
var ansichtVeranstaltungseite = "veranstaltungseite";
var alleAnsichten = [ansichtStartseite,ansichtHauptseite,ansichtProfilseite,ansichtVeranstaltungseite];