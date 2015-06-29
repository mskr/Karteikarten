package com.sopra.team1723.ctrl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.sql.Timestamp;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.sopra.team1723.data.*;
import com.sopra.team1723.data.Karteikarte.BeziehungsTyp;
import com.sopra.team1723.exceptions.*;

/**
 * @author Matthias Englert
 * Implementiert die Methoden des @ref IDatenbankmanager. Bietet eine
 * Schnittstelle zur Datenbank.
 */



// Die Methoden, die das Wort "Wrapper" beinhalten bauen selbst keine Connection zur Datenbank auf
// sondern fordern die Connection als Parameter. Damit ist es für eine Datenbankmanager-Methode möglich
// eine andere Datenbankmanager-Methode aufzurufen und bei beiden Methoden dieselbe Connection zu
// verwenden. Dies kann notwendig sein um das Konzept einer Transaktion zu gewährleisten.
// Dies ist wichtig falls ein Fehler auftritt und Änderungen in der Datenbank nur teilweise erledigt wurden.
// Wenn die gleiche für alle Änderungen die gleiche Connection verwendet wurde, dann macht ein
// "rollback" auf dieser Connection alle Änderungen rückgängig und die Datenbank bleibt in einem konsistenten
// Zustand.


public class Datenbankmanager implements IDatenbankmanager
{
    final int                                           UNIQUE_CONSTRAINT_ERROR = 1062;
    private static BasicDataSource                      connectionPoolSQL = new BasicDataSource();
    private static BasicDataSource                      connectionPoolNeo4j = new BasicDataSource();

    /**
     * Implementiert die Methoden des private ArrayList<Connection> connections
     * = null; private ArrayList<ReentrantLock> locks = null; @ref
     * IDatenbankmanager. Bietet eine Schnittstelle zur Datenbank.
     * 
     * @throws Exception
     */
    public Datenbankmanager() throws Exception
    {
    }

    static
    {
        connectionPoolSQL.setUrl("jdbc:mysql://localhost:3306/sopra");
        connectionPoolSQL.setDriverClassName("com.mysql.jdbc.Driver");
        connectionPoolSQL.setUsername("root");
        connectionPoolSQL.setPassword("");
        
        connectionPoolNeo4j.setUrl("jdbc:neo4j://localhost:7474/karteikarten");
        connectionPoolNeo4j.setDriverClassName("org.neo4j.jdbc.Driver");
        connectionPoolNeo4j.setUsername("neo4j");
        connectionPoolNeo4j.setPassword("hallo123");
        
    }

    public static synchronized Connection getConnectionNeo4j()
    {
        Connection connection = null;
        try {
            connection = connectionPoolNeo4j.getConnection();
        } catch (SQLException ex) {
            System.out.println("Error while getting a connection from the pool! \nNeo4j state:" + ex.getSQLState() + "\nMESSAGE" + ex.getMessage());
        }
        return connection;
    }
    
    public static synchronized Connection getConnectionSQL()
    {
        Connection connection = null;
        try {
            connection = connectionPoolSQL.getConnection();
        } catch (SQLException ex) {
            System.out.println("Error while getting a connection from the pool! \nSQL state:" + ex.getSQLState() + "\nMESSAGE" + ex.getMessage());
        }
        return connection;
    }

    
    // schließt eine Datenbankverbindung falls die Connection nicht null ist
    public void closeQuietly(Connection connection)
    {
        if (null == connection)
            return;
        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }
    
    // schließt ein PreparedStatement falls dieses nicht null ist
    protected void closeQuietly(PreparedStatement statement)
    {
        if (null == statement)
            return;
        try
        {
            statement.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // schließt ein ResultSet falls dieses nicht null ist
    protected void closeQuietly(ResultSet resultSet)
    {
        if (null == resultSet)
            return;
        try
        {
            resultSet.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 
     */

    @Override
    public Benutzer leseBenutzer(String eMail)
    {
        // Verbindungsaufbau zur Datenbank
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Benutzer benutzer = null;
        try
        {
            // lese Benutzer aus der Datenbank
            ps = conMysql
                    .prepareStatement("SELECT ID,Vorname,Nachname,Profilbild,Matrikelnummer,Studiengang,CryptedPW,Nutzerstatus,"
                            + "NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, Profilbild, Theme FROM benutzer WHERE eMail = ?");
            ps.setString(1, eMail);
            rs = ps.executeQuery();
            if (rs.next())
            {
                // erzeuge neues Benutzerobjekt mit den Daten aus der Datenbank
                benutzer = new Benutzer(rs.getInt("ID"), eMail, rs.getString("Vorname"), rs.getString("Nachname"),
                        rs.getInt("Matrikelnummer"), rs.getString("Studiengang"), rs.getString("CryptedPW"),
                        Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), rs.getBoolean("NotifyVeranstAenderung"),
                        rs.getBoolean("NotifyKarteikartenAenderung"), NotifyKommentare.valueOf(rs
                                .getString("NotifyKommentare")), rs.getString("Profilbild"), rs.getString("Theme"));

                benutzer.setProfilBildPfad(ServletController.dirProfilBilder + rs.getString("Profilbild"));
            }
        }
        catch (SQLException e)
        {
            benutzer = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }

        return benutzer;
    }

    @Override
    public Benutzer leseBenutzer(int id)
    {
        // Diese Methode liest einen Benuter au der Datenbank und baut dabei
        // im Gegensatz zu der Methode leseBenutzerWrapper eine eigene Connection auf
        Connection conMysql = getConnectionSQL();
        try
        {
            return leseBenutzerWrapper(id, conMysql);
        }
        finally
        {
            closeQuietly(conMysql);
        }
    }
    
    // liest Benutzer aus der Datenbank. Braucht zusätzlich eine Connection als Parameter
    private Benutzer leseBenutzerWrapper(int id, Connection conMysql){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Benutzer benutzer = null;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT eMail,Vorname,Nachname,Profilbild,Matrikelnummer,Studiengang,CryptedPW,Nutzerstatus,"
                            + "NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, Profilbild, Theme FROM benutzer WHERE ID = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                // erzeuge neues Benutzerobjekt mit den Daten aus der Datenbank
                benutzer = new Benutzer(id, rs.getString("eMail"), rs.getString("Vorname"), rs.getString("Nachname"),
                        rs.getInt("Matrikelnummer"), rs.getString("Studiengang"), rs.getString("CryptedPW"),
                        Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), rs.getBoolean("NotifyVeranstAenderung"),
                        rs.getBoolean("NotifyKarteikartenAenderung"), NotifyKommentare.valueOf(rs
                                .getString("NotifyKommentare")), rs.getString("Profilbild"), rs.getString("Theme"));

                benutzer.setProfilBildPfad(ServletController.dirProfilBilder + rs.getString("Profilbild"));
            }
        }
        catch (SQLException e)
        {
            benutzer = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benutzer;
    }

    @Override
    public void schreibeBenutzer(Benutzer benutzer) throws DbUniqueConstraintException, SQLException
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        if (ServletController.DEBUGMODE)
            System.out.println("------");
        // Passwort verschlüsseln
        String md5pwd = benutzer.getKennwort();
        String CryptedPW = BCrypt.hashpw(md5pwd, BCrypt.gensalt());

        if (ServletController.DEBUGMODE)
        {
            System.out.println("------");
            System.out.println("md5: " + md5pwd + "crypted: " + CryptedPW);
            System.out.println("------");
        }
        try
        {
            // Benutzer in die Datenbank einfügen
            ps = conMysql.prepareStatement("INSERT INTO benutzer (Vorname,Nachname,Matrikelnummer,eMail,Studiengang,"
                    + "CryptedPW, Nutzerstatus, NotifyKommentare, NotifyVeranstAenderung, "
                    + "NotifyKarteikartenAenderung) VALUES(?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, benutzer.getVorname());
            ps.setString(2, benutzer.getNachname());
            ps.setInt(3, benutzer.getMatrikelnummer());
            ps.setString(4, benutzer.geteMail());
            ps.setString(5, benutzer.getStudiengang());
            ps.setString(6, CryptedPW);
            ps.setString(7, benutzer.getNutzerstatus().name());
            ps.setString(8, benutzer.getNotifyKommentare().name());
            ps.setBoolean(9, benutzer.isNotifyVeranstAenderung());
            ps.setBoolean(10, benutzer.isNotifyKarteikartenAenderung());

            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            // Matrikelnummer und Email müssen eindeutig sein
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode()){
                if(e.getMessage().toLowerCase().contains("matrikelnummer"))
                    throw new DbUniqueConstraintException("matrikelnummer");
                else if(e.getMessage().toLowerCase().contains("email"))
                    throw new DbUniqueConstraintException("email");
            }
            else
                throw e;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }
    }

