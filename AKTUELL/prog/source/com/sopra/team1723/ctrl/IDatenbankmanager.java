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
     * Liest Benutzer mit der angegebenen Email aus der Datenbank.
     * @param eMail referenziert eindeutig einen Benutzer 
     * @return Gibt Benutzer zurück. Wird kein Benutzer gefunden oder
     * tritt ein Fehler auf liefert die Methode null zurück
     */
    public Benutzer leseBenutzer(String eMail);
    
    /**
     * Liest Benutzer mit der angegebenen id aus der Datenbank.
     * @param id referenziert eindeutig einen Benutzer 
     * @return Gibt Benutzer zurück. Wird kein Benutzer gefunden oder
     * tritt ein Fehler auf liefert die Methode null zurück
     */
    public Benutzer leseBenutzer(int id);

    /**
     * Legt neuen Benutzer in der Datenbank an. Tritt ein Fehler in der 
     * Datenbank auf, dann wird eine SQLException geworfen.
     * Wird versucht einen Benutzer mit einer bereits vorhandenen 
     * eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param benutzer-Objket, das in der Datenbnank gespeichert wird 
     * @throws DbUniqueConstraintException, SQLException
     */
    public void schreibeBenutzer(Benutzer benutzer)  throws DbUniqueConstraintException, SQLException;

    /**
     * Daten des angegebenen Benutzers werden in der Datenbank geupdatet. Kennwort, Matrikelnummer, 
     * Studiengang und Nutzerstatus werden nicht gesetzt, da der Benutzer diese nicht selbst ändern kann.
     * Die eMail kann auch geändert werden. Der Datensatz wird dabei mithilfe des Parameters alteMail identifiziert. 
     * Das Kennwort kann in einer separaten Methode bearbeitet werden{@link #passwortAendern(String, String)}.
     * Matrikelnummer und Studiengang kann ein Benutzer nicht selber bearbeiten. Tritt ein Fehler in der Datenbank 
     * auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer mit einer bereits vorhandenen 
     * eMail Adresse anzulegen, so wird eine DbUniqueConstraintException geworfen
     * @param alteMail referenziert eindeutig den zu bearbeitenden Benutzer
     * @param benutzer-Objket, das in der Datenbank geupdatet wird
     * @throws DbUniqueConstraintException, SQLException
     */
    public void bearbeiteBenutzer(String alteMail, Benutzer benutzer) throws SQLException, DbUniqueConstraintException;
    
   /**
    * Ändert das Profilbild eines Benutzers in der Datenbank.
    * @param eMail referenziert eindeutig den Benutzer
    * @param dateiName, Dateiname des neuen Profilbilds
    * @return true, falls kein Fehler auftritt, false bei einem Fehler
    */
    public boolean aendereProfilBild(String eMail, String dateiName);
    
    /**
     * Daten des angegebenen Benutzers werden in der Datenbank geupdatet. Das Kennwort wird nicht gesetzt
     * {@link #passwortAendern(String, String)}. Diese Methode wird verwendet, wenn
     * der Admin die Daten eines Benutzers bearbeiten möchte. Er kann dabei alle Attribute also auch
     * Matrikelnummer und Studiengang ändern. Um den Datensatz zu identifizieren wird der Parameter alteMail
     * verwendet. Tritt ein Fehler in der Datenbank auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer
     * mit einer bereits vorhandenen eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param alteMail referenziert eindeutig den zu bearbeitenden Benutzer
     * @param benutzer-Objket, das in der Datenbank geupdatet wird 
     * @throws DbUniqueConstraintException, SQLException
     */
    public void bearbeiteBenutzerAdmin(String alteMail, Benutzer benutzer) throws SQLException, DbUniqueConstraintException;

    /**
     * Entfernt den Benutzer aus der Datenbank. 
     * @param eMail referenziert eindeutig den zu löschenden Benutzer
     * @return  Tritt ein Fehler auf gibt die Methode false zurück. Ansonsten true.
     * (Auch wenn der Benutzer gar nicht in der Datenbank vorhanden war.)
     */
    public boolean loescheBenutzer(String eMail);

    /**
     * Überprüft, ob eMail und Passwort zusammenpassen. 
     * Tritt ein Fehler in der Datenbank auf, dann wird eine
     * SQLException geworfen. Wenn kein Benutzer zu der angegebenen
     * eMail und Passwort gefunden wird, wird eine DbFalseLoginDataException geworfen
     * @param eMail referenziert eindeutig den Benutzer
     * @param passwort des Benutzers der sich einloggen will.
     * @throws SQLException, DbFalseLoginDataException
     */
    public void pruefeLogin(String eMail, String passwort) throws SQLException, DbFalseLoginDataException;
    
    /**
     * Gibt eine Liste aller Studiengänge zurück. 
     * @return Liste der Studiengaenge. Tritt ein Fehler auf wird null zurückgegeben. Sind keine Studiengänge in der 
     * Datenbnak vorhanden, dann wird eine leere Liste zurückgegeben
     */
    public List<String> leseStudiengaenge();

    /**
     * Ändert das Passwort des angegebenen Benutzers. 
     * @param eMail referenziert eindeutig den Benutzer
     * @param neuesPasswort 
     * @return Liefert bei Erfolg true zurück. (Auch wenn der Benutzer gar nicht in der Datenbank
     * vorhanden ist.) Bei einem Fehler wird false zurückgegeben.
     */
    public boolean passwortAendern(String eMail , String neuesPasswort );

    /**
     * Holt Veranstaltung mit der id des Parameters aus der Datenbank.
     * @param id referenziert eindeutig eine Veranstaltung 
     * @return Veranstaltung mit der angegebenen id. Ist keine Veranstaltung mit diesem Titel 
     * vorhanden oder tritt ein Fehler auf, wird null zurückgegeben.
     */
    public Veranstaltung leseVeranstaltung(int id);
    
    /**
     * Holt alle Veranstaltungen aus der Datenbank und packt sie in eine Array List.
     * @return Liste aller Veranstaltungen. Gibt es keine Veranstaltungen wird eine 
     * leere Liste zurückgegeben. Bei einem Fehler kommt null zurück.
     */
    public List<Veranstaltung> leseAlleVeranstaltungen();
    
    /**
     * Holt Veranstaltungen, die von dem angegebenen Studiengang gehört werden können
     * aus der Datenbank.
     * @param studiengang
     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die 
     * Methode eine leere Liste zurück. Bei einem Fehler kommt null zurück.
     */
    public List<Veranstaltung> leseVeranstaltungenStudiengang(String studiengang);

    /**
     * Holt alle Veranstaltungen aus dem angegebenen Semester aus der Datenbank.
     * @param semester 
     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die Methode eine leere Liste zurück.
     * Bei einem Fehler kommt null zurück.
     */
    public List<Veranstaltung> leseVeranstaltungenSemester(String semester);
    
    /**
     * Holt alle Veranstaltungen des angegebenen Benutzers.
     * @param benutzer referenziert eindeutig einen Benutzer 
     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die Methode eine leere Liste zurück.
     * Bei einem Fehler kommt null zurück.
     */
    public List<Veranstaltung> leseVeranstaltungen(int benutzer);
    
    /**
     * Holt alle Moderatoren zu der angegebenen Veranstaltung aus der Datenbank.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return Liste von Moderatoren der Veranstaltung.Hat die Veranstaltung  ine Moderatoren
     * wird die leere Liste zurückgegeben. Bei einem Fehler wird null zurückgegeben.
     */
    public List<Benutzer> leseModeratoren(int veranstaltung);
    
    /**
     * Holt alle Studiengaenge zu der angegebenen Veranstaltung aus der Datenbank.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return Liste der Studiengänge zu dieser Veranstaltung. Wird die Veranstaltung von keinem Studiengang
     * gehört wird die leere Liste zurückgegeben. Bei einem Fehler wird null zurückgegeben.
     */
    public List<String> leseStudiengaenge(int veranstaltung);
    
    
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
     * 
     * Diese Methode durchsucht die Datenbank nach dem suchmuster. Die Methode ist flexibel gebaut.
     * Das heißt, dass man in einer Liste angeben kann nach welchen Feldern in der Datenbank gesucht
     * werden soll. Die Namen der Felder sind eindeutig durch den Klassennamen und den Attributnamen bestimmt.
     * Die beiden Werte sind in der Klasse Klassenfeld gekapselt. Die Methode liefert die zum suchmuster
     * ähnlichen Ergebnisse in einer Liste zurück. In der Klasse ErgebnisseSuchfeld sind dabei 
     * der ähnliche Text, der Klassenname und die ID des Objekts zu dem der ähnliche Text gehört
     * gekapselt. In der Liste werden maximal 5 Einträge gespeichert. Ist der Rückgabewert null, so
     * ist ein Fehler aufgetreten. Gibt es kein ähnliches Feld zu dem Suchmuster, dann liefert die
     * Methode eine leere Liste zurück.
     * @param suchmuster nach dem Felder in der Datenbank verglichen werden
     * @param suchfeld gibt an welche Felder in der Datenbank mit dem Suchmuster verglichen werden.
     * @return Liste mit Objekten der Klasse ErgebnisseSuchfeld. Wird kein ähnliches Feld gefunden
     * gibt die Methode die leere Liste zurück. Bei einem Fehler wird null zurückgegeben.
     */
    public List<ErgebnisseSuchfeld> durchsucheDatenbank(String suchmuster, List<Klassenfeld> suchfeld);
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