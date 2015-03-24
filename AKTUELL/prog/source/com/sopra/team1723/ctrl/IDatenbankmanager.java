package com.sopra.team1723.ctrl;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.*;

import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.DbFalseLoginDataException;
import com.sopra.team1723.exceptions.DbUniqueConstraintException;

/**
 * Interface zur Datenbank
 */
public interface IDatenbankmanager {

    /**
     * Liest Benutzer mit der angegebenen Email aus. Wird kein Benutzer
     * gefunden, so gibt die Methode null zur�ck
     * @param eMail 
     * @return
     */
    public Benutzer leseBenutzer(String eMail);

    /**
     * Legt neuen Benutzer in der Datenbank an. Tritt ein Fehler in der 
     * Datenbank auf, dann wird eine SQLException geworfen.
     * Wird versucht einen Benutzer mit einer bereits vorhandenen 
     * eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param benutzer 
     * @return
     */
    public void schreibeBenutzer(Benutzer benutzer)  throws DbUniqueConstraintException, SQLException;

    /**
     * Daten des angegebenen Benutzers werden in der Datenbank geupdatet. Kennwort, Matrikelnummer, 
     * Studiengang und Nutzerstatus werden nicht gesetzt, da der Benutzer diese nicht selbst �ndern kann
     * Die eMail kann auch ge�ndert werden.Der Datensatz wird dabei mithilfe des Parameters alteMail identifiziert. 
     * Diese Methode wird aufgerufen,wenn ein Benutzer sein eigenes Profil bearbeiten will. Das Kennwort kann in einer separaten Methode
     * bearbeitet werden. Matrikelnummer und Studiengang kann ein Benutzer nicht selber bearbeiten.
     * Tritt ein Fehler in der Datenbank auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer
     * mit einer bereits vorhandenen eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param benutzer 
     * @return
     */
    public void bearbeiteBenutzer(String alteMail, Benutzer benutzer) throws SQLException, DbUniqueConstraintException;
    

   /**
    * TODO
    * @param eMail
    * @param dateiName
 * @return 
    * @throws SQLException
    */
    public boolean aendereProfilBild(String eMail, String dateiName);
    
    /**
     * Daten des angegebenen Benutzers werden in der Datenbank geupdatet. Das Kennwort wird nicht gesetzt. Diese Methode wird verwendet, wenn
     * der Admin die Daten eines Benutzers bearbeiten m�chte. Er kann dabei alle Attribute also auch
     * Matrikelnummer und Studiengang �ndern. Um den Datensatz zu identifizieren wird der Parameter alteMail
     * verwendet. Tritt ein Fehler in der Datenbank auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer
     * mit einer bereits vorhandenen eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param benutzer 
     * @return
     */
    public void bearbeiteBenutzerAdmin(String alteMail, Benutzer benutzer) throws SQLException, DbUniqueConstraintException;

    /**
     * Entfernt den Benutzer aus der Datenbank. Tritt ein Fehler auf gibt
     * die Methode false zur�ck. Ansonsten true. (Auch wenn der Benutzer
     * gar nicht in der Datenbank vorhanden war.)
     * @param eMail 
     * @return
     */
    public boolean loescheBenutzer(String eMail);

    /**
     * �berpr�ft, ob eMail und Passwort zusammenpassen. 
     * Tritt ein Fehler in der Datenbank auf, dann wird eine
     * SQLException geworfen. Wenn kein Benutzer zu der angegebenen
     * eMail und Passwort gefunden wird, wird eine DbFalseLoginDataException geworfen
     * @param eMail 
     * @param passwort 
     * @return
     */
    public void pruefeLogin(String eMail, String passwort) throws SQLException, DbFalseLoginDataException;
    
    /**
     * Gibt eine Liste aller Studieng�nge zur�ck. Tritt ein Fehler
     * auf wird null zur�ckgegeben. Sind keine Studieng�nge in der 
     * Datenbnak vorhanden, dann wird eine leere Liste zur�ckgegeben
     * @param eMail 
     * @param passwort 
     * @return
     */
    public List<String> leseStudiengaenge();

