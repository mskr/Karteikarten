package com.sopra.team1723.ctrl;

/**
 * Diese Klasse enthält alle Parameter die übertragen werden sowie die JSON-Objekte
 * @author Andreas
 *
 */
public class ParamDefines
{
    public final static String Action = "action";
    public final static String ActionLogin = "login";
    public final static String ActionLogout = "logout";
    public final static String ActionRegister = "registrieren";
    public final static String ActionResetPasswort = "resetPasswort";
    public final static String ActionGetBenutzer = "getBenutzer";
    public final static String ActionGetOtherBenutzer = "getOtherBenutzer";
    public final static String ActionGetStudiengaenge = "getStudiengaenge";
    public final static String ActionGetSemester = "getSemester";
    public final static String ActionAenderePasswort = "aenderePasswort";
    public final static String ActionAendereProfil = "aendereProfil";
    public final static String ActionUploadProfilBild = "uploadProfilBild";
    public final static String ActionSucheBenVeranst = "sucheBenVeranst";
    
    public final static String ActionLeseVeranst = "leseVeranstaltungen";       // Liefert eine Liste von Veranstaltugen
    public final static String LeseVeranstMode = "mode";                        // Liefert Veranstaltungen
    public final static String LeseVeranstModeAlle = "alle";                    // Liefert alle Veranstaltungen
    public final static String LeseVeranstModeSemester = "semester";            // Liefert alle Veranstaltungen im aktuellen Semester
    public final static String LeseVeranstModeStudiengang = "studiengang";      // Liefert alle Veranstaltungen im aktuellen Studiengang (TODO)
    public final static String LeseVeranstModeMeine = "meine";                  // Liefert meine Veranstaltungen 

    public final static String ActionAusschreiben = "ausschreiben";
    public final static String ActionEinschreiben = "einschreiben";
    
    
    public final static String ActionLeseBenachrichtungen = "leseBen";
    

    public final static String Id = "id";
    public final static String Email = "email";
    public final static String EmailNew = "emailNew";
    public final static String Password = "pass";
    public final static String PasswordNew = "passNew";
    public final static String Vorname = "vorname";
    public final static String Nachname = "nachname";
    public final static String MatrikelNr = "matrikelnr";
    public final static String Studiengang = "studiengang";
    public final static String Nutzerstatus = "nutzerstatus";
    public final static String NotifyVeranstAenderung = "notifyVeranstAenderung";
    public final static String NotifyKarteikartenAenderung = "notifyKarteikartenAenderung";
    public final static String NotifyKommentare = "notifyKommentare";
    public final static String ProfilBildPfad = "profilBildPfad";
    public final static String UploadFile = "file";
    public final static String Suchmuster = "suchmuster";
    

    public final static String Titel = "titel";
    public final static String Beschr = "beschr";
    public final static String Semester = "semester";
    public final static String BewertungenErlauben = "bewertungenErlauben";
    public final static String ModeratorKkBearbeiten = "moderatorKkBearbeiten";
    public final static String KommentareErlauben = "kommentareErlauben";
    public final static String Ersteller = "ersteller";
    public final static String Moderatoren = "moderatoren";
    public final static String AnzTeilnehmer = "anzTeilnehmer";
    public final static String Angemeldet = "angemeldet";
    public final static String KennwortGesetzt = "kennwortGesetzt";
    
    public final static String SuchfeldErgText = "suchfeldErgText";
    public final static String SuchfeldErgKlasse= "suchfeldErgKlasse";
    public final static String SuchfeldErgId = "suchfeldErgId";

    public final static String benInhalt = "benInhalt";
    public final static String benGelesen = "benGelesen";
    public final static String benType = "benType";
    public final static String benTypeModerator = "moderator";
    public final static String benTypeVeranstaltung = "veranst";
    public final static String benTypeKarteikarte = "karteikarte";
    public final static String benTypeProfil = "profil";
    public final static String benTypeKommentar = "kommentar";
    public final static String benErstelldaum = "benErstelldaum";
    public final static String benVeranst = "benVeranst";
    public final static String benKarteikarte = "benKarteikarte";
    public final static String benKommentar = "benKommentar";
    public final static String benProfil = "benProfil";
    
    
    public final static String AktSemester = "aktSemester";
    
    
    public final static String GewaehltesSemester = "gewaehltesSemester";
    
    
    /**
     * JSON-Feld-Werte, die nicht schon als Parameter vorkommen
     */
    public final static String jsonErrorTxt = "error";
    public final static String jsonErrorNoError = "noerror";                        // Kein Fehler

    // Alle bekannten Fehlertypen
    public final static String jsonErrorSystemError = "systemerror";                // Ein Interner Fehler ist aufgetreten
    public final static String jsonErrorInvalidParam = "invalidparam";              // Allgemeiner Fehler. Die Übergebene Parameter sind unbekannt oder es fehlen Parameter
    public final static String jsonErrorNotLoggedIn = "notloggedin";                // Der Benutzer ist nicht eingeloggt und hat deshalb nicht die benötigen Rechte
    public final static String jsonErrorLoginFailed = "loginfailed";                // Login ist fehlgeschlagen. Email existiert nicht oder Passwort ist falsch.  
    public final static String jsonErrorEmailAlreadyInUse = "emailalreadyinuse";    // Fehler beim Registieren. Email-Adresse schon vergeben
    public final static String jsonErrorPwResetFailed = "pwresetfailed";            // Fehler beim Zurücksetzen des Passworts
    public final static String jsonErrorSessionExpired = "sessionexpired";          // Fehler beim Zurücksetzen des Passworts
    
    // ArrayResults
    public final static String jsonArrResult = "arrResult";
    public final static String jsonStrResult = "strResult";
    public final static String jsonArrSuchfeldResult = "arrSuchfeldResult";
    
    public final static int WiSeMonatBeginn = 10;
    public final static int WiSeTagBeginn = 1;
    public final static int WiSeMonatEnde = 3;
    public final static int WiSeTagEnde = 31;
    public final static int SoSeMonatBeginn = 4;
    public final static int SoSeTagBeginn = 1;
    public final static int SoSeMonatEnde = 9;
    public final static int SoSeTagEnde = 30;
    
}
