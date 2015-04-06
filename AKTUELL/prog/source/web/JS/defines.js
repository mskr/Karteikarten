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
var suchfeldServlet = "SuchfeldServlet";
var benachrichtungsServlet = "BenachrichtigungsServlet";

var actionLogin = "login";
var actionLogout = "logout";
var actionRegister = "registrieren";
var actionResetPasswort = "resetPasswort";
var actionGetBenutzer = "getBenutzer";
var actionGetStudiengaenge = "getStudiengaenge";
var actionGetOtherBenutzer = "getOtherBenutzer";
var actionGetSemester = "getSemester";
var actionAenderePasswort = "aenderePasswort";
var actionAendereProfil = "aendereProfil";
var actionUploadProfilBild = "uploadProfilBild";
var actionSucheBenVeranst = "sucheBenVeranst";
var actionErstelleVeranst = "erstelleVeranst";

var actionLeseVeranst = "leseVeranstaltungen";       // Liefert eine Liste von Veranstaltugen
var leseVeranstMode = "mode";                        // Liefert Veranstaltungen
var leseVeranstModeStudiengangSemester = "studiengangSemester";            // Liefert alle Veranstaltungen im aktuellen Semester und studiengang
var leseVeranstModeMeine = "meine";                  // Liefert meine Veranstaltungen 

var actionAusschreiben = "ausschreiben";
var actionEinschreiben = "einschreiben";
var actionLeseBenachrichtungen = "leseBen";

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
var paramSuchmuster = "suchmuster";

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
var paramAngemeldet = "angemeldet";
var paramKennwortGesetzt = "kennwortGesetzt";

var paramAktSemester = "aktSemester";
var paramGewaehltesStudiengang = "gewaehlterStudiengang";
var paramGewaehltesSemester = "gewaehltesSemester";

// Benachrichtigungen
var paramBenInhalt = "benInhalt";
var paramBenGelesen = "benGelesen";
var paramBenType = "benType";
var paramBenTypeModerator = "moderator";
var paramBenTypeVeranstaltung = "veranst";
var paramBenTypeKarteikarte = "karteikarte";
var paramBenTypeProfil = "profil";
var paramBenTypeKommentar = "kommentar";
var paramBenErstelldaum = "benErstelldaum";
var paramBenVeranst = "benVeranst";
var paramBenKarteikarte = "benKarteikarte";
var paramBenKommentar = "benKommentar";
var paramBenProfil = "benProfil";

// Schluesselnamen fuer JSON Objekte
var keyJsonArrResult = "arrResult";
var keyJsonArrSuchfeldResult = "arrSuchfeldResult";
var keyJsonSuchfeldErgText = "suchfeldErgText";
var keyJsonSuchfeldErgKlasse = "suchfeldErgKlasse";
var keyJsonSuchfeldErgId = "suchfeldErgId";

// GET-Parameter fuer den urlHandler.js
var urlParamLocation = "location";
var urlParamId = "id";

var ansichtStartseite = "startseite";
var ansichtHauptseite = "hauptseite";
var ansichtProfilseite = "profilseite";
var ansichtVeranstaltungseite = "veranstaltungseite";
var alleAnsichten = [ansichtStartseite,ansichtHauptseite,ansichtProfilseite,ansichtVeranstaltungseite];