    /**
     * �ndert das Passwort des angegebenen Benutzers. Liefert bei Erfolg
     * true zur�ck. (Auch wenn der Benutzer gar nicht in der Datenbank
     * vorhanden ist.) Bei einem Fehler wird false zur�ckgegeben.
     * @param eMail String 
     * @param neuesPasswort String 
     * @return
     */
    public boolean passwortAendern(String eMail , String neuesPasswort );

    /**
     * Holt Veranstaltung mit dem Titel des Parameters aus der Datenbank.
     * Ist keine Veranstaltung mit diesem Titel vorhanden oder tritt ein
     * Fehler auf, wird null zur�ckgegeben.
     * @param veranstTitel 
     * @return
     */
    public Veranstaltung leseVeranstaltung(String veranstTitel);

    /**
     * Fugt neue Veranstaltung in die Datenbank ein. Bei Erfolg wird
     * true zuruckgegeben. Bei einem Fehler in der Datenbank oder falls
     * es schon eine Veranstaltung mit dem gleichen Titel gibt, wird false
     * zuruckgeliefert.
     * @param veranst 
     * @return
     */
    public boolean schreibeVeranstaltung(Veranstaltung veranst);

    /**
     * Daten der angegebenen Veranstaltung werden in der Datenbank
     * geupdatet. Bei Erfolg liefert die Methode true zuruck (auch wenn
     * die Veranstaltung gar nicht in der Datenbank vorhanden war). Bei
     * einem Fehler false.
     * @param veranst 
     * @return
     */
    public boolean bearbeiteVeranstaltung(Veranstaltung veranst);

    /**
     * Entfernt die Veranstaltung aus der Datenbank. Tritt ein Fehler auf
     * gibt die Methode false zuruck. Ansonsten true. (Auch wenn die
     * Veranstaltung gar nicht in der Datenbank vorhanden war.)
     * @param veranstTitel 
     * @return
     */
    public boolean loescheVeranstaltung(String veranstTitel);

    /**
     * Holt Daten der Karteikarte anhand der ID aus der Datenbank und
     * return
     * @param karteikID 
     * @return
     */
    public Karteikarte leseKarteikarte(int karteikID);

    /**
     * @param vaterKarteikID 
     * @return
     */
    public Karteikarte[] leseKindKarteikarten(int vaterKarteikID);

    /**
     * Fugt neue Karteikarte in die Datenbank ein. Bei Erfolg wird true
     * zuruckgegeben. Bei einem Fehler in der Datenbank wird false
     * zuruckgeliefert.
     * @param karteik 
     * @param vaterKarteikID 
     * @param Position 
     * @return
     */
    public boolean schreibeKarteikarte(Karteikarte karteik, int vaterKarteikID, int Position);

    /**
     * @param karteik 
     * @param sohnKarteikID
     */
    public void schreibeErsteKarteikarte(Karteikarte karteik, int sohnKarteikID);

    /**
     * Daten der angegebenen Karteikarte werden in der Datenbank geupdatet.
     * Bei Erfolg liefert die Methode true zuruck (auch wenn die
     * Karteikarte gar nicht in der Datenbank vorhanden war). Bei einem
     * Fehler false.
     * @param karteik 
     * @return
     */
    public boolean bearbeiteKarteikarte(Karteikarte karteik);

    /**
     * Entfernt die Karteikarte aus der Datenbank. Tritt ein Fehler auf
     * gibt die Methode false zuruck. Ansonsten true. (Auch wenn die
     * Karteikarte gar nicht in der Datenbank vorhanden war.)
     * @param karteikID 
     * @return
     */
    public boolean loescheKarteikarte(int karteikID);

    /**
     * Speichert die Bewertung, die der Benutzer dieser Karteikarte gegeben
     * hat. Die Gesamtbewertung der Karteikarte wird entsprechend
     * angepasst. Bei einem Fehler wird false zuruckgeliefert ansonsten
     * true.
     * @param karteikID 
     * @param bewert 
     * @param benutzer 
     * @return
     */
    public boolean bewerteKarteikarte(int karteikID, int bewert, String benutzer);

