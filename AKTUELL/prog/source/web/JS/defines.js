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
var fileUploadServlet = "FileUploadServlet";

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
var actionUploadProfilBild = "uploadProfilBild";
var actionLeseVeranst = "leseVeranstaltungen";       // Liefert eine Liste von Veranstaltugen
var leseVeranstMode = "mode";                        // Liefert Veranstaltungen
var leseVeranstModeAlle = "alle";                    // Liefert alle Veranstaltungen
var leseVeranstModeSemester = "semester";            // Liefert alle Veranstaltungen im aktuellen Semester
var leseVeranstModeStudiengang = "studiengang";      // Liefert alle Veranstaltungen im aktuellen Studiengang (TODO)
var leseVeranstModeMeine = "meine";                  // Liefert meine Veranstaltungen 

// GET/POST-Parameter fuer die Servlets
// Werden auch als Schluesselnamen fuer JSON Objekte verwendet
var paramId = "id";
var paramEmail = "email";
var paramEmailNew = "emailNew";
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
var paramProfilBild = "profilBildPfad";

// Veranstaltungen
var paramTitel = "titel";
var paramBeschr = "beschr";
var paramSemester = "semester";
var paramBewertungenErlauben = "bewertungenErlauben";
var paramModeratorKkBearbeiten = "moderatorKkBearbeiten";
var paramKommentareErlauben = "kommentareErlauben";
var paramErsteller = "ersteller";
var paramModeratoren = "moderatoren";
var paramAnzTeilnehmer = "anzTeilnehmer";

// Schluesselnamen fuer JSON Objekte
var keyJsonArrResult = "arrResult";

// GET-Parameter fuer den urlHandler.js
var urlParamLocation = "location";
var urlParamId = "id";

var ansichtStartseite = "startseite";
var ansichtHauptseite = "hauptseite";
var ansichtProfilseite = "profilseite";
var ansichtVeranstaltungseite = "veranstaltungseite";
var alleAnsichten = [ansichtStartseite,ansichtHauptseite,ansichtProfilseite,ansichtVeranstaltungseite];