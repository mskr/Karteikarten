package com.sopra.team1723.ctrl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.*;

import org.json.simple.JSONObject;

import java.sql.Connection;
import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.DbFalseLoginDataException;
import com.sopra.team1723.exceptions.DbFalsePasswortException;
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
     * Die eMail kann auch geändert werden. Um den Datensatz zu identifizieren wird die ID im Benutzer Objekt verwendet. 
     * Das Kennwort kann in einer separaten Methode bearbeitet werden{@link #passwortAendern(String, String)}.
     * Matrikelnummer und Studiengang kann ein Benutzer nicht selber bearbeiten. Tritt ein Fehler in der Datenbank 
     * auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer mit einer bereits vorhandenen 
     * eMail Adresse anzulegen, so wird eine DbUniqueConstraintException geworfen
     * @param benutzer-Objket, das in der Datenbank geupdatet wird
     * @throws DbUniqueConstraintException, SQLException
     */
    public void bearbeiteBenutzer(Benutzer benutzer) throws SQLException, DbUniqueConstraintException;

    /**
     * Ändert das Profilbild eines Benutzers in der Datenbank.
     * @param benutzerId referenziert eindeutig den Benutzer
     * @param dateiName, Dateiname des neuen Profilbilds
     * @return true, falls kein Fehler auftritt, false bei einem Fehler
     */
    public boolean aendereProfilBild(int benutzerId, String dateiName);

    /**
     * Daten des angegebenen Benutzers werden in der Datenbank geupdatet. Das Kennwort wird nicht gesetzt
     * {@link #passwortAendern(String, String)}. Diese Methode wird verwendet, wenn
     * der Admin die Daten eines Benutzers bearbeiten möchte. Er kann dabei alle Attribute also auch
     * Matrikelnummer und Studiengang ändern. Um den Datensatz zu identifizieren wird die ID im Benutzer Objekt
     * verwendet. Tritt ein Fehler in der Datenbank auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer
     * mit einer bereits vorhandenen eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param benutzer-Objket, das in der Datenbank geupdatet wird 
     * @throws DbUniqueConstraintException, SQLException
     */
    public void bearbeiteBenutzerAdmin(Benutzer benutzer, Benutzer aktuellerBenutzer) throws SQLException, DbUniqueConstraintException;

    /**
     * Entfernt den Benutzer aus der Datenbank. 
     * @param benutzerId referenziert eindeutig den zu löschenden Benutzer
     * @return  Tritt ein Fehler auf gibt die Methode false zurück. Ansonsten true.
     * (Auch wenn der Benutzer gar nicht in der Datenbank vorhanden war.)
     */
    public boolean loescheBenutzer(int benutzerId);

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
     * Datenbank vorhanden, dann wird eine leere Liste zurückgegeben
     */
    public List<String> leseStudiengaenge();

    /**
     * Gibt eine Liste aller Semester zurück. 
     * @return Liste der Semester. Tritt ein Fehler auf wird null zurückgegeben. Werden keine Semester in der 
     * Datenbank gefunden, dann wird eine leere Liste zurückgegeben
     */
    public Map<Integer,String> leseSemester();

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

    //    /**
    //     * Holt alle Veranstaltungen aus der Datenbank und packt sie in eine Array List.
    //     * @return Liste aller Veranstaltungen. Gibt es keine Veranstaltungen wird eine 
    //     * leere Liste zurückgegeben. Bei einem Fehler kommt null zurück.
    //     */
    //    public List<Veranstaltung> leseAlleVeranstaltungen();
    //    
    //    /**
    //     * Holt Veranstaltungen, die von dem angegebenen Studiengang gehört werden können
    //     * aus der Datenbank.
    //     * @param studiengang
    //     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die 
    //     * Methode eine leere Liste zurück. Bei einem Fehler kommt null zurück.
    //     */
    //    public List<Veranstaltung> leseVeranstaltungenStudiengang(String studiengang);
    //
    //    /**
    //     * Holt alle Veranstaltungen aus dem angegebenen Semester aus der Datenbank.
    //     * @param semester 
    //     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die Methode eine leere Liste zurück.
    //     * Bei einem Fehler kommt null zurück.
    //     */
    //    public List<Veranstaltung> leseVeranstaltungenSemester(String semester);

    /**
     * Holt alle Veranstaltungen zu dem angegebenen Semester und Studiengang aus der Datenbank.
     * @param semester, studiengang 
     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die Methode eine leere Liste zurück.
     * Bei einem Fehler kommt null zurück.    
    */
    public List<Veranstaltung> leseVeranstaltungen(String semester, String studiengang);

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
     * Prüft ob der Benutzer Moderator dieser Veranstaltung ist.
     * @param benutzer referenziert eindeutig einen Benutzer
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return Gibt true zurück, wenn der Benutzer Moderator der Veranstaltung ist und false falls nicht.
     * Tritt ein Fehler auf wird null zurückgegeben
     */
    public Boolean istModerator(int benutzer, int veranstaltung);

    /**
     * Prüft, ob ein Benutzer in eine Veranstaltung eingeschrieben ist.
     * @param benutzer
     * @param veranstaltung
     * @return true, falls eingeschrieben, false andernfalls.
     */
    public boolean angemeldet(int benutzer, int veranstaltung) throws SQLException;

    /**
     * Fügt eine neue Veranstaltung in die Datenbank ein.
     * Werden Titel und Semester zu bereits vorhandenen Werten geändert,
     * wird eine DbUniqueConstraintException geworfen. Bei einem anderen Fehler wird
     * eine SQLException geworfen.
     * @param veranst-Objekt, das in die db eingefügt wird
     * @param studiengaenge, die zu der Veranstaltung gehören
     * Veranstaltung werden.
     * @return gibt die ID der soeben eingefügten Veranstaltung zurück.
     */
    public int schreibeVeranstaltung(Veranstaltung veranst, String[] studiengaenge, int[] moderatorenIds) 
            throws SQLException, DbUniqueConstraintException, DbFalsePasswortException;

    /**
     * Daten der angegebenen Veranstaltung werden in der Datenbank
     * geupdatet. Werden Titel und Semester zu bereits vorhandenen Werten geändert,
     * wird eine DbUniqueConstraintException geworfen. Bei einem anderen Fehler wird
     * eine SQLException geworfen.
     * @param veranst-Objekt, das in der db geupdatet wird
     * @return
     */
    public void bearbeiteVeranstaltung(Veranstaltung veranst,String[] studiengaenge, int[] moderatorenIds) throws SQLException, DbUniqueConstraintException;

    /**
     * Entfernt die Veranstaltung aus der Datenbank. Tritt ein Fehler auf
     * gibt die Methode false zuruck. Ansonsten true. (Auch wenn die
     * Veranstaltung gar nicht in der Datenbank vorhanden war.)
     * @param veranstTitel 
     * @return
     */
    public boolean loescheVeranstaltung(int veranstId);

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
    public Map<IjsonObject,Integer> durchsucheDatenbank(String suchmuster);
    
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
    public Map<IjsonObject, Integer>  durchsucheDatenbankVeranstaltung(String suchmuster);
    
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
    public Map<IjsonObject, Integer>  durchsucheDatenbankBenutzer(String suchmuster);
    
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
    public Map<IjsonObject,Integer> durchsucheDatenbankStudiengang(String suchmuster);

    /**
     * Liest die aktuellsten Benachrichtigungen für einen Benutzer aus der Datenbank
     * @param benutzer referenziert eindeutig einen Benuter
     * @param limit gibt an wie viele Benachrichtiungen für den Benutzer maximal ausgelesen werden 
     * @return Liste der aktuellsten Benachrichtigungen. Gibt es für den Benutzer keine Benachrichtigungen
     * wird eine leere Liste zurückgegeben. Bei einem Fehler kommt null zurück.
     */
    public List<Benachrichtigung> leseBenachrichtigungen(int benutzer, int limit);

    /**
     * Holt eine Benachrichtigung für eine Einladung als Moderator aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrEinlModerator Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zurück.
     */
    public BenachrEinlModerator leseBenachrEinlModerator(int id);

    /**
     * Holt eine Benachrichtigung für eine Karteikartenänderung aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrKarteikAenderung Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zurück.
     */
    public BenachrKarteikAenderung leseBenachrKarteikAenderung(int id);

    /**
     * Holt eine Benachrichtigung für einen neuen Kommentar aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrNeuerKommentar Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zurück.
     */
    public BenachrNeuerKommentar leseBenachrNeuerKommentar(int id);

    /**
     * Holt eine Benachrichtigung für ein geändertes Profil aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrProfilGeaendert Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zurück.
     */
    public BenachrProfilGeaendert leseBenachrProfilGeaendert(int id);

    /**
     * Holt eine Benachrichtigung für eine Veranstaltungsänderung aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrVeranstAenderung Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zurück.
     */
    public BenachrVeranstAenderung leseBenachrVeranstAenderung(int id);
    
    /**
     * Schreibt die Benachrichtigung in die Datenbank
     * @param benachrichtigung Objekt. Die Methode setzt das Attribut gelesen
     * standardmäßig auf false.
     * @return Liefert bei Erfolg true zurück und bei einem Fehler false.
     */
    public void schreibeBenachrichtigung(Benachrichtigung benachrichtigung, Connection conMysql) throws SQLException;

    /**
     * Setzt das Attribut gelesen der Benachrichtigung auf true.
     * @param benID referenziert eindeutig eine Benachrichtigung
     * @return Liefert bei Erfolg true zurück und bei einem Fehler false.
     */
    public boolean markiereBenAlsGelesen(int benID, int benutzerID);
    
    /**
     * Markiert die Einladung als akzeptiert und trägt den Benutzer als Moderator
     * für die Veranstaltung ein.
     * @param benachrichtigung referenziert eindeutig eine Benachrichtigung
     * @param benutzer, zu einer Benachrichtigung können mehrere Benutzer gehören,
     * weshalb noch der Benutzer übergeben werden muss
     * @return Liefert bei Erfolg true zurück (auch wenn die benachrichtigung nicht existiert)
     * und bei einem Fehler false.
     */
    public boolean einladungModeratorAnnehmen(int benachrichtigung, int benutzer);
    
    /**
     * Markiert die Einladung als abgelehnt
     * @param benachrichtigung referenziert eindeutig eine Benachrichtigung
     * @param benutzer, zu einer Benachrichtigung können mehrere Benutzer gehören,
     * weshalb noch der Benutzer übergeben werden muss
     * @return Liefert bei Erfolg true zurück (auch wenn die benachrichtigung nicht existiert)
     * und bei einem Fehler false.
     */
    public boolean einladungModeratorAblehnen(int benachrichtigung, int benutzer);
    
    /**
     * Holt Daten der Karteikarte anhand der ID aus der Datenbank
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @return Es wird ein Karteikarten Objekt zurückgegeben. Tritt ein Fehler
     * auf oder gibt es keine Karteikarte mit dieser ID, kommt null zurück
     */
    public Karteikarte leseKarteikarte(int karteikID);

    /** Gibt alle Kinder einer Karteikarte zurück.
     * @param vaterKarteikID referenziert eindeutig eine Karteikarte
     * @return Gibt eine Map zurück, in der der Schlüssel angibt das wievielte
     * Kind die Karteikarte ist. (Es wird bei 0 mit Zählen begonnen)
     *  Von der Karteikarte werden nur ID und Name zurückgegeben. 
     * Tritt ein Fehler auf wird null zurückgegeben.
     * Hat die angegebene Karteikarte keine Kinder, so wird eine leere Map
     * zurückgeliefert.
     */
    public Map<Integer,Tupel<Integer,String>> leseKindKarteikarten(int vaterKarteikID);
    
    
    public Map<Integer,Karteikarte> leseNachfolger(int karteikarte, int anzNachfolger);
    
    public Map<Integer,Karteikarte> leseVorgaenger(int karteikarte, int anzVorgänger);

    /**
     * Fügt neue Karteikarte in die Datenbank ein. Bei einem Fehler wird eine
     * SQLException geworfen.  
     * @param karteik
     * @return ID der gerade eingefügten Karteikarte
     */
    public int schreibeKarteikarte(Karteikarte karteik, int vaterKK, int ueberliegendeBruderKK) throws SQLException;
    
    public void connectKk(int vonKK, int zuKK, Karteikarte.BeziehungsTyp typ, Connection conNeo4j) throws SQLException;
    
    public void disconnectKk(int vonKK, int zuKK, Connection conNeo4j) throws SQLException;

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
     * angepasst. 
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @param bewert Bewertung des Benutzers, die entweder 1 oder -1 ist
     * @param benutzer referenziert eindeutig einen Benutzer
     * @return Bei einem Fehler wird false zurückgeliefert ansonsten
     * true.
     */
    public boolean bewerteKarteikarte(int karteikID, int bewert, int benutzer);

    /**
     * Gibt true zuruck, falls der Benutzer diese Karteikarte bereits bewertet
     * hat. Ansonsten wird false zuruckgegeben.
     * @param karteikID 
     * @param benutzer 
     * @return
     */
    public boolean hatKarteikarteBewertet(int karteikID, int benutzer);

    /**
     * Gibt alle Kommentare zu einer Karteikarte zuruck. Bei einem Fehler
     * wird null zuruckgegeben
     * @param karteikID 
     * @param vaterKID 
     * @return
     */
    public ArrayList<Kommentar> leseThemenKommentare(int karteikID, int aktBenutzerID);
    public ArrayList<Kommentar> leseAntwortKommentare(int vaterKID, int aktBenutzerID);

    /**
     * Fugt neuen Kommentar in die Datenbank ein. Bei Erfolg wird
     * true zuruckgegeben. Bei einem Fehler in der Datenbank wird false
     * zuruckgeliefert.
     * @param kommentar 
     * @return
     */
    public boolean schreibeKommentar(Kommentar kommentar);

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
    public boolean bewerteKommentar(int kommentarID, int bewert, int benutzerId);

    /**
     * Gibt true zuruck, falls der Benutzer diesen Kommentar bereits bewertet
     * hat. Ansonsten wird false zuruckgegeben.
     * @param kommentarID 
     * @param benutzer 
     * @return
     */
    public boolean hatKommentarBewertet(int kommentarID, int benutzerId);


    /**
     * Schreibt Benutzer in die Veranstaltung ein. Ist der Benutzer bereits in die Veranstaltung eingetragen,
     * wird eine DbUniqueConstraintException geworfen. Ist das Passwort für die angegebene Veranstaltung falsch,
     * wird eine DbFalsePasswortException geworfen. Tritt ein anderer Fehler auf wird eine SQLException
     * geworfen. Gibt es die Veranstaltung oder den Benutzer nicht, passiert in der Datenbank gar nichts.
     * Es wird in dem Fall aber auch keine Exception geworfen.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung 
     * @param benutzer referenziert eindeutig einen Benutzer 
     * @param kennwort für den Zugang zur Veranstaltung
     * @throws SQLException, DbUniqueConstraintException, DbFalsePasswortException
     */
    public void zuVeranstaltungEinschreiben(int veranstaltung, int benutzer, String kennwort, Connection conMysql, boolean isAdmin) throws SQLException, 
    DbUniqueConstraintException, DbFalsePasswortException;

    /**
     * Meldet Benutzer von der Veranstaltung ab.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @param benutzer referenziert eindeutig einen Benutzer
     * @return false falls ein Fehler auftritt. Ansonsten true (Auch wenn der Benutzer gar nicht
     * zu der Veranstaltung angemeldet war)
     */
    public boolean vonVeranstaltungAbmelden(int veranstaltung, int benutzer);

    /**
     * Liest alle Notizen eines Benutzers zu einer Karteikarte
     * @param benutzer referenziert eindeutig einen Benutzer 
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @return Liste von Notiz-Objekten. Bei einem Fehler wird null zurückgegeben.
     * Gibt es keine Notizen von dem Benutzer zu dieser Karteikarte wird
     * eine leere Liste zurückgegeben
     */
    public Notiz leseNotiz(int benutzer, int karteikID);

    /**
     * Fügt die angegebene Notiz in die Datenbank ein
     * @param notiz
     * @return Liefert true, falls kein Fehler aufgetreten ist, ansonsten false
     */
    public boolean schreibeNotiz(Notiz notiz);

    /**
     * Updatet die angegebene Notiz in der Datenbank
     * @param notiz 
     * @return Liefert true, falls kein Fehler aufgetreten ist, ansonsten false
     */
    public boolean bearbeiteNotiz(Notiz notiz);

    /**
     * Löscht die Notiz aus der Datenbank
     * @param notizID referenziert eindeutig eine Notiz
     * @return Liefert true, falls kein Fehler aufgetreten ist, ansonsten false
     */
    public boolean loescheNotiz(int notizID);

    /**
     * @param eMail 
     * @param status 
     * @return
     */

    public Kommentar leseKommentar(int kommId);
    



    
}