    /**
     * Gibt true zuruck, falls der Benutzer diese Karteikarte bereits bewertet
     * hat. Ansonsten wird false zuruckgegeben.
     * @param karteikID 
     * @param benutzer 
     * @return
     */
    public boolean hatKarteikarteBewertet(int karteikID, String benutzer);

    /**
     * Gibt alle Kommentare zu einer Karteikarte zuruck. Bei einem Fehler
     * wird null zuruckgegeben
     * @param karteikID 
     * @param vaterKID 
     * @return
     */
    public Kommentar[] leseKommentare(int karteikID, int vaterKID);

    /**
     * Fugt neuen Kommentar in die Datenbank ein. Bei Erfolg wird
     * true zuruckgegeben. Bei einem Fehler in der Datenbank wird false
     * zuruckgeliefert.
     * @param kommentar 
     * @return
     */
    public boolean schreibeKommentar(Kommentar kommentar);

    /**
     * Daten des angegebenen Kommentars werden in der Datenbank geupdatet.
     * Bei Erfolg liefert die Methode true zuruck (auch wenn der
     * Kommentar gar nicht in der Datenbank vorhanden war). Bei einem
     * Fehler false.
     * @param kommentar 
     * @return
     */
    public boolean bearbeiteKommentar(Kommentar kommentar);

    /**
     * Entfernt den Kommentar aus der Datenbank. Tritt ein Fehler auf
     * gibt die Methode false zuruck. Ansonsten true. (Auch wenn der
     * Kommentar gar nicht in der Datenbank vorhanden war.)
     * @param kommentarID 
     * @return
     */
    public boolean loescheKommentar(int kommentarID);

    /**
     * Speichert die Bewertung, die der Benutzer diesem Kommentar gegeben
     * hat. Die Gesamtbewertung des Kommentars wird entsprechend
     * angepasst. Bei einem Fehler wird false zuruckgeliefert ansonsten
     * true.
     * @param kommentarID 
     * @param bewert 
     * @param benutzer 
     * @return
     */
    public boolean bewerteKommentar(int kommentarID, int bewert, String benutzer);

    /**
     * Gibt true zuruck, falls der Benutzer diesen Kommentar bereits bewertet
     * hat. Ansonsten wird false zuruckgegeben.
     * @param kommentarID 
     * @param benutzer 
     * @return
     */
    public boolean hatKommentarBewertet(int kommentarID, String benutzer);

    /**
     * Gibt true zuruck, falls der Benutzer ein Moderator dieser Veranstaltung
     * ist. Ansonsten false.
     * @param veranst 
     * @param benutzerMail 
     * @return
     */
    public boolean istModerator(Veranstaltung veranst, String benutzerMail);

    /**
     * Schreibt Benutzer in die Veranstaltung ein. Tritt ein Fehler auf gibt
     * die Methode false zuruck. Ansonsten true.
     * @param veranstTitel 
     * @param eMail 
     * @return
     */
    public boolean zuVeranstaltungEinschreiben(String veranstTitel, String eMail);

    /**
     * Meldet Benutzer von der Veranstaltung ab. Tritt ein Fehler auf gibt
     * die Methode false zuruck. Ansonsten true.
     * @param veranstTitel 
     * @param eMail 
     * @return
     */
    public boolean vonVeranstaltungAbmelden(String veranstTitel, String eMail);

    /**
     * @param erstellerEMail 
     * @param karteikID 
     * @return
     */
    public Notiz[] leseNotizen(String erstellerEMail, int karteikID);

    /**
     * @param notiz 
     * @return
     */
    public boolean schreibeNotiz(Notiz notiz);

    /**
     * @param notiz 
     * @return
     */
    public boolean bearbeiteNotiz(Notiz notiz);

    /**
     * @param notizID 
     * @return
     */
    public boolean loescheNotiz(int notizID);

    /**
     * @param eMail 
     * @param status 
     * @return
     */
    public boolean rolleZuweisen(String eMail, Nutzerstatus status);

}