    @Override
    public void bearbeiteBenutzer(Benutzer benutzer) throws SQLException, DbUniqueConstraintException
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            // ändere Benutzerdaten in der Datenbank
            ps = conMysql.prepareStatement("UPDATE benutzer SET eMail=?, Vorname=?,Nachname=?,"
                    + "NotifyKommentare=?, NotifyVeranstAenderung=?," 
                    + "NotifyKarteikartenAenderung=?, Theme=? WHERE ID = ?");
            ps.setString(1, benutzer.geteMail());
            ps.setString(2, benutzer.getVorname());
            ps.setString(3, benutzer.getNachname());
            ps.setString(4, benutzer.getNotifyKommentare().name());
            ps.setBoolean(5, benutzer.isNotifyVeranstAenderung());
            ps.setBoolean(6, benutzer.isNotifyKarteikartenAenderung());
            ps.setString(7, benutzer.getTheme());
            ps.setInt(8, benutzer.getId());
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
    }

    @Override
    public void bearbeiteBenutzerAdmin(Benutzer benutzer, Benutzer admin) throws SQLException,
            DbUniqueConstraintException
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            // Das Bearbeiten des Benutzers und das Schreiben einer Benachrichtiung
            // soll eine Transaktion sein
            conMysql.setAutoCommit(false);
            // ändere Benutzerdaten in der Datenbank
            ps = conMysql
                    .prepareStatement("UPDATE benutzer SET eMail=?, Vorname=?,Nachname=?,Matrikelnummer=?,Studiengang=?,"
                            + "Nutzerstatus=?, NotifyKommentare=?, NotifyVeranstAenderung=?,"
                            + "NotifyKarteikartenAenderung=?  WHERE ID = ?");
            ps.setString(1, benutzer.geteMail());
            ps.setString(2, benutzer.getVorname());
            ps.setString(3, benutzer.getNachname());
            ps.setInt(4, benutzer.getMatrikelnummer());
            ps.setString(5, benutzer.getStudiengang());
            ps.setString(6, benutzer.getNutzerstatus().name());
            ps.setString(7, benutzer.getNotifyKommentare().name());
            ps.setBoolean(8, benutzer.isNotifyVeranstAenderung());
            ps.setBoolean(9, benutzer.isNotifyKarteikartenAenderung());
            ps.setInt(10, benutzer.getId());
            ps.executeUpdate();

            closeQuietly(ps);

            if (benutzer.getId() != admin.getId())
                schreibeBenachrichtigungWrapper(new BenachrProfilGeaendert(new GregorianCalendar(), benutzer.getId(),
                        admin), conMysql);

            // Führe alle Änderungen aus
            conMysql.commit();
        }
        catch (SQLException e)
        {
            conMysql.rollback();
            e.printStackTrace();
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        }
        finally
        {
            conMysql.setAutoCommit(true);
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
    }

    @Override
    public boolean loescheBenutzer(int benutzerId)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try
        {
            // lösche Benutzer
            ps = conMysql.prepareStatement("DELETE FROM benutzer WHERE ID=?");
            ps.setInt(1, benutzerId);
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            erfolgreich = false;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }
        return erfolgreich;
    }
    @Override
    public boolean loescheProfilBild(int benutzerId)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try
        {
            // Füge Pfad des Profilbilds in die Datenbank ein
            ps = conMysql.prepareStatement("UPDATE benutzer SET Profilbild=? WHERE ID=?");
            ps.setString(1, "default.png");
            ps.setInt(2, benutzerId);
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            erfolgreich = false;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }
        return erfolgreich;
    }
    @Override
    public void pruefeLogin(String eMail, String passwort) throws SQLException, DbFalseLoginDataException
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            if (ServletController.DEBUGMODE)
                System.out.println("DB prueft: email=" + eMail + ", passwort=" + passwort);
            // Suche Benutzer mit der angegebenen E-Mail
            ps = conMysql.prepareStatement("SELECT * FROM benutzer WHERE eMail = ?");
            ps.setString(1, eMail);
            rs = ps.executeQuery();
            if (!rs.next())
            {
                throw new DbFalseLoginDataException();
            }
            else
            {
                String Crypted = rs.getString("CryptedPW");
                System.out.println("PASSWORT=" + passwort + "\nCRYPTED=" + Crypted);
                // Prüfe ob Passwörter übereinstimmen
                if (BCrypt.checkpw(passwort, Crypted) == false)
                {
                    throw new DbFalseLoginDataException();
                }
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();

            throw e;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }

    }

    @Override
    public List<String> leseStudiengaenge()
    {
        Connection conMysql = getConnectionSQL();
        ArrayList<String> studiengaenge = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            // Lese alle Studiengänge
            ps = conMysql.prepareStatement("SELECT Name FROM Studiengang");
            rs = ps.executeQuery();
            while (rs.next())
            {
                studiengaenge.add(rs.getString("Name"));
            }
        }
        catch (SQLException e)
        {
            studiengaenge = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return studiengaenge;
    }

    @Override
    public Map<Integer, String> leseSemester()
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer, String> semester = null;
        try
        {
            semester = new HashMap<Integer, String>();
            // Lese alle Semester
            ps = conMysql.prepareStatement("SELECT ID, Name FROM Semester");
            rs = ps.executeQuery();
            while (rs.next())
            {
                semester.put(rs.getInt("ID"), rs.getString("Name"));
            }
        }
        catch (SQLException e)
        {
            semester = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return semester;
    }

    @Override
    public boolean passwortAendern(String eMail, String neuesPasswort)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try
        {
            // ändere Passwort
            ps = conMysql.prepareStatement("UPDATE benutzer SET CryptedPW=? WHERE eMail=?");
            ps.setString(1, neuesPasswort);
            ps.setString(2, eMail);
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            erfolgreich = false;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return erfolgreich;
    }

    @Override
    public boolean aendereProfilBild(int benutzerId, String dateiName)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try
        {
            // ändere Profilbild
            ps = conMysql.prepareStatement("UPDATE benutzer SET Profilbild=? WHERE ID=?");
            ps.setString(1, dateiName);
            ps.setInt(2, benutzerId);
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            erfolgreich = false;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return erfolgreich;
    }

    @Override
    public Veranstaltung leseVeranstaltung(int id)
    {
        Connection conMysql = getConnectionSQL();
        try
        {
            return leseVeranstaltungWrapper(id, conMysql);
        }
        finally
        {
            closeQuietly(conMysql);
        }
    }
    
    private Veranstaltung leseVeranstaltungWrapper(int id, Connection conMysql){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Veranstaltung veranstaltung = null;
        try
        {
            // Lese Veranstaltung zu einer bestimmten ID. Es wird außerdem noch die Anzahl der Teilnehmer an der Veranstaltung ausgelesen.
            // Die Anzahl ist kein Attribut der Veranstaltung-Tabelle. Die Anzahl Teilnehmer muss über die Zuordnungstabelle
            // zwischen Benutzer und Veranstaltung ermittelt werden.
            ps = conMysql.prepareStatement("SELECT v.ID, Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt,"
                    + "ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, count(bvz.ID) AS AnzTeilnehmer,"
                    + " ErsteKarteikarte "
                    + "FROM veranstaltung AS v LEFT OUTER JOIN benutzer_veranstaltung_zuordnung AS bvz "
                    + "ON v.ID = bvz.Veranstaltung "
                    + "GROUP BY v.ID, Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt,"
                    + " ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, ErsteKarteikarte "
                    + "HAVING v.ID=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                // In der Tabelle Veranstaltung steht nur die Benutzer-ID des Erstellers der Veranstaltung zu Verfügung.
                // Deshalb wird hier das komplette Benutzer-Objekt geholt
                Benutzer ersteller = leseBenutzerWrapper(rs.getInt("Ersteller"),conMysql);
                // Prüfe ob die Mehtode leseBenutzerWrapper erfolgreich war
                if(ersteller == null)
                    return null;
                // Erzeuge neues Veranstaltung-Objekt
                veranstaltung = new Veranstaltung(id, rs.getString("Titel"), rs.getString("Beschreibung"),
                        rs.getString("Semester"), rs.getString("Kennwort"), rs.getBoolean("BewertungenErlaubt"),
                        rs.getBoolean("ModeratorKarteikartenBearbeiten"), ersteller,
                        rs.getBoolean("KommentareErlaubt"), rs.getInt("AnzTeilnehmer"), rs.getInt("ErsteKarteikarte"));
            }
        }
        catch (SQLException e)
        {
            veranstaltung = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return veranstaltung;
    }

    @Override
    public List<Veranstaltung> leseVeranstaltungen(String semester, String studiengang)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Veranstaltung> veranstaltungen = null;
        try
        {
            veranstaltungen = new ArrayList<Veranstaltung>();
            // Lese Veranstaltungen in einem Semester und zu einem Studiengang. Es wird außerdem noch die Anzahl der Teilnehmer an der Veranstaltung ausgelesen.
            // Die Anzahl ist kein Attribut der Veranstaltung-Tabelle. Die Anzahl Teilnehmer muss über die Zuordnungstabelle
            // zwischen Benutzer und Veranstaltung ermittelt werden. 
            ps = conMysql
                    .prepareStatement("SELECT v.ID, Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt, "
                            + "ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, count(bvz.ID) AS AnzTeilnehmer, "
                            + "ErsteKarteikarte FROM benutzer_veranstaltung_zuordnung AS bvz RIGHT OUTER JOIN veranstaltung AS v ON "
                            + "v.ID = bvz.Veranstaltung JOIN veranstaltung_studiengang_zuordnung AS vsz ON v.ID = vsz.Veranstaltung"
                            + " WHERE vsz.Studiengang =? AND Semester =? GROUP BY v.ID");
            ps.setString(1, studiengang);
            ps.setString(2, semester);
            rs = ps.executeQuery();
            while (rs.next())
            {
                // In der Tabelle Veranstaltung steht nur die Benutzer-ID des Erstellers der Veranstaltung zu Verfügung.
                // Deshalb wird hier das komplette Benutzer-Objekt geholt
                Benutzer ersteller = leseBenutzerWrapper(rs.getInt("Ersteller"), conMysql);
                // Prüfe ob die Mehtode leseBenutzerWrapper erfolgreich war
                if(ersteller == null)
                    return null;
                // Erzeuge neues Veranstaltung-Objekt und füge es der Liste hinzu.
                veranstaltungen.add(new Veranstaltung(rs.getInt("v.ID"), rs.getString("Titel"), rs
                        .getString("Beschreibung"), rs.getString("Semester"), rs.getString("Kennwort"), rs
                        .getBoolean("BewertungenErlaubt"), rs.getBoolean("ModeratorKarteikartenBearbeiten"),
                        ersteller, rs.getBoolean("KommentareErlaubt"), rs
                                .getInt("AnzTeilnehmer"), rs.getInt("ErsteKarteikarte")));
            }
        }
        catch (SQLException e)
        {
            veranstaltungen = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return veranstaltungen;

    }

    @Override
    public List<Veranstaltung> leseVeranstaltungen(int benutzer)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Veranstaltung> veranstaltungen = null;
        try
        {
            veranstaltungen = new ArrayList<Veranstaltung>();
            // Lese Veranstaltungen zu einem Benutzer. Es wird außerdem noch die Anzahl der Teilnehmer an der Veranstaltung ausgelesen.
            // Die Anzahl ist kein Attribut der Veranstaltung-Tabelle. Die Anzahl Teilnehmer muss über die Zuordnungstabelle
            // zwischen Benutzer und Veranstaltung ermittelt werden. 
            ps = conMysql
                    .prepareStatement("SELECT v.ID, Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt, "
                            + "ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, count(bvz.ID) AS AnzTeilnehmer, "
                            + "ErsteKarteikarte FROM veranstaltung AS v LEFT OUTER JOIN benutzer_veranstaltung_zuordnung AS bvz ON "
                            + "v.ID = bvz.Veranstaltung WHERE ? IN (SELECT Benutzer FROM benutzer_veranstaltung_zuordnung WHERE"
                            + " Veranstaltung = v.ID) GROUP BY v.ID");
            ps.setInt(1, benutzer);
            rs = ps.executeQuery();
            while (rs.next())
            {
                // In der Tabelle Veranstaltung steht nur die Benutzer-ID des Erstellers der Veranstaltung zu Verfügung.
                // Deshalb wird hier das komplette Benutzer-Objekt geholt
                Benutzer ersteller = leseBenutzerWrapper(rs.getInt("Ersteller"), conMysql);
                // Prüfe ob die Mehtode leseBenutzerWrapper erfolgreich war
                if(ersteller == null)
                    return null;
                // Erzeuge neues Veranstaltung-Objekt und füge es der Liste hinzu.
                veranstaltungen.add(new Veranstaltung(rs.getInt("ID"), rs.getString("Titel"), rs
                        .getString("Beschreibung"), rs.getString("Semester"), rs.getString("Kennwort"), rs
                        .getBoolean("BewertungenErlaubt"), rs.getBoolean("ModeratorKarteikartenBearbeiten"),
                        ersteller, rs.getBoolean("KommentareErlaubt"), rs
                                .getInt("AnzTeilnehmer"), rs.getInt("ErsteKarteikarte")));
            }
        }
        catch (SQLException e)
        {
            veranstaltungen = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }

        return veranstaltungen;
    }

    @Override
    public List<Benutzer> leseModeratoren(int veranstaltung)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Benutzer> moderatoren = null;
        try
        {
            moderatoren = new ArrayList<Benutzer>();
            // lese Moderatoren aus der Datenbank. Welche das für die gegebene Veranstaltung sind steht in der Moderator-Tabelle.
            // Für die kompletten Daten eines Moderators wird zusätzlich noch die Benutzer-Tabelle benötigt
            ps = conMysql
                    .prepareStatement("SELECT b.ID, eMail, Vorname, Nachname, Profilbild, Matrikelnummer, Studiengang, "
                            + "CryptedPW, Nutzerstatus, NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, "
                            + "Profilbild, Theme FROM benutzer AS b JOIN moderator AS m ON b.ID = m.Benutzer AND m.Veranstaltung =?");
            ps.setInt(1, veranstaltung);
            rs = ps.executeQuery();
            while (rs.next())
            {
                // erzeuge neues Benutzer-Objekt und füge es der Liste von Moderatoren hinzu
                moderatoren.add(new Benutzer(rs.getInt("b.ID"), rs.getString("eMail"), rs.getString("Vorname"), rs
                        .getString("Nachname"), rs.getInt("Matrikelnummer"), rs.getString("Studiengang"), rs
                        .getString("CryptedPW"), Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), rs
                        .getBoolean("NotifyVeranstAenderung"), rs.getBoolean("NotifyKarteikartenAenderung"),
                        NotifyKommentare.valueOf(rs.getString("NotifyKommentare")), rs.getString("Profilbild"), rs.getString("Theme")));
            }
        }
        catch (SQLException e)
        {
            moderatoren = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }

        return moderatoren;
    }

    @Override
    public List<String> leseStudiengaenge(int veranstaltung)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> studiengaenge = null;
        try
        {
            studiengaenge = new ArrayList<String>();
            // Lese Studiengänge zu einer Veranstaltung. Dafür wird die Zuordnungstabelle zwischen
            // Veranstaltung und Studiengang verwendet
            ps = conMysql.prepareStatement("SELECT Studiengang FROM veranstaltung_studiengang_zuordnung "
                    + " WHERE Veranstaltung =?");
            ps.setInt(1, veranstaltung);
            rs = ps.executeQuery();
            while (rs.next())
            {
                // Füge Studiengang der Liste hinzu
                studiengaenge.add(rs.getString("Studiengang"));
            }
        }
        catch (SQLException e)
        {
            studiengaenge = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }

        return studiengaenge;
    }

    private Boolean istModeratorWrapper(int benutzer, int veranstaltung, Connection conMysql){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Boolean istModerator = false;
        try
        {
            // Lese alle Moderatoren aus, die eine bestimmte Benutzer-ID haben und in einer bestimmten Veranstaltung
            // Moderator sind.
            ps = conMysql.prepareStatement("SELECT ID FROM moderator WHERE Benutzer =? AND Veranstaltung =?");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            rs = ps.executeQuery();
            // Wird kein Moderator mit den geforderten Parametern gefunden, dann ist der Benutzer kein Moderator
            // für die Veranstaltung
            if (rs.next())
                istModerator = true;

        }
        catch (SQLException e)
        {
            istModerator = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return istModerator;
    }
    
    @Override
    public Boolean istModerator(int benutzer, int veranstaltung)
    {
        Connection conMysql = getConnectionSQL();

        try
        {
            // Verwende istModeratorWrapper mit eigener Connection
            return istModeratorWrapper(benutzer, veranstaltung, conMysql);
        }
        finally
        {
            closeQuietly(conMysql);
        }
    }

    // Diese Methode bekommt als Parameter eine Map. Eine Map besteht aus key-value Paaren und hat keine Sortierung. Mit dieser Methode
    // kann die Map nach den values sortiert werden. Das Ergebnis wird in eine LinkedHashMap gepackt. Diese hat eine Sortierung
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map)
    {
        // Mache aus der Map eine Liste von key-value Paaren
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        // Sortiere diese Liste
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            // Kriterium nach was sortiert werden soll. In desem Fall nach den values
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Schreibe die Einträge der Liste "list" in eine LinkedHashMap
        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public Map<IjsonObject, Integer> durchsucheDatenbank(String suchmuster)
    {
        Map<IjsonObject, Integer> alleErgebnisse = new HashMap<IjsonObject, Integer>();
        // suche nach Veranstaltungen und schreibe die Ergebnisse in die Map
        alleErgebnisse.putAll(durchsucheDatenbankVeranstaltung(suchmuster));
        // suche nach Benutzern und schreibe die Ergebnisse in die Map
        alleErgebnisse.putAll(durchsucheDatenbankBenutzer(suchmuster));

        // Sortiere die Map nach den values. Die values sind in diesem Fall Integer, die beschreiben
        // wie gut das Suchergebnis zum Suchmuster passt
        LinkedHashMap<IjsonObject, Integer> sortierteErgebnisse = sortByValue(alleErgebnisse);
        // Die LinkedHashMap sortierteErgebnisse kann bis zu 10 Ergebnisse enthalten. Wir wollen aber nur 5
        LinkedHashMap<IjsonObject, Integer> teilErgebnisse = new LinkedHashMap<IjsonObject, Integer>();
        Iterator<Entry<IjsonObject, Integer>> it = sortierteErgebnisse.entrySet().iterator();
        int i = 0;
        Entry<IjsonObject, Integer> entry;
        // Deshalb wählen wir hier die 5 ersten Einträge aus
        while (i < 5 && it.hasNext())
        {
            entry = it.next();
            teilErgebnisse.put(entry.getKey(), entry.getValue());
            ++i;
        }

        return teilErgebnisse;
    }

    @Override
    public Map<IjsonObject, Integer> durchsucheDatenbankVeranstaltung(String suchmuster)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<IjsonObject, Integer> ergebnisse = null;
        try
        {
            ergebnisse = new HashMap<IjsonObject, Integer>();
            // Vergleiche die Titel einer Veranstaltung mittels der levenshtein-Funktion mit dem Suchmuster
            // und gebe die 5 besten Treffer zurück
            ps = conMysql
                    .prepareStatement("SELECT ID, levenshtein(?,Titel) AS lev FROM Veranstaltung ORDER BY lev LIMIT 5");
            ps.setString(1, suchmuster);
            rs = ps.executeQuery();
            while (rs.next())
            {
                // Lese die Veranstaltung zu der gegebenen ID
                Veranstaltung veranst = leseVeranstaltungWrapper(rs.getInt("ID"),conMysql);
                // Prüfe ob ein Fehler bei leseVeranstaltungWrapper aufgetreten ist
                if(veranst == null)
                    return null;
                // Veranstaltung implementiert das Interface IjsonObject.
                // Füge veranst und die Bewertung der Ergebnisliste hinzu
                ergebnisse.put(veranst, rs.getInt("lev"));
            }

        }
        catch (SQLException e)
        {
            ergebnisse = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return ergebnisse;
    }

    @Override
    public Map<IjsonObject, Integer> durchsucheDatenbankBenutzer(String suchmuster)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<IjsonObject, Integer> ergebnisse = null;
        try
        {
            ergebnisse = new HashMap<IjsonObject, Integer>();
            // Vergleiche die Vorname und Nachname eines Benutzers mittels der levenshtein-Funktion mit dem Suchmuster
            // und gebe die 5 besten Treffer zurück
            ps = conMysql.prepareStatement("SELECT * , levenshtein(?,CONCAT(Vorname,' ',Nachname)) AS lev"
                    + " FROM Benutzer ORDER BY lev LIMIT 5 ");
            ps.setString(1, suchmuster);
            rs = ps.executeQuery();
            while (rs.next())
            {
                // Lese den Benutzer zu der gegebenen ID
                Benutzer benutzer = leseBenutzerWrapper(rs.getInt("ID"),conMysql);
                // Prüfe ob ein Fehler bei leseBenutzerWrapper aufgetreten ist
                if(benutzer == null)
                    return null;
                // Benutzer implementiert das Interface IjsonObject.
                // Füge benutzer und die Bewertung der Ergebnisliste hinzu
                ergebnisse.put(benutzer, rs.getInt("lev"));
            }

        }
        catch (SQLException e)
        {
            ergebnisse = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return ergebnisse;
    }

    @Override
    public Map<IjsonObject, Integer> durchsucheDatenbankStudiengang(String suchmuster)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<IjsonObject, Integer> ergebnisse = null;
        try
        {
            ergebnisse = new HashMap<IjsonObject, Integer>();
            // Vergleiche die Studiengänge mittels der levenshtein-Funktion mit dem Suchmuster
            // und gebe die 5 besten Treffer zurück
            ps = conMysql
                    .prepareStatement("SELECT Name, levenshtein(?,Name) AS lev FROM Studiengang ORDER BY lev LIMIT 5");
            ps.setString(1, suchmuster);
            // ps.setString(2, suchmuster);
            rs = ps.executeQuery();
            while (rs.next())
            {
                if (ServletController.DEBUGMODE)
                    System.out.println(rs.getString("Name") + " " + suchmuster + " " + rs.getInt("lev"));
                // Studiengang implementiert das Interface IjsonObject.
                // Füge Studiengang und die Bewertung der Ergebnisliste hinzu
                ergebnisse.put(new Studiengang(rs.getString("Name")), rs.getInt("lev"));
            }

        }
        catch (SQLException e)
        {
            ergebnisse = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return ergebnisse;
    }

    @Override
    public boolean angemeldet(int benutzer, int veranstaltung) throws SQLException
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean angemeldet = false;
        try
        {
            // Suche in der Zuordnungstabelle zwischen Benutzer und Veranstaltung nach Einträgen mit
            // der benutzer-ID und der veranstaltungs-ID aus den Parametern
            ps = conMysql
                    .prepareStatement("SELECT ID FROM benutzer_veranstaltung_zuordnung WHERE Benutzer =? AND Veranstaltung =?");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            rs = ps.executeQuery();
            // Wird ein Ergebnis gefunden, ist der Benutzer zu der Veranstaltung angemeldet.
            // Ansonsten nicht
            if (rs.next())
                angemeldet = true;
            else
                angemeldet = false;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw e;

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return angemeldet;
    }

    @Override
    public int schreibeVeranstaltung(Veranstaltung veranst, String[] studiengaenge, int[] moderatorenIds)
            throws SQLException, DbUniqueConstraintException
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();

        PreparedStatement ps = null;
        ResultSet rs = null;

        int veranstId = -1;
        try
        {
            conMysql.setAutoCommit(false);
            conNeo4j.setAutoCommit(false);

            // Beim Anlegen einer Veranstaltung wird immer gleich die erste Karteikarte miterzeugt
            // Dazu wird in der Neo4j Datenbank zuerst ein leerer Knoten erzeugt. Die ID dieses Knotens
            // wird gespeichert und später in der MySql Datenbank verwendet
            ps = conNeo4j.prepareStatement("CREATE (n) RETURN id(n) AS ID");
            rs = ps.executeQuery();
            int insertedKkId;
            // Wenn ein Knoten in der Neo4j Datenbank erzeugt wird, dann muss dieser auch eine ID haben.
            // Ansonsten ist ein Fehler aufgetreten und es wird eine Exception geworfen
            if (rs.next())
                insertedKkId = rs.getInt("ID");
            else
                throw new SQLException();

            closeQuietly(ps);
            closeQuietly(rs);

            // Schreibe Veranstaltung in die Datenbank
            ps = conMysql
                    .prepareStatement("INSERT INTO veranstaltung (Titel, Beschreibung, Semester, Kennwort, KommentareErlaubt,"
                            + "BewertungenErlaubt, ModeratorKarteikartenBearbeiten, Ersteller, ErsteKarteikarte) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setString(1, veranst.getTitel());
            ps.setString(2, veranst.getBeschreibung());
            ps.setString(3, veranst.getSemester());
            // Eine Veranstaltung kann auch kein Passwort haben. Dann wird das Attribut Kennwort in der Datenbank auf null gesetzt
            if (veranst.getZugangspasswort().equals(""))
                ps.setNull(4, Types.INTEGER);
            else
                ps.setString(4, veranst.getZugangspasswort());
            ps.setBoolean(5, veranst.isKommentareErlaubt());
            ps.setBoolean(6, veranst.isBewertungenErlaubt());
            ps.setBoolean(7, veranst.isModeratorKarteikartenBearbeiten());
            ps.setInt(8, veranst.getErsteller().getId());
            ps.setInt(9, insertedKkId);

            ps.executeUpdate();

            ps.close();
            // Lese die von MySql automatisch erzeugte ID der soeben eingefügten Veranstaltung aus
            ps = conMysql.prepareStatement("SELECT LAST_INSERT_ID();");
            rs = ps.executeQuery();
            if (!rs.next())
                throw new SQLException();
            veranstId = rs.getInt(1);
            veranst.setId(veranstId);
            closeQuietly(ps);
            closeQuietly(rs);

            // Füge eine nicht vom Benutzer erzeugte Karteikarte in die Datenbank ein. Diese ist die
            // erste Karteikarte der Veranstaltung
            ps = conMysql.prepareStatement("INSERT INTO karteikarte (ID,Titel,Inhalt,Typ,Veranstaltung, "
                    + "Satz, Lemma, Beweis, Definition, Wichtig, Grundlagen, "
                    + " Zusatzinformation, Exkurs, Beispiel, Uebung) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, insertedKkId);
            ps.setString(2, veranst.getTitel());
            ps.setString(3, "");
            ps.setString(4, KarteikartenTyp.TEXT.toString());
            ps.setInt(5, veranstId);
            ps.setBoolean(6, false);
            ps.setBoolean(7, false);
            ps.setBoolean(8, false);
            ps.setBoolean(9, false);
            ps.setBoolean(10, false);
            ps.setBoolean(11, false);
            ps.setBoolean(12, false);
            ps.setBoolean(13, false);
            ps.setBoolean(14, false);
            ps.setBoolean(15, false);
            ps.executeUpdate();

            closeQuietly(ps);

            if (studiengaenge != null)
            {
                // Füge in die Zuordnungstabelle zwischen Veranstaltung und Studiengang die Liste "studiengaenge" ein
                ps = conMysql
                        .prepareStatement("INSERT INTO veranstaltung_studiengang_zuordnung (Veranstaltung, Studiengang) VALUES(?,?)");
                for (String stg : studiengaenge)
                {
                    ps.setInt(1, veranstId);
                    ps.setString(2, stg);
                    // Mithilfe von addBatch() muss nicht für jedes einzufügende Tupel ein eigenes SQL-Statement ausgeführt werden
                    ps.addBatch();
                }
                // Sondern nur ein Statement für alle Tupel
                ps.executeBatch();
                closeQuietly(ps);
            }

            if (moderatorenIds != null)
            {
                // Füge in die Tabelle Moderator die Liste "moderatorenIds" ein
                ps = conMysql.prepareStatement("INSERT INTO moderator (Benutzer, Veranstaltung) VALUES(?,?)");
                for (int i = 0; i < moderatorenIds.length; ++i)
                {
                    ps.setInt(1, moderatorenIds[i]);
                    ps.setInt(2, veranstId);
                    ps.addBatch();

                    // Ein Moderator einer Veranstaltung muss auch zu dieser eingeschrieben sein
                    zuVeranstaltungEinschreibenWrapper(veranstId, moderatorenIds[i], veranst.getZugangspasswort(), conMysql,
                            false);
                    // Ein Benutzer wird benachrichtigt, dass er von einem Dozenten zu einem Moderator einer Veranstaltung gemacht wurde
                    schreibeBenachrichtigungWrapper(new BenachrEinlModerator(moderatorenIds[i], veranst), conMysql);
                }
                ps.executeBatch();

            }

            // Der Dozent einer Veranstaltung muss auch immer für die Veranstaltung eingeschrieben sein
            zuVeranstaltungEinschreibenWrapper(veranstId, veranst.getErsteller().getId(), veranst.getZugangspasswort(),
                    conMysql, false); // ignoriere isAdmin, da Passwort ohnehin
                                      // korrekt

            conNeo4j.commit();
            conMysql.commit();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            conMysql.rollback();
            conNeo4j.rollback();
            // Veranstaltungstitel und Semester sind unique
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        }
        catch (DbFalsePasswortException e)
        {
            e.printStackTrace();
            throw new SQLException();
        }
        finally
        {
            conMysql.setAutoCommit(true);
            conNeo4j.setAutoCommit(true);
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }

        return veranstId;
    }

    @Override
    public void bearbeiteVeranstaltung(Veranstaltung veranst, String[] studiengaenge, int[] moderatorenIds, int bearbeiter)
            throws SQLException, DbUniqueConstraintException
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            conMysql.setAutoCommit(false);
            // Bearbeite Veranstaltung
            ps = conMysql.prepareStatement("UPDATE veranstaltung SET Titel=?, Beschreibung=?, Semester=?,"
                    + "Kennwort=?, KommentareErlaubt=?, BewertungenErlaubt=?, ModeratorKarteikartenBearbeiten=?,"
                    + " Ersteller=?, ErsteKarteikarte=? WHERE ID = ?");
            ps.setString(1, veranst.getTitel());
            ps.setString(2, veranst.getBeschreibung());
            ps.setString(3, veranst.getSemester());
            // Eine Veranstaltung kann auch kein Passwort haben. Dann wird das Attribut Kennwort der Tabelle Veranstaltung auf
            // null gesetzt
            if (veranst.getZugangspasswort().equals(""))
                ps.setNull(4, Types.INTEGER);
            else
                ps.setString(4, veranst.getZugangspasswort());
            ps.setBoolean(5, veranst.isKommentareErlaubt());
            ps.setBoolean(6, veranst.isBewertungenErlaubt());
            ps.setBoolean(7, veranst.isModeratorKarteikartenBearbeiten());
            ps.setInt(8, veranst.getErsteller().getId());
            ps.setInt(9, veranst.getErsteKarteikarte());
            ps.setInt(10, veranst.getId());

            ps.executeUpdate();
            closeQuietly(ps);

            // Anstatt zu schauen wie sich die Liste der Studiengänge der Veranstaltung geändert hat,
            // werden zuerst alle Studiengänge in der Zuordnungstabelle zwischen Veranstaltung und Studiengang
            // gelöscht und danach die neue Liste, die als Parameter bereit steht, eingefügt
            ps = conMysql.prepareStatement("DELETE FROM veranstaltung_studiengang_zuordnung WHERE Veranstaltung = ?");
            ps.setInt(1, veranst.getId());
            ps.executeUpdate();
            closeQuietly(ps);

            if (studiengaenge != null)
            {
               // Füge Liste der Studiengänge in die Datenbank ein
                ps = conMysql
                        .prepareStatement("INSERT INTO veranstaltung_studiengang_zuordnung (Veranstaltung, Studiengang) VALUES(?,?)");
                for (String stg : studiengaenge)
                {
                    ps.setInt(1, veranst.getId());
                    ps.setString(2, stg);
                    ps.addBatch();
                }
                ps.executeBatch();
                closeQuietly(ps);
            }

            if (moderatorenIds != null)
            {

                for (int i = 0; i < moderatorenIds.length; ++i)
                {
                    // Prüfe ob der Benutzer bereits Moderator der Veranstaltung ist
                    Boolean istModerator = istModeratorWrapper(moderatorenIds[i], veranst.getId(), conMysql);
                    // Prüfe ob ein Fehler aufgetreten ist
                    if(istModerator == null)
                        throw new SQLException();
                    // Falls nicht bekommt er eine Benachrichtigung
                    if (!istModerator)
                        schreibeBenachrichtigungWrapper(new BenachrEinlModerator(moderatorenIds[i], veranst), conMysql);
                }

                // Anstatt zu schauen wie sich die Liste der Moderatoren der Veranstaltung geändert hat,
                // werden zuerst alle Moderatoren der Veranstaltung gelöscht und danach die neue Liste, die als Parameter bereit steht, eingefügt
                ps = conMysql.prepareStatement("DELETE FROM moderator WHERE Veranstaltung = ?");
                ps.setInt(1, veranst.getId());
                ps.executeUpdate();
                closeQuietly(ps);

                // Schreibe die alten Moderatoren aus
                ps = conMysql
                        .prepareStatement("DELETE FROM benutzer_veranstaltung_zuordnung WHERE Veranstaltung = ? AND Benutzer = ?");
                for (int i = 0; i < moderatorenIds.length; ++i)
                {
                    ps.setInt(1, veranst.getId());
                    ps.setInt(2, moderatorenIds[i]);
                    ps.addBatch();
                }
                ps.executeBatch();
                closeQuietly(ps);

                // Füge die neuen Moderatoren in die Datenbank ein
                ps = conMysql.prepareStatement("INSERT INTO moderator (Benutzer, Veranstaltung) VALUES(?,?)");
                for (int i = 0; i < moderatorenIds.length; ++i)
                {
                    ps.setInt(1, moderatorenIds[i]);
                    ps.setInt(2, veranst.getId());
                    ps.addBatch();

                    // neue Moderatoren einschreiben
                    zuVeranstaltungEinschreibenWrapper(veranst.getId(), moderatorenIds[i], veranst.getZugangspasswort(),
                            conMysql, false);

                }
                ps.executeBatch();

            }

            // Benachrichtigung dass sich etwas an der Veranstaltung geändert hat
            BenachrVeranstAenderung bva = new BenachrVeranstAenderung(veranst, bearbeiter);
            schreibeBenachrichtigungWrapper(bva, conMysql);

            conMysql.commit();

        }
        catch (SQLException e)
        {
            conMysql.rollback();
            e.printStackTrace();
            // Titel und Semester einer Veranstaltung sind unique
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;

        }
        catch (DbFalsePasswortException e)
        {
            e.printStackTrace();
            throw new SQLException();
        }
        catch (DbUniqueConstraintException e)
        {
            conMysql.rollback();
            throw e;
        }
        finally
        {
            conMysql.setAutoCommit(true);
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
    }

    @Override
    public boolean loescheVeranstaltung(int veranstId)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        try
        {
            // Lösche Veranstaltung mit der gegebenen veranstId aus der Datenbank
            ps = conMysql.prepareStatement("DELETE FROM VERANSTALTUNG WHERE ID = ?");
            ps.setInt(1, veranstId);
            int res = ps.executeUpdate();
            if (res != 1)
                return false;
            else
                return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }
        return false;
    }

    @Override
    public List<Benachrichtigung> leseBenachrichtigungen(int benutzer, int limit)
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Benachrichtigung> aktBenachrichtigungen = null;
        try
        {
            aktBenachrichtigungen = new ArrayList<Benachrichtigung>();

            // Lese Die IDs der speziellen Benachrichtigungen aus der Datenbank. Geordnet wird nach 2 Attributen.
            // Zuerst wird nach gelesen/nicht-gelesen sortiert. Dabei werden noch nicht gelesene Benachrichtigungen
            // bevorzugt. Als zweites wird nach dem Erstelldatum der Benachrichtigung sortiert
            ps = conMysql
                    .prepareStatement("SELECT bem.ID AS ID, 'benachrichtigung_einladung_moderator' AS Tabelle, gelesen AS gel,"
                            + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_einladung_moderator AS bem JOIN benachrichtigung AS b ON"
                            + " bem.Benachrichtigung = b.ID WHERE bem.Benutzer =? UNION "

                            + "SELECT bk.ID AS ID, 'benachrichtigung_karteikartenaenderung' AS Tabelle, gelesen AS gel,"
                            + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_karteikartenaenderung AS bk JOIN benachrichtigung AS b ON"
                            + " bk.Benachrichtigung = b.ID WHERE bk.Benutzer =? UNION "

                            + "SELECT bnk.ID AS ID, 'benachrichtigung_neuer_kommentar' AS Tabelle , gelesen AS gel,"
                            + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_neuer_kommentar AS bnk JOIN benachrichtigung AS b ON"
                            + " bnk.Benachrichtigung = b.ID WHERE bnk.Benutzer =? UNION "

                            + "SELECT bpg.ID AS ID, 'benachrichtigung_profil_geaendert' AS Tabelle , gelesen AS gel,"
                            + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_profil_geaendert AS bpg JOIN benachrichtigung AS b ON"
                            + " bpg.Benachrichtigung = b.ID WHERE bpg.Benutzer =? UNION "

                            + "SELECT bv.ID AS ID, 'benachrichtigung_veranstaltungsaenderung' AS Tabelle , gelesen AS gel,"
                            + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_veranstaltungsaenderung AS bv JOIN benachrichtigung AS b ON"
                            + " bv.Benachrichtigung = b.ID WHERE bv.Benutzer =? "
                            + "ORDER BY gel, Erstelldatum DESC LIMIT ?");

            ps.setInt(1, benutzer);
            ps.setInt(2, benutzer);
            ps.setInt(3, benutzer);
            ps.setInt(4, benutzer);
            ps.setInt(5, benutzer);
            ps.setInt(6, limit);
            rs = ps.executeQuery();
            String tabelle;
            int id;
            
            Benachrichtigung benachrichtigung;
            // Lese nun die Attribute für die speziellen Benachrichtigungen aus
            while (rs.next())
            {
                tabelle = rs.getString("Tabelle");
                id = rs.getInt("ID");
                if (tabelle.equals("benachrichtigung_einladung_moderator")){
                    benachrichtigung = leseBenachrEinlModerator(id,conMysql);
                    if(benachrichtigung == null)
                        return null;
                    aktBenachrichtigungen.add(benachrichtigung);
                }
                else if (tabelle.equals("benachrichtigung_karteikartenaenderung")){
                    benachrichtigung = leseBenachrKarteikAenderung(id,conMysql, conNeo4j);
                    if(benachrichtigung == null)
                        return null;
                    aktBenachrichtigungen.add(benachrichtigung);
                }
                else if (tabelle.equals("benachrichtigung_neuer_kommentar")){
                    benachrichtigung = leseBenachrNeuerKommentar(id,conMysql, conNeo4j);
                    if(benachrichtigung == null)
                        return null;
                    aktBenachrichtigungen.add(benachrichtigung);
                }
                else if (tabelle.equals("benachrichtigung_profil_geaendert")){
                    benachrichtigung = leseBenachrProfilGeaendert(id,conMysql);
                    if(benachrichtigung == null)
                        return null;
                    aktBenachrichtigungen.add(benachrichtigung);
                }
                else{
                    benachrichtigung = leseBenachrVeranstAenderung(id,conMysql);
                    if(benachrichtigung == null)
                        return null;
                    aktBenachrichtigungen.add(benachrichtigung);
                }
            }

        }
        catch (SQLException e)
        {
            aktBenachrichtigungen = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }
        return aktBenachrichtigungen;
    }

    private BenachrEinlModerator leseBenachrEinlModerator(int id, Connection conMysql)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrEinlModerator benachrichtigung = null;
        try
        {
            // Lese Benachrichtigung für die Einladung als Moderator
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Veranstaltung, Gelesen, Angenommen"
                            + " FROM benachrichtigung_einladung_moderator AS bem JOIN benachrichtigung AS b ON bem.Benachrichtigung"
                            + "= b.ID WHERE bem.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getTimestamp("Erstelldatum").getTime());
                
                // Lese Veranstaltung zu gegebener ID
                Veranstaltung veranst = leseVeranstaltungWrapper(rs.getInt("Veranstaltung"),conMysql);
                if(veranst == null)
                    return null;
                // Ereuge Benachrichtigungs-Objekt
                benachrichtigung = new BenachrEinlModerator(rs.getInt("Benachrichtigung"), rs.getString("Inhalt"), cal,
                        rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), veranst,
                        rs.getBoolean("Angenommen"));
            }
        }
        catch (SQLException e)
        {
            benachrichtigung = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    private BenachrKarteikAenderung leseBenachrKarteikAenderung(int id, Connection conMysql, Connection conNeo4j)
    {        
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrKarteikAenderung benachrichtigung = null;
        try
        {
            // Benachrichtigung für Karteikarten Änderung auslesen
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Karteikarte, Gelesen"
                            + " FROM benachrichtigung_karteikartenaenderung AS bk JOIN benachrichtigung AS b ON bk.Benachrichtigung"
                            + "= b.ID WHERE bk.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getTimestamp("Erstelldatum").getTime());
                // Lese Karteikarte aus der ID
                Karteikarte kk = leseKarteikarteWrapper(rs.getInt("Karteikarte"),conMysql,conNeo4j);
                if (kk == null)
                    return null;

                // Erzeuge neue Benachrichtigung
                benachrichtigung = new BenachrKarteikAenderung(rs.getInt("Benachrichtigung"), rs.getString("Inhalt"),
                        cal, rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), kk);
            }
        }
        catch (SQLException e)
        {
            benachrichtigung = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    private BenachrNeuerKommentar leseBenachrNeuerKommentar(int id, Connection conMysql, Connection conNeo4j)
    {
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrNeuerKommentar benachrichtigung = null;
        try
        {
            // Lese Benachrichtigung für neuen Kommentar
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Kommentar, Gelesen"
                            + " FROM benachrichtigung_neuer_kommentar AS bnk JOIN benachrichtigung AS b ON bnk.Benachrichtigung"
                            + "= b.ID WHERE bnk.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                // Nur Themenkommentare haben die zugeordnete Karteikarte gespeichert
                Kommentar kommentar = leseKommentar(rs.getInt("Kommentar"));
                Kommentar vaterKommentar = kommentar;
                // Hole Vaterkommentar zu dem Kommentar
                if (kommentar.getVaterID() != -1)
                    vaterKommentar = leseKommentar(kommentar.getVaterID());
                if (vaterKommentar == null)
                    return null;
                // Lese Karteikarte aus der ID des Vater-Kommentars
                Karteikarte karteik = leseKarteikarteWrapper(vaterKommentar.getKarteikartenID(), conMysql, conNeo4j);
                // Prüfe ob bei leseKarteikarteWrapper ein Fehler aufgetreten ist
                if(karteik == null)
                    return null;

                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getTimestamp("Erstelldatum").getTime());
                // ereuge neue Benachrichtigung
                benachrichtigung = new BenachrNeuerKommentar(rs.getInt("Benachrichtigung"), rs.getString("Inhalt"),
                        cal, rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), rs.getInt("Kommentar"), karteik);
            }
        }
        catch (SQLException e)
        {
            benachrichtigung = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    private BenachrProfilGeaendert leseBenachrProfilGeaendert(int id, Connection conMysql)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrProfilGeaendert benachrichtigung = null;
        try
        {
            // Lese Benachrichtigung zu einer Profil-Änderung
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Admin, Gelesen"
                            + " FROM benachrichtigung_profil_geaendert AS bpg JOIN benachrichtigung AS b ON bpg.Benachrichtigung"
                            + "= b.ID WHERE bpg.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getTimestamp("Erstelldatum").getTime());
                
                // Lese Benutzerdaten des Admins, der das Profil des Benutzers geändert hat
                Benutzer admin = leseBenutzerWrapper(rs.getInt("Admin"),conMysql);
                // Prüfe ob bei leseBenutzerWrapper ein Fehler aufgetreten ist
                if(admin == null)
                    return null;
                
                // erzeuge neue Benachrichtigung
                benachrichtigung = new BenachrProfilGeaendert(rs.getInt("Benachrichtigung"), rs.getString("Inhalt"),
                        cal, rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), admin);
            }
        }
        catch (SQLException e)
        {
            benachrichtigung = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    private BenachrVeranstAenderung leseBenachrVeranstAenderung(int id, Connection conMysql)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrVeranstAenderung benachrichtigung = null;
        try
        {
            // Lese Benachrichtigung zu einer Veranstaltungsänderung
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Veranstaltung, Gelesen"
                            + " FROM benachrichtigung_veranstaltungsaenderung AS bv JOIN benachrichtigung AS b ON bv.Benachrichtigung"
                            + "= b.ID WHERE bv.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getTimestamp("Erstelldatum").getTime());
                
                // Lese Veranstaltung die geändert wurde
                Veranstaltung veranst = leseVeranstaltungWrapper(rs.getInt("Veranstaltung"),conMysql);
                // Prüfe ob bei leseVeranstaltungWrapper ein Fehler aufgetreten ist
                if(veranst == null)
                    return null;
                // Erzeuge neue Benachrichtigung
                benachrichtigung = new BenachrVeranstAenderung(rs.getInt("Benachrichtigung"), rs.getString("Inhalt"),
                        cal, rs.getInt("Benutzer"), rs.getBoolean("Gelesen"),veranst);
            }
        }
        catch (SQLException e)
        {
            benachrichtigung = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    @Override
    public void schreibeBenachrichtigung(Benachrichtigung benachrichtigung) throws SQLException
    {
        // schreibe Benachrichtigung mit neuer Connection
        Connection conMysql = getConnectionSQL();

        try
        {

            conMysql.setAutoCommit(false);
            schreibeBenachrichtigungWrapper(benachrichtigung, conMysql);

            conMysql.commit();

        }
        catch (SQLException e)
        {
            conMysql.rollback();
            throw e;
        }
        finally
        {

            conMysql.setAutoCommit(true);
            closeQuietly(conMysql);
        }

    }
    
    private void schreibeBenachrichtigungWrapper(Benachrichtigung benachrichtigung, Connection conMysql) throws SQLException{
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            // Erzeuge zuerst eine Benachrichtiung in der allgemeinen Benachrichtigung-Tabelle
            ps = conMysql.prepareStatement("INSERT INTO benachrichtigung (Inhalt) VALUES (?); ");
            ps.setString(1, benachrichtigung.getInhalt());
            ps.executeUpdate();
            closeQuietly(ps);

            // Hole die von MySql automatisch eingefügte ID, da sie für die spezielle Benachrichtigung
            // benötigt wird
            ps = conMysql.prepareStatement("SELECT LAST_INSERT_ID();");
            rs = ps.executeQuery();
            if (!rs.next())
                throw new SQLException();
            int id = rs.getInt(1);
            closeQuietly(ps);

            // Prüfe welche spezielle Benachrichtigung vorliegt und füge diese in die Datenbank ein.
            // Dabei wird auf die allgemeine Benachrichtigung-Tabelle referenziert
            if (benachrichtigung instanceof BenachrEinlModerator)
            {
                BenachrEinlModerator bem = (BenachrEinlModerator) benachrichtigung;
                ps = conMysql
                        .prepareStatement("INSERT INTO benachrichtigung_einladung_moderator (Benachrichtigung, Benutzer,"
                                + "Veranstaltung) VALUES (?,?,?)");
                ps.setInt(1, id);
                ps.setInt(2, bem.getBenutzer());
                ps.setInt(3, bem.getVeranstaltung().getId());

            }
            else if (benachrichtigung instanceof BenachrKarteikAenderung)
            {
                BenachrKarteikAenderung bk = (BenachrKarteikAenderung) benachrichtigung;
                // Nur Benutzer, die zu der Veranstaltung eingeschrieben sind, zu der die Karteikarte gehört können
                // benachrichtigt werden. Zusätzlich müssen die Benutzer in den Profileinstellungen festgelegt haben,
                // dass sie darüber benachrichtigt werden wollen
                ps = conMysql
                        .prepareStatement("INSERT INTO benachrichtigung_karteikartenaenderung (Benachrichtigung, Karteikarte,"
                                + "Benutzer) "
                                + "SELECT ?,?,Benutzer FROM benutzer AS b JOIN benutzer_veranstaltung_zuordnung AS bvz"
                                + " ON b.ID = bvz.Benutzer JOIN Karteikarte AS k ON bvz.Veranstaltung = k.Veranstaltung "
                                + "WHERE k.ID = ? AND b.NotifyKarteikartenAenderung = true AND b.ID != ?");
                ps.setInt(1, id);
                ps.setInt(2, bk.getKarteikarte().getId());
                ps.setInt(3, bk.getKarteikarte().getId());
                ps.setInt(4,bk.getBenutzer());

            }
            else if (benachrichtigung instanceof BenachrNeuerKommentar)
            {
                BenachrNeuerKommentar bnk = (BenachrNeuerKommentar) benachrichtigung;
                Kommentar kommentar = leseKommentarWrapper(bnk.getKommentarId(), conMysql);
                if (kommentar == null)
                    throw new SQLException();
                // Es gibt 2 Möglichkeiten in den Benutzereinstellungen, damit man zu neuen Kommentaren benachrichtigt wird.
                // "Diskussion Teilgenommen" und "Veranstaltung Teilgenommen". 
                // Bei "Diskussion Teilgenommen" gibt es 2 Möglichkeiten. Entweder hat der Kommentar eines Benutzers den gleichen Vaterkommentar
                // wie der Kommentar, der gerade eingefüt wird oder der Kommentar des Benutzers ist selbst der Vaterkommentar
                ps = conMysql
                        .prepareStatement("INSERT INTO benachrichtigung_neuer_kommentar (Benachrichtigung, Benutzer,"
                                + "Kommentar) SELECT DISTINCT ?,k.Benutzer,? FROM Kommentar AS k JOIN Benutzer AS b ON "
                                + "k.Benutzer = b.ID WHERE (k.Vaterkommentar = ? OR k.ID = ?) AND b.NotifyKommentare = 'DISKUSSION_TEILGENOMMEN' "
                                + "AND b.ID != ?");
                ps.setInt(1, id);
                ps.setInt(2, bnk.getKommentarId());
                ps.setInt(3, kommentar.getVaterID());
                ps.setInt(4, kommentar.getVaterID());
                ps.setInt(5, bnk.getBenutzer());

                ps.executeUpdate();

                // Bei "Veranstaltung Teilgenommen" wird ein Benutzer benachrichtigt, falls er für die Veranstaltung
                // eingeschrieben ist zu der der Kommentar gehört
                ps = conMysql
                        .prepareStatement("INSERT INTO benachrichtigung_neuer_kommentar (Benachrichtigung, Benutzer,"
                                + "Kommentar) SELECT DISTINCT ?,bvz.Benutzer, ? FROM benutzer_veranstaltung_zuordnung AS bvz "
                                + "JOIN Benutzer AS b ON bvz.Benutzer = b.ID WHERE bvz.Veranstaltung = ? AND "
                                + "b.NotifyKommentare = 'VERANSTALTUNG_TEILGENOMMEN' AND b.ID != ?");
                ps.setInt(1, id);
                ps.setInt(2, bnk.getKommentarId());
                ps.setInt(3, bnk.getKarteikarte().getVeranstaltung());
                ps.setInt(4, bnk.getBenutzer());
            }
            else if (benachrichtigung instanceof BenachrProfilGeaendert)
            {
                BenachrProfilGeaendert bpg = (BenachrProfilGeaendert) benachrichtigung;
                ps = conMysql
                        .prepareStatement("INSERT INTO benachrichtigung_profil_geaendert (Benachrichtigung, Benutzer,"
                                + "Admin) VALUES (?,?,?);");
                ps.setInt(1, id);
                ps.setInt(2, bpg.getBenutzer());
                ps.setInt(3, bpg.getAdmin().getId());

            }
            else if (benachrichtigung instanceof BenachrVeranstAenderung)
            {
                // Nur Benutzer, die zu der Veranstaltung eingeschrieben sind können
                // benachrichtigt werden. Zusätzlich müssen die Benutzer in den Profileinstellungen festgelegt haben,
                // dass sie darüber benachrichtigt werden wollen
                BenachrVeranstAenderung bv = (BenachrVeranstAenderung) benachrichtigung;
                ps = conMysql.prepareStatement("INSERT INTO benachrichtigung_veranstaltungsaenderung"
                        + " (Benachrichtigung, Veranstaltung, Benutzer)"
                        + " SELECT ?,?,Benutzer FROM benutzer_veranstaltung_zuordnung AS bvz "
                        + "JOIN Benutzer AS b ON bvz.Benutzer = b.ID WHERE bvz.Veranstaltung = ? "
                        + "AND b.NotifyVeranstAenderung = true AND b.ID != ?");
                ps.setInt(1, id);
                ps.setInt(2, bv.getVeranstaltung().getId());
                ps.setInt(3, bv.getVeranstaltung().getId());
                ps.setInt(4, bv.getBenutzer());
            }
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }
    }

    public boolean markiereBenAlsGelesen(int benID, int benutzerID)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        int updates = 0;
        try
        {
            String sql = "UPDATE benachrichtigung_veranstaltungsaenderung " + "SET Gelesen = 1 "
                    + "WHERE Benachrichtigung = ? AND Benutzer = ? ";
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, benID);
            ps.setInt(2, benutzerID);
            updates += ps.executeUpdate();

            sql = "UPDATE benachrichtigung_profil_geaendert " + "SET Gelesen = 1 "
                    + "WHERE Benachrichtigung = ? AND Benutzer = ? ";
            closeQuietly(ps);
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, benID);
            ps.setInt(2, benutzerID);
            updates += ps.executeUpdate();

            sql = "UPDATE benachrichtigung_neuer_kommentar " + "SET Gelesen = 1 "
                    + "WHERE Benachrichtigung = ? AND Benutzer = ? ";
            closeQuietly(ps);
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, benID);
            ps.setInt(2, benutzerID);
            updates += ps.executeUpdate();

            sql = "UPDATE benachrichtigung_karteikartenaenderung " + "SET Gelesen = 1 "
                    + "WHERE Benachrichtigung = ? AND Benutzer = ? ";
            closeQuietly(ps);
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, benID);
            ps.setInt(2, benutzerID);
            updates += ps.executeUpdate();

            sql = "UPDATE benachrichtigung_einladung_moderator " + "SET Gelesen = 1 "
                    + "WHERE Benachrichtigung = ? AND Benutzer = ? ";
            closeQuietly(ps);
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, benID);
            ps.setInt(2, benutzerID);
            updates += ps.executeUpdate();

            if (updates != 1)
            {
                System.err.println("[Benachrichtigung gelesen] Es wurden " + updates
                        + " anstatt einem Eintrag in der Tabelle geändert.");
                return false;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }

        return true;
    }

    @Override
    public Karteikarte leseKarteikarte(int karteikID)
    {
        // Lese Karteikarte mit eigener Conneciton
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();
        try
        {
            return leseKarteikarteWrapper(karteikID, conMysql, conNeo4j);
        }

        finally
        {
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }
    }
    
    private Karteikarte leseKarteikarteWrapper(int karteikID, Connection conMysql, Connection conNeo4j){
        PreparedStatement ps = null;
        ResultSet rs = null;

        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        Karteikarte karteikarte = null;
        try
        {
            // Lese Verweise der Karteikarte aus der Neo4j Datenbank
            ps = conNeo4j.prepareStatement("MATCH(n)-[r:" + BeziehungsTyp.V_VORAUSSETZUNG.toString().toLowerCase()
                    + "|" + BeziehungsTyp.V_UEBUNG.toString().toLowerCase() + "|" + ""
                    + BeziehungsTyp.V_ZUSATZINFO.toString().toLowerCase() + "|"
                    + BeziehungsTyp.V_SONSTIGES.toString().toLowerCase() + "]->(m)"
                    + " WHERE id(n) = {1} RETURN id(m) AS zielID, type(r) AS Typ");
            ps.setInt(1, karteikID);
            rs = ps.executeQuery();
            ArrayList<Tripel<BeziehungsTyp, Integer, String>> verweise = new ArrayList<Tripel<BeziehungsTyp, Integer, String>>();
            while (rs.next())
            {
                // Lese Name der Karteikarte aus der MySql Datenbank
                ps2 = conMysql.prepareStatement("SELECT Titel FROM Karteikarte WHERE ID = ?");
                ps2.setInt(1, rs.getInt("zielID"));
                rs2 = ps2.executeQuery();
                if (!rs2.next())
                    return null;
                // erstelle neuen Verweis
                verweise.add(new Tripel<Karteikarte.BeziehungsTyp, Integer, String>(BeziehungsTyp.valueOf(rs.getString(
                        "Typ").toUpperCase()), rs.getInt("zielID"), rs2.getString("Titel")));
            }

            closeQuietly(ps);
            closeQuietly(rs);

            // Lese restliche Daten der Karteikarte aus der MySql Datenbank
            ps = conMysql.prepareStatement("SELECT Titel, Inhalt, Typ, Bewertung, Aenderungsdatum, Veranstaltung,"
                    + " Bewertung, Satz, Lemma, Beweis, Definition, Wichtig, Grundlagen, Zusatzinformation, Exkurs,"
                    + " Beispiel, Uebung FROM karteikarte WHERE ID = ?");
            ps.setInt(1, karteikID);

            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                cal.setTime(rs.getTimestamp("Aenderungsdatum"));
                karteikarte = new Karteikarte(karteikID, rs.getString("Titel"), cal, rs.getString("Inhalt"),
                        KarteikartenTyp.valueOf(rs.getString("Typ")), rs.getInt("Veranstaltung"),
                        rs.getInt("Bewertung"), rs.getBoolean("Satz"), rs.getBoolean("Lemma"), rs.getBoolean("Beweis"),
                        rs.getBoolean("Definition"), rs.getBoolean("Wichtig"), rs.getBoolean("Grundlagen"),
                        rs.getBoolean("Zusatzinformation"), rs.getBoolean("Exkurs"), rs.getBoolean("Beispiel"),
                        rs.getBoolean("Uebung"), verweise);
            }
        }
        catch (SQLException e)
        {
            karteikarte = null;
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            karteikarte = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(ps2);
            closeQuietly(rs2);
        }
        return karteikarte;
    }

    @Override
    public Map<Integer, Tupel<Integer, String>> leseKindKarteikarten(int vaterKarteikID)
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();

        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psMysql = null;
        ResultSet rsMysql = null;
        HashMap<Integer, Tupel<Integer, String>> kindKarteikarten = null;
        try
        {
            kindKarteikarten = new HashMap<Integer, Tupel<Integer, String>>();
            // Lese alle Kindkarteikarten einer Karteikarte. Das erste Kind ist mit einer h_child Beziehung
            // zu der Karteikarte verknüpft. Die anderen hängen als verkettete Liste in einer h_brother Relation
            // an dem ersten Kind.
            ps = conNeo4j.prepareStatement("MATCH (n)-[:h_child]->()-[:h_brother*0..]-(m)" + " WHERE id(n) = {1}"
                    + " return id(m) AS ID");
            ps.setInt(1, vaterKarteikID);

            rs = ps.executeQuery();

            int i = 0;
            while (rs.next())
            {
                // Lese die Titel der Kindkarteikarten aus der MySql-Datenbank
                psMysql = conMysql.prepareStatement("SELECT Titel FROM Karteikarte WHERE ID = ?");
                psMysql.setInt(1, rs.getInt("ID"));
                rsMysql = psMysql.executeQuery();
                if (!rsMysql.next())
                    return null;

                kindKarteikarten.put(i, new Tupel<Integer, String>(rs.getInt("ID"), rsMysql.getString("Titel")));
                ++i;
                closeQuietly(psMysql);
                closeQuietly(rsMysql);
            }
        }
        catch (SQLException e)
        {
            kindKarteikarten = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(psMysql);
            closeQuietly(rsMysql);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }

        return kindKarteikarten;
    }

    @Override
    public Map<Integer, Karteikarte> leseNachfolger(int karteikarte, int anzNachfolger)
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();

        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<Integer, Karteikarte> nachfolger = new HashMap<Integer, Karteikarte>();
        try
        {
            Integer aktuelleKarteikarte = karteikarte;
            int i = 0;
            while (anzNachfolger > 0)
            {
                // Folge dem h_child Pfad. Dabei dürfen maximal "anzNachfolger" viele ausgelesen werden
                ps = conNeo4j.prepareStatement("MATCH (n)-[:h_child*1.." + anzNachfolger + "]->(m)"
                        + " WHERE id(n) = {1}" + " return id(m) AS ID");
                ps.setInt(1, aktuelleKarteikarte);

                rs = ps.executeQuery();

                while (rs.next())
                {
                    // Die Kind-Karteikarte wird zur neuen aktuellen Karteikarte
                    aktuelleKarteikarte = rs.getInt("ID");
                    // lese aus der ID alle Daten der Karteikarte
                    Karteikarte aktKarteik = leseKarteikarteWrapper(aktuelleKarteikarte,conMysql,conNeo4j);
                    // Prüfe ob bei der Methode leseKArteikarteWrapper ein Fehler aufgetreten ist
                    if (aktKarteik == null)
                        return null;
                    // Füge die Kind-Karteikarte in die Liste der Nachfolger ein
                    nachfolger.put(i, aktKarteik);
                    --anzNachfolger;
                    ++i;
                }

                closeQuietly(ps);
                closeQuietly(rs);

                // Gibt es keine Kind-Karteikarte mehr und es müssen aber noch mehr Karteikarten ausgelesen
                // werdnen, dann wird einmal dem h_brother Pfad gefolgt
                Integer bruderKarteik;
                do
                {
                    bruderKarteik = gibBruder(aktuelleKarteikarte, conNeo4j);

                    // Prüfe ob gibBruder ein Fehler liefert
                    if (bruderKarteik == null)
                        return null;
                    else if (bruderKarteik == -1)
                    {
                        // Falls die aktuelleKarteikarte keinen Bruder mehr hat, wird die verkettete Liste aus
                        // h_brother Relationenen zurückgelaufen und dann eine Ebene höher gegangen
                        ps = conNeo4j.prepareStatement("MATCH p=((n)<-[:h_brother*0..]-(m))" + " WHERE id(n) = {1}"
                                + " return id(m) AS ID ORDER BY length(p) DESC LIMIT 1");
                        ps.setInt(1, aktuelleKarteikarte);

                        rs = ps.executeQuery();

                        while (rs.next())
                        {
                            // Diese Karteikarten wurden schon besucht. Deshalb werden sie nicht nochmal
                            // in die Liste "kindKarteiarten" hinzugefüt
                            aktuelleKarteikarte = rs.getInt("ID");
                        }
                        closeQuietly(ps);
                        closeQuietly(rs);

                        Integer vaterKarteikarte = gibVater(aktuelleKarteikarte, conNeo4j);
                        if (vaterKarteikarte == null)
                            return null;
                        else if (vaterKarteikarte == -1)
                            return nachfolger;
                        else
                            aktuelleKarteikarte = vaterKarteikarte;
                    }
                }
                while (bruderKarteik == -1);

                Karteikarte bruderKk = leseKarteikarteWrapper(bruderKarteik,conMysql,conNeo4j);
                if (bruderKk == null)
                    return null;
                nachfolger.put(i, bruderKk);
                aktuelleKarteikarte = bruderKarteik;
                ++i;
                --anzNachfolger;

            }
        }
        catch (SQLException e)
        {
            nachfolger = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }

        return nachfolger;
    }

    @Override
    public Map<Integer, Karteikarte> leseVorgaenger(int karteikarte, int anzVorgänger)
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();

        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<Integer, Karteikarte> vorgaenger = new HashMap<Integer, Karteikarte>();
        try
        {
            Integer aktuelleKarteikarte = karteikarte;
            int i = 0;
            while (anzVorgänger > 0)
            {
                // Lese die erste übergeordnete Bruder Karteikarte
                ps = conNeo4j.prepareStatement("MATCH (n)<-[:h_brother]-(m)" + " WHERE id(n) = {1}"
                        + " return id(m) AS ID");
                ps.setInt(1, aktuelleKarteikarte);

                rs = ps.executeQuery();

                closeQuietly(ps);
                closeQuietly(rs);

                // Gibt es keine übergeordnete Bruder Karteikarte, dann wird der Vater ausgelesen
                if (!rs.next())
                {
                    aktuelleKarteikarte = gibVater(aktuelleKarteikarte, conNeo4j);
                    if (aktuelleKarteikarte == null)
                        return null;
                    // Gibt es keinen Vater mehr, dann können keine weiteren Vorgänger gefunden werden und
                    // es wird an dieser Stelle abgebrochen
                    else if (aktuelleKarteikarte == -1)
                        return vorgaenger;

                    Karteikarte aktKarteik = leseKarteikarteWrapper(aktuelleKarteikarte, conMysql, conNeo4j);
                    if (aktKarteik == null)
                        return null;
                    vorgaenger.put(i, aktKarteik);
                    --anzVorgänger;
                    ++i;
                    // Gehe zum nächsten Schleifendurchlauf, da für die Vaterkarteikarte erst wieder
                    // der übergeordnete Bruder und keine Kinder gelesen werden sollen
                    continue;
                }
                else
                {
                    aktuelleKarteikarte = rs.getInt("ID");
                }

                boolean abbruch = false;
                do
                {
                    // Hole Kind der übergeordneten Bruder Karteikarte
                    ps = conNeo4j.prepareStatement("MATCH (n)-[:h_child]->(m)" + " WHERE id(n) = {1}"
                            + " return id(m) AS ID");

                    ps.setInt(1, aktuelleKarteikarte);
                    rs = ps.executeQuery();

                    if (!rs.next())
                    {
                        abbruch = true;

                        Karteikarte aktKarteik = leseKarteikarteWrapper(aktuelleKarteikarte, conMysql, conNeo4j);
                        if (aktKarteik == null)
                            return null;
                        vorgaenger.put(i, aktKarteik);
                        --anzVorgänger;
                        ++i;
                        closeQuietly(ps);
                        closeQuietly(rs);
                    }
                    else
                    {
                        aktuelleKarteikarte = rs.getInt("ID");
                        closeQuietly(ps);
                        closeQuietly(rs);
                        // Hole letzten Bruder in der verketteten Liste
                        ps = conNeo4j.prepareStatement("MATCH p=((n)-[:h_brother*0..]->(m))" + " WHERE id(n) = {1}"
                                + " return id(m) AS ID ORDER BY length(p) DESC LIMIT 1");

                        ps.setInt(1, aktuelleKarteikarte);
                        rs = ps.executeQuery();

                        if (rs.next())
                            aktuelleKarteikarte = rs.getInt("ID");
                        else
                            return null;

                        closeQuietly(ps);
                        closeQuietly(rs);
                    }
                }
                while (abbruch == false);

            }
        }
        catch (SQLException e)
        {
            vorgaenger = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }

        return vorgaenger;
    }

    private Integer gibBruder(int karteikarte, Connection conNeo4j)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer karteik;
        try
        {
            // Lese ID der Bruderkarteikarte
            ps = conNeo4j.prepareStatement("MATCH (n)-[:h_brother]->(m)" + " WHERE id(n) = {1} return id(m) AS ID");
            ps.setInt(1, karteikarte);
            rs = ps.executeQuery();
            if (rs.next())
                karteik = rs.getInt("ID");
            else
                karteik = -1;
        }
        catch (SQLException e)
        {
            karteik = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return karteik;
    }

    private Integer gibAelterenBruder(int karteikarte, Connection conNeo4j)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer karteik;
        try
        {
            // Lese ID der übergeordneten Bruderkarteikarte
            ps = conNeo4j.prepareStatement("MATCH (n)<-[:h_brother]-(m)" + " WHERE id(n) = {1} return id(m) AS ID");
            ps.setInt(1, karteikarte);
            rs = ps.executeQuery();
            if (rs.next())
                karteik = rs.getInt("ID");
            else
                karteik = -1;
        }
        catch (SQLException e)
        {
            karteik = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return karteik;
    }

    private Integer gibVater(int karteikarte, Connection conNeo4j)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer karteik;
        try
        {
            // Lese ID der Vaterkarteikarte
            ps = conNeo4j.prepareStatement("MATCH (n)<-[:h_child]-(m)" + " WHERE id(n) = {1} return id(m) AS ID");
            ps.setInt(1, karteikarte);
            rs = ps.executeQuery();
            if (rs.next())
                karteik = rs.getInt("ID");
            else
                karteik = -1;
        }
        catch (SQLException e)
        {
            karteik = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return karteik;
    }

    private Integer gibKind(int karteikarte, Connection conNeo4j)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer karteik;
        try
        {
            // Lese ID der Kindkarteiarte
            ps = conNeo4j.prepareStatement("MATCH (n)-[:h_child]->(m)" + " WHERE id(n) = {1} return id(m) AS ID");
            ps.setInt(1, karteikarte);
            rs = ps.executeQuery();
            if (rs.next())
                karteik = rs.getInt("ID");
            else
                karteik = -1;
        }
        catch (SQLException e)
        {
            karteik = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return karteik;
    }

    @Override
    public int schreibeKarteikarte(Karteikarte karteik, int vaterKK, int ueberliegendeBruderKK) throws SQLException
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();

        PreparedStatement ps = null;
        ResultSet rs = null;
        int insertedId = -1;

        try
        {
            conNeo4j.setAutoCommit(false);
            conMysql.setAutoCommit(false);

            // Erstelle neuen Knoten in der Neo4j Datenbank und lese die von Neo4j automatisch
            // erstellte ID damit sie für die MySql Datenbank zur Verfügung steht
            ps = conNeo4j.prepareStatement("CREATE(n) RETURN id(n) AS Id");

            rs = ps.executeQuery();
            if (!rs.next())
                return insertedId;
            insertedId = rs.getInt("Id");
            karteik.setId(insertedId);

            closeQuietly(ps);

            // Gibt es eine überliegende Bruder Karteikarte, dann muss die neue Karteikarte mit dieser
            // über die h_brother Relation verknüpft werden
            if (ueberliegendeBruderKK != -1)
            {
                Integer unterliegendeBruderKK = gibBruder(ueberliegendeBruderKK, conNeo4j);
                // Hat die überliegende Bruder Karteikarte eine unterliegende Bruder Karteikarte, dann
                // muss diese Verbindung aufgetrennt werden und die neue Karteiakarte dazwischen
                // eingehängt werden
                if (unterliegendeBruderKK == null)
                    throw new SQLException();
                else if (unterliegendeBruderKK != -1)
                {
                    disconnectKk(ueberliegendeBruderKK, unterliegendeBruderKK, conNeo4j);
                    connectKk(insertedId, unterliegendeBruderKK, BeziehungsTyp.H_BROTHER, conNeo4j);
                }

                connectKk(ueberliegendeBruderKK, insertedId, BeziehungsTyp.H_BROTHER, conNeo4j);

            }
            // Gibt es keine überliegende Bruder Karteikarte, dann muss die neue Karteikarte mit der
            // Vater Karteikarte über die h_child Relation verknüpft werden. Eine Vaterkarteikarte
            // sollte es immer geben, da über der "ersten Karteikarte" der Veranstaltung keine Karteikarte
            // eingefügt werden kann
            else if (vaterKK != -1)
            {
                Integer kindKarteikarte = gibKind(vaterKK, conNeo4j);
                
                // Hat die Vater Karteikarte eine Kind Karteikarte Karteikarte, dann
                // muss diese Verbindung aufgetrennt werden und die neue Karteiakarte dazwischen
                // eingehängt werden
                if (kindKarteikarte == null)
                    throw new SQLException();
                else if (kindKarteikarte != -1)
                {
                    disconnectKk(vaterKK, kindKarteikarte, conNeo4j);
                    connectKk(insertedId, kindKarteikarte, BeziehungsTyp.H_BROTHER, conNeo4j);
                }

                connectKk(vaterKK, insertedId, BeziehungsTyp.H_CHILD, conNeo4j);
            }
            else
            {
                throw new SQLException();
            }

            // erstelle die Verweise der Karteikarte zu anderen Karteikarten in der Neo4j Datenbank
            for (int i = 0; i < karteik.getVerweise().size(); ++i)
            {
                connectKk(karteik.getId(), karteik.getVerweise().get(i).y, karteik.getVerweise().get(i).x, conNeo4j);
            }

            closeQuietly(ps);

            // Schreibe restliche Daten der Karteikarte in die Datenbank
            ps = conMysql.prepareStatement("INSERT INTO karteikarte(ID,Titel,Inhalt,Typ,"
                    + "Veranstaltung, Satz, Lemma, Beweis, Definition, Wichtig, Grundlagen, "
                    + " Zusatzinformation, Exkurs, Beispiel, Uebung) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, insertedId);
            ps.setString(2, karteik.getTitel());
            ps.setString(3, karteik.getInhalt());
            ps.setString(4, karteik.getTyp().name());
            ps.setInt(5, karteik.getVeranstaltung());
            ps.setBoolean(6, karteik.isIstSatz());
            ps.setBoolean(7, karteik.isIstLemma());
            ps.setBoolean(8, karteik.isIstBeweis());
            ps.setBoolean(9, karteik.isIstDefinition());
            ps.setBoolean(10, karteik.isIstWichtig());
            ps.setBoolean(11, karteik.isIstGrundlage());
            ps.setBoolean(12, karteik.isIstZusatzinfo());
            ps.setBoolean(13, karteik.isIstExkurs());
            ps.setBoolean(14, karteik.isIstBeispiel());
            ps.setBoolean(15, karteik.isIstUebung());
            ps.executeUpdate();

            conNeo4j.commit();
            conMysql.commit();

        }
        catch (SQLException e)
        {
            conNeo4j.rollback();
            conMysql.rollback();
            e.printStackTrace();
            throw e;
        }
        finally
        {
            conNeo4j.setAutoCommit(true);
            conMysql.setAutoCommit(true);
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }

        return insertedId;
    }

    @Override
    public boolean bearbeiteKarteikarte(Karteikarte karteik, int bearbeiter)
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();
        PreparedStatement ps = null;

        boolean erfolgreich = true;

        try
        {
            conMysql.setAutoCommit(false);
            conNeo4j.setAutoCommit(false);

            // bearbeite Karteikarte
            ps = conMysql.prepareStatement("UPDATE karteikarte SET "
                    + "Titel = ?, Inhalt = ?, Typ = ?, Aenderungsdatum = ?, Satz = ?, Lemma = ?, Beweis = ?,"
                    + " Definition = ?, Wichtig = ?, Grundlagen = ?, Zusatzinformation = ?, Exkurs = ?,"
                    + " Beispiel = ?, Uebung = ? WHERE ID = ?");
            ps.setString(1, karteik.getTitel());
            ps.setString(2, karteik.getInhalt());
            ps.setString(3, karteik.getTyp().toString());
            ps.setTimestamp(4, new Timestamp(new GregorianCalendar().getTimeInMillis()));
            ps.setBoolean(5, karteik.isIstSatz());
            ps.setBoolean(6, karteik.isIstLemma());
            ps.setBoolean(7, karteik.isIstBeweis());
            ps.setBoolean(8, karteik.isIstDefinition());
            ps.setBoolean(9, karteik.isIstWichtig());
            ps.setBoolean(10, karteik.isIstGrundlage());
            ps.setBoolean(11, karteik.isIstZusatzinfo());
            ps.setBoolean(12, karteik.isIstExkurs());
            ps.setBoolean(13, karteik.isIstBeispiel());
            ps.setBoolean(14, karteik.isIstUebung());
            ps.setInt(15, karteik.getId());
            ps.executeUpdate();

            closeQuietly(ps);

            // lösche exitierende Verweise
            ps = conNeo4j
                    .prepareStatement("match n-[r:" + BeziehungsTyp.V_VORAUSSETZUNG.toString().toLowerCase() + "|"
                            + BeziehungsTyp.V_UEBUNG.toString().toLowerCase() + "|" + ""
                            + BeziehungsTyp.V_ZUSATZINFO.toString().toLowerCase() + "|"
                            + BeziehungsTyp.V_SONSTIGES.toString().toLowerCase() + "]->() " + "where id(n) = {1} "
                            + "delete r");
            ps.setInt(1, karteik.getId());
            ps.executeUpdate();

            // Füge neue Verweise ein
            for (Tripel<BeziehungsTyp, Integer, String> verweis : karteik.getVerweise())
            {
                connectKk(karteik.getId(), verweis.y, verweis.x, conNeo4j);
            }
            
            schreibeBenachrichtigungWrapper(new BenachrKarteikAenderung(karteik, bearbeiter), conMysql);

            conNeo4j.commit();
            conMysql.commit();
        }
        catch (SQLException e)
        {
            erfolgreich = false;
            e.printStackTrace();
            try
            {
                conMysql.rollback();
                conNeo4j.rollback();
            }
            catch (SQLException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }

        return erfolgreich;
    }

    @Override
    public boolean loescheKarteikarte(int karteikID)
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();

        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psMysql = null;

        boolean erfolgreich = true;

        try
        {
            conNeo4j.setAutoCommit(false);
            conMysql.setAutoCommit(false);

            // Lösche rekursiv aus der Neo4j Datenbank die Karteikarten und alle Verweise.
            // Dabei wird die Karteikarte selbst noch nicht gelöscht. Dies geschieht weiter unten
            ps = conNeo4j.prepareStatement("match p=((n)-[:h_child]->(m)-[:h_child|h_brother*0..]->(o))"
                    + " where id(n) = {1}" + " with o" + " match (o)-[r]-()" + " delete o,r "
                    + "return distinct(id(o)) AS ID_o");
            ps.setInt(1, karteikID);
            rs = ps.executeQuery();

            // Lösche Karteikarten aus der MySql Datenbank
            psMysql = conMysql.prepareStatement("DELETE FROM Karteikarte WHERE ID = ?");
            boolean loescheInMysql = rs.next();
            if (loescheInMysql)
            {
                do
                {
                    psMysql.setInt(1, rs.getInt("ID_o"));
                    psMysql.addBatch();
                }
                while (rs.next());

                psMysql.executeBatch();
                closeQuietly(psMysql);
            }

            closeQuietly(ps);

            // Wenn eine Karteikarte aus der Datenbank gelöscht wird, entsteht eine Lücke im Strukturbaum
            // Diese muss geschlossen werden
            Integer bruder = gibBruder(karteikID, conNeo4j);
            if (bruder == null)
                throw new SQLException();

            // Hat die Karteikarte keinen Bruder, dann entsteht keine Lücke
            if (bruder != -1)
            {
                Integer aeltererBruder = gibAelterenBruder(karteikID, conNeo4j);

                if (aeltererBruder == null)
                    throw new SQLException();
                // Hat die Karteikarte einen übergeordneten Bruder, dann muss
                // der übergeordnete Bruder und der unterliegende Bruder mit h_brother
                // verknüpft werden
                else if (aeltererBruder != -1)
                    connectKk(aeltererBruder, bruder, BeziehungsTyp.H_BROTHER, conNeo4j);
                else
                {
                    // Ansonsten muss die Vaterkarteikarte und die unterliegende
                    // Bruder Karteikarte mit h_child verknüpft werden
                    Integer vater = gibVater(karteikID, conNeo4j);
                    if (vater == null || vater == -1)
                        throw new SQLException();
                    else
                        connectKk(vater, bruder, BeziehungsTyp.H_CHILD, conNeo4j);
                }
            }

            // Lösche die Karteikarte aus der MySql Datenbank
            psMysql = conMysql.prepareStatement("DELETE FROM Karteikarte WHERE ID = ?");
            psMysql.setInt(1, karteikID);
            psMysql.executeUpdate();

            // Lösche die Karteikarte und alle ihre Verweise aus der Neo4j Datenbank
            ps = conNeo4j.prepareStatement("match(n) " + "where id(n) = {1} " + "OPTIONAL match n-[r]-() "
                    + "delete n,r");
            ps.setInt(1, karteikID);
            ps.executeUpdate();
            closeQuietly(ps);


            conNeo4j.commit();
            conMysql.commit();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            try
            {
                conNeo4j.rollback();
                conMysql.rollback();
            }
            catch (SQLException e1)
            {
                e1.printStackTrace();
            }
        }
        finally
        {
            try
            {
                conNeo4j.setAutoCommit(true);
                conMysql.setAutoCommit(true);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(psMysql);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }

        return erfolgreich;
    }

    @Override
    public boolean bewerteKarteikarte(int karteikID, int bewert, int benutzer)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;

        boolean erfolgreich = true;

        try
        {
            // benutzer bewertet Karteikarte
            ps = conMysql
                    .prepareStatement("INSERT INTO bewertung_karteikarte (Bewertung,Benutzer,KarteikarteID) VALUES (?,?,?)");
            ps.setInt(1, bewert);
            ps.setInt(2, benutzer);
            ps.setInt(3, karteikID);

            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            erfolgreich = false;

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }

        return erfolgreich;
    }

    @Override
    public boolean hatKarteikarteBewertet(int karteikID, int benutzer)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            ps = conMysql
                    .prepareStatement("SELECT ID FROM bewertung_karteikarte WHERE Benutzer = ? AND KarteikarteID = ?");
            ps.setInt(1, benutzer);
            ps.setInt(2, karteikID);

            rs = ps.executeQuery();
            // Wenn es ein Ergebnistupel gibt, dann hat der Benutzer die Karteikarte bereits bewertet
            // ansonsten nicht
            if (rs.next())
                return true;
            else
                return false;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;

        }
        finally
        {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conMysql);
        }

    }

    @Override
    public ArrayList<Kommentar> leseThemenKommentare(int karteikID, int aktBenutzerID)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;

        ArrayList<Kommentar> komms = new ArrayList<Kommentar>();

        try
        {
            // Lese Themenkommentare zu einer Karteikarte aus der View "kommentaruebersicht"
            String sql = "SELECT * FROM kommentaruebersicht WHERE Karteikarte = ?";
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, karteikID);

            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                GregorianCalendar g = new GregorianCalendar();
                g.setTime(rs.getTimestamp("erstelldatum"));
                
                // lese Benutzer aus der ID
                Benutzer benutzer = leseBenutzerWrapper(rs.getInt("Benutzer"), conMysql);
                // Prüfe ob leseBenutzerWrapper einen Fehler liefert
                if(benutzer == null)
                    return null;
                // Prüfe ob der aktuelle Benutzer die Karteikarte bewertet hat
                Boolean hatKommentarBewertet = hatKommentarBewertetWrapper(rs.getInt("ID"), aktBenutzerID, conMysql);
                if(hatKommentarBewertet == null)
                    return null;
                
                // Erstelle neuen Kommentar
                Kommentar k = new Kommentar(rs.getInt("ID"), rs.getString("Inhalt"), g,
                        benutzer, rs.getInt("Bewertung"), hatKommentarBewertet, -1, rs.getInt("Karteikarte"), rs.getInt("AnzKinder"));

                // Füge Kommentar der Liste von Themen-Kommentaren hinzu
                komms.add(k);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }

        return komms;
    }

    private Kommentar leseKommentarWrapper(int kommId, Connection conMysql)
    {
        PreparedStatement ps = null;

        Kommentar k = null;

        try
        {
            String sql = "SELECT * FROM kommentaruebersicht WHERE ID = ?";
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, kommId);

            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {

                GregorianCalendar g = new GregorianCalendar();
                g.setTime(rs.getTimestamp("erstelldatum"));

                int vaterID = rs.getInt("VaterKommentar");
                if (rs.wasNull())
                    vaterID = -1;
                int kkId = rs.getInt("Karteikarte");
                if (rs.wasNull())
                    kkId = -1;

                Benutzer benutzer = leseBenutzerWrapper(rs.getInt("Benutzer"),conMysql);
                if(benutzer == null)
                    return null;
                
                Boolean hatKommentarBewertet = hatKommentarBewertetWrapper(rs.getInt("ID"), rs.getInt("Benutzer"), conMysql);
                if(hatKommentarBewertet == null)
                    return null;
                
                k = new Kommentar(rs.getInt("ID"), rs.getString("Inhalt"), g, benutzer,
                        rs.getInt("Bewertung"),hatKommentarBewertet , vaterID,
                        kkId, rs.getInt("AnzKinder"));
            }

        }
        catch (SQLException e)
        {
            k = null;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
        }

        return k;
    }

    @Override
    public Kommentar leseKommentar(int kommId)
    {
        Connection conMysql = getConnectionSQL();

        Kommentar k = leseKommentarWrapper(kommId, conMysql);

        return k;
    }

    @Override
    public ArrayList<Kommentar> leseAntwortKommentare(int vaterKID, int aktBenutzerID)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;

        ArrayList<Kommentar> komms = new ArrayList<Kommentar>();

        try
        {
            String sql = "SELECT * FROM kommentaruebersicht WHERE Vaterkommentar = ?";
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, vaterKID);

            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                GregorianCalendar g = new GregorianCalendar();
                g.setTime(rs.getTimestamp("erstelldatum"));
                
                Benutzer benutzer = leseBenutzerWrapper(rs.getInt("Benutzer"), conMysql);
                if(benutzer == null)
                    return null;
                
                Boolean hatKommentarBewertet = hatKommentarBewertetWrapper(rs.getInt("ID"), aktBenutzerID, conMysql);
                if(hatKommentarBewertet == null)
                    return null;
                
                Kommentar k = new Kommentar(rs.getInt("ID"), rs.getString("Inhalt"), g,
                        benutzer, rs.getInt("bewertung"),hatKommentarBewertet , rs.getInt("VaterKommentar"), -1, 0);

                komms.add(k);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }

        return komms;
    }

    @Override
    public boolean schreibeKommentar(Kommentar kommentar)
    {
        Connection conMysql = getConnectionSQL();
        Connection conNeo4j = getConnectionNeo4j();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;

        // Mindestens eins muss angegeben sein
        if (kommentar.getKarteikartenID() == -1 && kommentar.getVaterID() == -1)
        {
            return false;
        }

        try
        {
            conMysql.setAutoCommit(false);

            String sql = "INSERT INTO kommentar(Inhalt, Benutzer, Karteikarte, Vaterkommentar) VALUES(?,?,?,?)";
            ps = conMysql.prepareStatement(sql);
            ps.setString(1, kommentar.getInhalt());
            ps.setInt(2, kommentar.getErsteller().getId());
            // Folgendes nur setzen, wenn nicht -1
            if (kommentar.getKarteikartenID() == -1)
                ps.setNull(3, java.sql.Types.INTEGER);
            else
                ps.setInt(3, kommentar.getKarteikartenID());
            if (kommentar.getVaterID() == -1)
                ps.setNull(4, java.sql.Types.INTEGER);
            else
                ps.setInt(4, kommentar.getVaterID());

            ps.executeUpdate();
            closeQuietly(ps);

            ps = conMysql.prepareStatement("SELECT LAST_INSERT_ID();");
            rs = ps.executeQuery();
            if (!rs.next())
                throw new SQLException();
            int kommentarId = rs.getInt(1);

            Karteikarte karteik = null;
            if (kommentar.getVaterID() != -1)
            {
                Kommentar vaterKommentar = leseKommentar(kommentar.getVaterID());
                karteik = leseKarteikarteWrapper(vaterKommentar.getKarteikartenID(),conMysql, conNeo4j);
            }
            else
                karteik = leseKarteikarteWrapper(kommentar.getKarteikartenID(), conMysql, conNeo4j);

            if (karteik == null)
                throw new SQLException();

            Veranstaltung veranst = leseVeranstaltungWrapper(karteik.getVeranstaltung(),conMysql);
            if(veranst == null)
                throw new SQLException();
            
            schreibeBenachrichtigungWrapper(new BenachrNeuerKommentar(kommentar.getErsteller().getId(), karteik,veranst, kommentarId),
                    conMysql);

            conMysql.commit();

        }
        catch (SQLException e)
        {
            erfolgreich = false;
            try
            {
                conMysql.rollback();
            }
            catch (SQLException e1)
            {
                e1.printStackTrace();
            }
            e.printStackTrace();

        }
        finally
        {
            try
            {
                conMysql.setAutoCommit(true);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
            closeQuietly(conNeo4j);
        }

        return erfolgreich;
    }

    @Override
    public boolean loescheKommentar(int kommentarID)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        boolean erfolgreich = true;

        try
        {
            String sql = "DELETE FROM kommentar WHERE ID = ?";
            ps = conMysql.prepareStatement(sql);

            ps.setInt(1, kommentarID);

            ps.executeUpdate();

            closeQuietly(ps);

        }
        catch (SQLException e)
        {
            erfolgreich = false;
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }

        return erfolgreich;
    }

    @Override
    public boolean bewerteKommentar(int kommentarID, int bewert, int benutzerId)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        boolean erfolgreich = true;

        try
        {
            conMysql.setAutoCommit(false);

            String sql = "INSERT INTO bewertung_kommentar(Bewertung, Benutzer, KommentarID) VALUES(?,?,?)";
            ps = conMysql.prepareStatement(sql);

            ps.setInt(1, bewert);
            ps.setInt(2, benutzerId);
            ps.setInt(3, kommentarID);

            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            try
            {
                conMysql.rollback();
            }
            catch (SQLException e1)
            {}
            erfolgreich = false;
            e.printStackTrace();

        }
        finally
        {
            try
            {
                conMysql.setAutoCommit(true);
            }
            catch (SQLException e)
            {}
            closeQuietly(ps);
            closeQuietly(conMysql);
        }

        return erfolgreich;
    }

    @Override
    public Boolean hatKommentarBewertet(int kommentarID, int benutzerId)
    {
        Connection conMysql = getConnectionSQL();

        try
        {
            Boolean hatKommentarBewertet = hatKommentarBewertetWrapper(kommentarID, benutzerId, conMysql);
            if(hatKommentarBewertet == null)
                return null;
            return hatKommentarBewertet; 
        }
        finally
        {
            closeQuietly(conMysql);
        }

    }
    
    private Boolean hatKommentarBewertetWrapper(int kommentarID, int benutzerId, Connection conMysql){
        PreparedStatement ps = null;
        boolean erfolgreich = true;

        try
        {
            String sql = "SELECT COUNT(*) AS anz FROM bewertung_kommentar WHERE Benutzer = ? AND KommentarID = ?";
            ps = conMysql.prepareStatement(sql);

            ps.setInt(1, benutzerId);
            ps.setInt(2, kommentarID);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("anz") > 0;
        }
        catch (SQLException e)
        {
            erfolgreich = false;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
        }

        return erfolgreich;
    }

    @Override
    public void zuVeranstaltungEinschreiben(int veranstaltung, int benutzer, String kennwort, boolean isAdmin) 
            throws SQLException, DbUniqueConstraintException, DbFalsePasswortException
    {
        Connection conMysql = getConnectionSQL();

        try{
            zuVeranstaltungEinschreibenWrapper(veranstaltung, benutzer, kennwort, conMysql, isAdmin);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        }
        catch (DbFalsePasswortException e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            closeQuietly(conMysql);
        }
    }
    
    private void zuVeranstaltungEinschreibenWrapper(int veranstaltung, int benutzer, String kennwort, Connection conMysql,
            boolean isAdmin) throws SQLException, DbUniqueConstraintException, DbFalsePasswortException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            ps = conMysql.prepareStatement("SELECT Kennwort FROM veranstaltung WHERE ID =?");
            ps.setInt(1, veranstaltung);
            rs = ps.executeQuery();
            if (rs.next())
            {
                if (rs.getString("Kennwort") != null && rs.getString("Kennwort").equals(kennwort) == false && !isAdmin)
                    throw new DbFalsePasswortException();
            }
            closeQuietly(ps);

            ps = conMysql.prepareStatement("INSERT INTO benutzer_veranstaltung_zuordnung (Benutzer, Veranstaltung)"
                    + "VALUES(?,?)");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        }
        catch (DbFalsePasswortException e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
        }
    }

    @Override
    public boolean vonVeranstaltungAbmelden(int veranstaltung, int benutzer)
    {
        Connection conMysql = getConnectionSQL();

        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try
        {
            ps = conMysql
                    .prepareStatement("DELETE FROM benutzer_veranstaltung_zuordnung WHERE Benutzer=? AND Veranstaltung=?");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            if (ps.executeUpdate() != 1)
                erfolgreich = false;
        }
        catch (SQLException e)
        {
            erfolgreich = false;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }
        return erfolgreich;
    }

    @Override
    public Notiz leseNotiz(int benutzer, int karteikID)
    {
        Connection conMysql = getConnectionSQL();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Notiz notiz = null;
        try
        {
            ps = conMysql.prepareStatement("SELECT ID, Inhalt, Benutzer, KarteikarteID FROM Notiz WHERE"
                    + " Benutzer = ? AND KarteikarteID = ?");
            ps.setInt(1, benutzer);
            ps.setInt(2, karteikID);

            rs = ps.executeQuery();

            if (rs.next())
            {
                notiz = new Notiz(rs.getInt("ID"), rs.getString("Inhalt"), rs.getInt("Benutzer"),
                        rs.getInt("KarteikarteID"));

            } else
                return null;
        }
        catch (SQLException e)
        {
            notiz = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return notiz;
    }

    @Override
    public boolean schreibeNotiz(Notiz notiz)
    {
        Connection conMysql = getConnectionSQL();

        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try
        {
            ps = conMysql.prepareStatement("INSERT INTO Notiz (Inhalt,Benutzer,KarteikarteID) VALUES(?,?,?)");
            ps.setString(1, notiz.getInhalt());
            ps.setInt(2, notiz.getErsteller());
            ps.setInt(3, notiz.getKarteikarte());

            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            erfolgreich = false;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }
        return erfolgreich;
    }

    @Override
    public boolean bearbeiteNotiz(Notiz notiz)
    {
        Connection conMysql = getConnectionSQL();

        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;;
        try
        {
            ps = conMysql.prepareStatement("UPDATE Notiz SET Inhalt = ? WHERE Benutzer = ? AND KarteikarteID = ?");
            ps.setString(1, notiz.getInhalt());
            ps.setInt(2, notiz.getErsteller());
            ps.setInt(3, notiz.getKarteikarte());

            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            erfolgreich = false;

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            closeQuietly(conMysql);
        }
        return erfolgreich;
    }

    @Override
    public boolean loescheNotiz(int notizID)
    {
        Connection conMysql = getConnectionSQL();
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try
        {
            ps = conMysql.prepareStatement("DELETE FROM Notiz WHERE ID=?");
            ps.setInt(1, notizID);
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            erfolgreich = false;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(conMysql);
        }
        return erfolgreich;
    }

    private void connectKk(int vonKK, int zuKK, BeziehungsTyp typ, Connection conNeo4j) throws SQLException
    {
        boolean verbindungAbbauen = false;
        if (conNeo4j == null)
        {
            conNeo4j = getConnectionNeo4j();
            verbindungAbbauen = true;
        }

        PreparedStatement ps = null;
        try
        {
            ps = conNeo4j.prepareStatement("MATCH (n),(m) " + "WHERE id(n) = {1} AND id(m) = {2} " + "CREATE (n)-[:"
                    + typ.toString().toLowerCase() + "]->(m)");
            ps.setInt(1, vonKK);
            ps.setInt(2, zuKK);
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            throw e;
        }
        finally
        {
            closeQuietly(ps);
            if (verbindungAbbauen)
                closeQuietly(conNeo4j);
//                conLockNeo4j.getValue().unlock();
        }

    }

    private void disconnectKk(int vonKK, int zuKK, Connection conNeo4j) throws SQLException
    {
        PreparedStatement ps = null;
        try
        {
            ps = conNeo4j.prepareStatement("MATCH (n)-[r]->(m) " + "WHERE id(n) = {1} AND id(m) = {2} " + "DELETE r");
            ps.setInt(1, vonKK);
            ps.setInt(2, zuKK);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw e;
        }
        finally
        {
            closeQuietly(ps);
        }
    }

}
