package com.sopra.team1723.ctrl;

import java.util.Calendar;

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
    public final static String ActionDeleteBenutzer = "deleteBenutzer";
    public final static String ActionGetBenutzer = "getBenutzer";
    public final static String ActionGetOtherBenutzer = "getOtherBenutzer";
    public final static String ActionGetStudiengaenge = "getStudiengaenge";
    public final static String ActionGetSemester = "getSemester";
    public final static String ActionAenderePasswort = "aenderePasswort";
    public final static String ActionAendereProfil = "aendereProfil";
    public final static String ActionUploadProfilBild = "uploadProfilBild";
    public final static String ActionUploadKKBild = "uploadKKBild";
    public final static String ActionUploadKKVideo = "uploadKKVideo";
    public final static String ActionSucheBenVeranst = "sucheBenVeranst";
    public final static String ActionSucheBenutzer = "sucheBenutzer";
    public final static String ActionSucheStudiengang = "sucheStudiengang";
    public final static String ActionLoescheVn = "vnLoeschen";
    public final static String ActionPing = "ping";

    
    public final static String ActionGetVeranstaltung = "getVeranstaltung";
    public final static String ActionLeseVeranst = "leseVeranstaltungen";       // Liefert eine Liste von Veranstaltugen
    public final static String LeseVeranstMode = "mode";                        // Liefert Veranstaltungen
    public final static String LeseVeranstModeStudiengangSemester =             // Liefert alle Veranstaltungen
            "studiengangSemester";                                              // für das gewählte Semester und Studiengang
    public final static String LeseVeranstModeMeine = "meine";                  // Liefert meine Veranstaltungen 
    public final static String ActionVeranstaltungBearbeiten = "bearbeiteVeranst";

    public final static String ActionAusschreiben = "ausschreiben";
    public final static String ActionEinschreiben = "einschreiben";
    public final static String ActionVeranstErstellen = "erstelleVeranst";

    public final static String ActionGetStudgVn = "getStudgVn";
    public final static String ActionGetModeratorenVn = "getModVn";

    public final static String ActionLeseBenachrichtungen = "leseBen";
    public final static String ActionMarkiereBenGelesen = "benGelesen";
    public final static String ActionEinlModeratorAnnehmen = "einlModAnnehmen";
    public final static String ActionEinlModeratorAblehnen = "einlModAblehnen";

    public final static String ActionGetKarteikartenVorgaenger = "getKkVor";
    public final static String ActionGetKarteikartenNachfolger = "getKkNach";
    public final static String ActionGetKarteikarteByID = "getKarteikarte";
    public final static String ActionGetKarteikartenKinder = "getKKKinder";
    public final static String ActionGetKarteikartenVater = "getKKVater";
    public final static String ActionErstelleKarteikarte = "erstelleKarteikarte";
    
    public final static String ActionSpeichereNotiz = "speichereNotiz";
    public final static String ActionLeseNotiz = "leseNotiz";
    
    public final static String ActionVoteKommentarUp = "voteUpKomm";
    public final static String ActionVoteKommentarDown = "voteDownKomm";
    public final static String ActionDeleteKommentar = "deleteKomm";
    public final static String ActionErstelleAntwortKommentar = "erstelleAntwKomm";
    public final static String ActionErstelleThemaKommentar = "erstelleThemaKomm";
    public final static String ActionLeseAntwortKommentar = "leseAntwKomm";
    public final static String ActionLeseThemaKommentar = "leseThemaKomm";
    
    public final static String ActionVoteKarteikarteUp = "voteUpKarteik";
    public final static String ActionVoteKarteikarteDown = "voteDownKarteik";
    

    public final static String Klasse = "klasse";
    public final static String KlasseBenutzer = "klasseBenutzer";
    public final static String KlasseVeranst = "klasseVeranst";
    public final static String KlasseStudiengang = "klasseStudiengang";
    public final static String KlasseKarteikarte = "klasseKarteikarte";


    // Parameter für Benutzer
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


    // Parameter für Veranstaltung
    public final static String Titel = "titel";
    public final static String Beschr = "beschr";
    public final static String Semester = "semester";
    public final static String BewertungenErlauben = "bewertungenErlauben";
    public final static String ModeratorKkBearbeiten = "moderatorKkBearbeiten";
    public final static String KommentareErlauben = "kommentareErlauben";
    public final static String Ersteller = "ersteller";
    public final static String ErsteKarteikarte = "ersteKarteikarte";
    public final static String Moderatoren = "moderatoren";
    public final static String AnzTeilnehmer = "anzTeilnehmer";
    public final static String Angemeldet = "angemeldet";
    public final static String KennwortGesetzt = "kennwortGesetzt";
    
    
    // Parameter für Karteikarte  
    public final static String Veranstaltung = "veranstaltung";
    public final static String Aenderungsdatum = "aenderungsdatum";
    public final static String Bewertung = "bewertung";
    
    public final static String Reihenfolge = "reihenfolge";
    
    // Kommentare
    public final static String HatGevoted = "hatgevotet";
    public final static String ErstellDatum = "erstellDatum";
    public final static String AntwortCount = "antwCount";
    public final static String KommentarVaterId = "vaterId";
    public final static String KommentarKKid = "kkId";
    
    // Parameter für Benachrichtigung
    public final static String Inhalt = "inhalt";
    public final static String Gelesen = "gelesen";
    public final static String Type = "type";
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
    public final static String GewaehlterStudiengang = "gewaehlterStudiengang";


    /**
     * JSON-Feld-Werte, die nicht schon als Parameter vorkommen
     */

    public final static String jsonErrorCode = "error";
    public final static String jsonErrorMsg = "errorMessage";
    public final static String jsonErrorNoError = "noerror";                        // Kein Fehler

    // Alle bekannten Fehlertypen
    public final static String jsonErrorSystemError = "systemerror";                // Ein Interner Fehler ist aufgetreten
    public final static String jsonErrorInvalidParam = "invalidparam";              // Allgemeiner Fehler. Die Übergebene Parameter sind unbekannt oder es fehlen Parameter
    public final static String jsonErrorNotLoggedIn = "notloggedin";                // Der Benutzer ist nicht eingeloggt und hat deshalb nicht die benötigen Rechte
    public final static String jsonErrorLoginFailed = "loginfailed";                // Login ist fehlgeschlagen. Email existiert nicht oder Passwort ist falsch.  
    public final static String jsonErrorEmailAlreadyInUse = "emailalreadyinuse";    // Fehler beim Registieren. Email-Adresse schon vergeben
    public final static String jsonErrorPwResetFailed = "pwresetfailed";            // Fehler beim Zurücksetzen des Passworts
    public final static String jsonErrorSessionExpired = "sessionexpired";          // Fehler beim Zurücksetzen des Passworts
    public final static String jsonErrorNotAllowed = "notallowed";                  // Fehler unerlaubte aktion

    // ArrayResults
    public final static String jsonArrResult = "arrResult";
    public final static String jsonStrResult = "strResult";



    public static Calendar WiSeBeginn = Calendar.getInstance();
    public static Calendar WiSeEnde = Calendar.getInstance();
    static{
        WiSeBeginn.set(Calendar.MONTH, 9);
        WiSeBeginn.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println(WiSeBeginn.get(Calendar.DAY_OF_MONTH));
        WiSeEnde.set(Calendar.MONTH, 2);
        WiSeEnde.set(Calendar.DAY_OF_MONTH, 31); 
        System.out.println(WiSeEnde.get(Calendar.DAY_OF_MONTH));
    }


}
