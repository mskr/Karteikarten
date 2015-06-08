/**
 * @author Matthias
 * 
 */
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

import com.sopra.team1723.data.*;
import com.sopra.team1723.data.Karteikarte.BeziehungsTyp;
import com.sopra.team1723.exceptions.*;

/**
 * Implementiert die Methoden des @ref IDatenbankmanager. Bietet eine
 * Schnittstelle zur Datenbank.
 */
public class Datenbankmanager implements IDatenbankmanager
{
    final int                                         UNIQUE_CONSTRAINT_ERROR = 1062;
    private static HashMap<Connection, ReentrantLock> connectionsNeo4j        = null;
    private static HashMap<Connection, ReentrantLock> connections             = null;
    private final static int                          AnzConnections          = 10;

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
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("org.neo4j.jdbc.Driver");
            connectionsNeo4j = new HashMap<Connection, ReentrantLock>();
            for (int i = 0; i < AnzConnections; ++i)
                connectionsNeo4j.put(
                        DriverManager.getConnection("jdbc:neo4j://localhost:7474/karteikarten", "neo4j", "hallo123"),
                        new ReentrantLock());

            connections = new HashMap<Connection, ReentrantLock>();
            for (int i = 0; i < AnzConnections; ++i)
                connections.put(DriverManager.getConnection("jdbc:mysql://localhost:3306/sopra", "root", ""),
                        new ReentrantLock());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static Entry<Connection, ReentrantLock> getConnectionNeo4j()
    {
        Iterator<Entry<Connection, ReentrantLock>> it = connectionsNeo4j.entrySet().iterator();
        Entry<Connection, ReentrantLock> defaultConnection = null;
        while (it.hasNext())
        {
            Entry<Connection, ReentrantLock> connection = it.next();
            if (!connection.getValue().isLocked())
            {
                connection.getValue().lock();
                return connection;
            }

            if (defaultConnection == null
                    || connection.getValue().getQueueLength() < defaultConnection.getValue().getQueueLength())
                defaultConnection = connection;
        }
        System.out.println("Keine Neo4j Connection frei! Warte in Queue");
        try
        {
            throw new Exception("No Connection free");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        defaultConnection.getValue().lock();
        return defaultConnection;
    }

    public static Entry<Connection, ReentrantLock> getConnection()
    {
        Iterator<Entry<Connection, ReentrantLock>> it = connections.entrySet().iterator();
        Entry<Connection, ReentrantLock> defaultConnection = null;
        while (it.hasNext())
        {
            Entry<Connection, ReentrantLock> connection = it.next();
            if (!connection.getValue().isLocked())
            {
                connection.getValue().lock();
                return connection;
            }

            if (defaultConnection == null
                    || connection.getValue().getQueueLength() < defaultConnection.getValue().getQueueLength())
                defaultConnection = connection;
        }
        System.out.println("Keine SQL Connection frei! Warte in Queue.");
        try
        {
            throw new Exception("No Connection free");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        defaultConnection.getValue().lock();
        return defaultConnection;
    }

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
            // ignored
        }

    }

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
            // ignored
        }
    }

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
            // ignored
        }
    }

    /**
     * 
     */

    @Override
    public Benutzer leseBenutzer(String eMail)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Benutzer benutzer = null;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT ID,Vorname,Nachname,Profilbild,Matrikelnummer,Studiengang,CryptedPW,Nutzerstatus,"
                            + "NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, Profilbild FROM benutzer WHERE eMail = ?");
            ps.setString(1, eMail);
            rs = ps.executeQuery();
            if (rs.next())
            {
                benutzer = new Benutzer(rs.getInt("ID"), eMail, rs.getString("Vorname"), rs.getString("Nachname"),
                        rs.getInt("Matrikelnummer"), rs.getString("Studiengang"), rs.getString("CryptedPW"),
                        Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), rs.getBoolean("NotifyVeranstAenderung"),
                        rs.getBoolean("NotifyKarteikartenAenderung"), NotifyKommentare.valueOf(rs
                                .getString("NotifyKommentare")), rs.getString("Profilbild"));

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
            conLock.getValue().unlock();
        }

        return benutzer;
    }

    @Override
    public Benutzer leseBenutzer(int id)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Benutzer benutzer = null;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT eMail,Vorname,Nachname,Profilbild,Matrikelnummer,Studiengang,CryptedPW,Nutzerstatus,"
                            + "NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, Profilbild FROM benutzer WHERE ID = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                benutzer = new Benutzer(id, rs.getString("eMail"), rs.getString("Vorname"), rs.getString("Nachname"),
                        rs.getInt("Matrikelnummer"), rs.getString("Studiengang"), rs.getString("CryptedPW"),
                        Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), rs.getBoolean("NotifyVeranstAenderung"),
                        rs.getBoolean("NotifyKarteikartenAenderung"), NotifyKommentare.valueOf(rs
                                .getString("NotifyKommentare")), rs.getString("Profilbild"));

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
            conLock.getValue().unlock();
        }

        return benutzer;
    }

    @Override
    public void schreibeBenutzer(Benutzer benutzer) throws DbUniqueConstraintException, SQLException
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        if (ServletController.DEBUGMODE)
            System.out.println("------");
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
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        }
        finally
        {
            closeQuietly(ps);
            conLock.getValue().unlock();
        }
    }

    @Override
    public void bearbeiteBenutzer(Benutzer benutzer) throws SQLException, DbUniqueConstraintException
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            ps = conMysql.prepareStatement("UPDATE benutzer SET eMail=?, Vorname=?,Nachname=?,"
                    + "NotifyKommentare=?, NotifyVeranstAenderung=?," + "NotifyKarteikartenAenderung=?  WHERE ID = ?");
            ps.setString(1, benutzer.geteMail());
            ps.setString(2, benutzer.getVorname());
            ps.setString(3, benutzer.getNachname());
            ps.setString(4, benutzer.getNotifyKommentare().name());
            ps.setBoolean(5, benutzer.isNotifyVeranstAenderung());
            ps.setBoolean(6, benutzer.isNotifyKarteikartenAenderung());
            ps.setInt(7, benutzer.getId());
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
            conLock.getValue().unlock();
        }
    }

    @Override
    public void bearbeiteBenutzerAdmin(Benutzer benutzer, Benutzer aktuellerBenutzer) throws SQLException,
            DbUniqueConstraintException
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            conMysql.setAutoCommit(false);
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

            if (benutzer.getId() != aktuellerBenutzer.getId())
                schreibeBenachrichtigung(new BenachrProfilGeaendert(new GregorianCalendar(), benutzer.getId(),
                        aktuellerBenutzer), conMysql);

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
            conLock.getValue().unlock();
        }
    }

    @Override
    public boolean loescheBenutzer(int benutzerId)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try
        {
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
            conLock.getValue().unlock();
        }
        return erfolgreich;
    }

    @Override
    public void pruefeLogin(String eMail, String passwort) throws SQLException, DbFalseLoginDataException
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            if (ServletController.DEBUGMODE)
                System.out.println("DB prueft: email=" + eMail + ", passwort=" + passwort);
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
            conLock.getValue().unlock();
        }

    }

    @Override
    public List<String> leseStudiengaenge()
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        ArrayList<String> studiengaenge = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
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
            conLock.getValue().unlock();
        }
        return studiengaenge;
    }

    @Override
    public Map<Integer, String> leseSemester()
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer, String> semester = null;
        try
        {
            semester = new HashMap<Integer, String>();
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
            conLock.getValue().unlock();
        }
        return semester;
    }

    @Override
    public boolean passwortAendern(String eMail, String neuesPasswort)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try
        {
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
            conLock.getValue().unlock();
        }
        return erfolgreich;
    }

    @Override
    public boolean aendereProfilBild(int benutzerId, String dateiName)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try
        {
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
            conLock.getValue().unlock();
        }
        return erfolgreich;
    }

    @Override
    public Veranstaltung leseVeranstaltung(int id)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Veranstaltung veranstaltung = null;
        try
        {
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
                veranstaltung = new Veranstaltung(id, rs.getString("Titel"), rs.getString("Beschreibung"),
                        rs.getString("Semester"), rs.getString("Kennwort"), rs.getBoolean("BewertungenErlaubt"),
                        rs.getBoolean("ModeratorKarteikartenBearbeiten"), leseBenutzer(rs.getInt("Ersteller")),
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
            conLock.getValue().unlock();
        }

        return veranstaltung;
    }

    @Override
    public List<Veranstaltung> leseVeranstaltungen(String semester, String studiengang)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Veranstaltung> veranstaltungen = null;
        try
        {
            veranstaltungen = new ArrayList<Veranstaltung>();
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
                veranstaltungen.add(new Veranstaltung(rs.getInt("v.ID"), rs.getString("Titel"), rs
                        .getString("Beschreibung"), rs.getString("Semester"), rs.getString("Kennwort"), rs
                        .getBoolean("BewertungenErlaubt"), rs.getBoolean("ModeratorKarteikartenBearbeiten"),
                        leseBenutzer(rs.getInt("Ersteller")), rs.getBoolean("KommentareErlaubt"), rs
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
            conLock.getValue().unlock();
        }
        return veranstaltungen;

    }

    @Override
    public List<Veranstaltung> leseVeranstaltungen(int benutzer)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Veranstaltung> veranstaltungen = null;
        try
        {
            veranstaltungen = new ArrayList<Veranstaltung>();
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
                veranstaltungen.add(new Veranstaltung(rs.getInt("ID"), rs.getString("Titel"), rs
                        .getString("Beschreibung"), rs.getString("Semester"), rs.getString("Kennwort"), rs
                        .getBoolean("BewertungenErlaubt"), rs.getBoolean("ModeratorKarteikartenBearbeiten"),
                        leseBenutzer(rs.getInt("Ersteller")), rs.getBoolean("KommentareErlaubt"), rs
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
            conLock.getValue().unlock();
        }

        return veranstaltungen;
    }

    @Override
    public List<Benutzer> leseModeratoren(int veranstaltung)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Benutzer> moderatoren = null;
        try
        {
            moderatoren = new ArrayList<Benutzer>();
            ps = conMysql
                    .prepareStatement("SELECT b.ID, eMail, Vorname, Nachname, Profilbild, Matrikelnummer, Studiengang, "
                            + "CryptedPW, Nutzerstatus, NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, "
                            + "Profilbild FROM benutzer AS b JOIN moderator AS m ON b.ID = m.Benutzer AND m.Veranstaltung =?");
            ps.setInt(1, veranstaltung);
            rs = ps.executeQuery();
            while (rs.next())
            {
                moderatoren.add(new Benutzer(rs.getInt("b.ID"), rs.getString("eMail"), rs.getString("Vorname"), rs
                        .getString("Nachname"), rs.getInt("Matrikelnummer"), rs.getString("Studiengang"), rs
                        .getString("CryptedPW"), Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), rs
                        .getBoolean("NotifyVeranstAenderung"), rs.getBoolean("NotifyKarteikartenAenderung"),
                        NotifyKommentare.valueOf(rs.getString("NotifyKommentare")), rs.getString("Profilbild")));
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
            conLock.getValue().unlock();
        }

        return moderatoren;
    }

    @Override
    public List<String> leseStudiengaenge(int veranstaltung)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> studiengaenge = null;
        try
        {
            studiengaenge = new ArrayList<String>();
            ps = conMysql.prepareStatement("SELECT Studiengang FROM veranstaltung_studiengang_zuordnung "
                    + " WHERE Veranstaltung =?");
            ps.setInt(1, veranstaltung);
            rs = ps.executeQuery();
            while (rs.next())
            {
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
            conLock.getValue().unlock();
        }

        return studiengaenge;
    }

    public Boolean istModeratorWrapper(int benutzer, int veranstaltung, Connection conMysql){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Boolean istModerator = false;
        try
        {
            ps = conMysql.prepareStatement("SELECT ID FROM moderator WHERE Benutzer =? AND Veranstaltung =?");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            rs = ps.executeQuery();
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
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

        try
        {
          return istModeratorWrapper(benutzer, veranstaltung, conMysql);
        }
        finally
        {
            conLock.getValue().unlock();
        }
    }

    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

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
        alleErgebnisse.putAll(durchsucheDatenbankVeranstaltung(suchmuster));
        alleErgebnisse.putAll(durchsucheDatenbankBenutzer(suchmuster));

        LinkedHashMap<IjsonObject, Integer> sortierteErgebnisse = sortByValue(alleErgebnisse);
        LinkedHashMap<IjsonObject, Integer> teilErgebnisse = new LinkedHashMap<IjsonObject, Integer>();
        Iterator<Entry<IjsonObject, Integer>> it = sortierteErgebnisse.entrySet().iterator();
        int i = 0;
        Entry<IjsonObject, Integer> entry;
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
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<IjsonObject, Integer> ergebnisse = null;
        try
        {
            ergebnisse = new HashMap<IjsonObject, Integer>();
            ps = conMysql
                    .prepareStatement("SELECT ID, levenshtein(?,Titel) AS lev FROM Veranstaltung ORDER BY lev LIMIT 5");
            ps.setString(1, suchmuster);
            rs = ps.executeQuery();
            while (rs.next())
            {
                ergebnisse.put(leseVeranstaltung(rs.getInt("ID")), rs.getInt("lev"));
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
            conLock.getValue().unlock();
        }
        return ergebnisse;
    }

    @Override
    public Map<IjsonObject, Integer> durchsucheDatenbankBenutzer(String suchmuster)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<IjsonObject, Integer> ergebnisse = null;
        try
        {
            ergebnisse = new HashMap<IjsonObject, Integer>();
            ps = conMysql.prepareStatement("SELECT * , levenshtein(?,CONCAT(Vorname,' ',Nachname)) AS lev"
                    + " FROM Benutzer ORDER BY lev LIMIT 5 ");
            ps.setString(1, suchmuster);
            rs = ps.executeQuery();
            while (rs.next())
            {
                ergebnisse.put(leseBenutzer(rs.getInt("ID")), rs.getInt("lev"));
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
            conLock.getValue().unlock();
        }
        return ergebnisse;
    }

    @Override
    public Map<IjsonObject, Integer> durchsucheDatenbankStudiengang(String suchmuster)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<IjsonObject, Integer> ergebnisse = null;
        try
        {
            ergebnisse = new HashMap<IjsonObject, Integer>();
            ps = conMysql
                    .prepareStatement("SELECT Name, levenshtein(?,Name) AS lev FROM Studiengang ORDER BY lev LIMIT 5");
            ps.setString(1, suchmuster);
            // ps.setString(2, suchmuster);
            rs = ps.executeQuery();
            while (rs.next())
            {
                if (ServletController.DEBUGMODE)
                    System.out.println(rs.getString("Name") + " " + suchmuster + " " + rs.getInt("lev"));
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
            conLock.getValue().unlock();
        }
        return ergebnisse;
    }

    @Override
    public boolean angemeldet(int benutzer, int veranstaltung) throws SQLException
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean angemeldet = false;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT ID FROM benutzer_veranstaltung_zuordnung WHERE Benutzer =? AND Veranstaltung =?");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            rs = ps.executeQuery();
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
            conLock.getValue().unlock();
        }
        return angemeldet;
    }

    @Override
    public int schreibeVeranstaltung(Veranstaltung veranst, String[] studiengaenge, int[] moderatorenIds)
            throws SQLException, DbUniqueConstraintException, DbFalsePasswortException
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;

        int veranstId = -1;
        try
        {
            conMysql.setAutoCommit(false);
            conNeo4j.setAutoCommit(false);

            ps = conNeo4j.prepareStatement("CREATE (n) RETURN id(n) AS ID");
            rs = ps.executeQuery();
            int insertedKkId;
            if (rs.next())
                insertedKkId = rs.getInt("ID");
            else
                throw new SQLException();

            closeQuietly(ps);
            closeQuietly(rs);

            ps = conMysql
                    .prepareStatement("INSERT INTO veranstaltung (Titel, Beschreibung, Semester, Kennwort, KommentareErlaubt,"
                            + "BewertungenErlaubt, ModeratorKarteikartenBearbeiten, Ersteller, ErsteKarteikarte) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setString(1, veranst.getTitel());
            ps.setString(2, veranst.getBeschreibung());
            ps.setString(3, veranst.getSemester());
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
            ps = conMysql.prepareStatement("SELECT LAST_INSERT_ID();");
            rs = ps.executeQuery();
            if (!rs.next())
                throw new SQLException();
            veranstId = rs.getInt(1);
            veranst.setId(veranstId);
            closeQuietly(ps);
            closeQuietly(rs);

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
                ps = conMysql
                        .prepareStatement("INSERT INTO veranstaltung_studiengang_zuordnung (Veranstaltung, Studiengang) VALUES(?,?)");
                for (String stg : studiengaenge)
                {
                    ps.setInt(1, veranstId);
                    ps.setString(2, stg);
                    ps.addBatch();
                }
                ps.executeBatch();
                closeQuietly(ps);
            }

            if (moderatorenIds != null)
            {
                ps = conMysql.prepareStatement("INSERT INTO moderator (Benutzer, Veranstaltung) VALUES(?,?)");
                for (int i = 0; i < moderatorenIds.length; ++i)
                {
                    ps.setInt(1, moderatorenIds[i]);
                    ps.setInt(2, veranstId);
                    ps.addBatch();

                    zuVeranstaltungEinschreiben(veranstId, moderatorenIds[i], veranst.getZugangspasswort(), conMysql,
                            false);
                    schreibeBenachrichtigung(new BenachrEinlModerator(moderatorenIds[i], veranst), conMysql);
                }
                ps.executeBatch();

            }

            zuVeranstaltungEinschreiben(veranstId, veranst.getErsteller().getId(), veranst.getZugangspasswort(),
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
            if (UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        }
        catch (DbFalsePasswortException e)
        {
            e.printStackTrace();
            conMysql.rollback();
            conNeo4j.rollback();
            throw e;
        }
        finally
        {
            conMysql.setAutoCommit(true);
            conNeo4j.setAutoCommit(true);
            closeQuietly(ps);
            closeQuietly(rs);
            conLock.getValue().unlock();
        }

        return veranstId;
    }

    @Override
    public void bearbeiteVeranstaltung(Veranstaltung veranst, String[] studiengaenge, int[] moderatorenIds)
            throws SQLException, DbUniqueConstraintException, DbFalsePasswortException
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            conMysql.setAutoCommit(false);
            ps = conMysql.prepareStatement("UPDATE veranstaltung SET Titel=?, Beschreibung=?, Semester=?,"
                    + "Kennwort=?, KommentareErlaubt=?, BewertungenErlaubt=?, ModeratorKarteikartenBearbeiten=?,"
                    + " Ersteller=?, ErsteKarteikarte=? WHERE ID = ?");
            ps.setString(1, veranst.getTitel());
            ps.setString(2, veranst.getBeschreibung());
            ps.setString(3, veranst.getSemester());
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

            ps = conMysql.prepareStatement("DELETE FROM veranstaltung_studiengang_zuordnung WHERE Veranstaltung = ?");
            ps.setInt(1, veranst.getId());
            ps.executeUpdate();
            closeQuietly(ps);

            if (studiengaenge != null)
            {
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
                    Boolean istModerator = istModeratorWrapper(moderatorenIds[i], veranst.getId(), conMysql);
                    if(istModerator == null)
                        throw new SQLException();
                    if (!istModerator)
                        schreibeBenachrichtigung(new BenachrEinlModerator(moderatorenIds[i], veranst), conMysql);
                }

                ps = conMysql.prepareStatement("DELETE FROM moderator WHERE Veranstaltung = ?");
                ps.setInt(1, veranst.getId());
                ps.executeUpdate();
                closeQuietly(ps);

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

                ps = conMysql.prepareStatement("INSERT INTO moderator (Benutzer, Veranstaltung) VALUES(?,?)");
                for (int i = 0; i < moderatorenIds.length; ++i)
                {
                    ps.setInt(1, moderatorenIds[i]);
                    ps.setInt(2, veranst.getId());
                    ps.addBatch();

                    zuVeranstaltungEinschreiben(veranst.getId(), moderatorenIds[i], veranst.getZugangspasswort(),
                            conMysql, false);

                }
                ps.executeBatch();

            }

            BenachrVeranstAenderung bva = new BenachrVeranstAenderung(veranst);
            schreibeBenachrichtigung(bva, conMysql);

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
        catch (DbFalsePasswortException e)
        {
            e.printStackTrace();
            conMysql.rollback();
            throw e;
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
            conLock.getValue().unlock();
        }
    }

    @Override
    public boolean loescheVeranstaltung(int veranstId)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        try
        {
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
            conLock.getValue().unlock();
        }
        return false;
    }

    @Override
    public List<Benachrichtigung> leseBenachrichtigungen(int benutzer, int limit)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Benachrichtigung> aktBenachrichtigungen = null;
        try
        {
            aktBenachrichtigungen = new ArrayList<Benachrichtigung>();

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
            while (rs.next())
            {
                tabelle = rs.getString("Tabelle");
                id = rs.getInt("ID");
                if (tabelle.equals("benachrichtigung_einladung_moderator"))
                    aktBenachrichtigungen.add(leseBenachrEinlModerator(id));
                else if (tabelle.equals("benachrichtigung_karteikartenaenderung"))
                    aktBenachrichtigungen.add(leseBenachrKarteikAenderung(id));
                else if (tabelle.equals("benachrichtigung_neuer_kommentar"))
                    aktBenachrichtigungen.add(leseBenachrNeuerKommentar(id));
                else if (tabelle.equals("benachrichtigung_profil_geaendert"))
                    aktBenachrichtigungen.add(leseBenachrProfilGeaendert(id));
                else
                    aktBenachrichtigungen.add(leseBenachrVeranstAenderung(id));
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
            conLock.getValue().unlock();
        }
        return aktBenachrichtigungen;
    }

    @Override
    public BenachrEinlModerator leseBenachrEinlModerator(int id)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrEinlModerator benachrichtigung = null;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Veranstaltung, Gelesen, Angenommen"
                            + " FROM benachrichtigung_einladung_moderator AS bem JOIN benachrichtigung AS b ON bem.Benachrichtigung"
                            + "= b.ID WHERE bem.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                rs.getTimestamp("Erstelldatum", cal);
                benachrichtigung = new BenachrEinlModerator(rs.getInt("Benachrichtigung"), rs.getString("Inhalt"), cal,
                        rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), leseVeranstaltung(rs.getInt("Veranstaltung")),
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
            conLock.getValue().unlock();
        }

        return benachrichtigung;
    }

    @Override
    public BenachrKarteikAenderung leseBenachrKarteikAenderung(int id)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrKarteikAenderung benachrichtigung = null;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Karteikarte, Gelesen"
                            + " FROM benachrichtigung_karteikartenaenderung AS bk JOIN benachrichtigung AS b ON bk.Benachrichtigung"
                            + "= b.ID WHERE bk.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                rs.getTimestamp("Erstelldatum", cal);
                Karteikarte kk = leseKarteikarte(rs.getInt("Karteikarte"));
                if (kk == null)
                    return null;

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
            conLock.getValue().unlock();
        }

        return benachrichtigung;
    }

    @Override
    public BenachrNeuerKommentar leseBenachrNeuerKommentar(int id)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrNeuerKommentar benachrichtigung = null;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Kommentar, Gelesen"
                            + " FROM benachrichtigung_neuer_kommentar AS bnk JOIN benachrichtigung AS b ON bnk.Benachrichtigung"
                            + "= b.ID WHERE bnk.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Kommentar kommentar = leseKommentar(rs.getInt("Kommentar"));
                Kommentar vaterKommentar = kommentar;
                if (kommentar.getVaterID() != -1)
                    vaterKommentar = leseKommentar(kommentar.getVaterID());
                if (vaterKommentar == null)
                    return null;
                Karteikarte karteik = leseKarteikarte(vaterKommentar.getKarteikartenID());

                Calendar cal = new GregorianCalendar();
                rs.getTimestamp("Erstelldatum", cal);
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
            conLock.getValue().unlock();
        }

        return benachrichtigung;
    }

    @Override
    public BenachrProfilGeaendert leseBenachrProfilGeaendert(int id)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrProfilGeaendert benachrichtigung = null;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Admin, Gelesen"
                            + " FROM benachrichtigung_profil_geaendert AS bpg JOIN benachrichtigung AS b ON bpg.Benachrichtigung"
                            + "= b.ID WHERE bpg.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                rs.getTimestamp("Erstelldatum", cal);
                benachrichtigung = new BenachrProfilGeaendert(rs.getInt("Benachrichtigung"), rs.getString("Inhalt"),
                        cal, rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), leseBenutzer(rs.getInt("Admin")));
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
            conLock.getValue().unlock();
        }

        return benachrichtigung;
    }

    @Override
    public BenachrVeranstAenderung leseBenachrVeranstAenderung(int id)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrVeranstAenderung benachrichtigung = null;
        try
        {
            ps = conMysql
                    .prepareStatement("SELECT Benachrichtigung, Inhalt, Erstelldatum, Benutzer, Veranstaltung, Gelesen"
                            + " FROM benachrichtigung_veranstaltungsaenderung AS bv JOIN benachrichtigung AS b ON bv.Benachrichtigung"
                            + "= b.ID WHERE bv.ID =?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next())
            {
                Calendar cal = new GregorianCalendar();
                rs.getTimestamp("Erstelldatum", cal);
                benachrichtigung = new BenachrVeranstAenderung(rs.getInt("Benachrichtigung"), rs.getString("Inhalt"),
                        cal, rs.getInt("Benutzer"), rs.getBoolean("Gelesen"),
                        leseVeranstaltung(rs.getInt("Veranstaltung")));
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
            conLock.getValue().unlock();
        }

        return benachrichtigung;
    }

    @Override
    public void schreibeBenachrichtigung(Benachrichtigung benachrichtigung, Connection conMysql) throws SQLException
    {
        Entry<Connection, ReentrantLock> conLock = null;
        boolean transaktion = true;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            if (conMysql == null)
            {
                conLock = getConnection();
                conMysql = conLock.getKey();
                transaktion = false;
                conMysql.setAutoCommit(false);
            }
            ps = conMysql.prepareStatement("INSERT INTO benachrichtigung (Inhalt) VALUES (?); ");
            ps.setString(1, benachrichtigung.getInhalt());
            ps.executeUpdate();
            closeQuietly(ps);

            ps = conMysql.prepareStatement("SELECT LAST_INSERT_ID();");
            rs = ps.executeQuery();
            if (!rs.next())
                throw new SQLException();
            int id = rs.getInt(1);
            closeQuietly(ps);

            if (benachrichtigung instanceof BenachrEinlModerator)
            {
                BenachrEinlModerator bem = (BenachrEinlModerator) benachrichtigung;
                ps = conMysql
                        .prepareStatement("INSERT INTO benachrichtigung_einladung_moderator (Benachrichtigung, Benutzer,"
                                + "Veranstaltung) VALUES (?,?,?)");
                ps.setInt(1, id);
                ps.setInt(2, bem.getBenutzer());
                ps.setInt(3, bem.getVeranstaltung().getId());

                // todo karteikarten und kommentar Benachrichtigungen müssen
                // noch angepasst werden
            }
            else if (benachrichtigung instanceof BenachrKarteikAenderung)
            {
                BenachrKarteikAenderung bk = (BenachrKarteikAenderung) benachrichtigung;
                ps = conMysql
                        .prepareStatement("INSERT INTO benachrichtigung_karteikartenaenderung (Benachrichtigung, Benutzer,"
                                + "Karteikarte) VALUES (?,?,?);");
                ps.setInt(1, id);
                ps.setInt(2, bk.getId());
                ps.setInt(3, bk.getKarteikarte().getId());

            }
            else if (benachrichtigung instanceof BenachrNeuerKommentar)
            {
                BenachrNeuerKommentar bnk = (BenachrNeuerKommentar) benachrichtigung;
                Kommentar kommentar = leseKommentarIntern(bnk.getKommentarId(), conMysql);
                if (kommentar == null)
                    throw new SQLException();
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
                BenachrVeranstAenderung bv = (BenachrVeranstAenderung) benachrichtigung;
                ps = conMysql.prepareStatement("INSERT INTO benachrichtigung_veranstaltungsaenderung"
                        + " (Benachrichtigung, Veranstaltung, Benutzer)"
                        + " SELECT ?,?,Benutzer FROM benutzer_veranstaltung_zuordnung AS bvz "
                        + "JOIN Benutzer AS b ON bvz.Benutzer = b.ID WHERE bvz.Veranstaltung = ? "
                        + "AND b.NotifyVeranstAenderung = true");
                ps.setInt(1, id);
                ps.setInt(2, bv.getVeranstaltung().getId());
                ps.setInt(3, bv.getVeranstaltung().getId());
            }
            // ACHTUNG es kann auch sein, dass keiner Eingeschrieben ist und
            // niemand die benachrichtung erhält!
            // if(ps.executeUpdate() == 0)
            // erfolgreich = false;
            ps.executeUpdate();

            if (!transaktion)
                conMysql.commit();

        }
        catch (SQLException e)
        {
            conMysql.rollback();

            e.printStackTrace();
            throw e;
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);

            if (!transaktion)
            {
                conMysql.setAutoCommit(true);
                conLock.getValue().unlock();
            }
        }

    }

    public boolean markiereBenAlsGelesen(int benID, int benutzerID)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
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

        }
        finally
        {
            closeQuietly(ps);
            conLock.getValue().unlock();
        }

        return true;
    }

    @Override
    public boolean einladungModeratorAnnehmen(int benachrichtigung, int benutzer)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try
        {
            String sql = "UPDATE benachrichtigung_einladung_moderator " + "SET Angenommen = 1 "
                    + "WHERE Benachrichtigung = ? AND Benutzer = ? ";
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, benachrichtigung);
            ps.setInt(2, benutzer);
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
            conLock.getValue().unlock();
        }

        return erfolgreich;
    }

    @Override
    public boolean einladungModeratorAblehnen(int benachrichtigung, int benutzer)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try
        {
            conMysql.setAutoCommit(false);
            String sql = "UPDATE benachrichtigung_einladung_moderator " + "SET Angenommen = 0 "
                    + "WHERE Benachrichtigung = ? AND Benutzer = ? ";
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, benachrichtigung);
            ps.setInt(2, benutzer);
            ps.executeUpdate();

            closeQuietly(ps);

            ps = conMysql
                    .prepareStatement("DELETE FROM moderator WHERE Veranstaltung = ANY (SELECT Veranstaltung FROM"
                            + " benachrichtigung_einladung_moderator WHERE Benachrichtigung = ? AND Benutzer = ?) AND Benutzer = ?");
            ps.setInt(1, benachrichtigung);
            ps.setInt(2, benutzer);
            ps.setInt(3, benutzer);
            ps.executeUpdate();

            conMysql.commit();

        }
        catch (SQLException e)
        {
            erfolgreich = false;
            e.printStackTrace();
            try
            {
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
                conMysql.setAutoCommit(true);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            closeQuietly(ps);
            conLock.getValue().unlock();
        }

        return erfolgreich;

    }

    @Override
    public Karteikarte leseKarteikarte(int karteikID)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;

        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        Karteikarte karteikarte = null;
        try
        {

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
                ps2 = conMysql.prepareStatement("SELECT Titel FROM Karteikarte WHERE ID = ?");
                ps2.setInt(1, rs.getInt("zielID"));
                rs2 = ps2.executeQuery();
                if (!rs2.next())
                    return null;
                verweise.add(new Tripel<Karteikarte.BeziehungsTyp, Integer, String>(BeziehungsTyp.valueOf(rs.getString(
                        "Typ").toUpperCase()), rs.getInt("zielID"), rs2.getString("Titel")));
            }

            closeQuietly(ps);
            closeQuietly(rs);

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
            conLock.getValue().unlock();
            conLockNeo4j.getValue().unlock();
        }
        return karteikarte;
    }

    @Override
    public Map<Integer, Tupel<Integer, String>> leseKindKarteikarten(int vaterKarteikID)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psMysql = null;
        ResultSet rsMysql = null;
        HashMap<Integer, Tupel<Integer, String>> kindKarteikarten = null;
        try
        {
            kindKarteikarten = new HashMap<Integer, Tupel<Integer, String>>();
            ps = conNeo4j.prepareStatement("MATCH (n)-[:h_child]->()-[:h_brother*0..]-(m)" + " WHERE id(n) = {1}"
                    + " return id(m) AS ID");
            ps.setInt(1, vaterKarteikID);

            rs = ps.executeQuery();

            int i = 0;
            while (rs.next())
            {
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
            conLock.getValue().unlock();
            conLockNeo4j.getValue().unlock();
        }

        return kindKarteikarten;
    }

    @Override
    public Map<Integer, Karteikarte> leseNachfolger(int karteikarte, int anzNachfolger)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<Integer, Karteikarte> kindKarteikarten = new HashMap<Integer, Karteikarte>();
        try
        {
            Integer aktuelleKarteikarte = karteikarte;
            int i = 0;
            while (anzNachfolger > 0)
            {
                ps = conNeo4j.prepareStatement("MATCH (n)-[:h_child*1.." + anzNachfolger + "]->(m)"
                        + " WHERE id(n) = {1}" + " return id(m) AS ID");
                ps.setInt(1, aktuelleKarteikarte);

                rs = ps.executeQuery();

                while (rs.next())
                {
                    aktuelleKarteikarte = rs.getInt("ID");
                    Karteikarte aktKarteik = leseKarteikarte(aktuelleKarteikarte);
                    if (aktKarteik == null)
                        return null;
                    kindKarteikarten.put(i, aktKarteik);
                    --anzNachfolger;
                    ++i;
                }

                closeQuietly(ps);
                closeQuietly(rs);

                Integer bruderKarteik;
                do
                {
                    bruderKarteik = gibBruder(aktuelleKarteikarte);

                    if (bruderKarteik == null)
                        return null;
                    else if (bruderKarteik == -1)
                    {
                        ps = conNeo4j.prepareStatement("MATCH p=((n)<-[:h_brother*0..]-(m))" + " WHERE id(n) = {1}"
                                + " return id(m) AS ID ORDER BY length(p) DESC LIMIT 1");
                        ps.setInt(1, aktuelleKarteikarte);

                        rs = ps.executeQuery();

                        while (rs.next())
                        {
                            aktuelleKarteikarte = rs.getInt("ID");
                        }
                        closeQuietly(ps);
                        closeQuietly(rs);

                        Integer vaterKarteikarte = gibVater(aktuelleKarteikarte);
                        if (vaterKarteikarte == null)
                            return null;
                        else if (vaterKarteikarte == -1)
                            return kindKarteikarten;
                        else
                            aktuelleKarteikarte = vaterKarteikarte;
                    }
                }
                while (bruderKarteik == -1);

                Karteikarte bruderKk = leseKarteikarte(bruderKarteik);
                if (bruderKk == null)
                    return null;
                kindKarteikarten.put(i, bruderKk);
                aktuelleKarteikarte = bruderKarteik;
                ++i;
                --anzNachfolger;

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
            conLockNeo4j.getValue().unlock();
        }

        return kindKarteikarten;
    }

    @Override
    public Map<Integer, Karteikarte> leseVorgaenger(int karteikarte, int anzVorgänger)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<Integer, Karteikarte> kindKarteikarten = new HashMap<Integer, Karteikarte>();
        try
        {
            Integer aktuelleKarteikarte = karteikarte;
            int i = 0;
            while (anzVorgänger > 0)
            {
                ps = conNeo4j.prepareStatement("MATCH (n)<-[:h_brother]-(m)" + " WHERE id(n) = {1}"
                        + " return id(m) AS ID");
                ps.setInt(1, aktuelleKarteikarte);

                rs = ps.executeQuery();

                closeQuietly(ps);
                closeQuietly(rs);

                if (!rs.next())
                {
                    aktuelleKarteikarte = gibVater(aktuelleKarteikarte);
                    if (aktuelleKarteikarte == null)
                        return null;
                    else if (aktuelleKarteikarte == -1)
                        return kindKarteikarten;

                    Karteikarte aktKarteik = leseKarteikarte(aktuelleKarteikarte);
                    if (aktKarteik == null)
                        return null;
                    kindKarteikarten.put(i, aktKarteik);
                    --anzVorgänger;
                    ++i;
                    continue;
                }
                else
                {
                    aktuelleKarteikarte = rs.getInt("ID");
                }

                boolean abbruch = false;
                do
                {

                    ps = conNeo4j.prepareStatement("MATCH (n)-[:h_child]->(m)" + " WHERE id(n) = {1}"
                            + " return id(m) AS ID");

                    ps.setInt(1, aktuelleKarteikarte);
                    rs = ps.executeQuery();

                    if (!rs.next())
                    {
                        abbruch = true;

                        Karteikarte aktKarteik = leseKarteikarte(aktuelleKarteikarte);
                        if (aktKarteik == null)
                            return null;
                        kindKarteikarten.put(i, aktKarteik);
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
            kindKarteikarten = null;
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            conLockNeo4j.getValue().unlock();
        }

        return kindKarteikarten;
    }

    private Integer gibBruder(int karteikarte)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer karteik;
        try
        {
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
            conLockNeo4j.getValue().unlock();
        }

        return karteik;
    }

    private Integer gibAelterenBruder(int karteikarte)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer karteik;
        try
        {
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
            conLockNeo4j.getValue().unlock();
        }

        return karteik;
    }

    private Integer gibVater(int karteikarte)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer karteik;
        try
        {
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
            conLockNeo4j.getValue().unlock();
        }

        return karteik;
    }

    private Integer gibKind(int karteikarte)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer karteik;
        try
        {
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
            conLockNeo4j.getValue().unlock();
        }

        return karteik;
    }

    @Override
    public int schreibeKarteikarte(Karteikarte karteik, int vaterKK, int ueberliegendeBruderKK) throws SQLException
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        int insertedId = -1;

        try
        {
            conNeo4j.setAutoCommit(false);
            conMysql.setAutoCommit(false);

            ps = conNeo4j.prepareStatement("CREATE(n) RETURN id(n) AS Id");

            rs = ps.executeQuery();
            if (!rs.next())
                return insertedId;
            insertedId = rs.getInt("Id");
            karteik.setId(insertedId);

            closeQuietly(ps);

            if (ueberliegendeBruderKK != -1)
            {
                Integer unterliegendeBruderKK = gibBruder(ueberliegendeBruderKK);
                if (unterliegendeBruderKK == null)
                    throw new SQLException();
                else if (unterliegendeBruderKK != -1)
                {
                    disconnectKk(ueberliegendeBruderKK, unterliegendeBruderKK, conNeo4j);
                    connectKk(insertedId, unterliegendeBruderKK, BeziehungsTyp.H_BROTHER, conNeo4j);
                }

                connectKk(ueberliegendeBruderKK, insertedId, BeziehungsTyp.H_BROTHER, conNeo4j);

            }
            else if (vaterKK != -1)
            {
                Integer kindKarteikarte = gibKind(vaterKK);
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

            for (int i = 0; i < karteik.getVerweise().size(); ++i)
            {
                connectKk(karteik.getId(), karteik.getVerweise().get(i).y, karteik.getVerweise().get(i).x, conNeo4j);
            }

            closeQuietly(ps);

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
            conLockNeo4j.getValue().unlock();
            conLock.getValue().unlock();
        }

        return insertedId;
    }

    @Override
    public boolean bearbeiteKarteikarte(Karteikarte karteik)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();
        PreparedStatement ps = null;

        boolean erfolgreich = true;

        try
        {
            conMysql.setAutoCommit(false);
            conNeo4j.setAutoCommit(false);

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

            ps = conNeo4j
                    .prepareStatement("match n-[r:" + BeziehungsTyp.V_VORAUSSETZUNG.toString().toLowerCase() + "|"
                            + BeziehungsTyp.V_UEBUNG.toString().toLowerCase() + "|" + ""
                            + BeziehungsTyp.V_ZUSATZINFO.toString().toLowerCase() + "|"
                            + BeziehungsTyp.V_SONSTIGES.toString().toLowerCase() + "]->() " + "where id(n) = {1} "
                            + "delete r");
            ps.setInt(1, karteik.getId());
            ps.executeUpdate();

            for (Tripel<BeziehungsTyp, Integer, String> verweis : karteik.getVerweise())
            {
                connectKk(karteik.getId(), verweis.y, verweis.x, conNeo4j);
            }

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
            conLock.getValue().unlock();
            conLockNeo4j.getValue().unlock();
        }

        return erfolgreich;
    }

    @Override
    public boolean loescheKarteikarte(int karteikID)
    {
        Entry<Connection, ReentrantLock> conLockNeo4j = getConnectionNeo4j();
        Connection conNeo4j = conLockNeo4j.getKey();

        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psMysql = null;

        boolean erfolgreich = true;

        try
        {
            conNeo4j.setAutoCommit(false);
            conMysql.setAutoCommit(false);

            ps = conNeo4j.prepareStatement("match p=((n)-[:h_child]->(m)-[:h_child|h_brother*0..]->(o))"
                    + " where id(n) = {1}" + " with o" + " match (o)-[r]-()" + " delete o,r "
                    + "return distinct(id(o)) AS ID_o");
            ps.setInt(1, karteikID);
            rs = ps.executeQuery();

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

            Integer bruder = gibBruder(karteikID);
            if (bruder == null)
                throw new SQLException();

            if (bruder != -1)
            {
                Integer aeltererBruder = gibAelterenBruder(karteikID);

                if (aeltererBruder == null)
                    throw new SQLException();
                else if (aeltererBruder != -1)
                    connectKk(aeltererBruder, bruder, BeziehungsTyp.H_BROTHER, conNeo4j);
                else
                {
                    Integer vater = gibVater(karteikID);
                    if (vater == null || vater == -1)
                        throw new SQLException();
                    else
                        connectKk(vater, bruder, BeziehungsTyp.H_CHILD, conNeo4j);
                }
            }

            psMysql = conMysql.prepareStatement("DELETE FROM Karteikarte WHERE ID = ?");
            psMysql.setInt(1, karteikID);
            psMysql.executeUpdate();

            ps = conNeo4j.prepareStatement("match(n) " + "where id(n) = {1} " + "OPTIONAL match n-[r]-() "
                    + "delete n,r");
            ps.setInt(1, karteikID);
            ps.executeUpdate();
            closeQuietly(ps);

            psMysql = conMysql.prepareStatement("DELETE FROM Karteikarte WHERE ID = ?");
            psMysql.setInt(1, karteikID);
            psMysql.executeUpdate();

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
            conLockNeo4j.getValue().unlock();
            conLock.getValue().unlock();
        }

        return erfolgreich;
    }

    @Override
    public boolean bewerteKarteikarte(int karteikID, int bewert, int benutzer)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;

        boolean erfolgreich = true;

        try
        {
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
            conLock.getValue().unlock();
        }

        return erfolgreich;
    }

    @Override
    public boolean hatKarteikarteBewertet(int karteikID, int benutzer)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            ps = conMysql
                    .prepareStatement("SELECT ID FROM bewertung_karteikarte WHERE Benutzer = ? AND KarteikarteID = ?");
            ps.setInt(1, benutzer);
            ps.setInt(2, karteikID);

            rs = ps.executeQuery();
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
            conLock.getValue().unlock();
        }

    }

    @Override
    public ArrayList<Kommentar> leseThemenKommentare(int karteikID, int aktBenutzerID)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;

        ArrayList<Kommentar> komms = new ArrayList<Kommentar>();

        try
        {
            String sql = "SELECT * FROM kommentaruebersicht WHERE Karteikarte = ?";
            ps = conMysql.prepareStatement(sql);
            ps.setInt(1, karteikID);

            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                GregorianCalendar g = new GregorianCalendar();
                g.setTime(rs.getTimestamp("erstelldatum"));
                Kommentar k = new Kommentar(rs.getInt("ID"), rs.getString("Inhalt"), g,
                        leseBenutzer(rs.getInt("Benutzer")), rs.getInt("Bewertung"), hatKommentarBewertet(
                                rs.getInt("ID"), aktBenutzerID), -1, rs.getInt("Karteikarte"), rs.getInt("AnzKinder"));

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
            conLock.getValue().unlock();
        }

        return komms;
    }

    private Kommentar leseKommentarIntern(int kommId, Connection conMysql)
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

                k = new Kommentar(rs.getInt("ID"), rs.getString("Inhalt"), g, leseBenutzer(rs.getInt("Benutzer")),
                        rs.getInt("Bewertung"), hatKommentarBewertet(rs.getInt("ID"), rs.getInt("Benutzer")), vaterID,
                        kkId, rs.getInt("AnzKinder"));
            }

        }
        catch (SQLException e)
        {
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
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

        Kommentar k = leseKommentarIntern(kommId, conMysql);

        conLock.getValue().unlock();

        return k;
    }

    @Override
    public ArrayList<Kommentar> leseAntwortKommentare(int vaterKID, int aktBenutzerID)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
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
                Kommentar k = new Kommentar(rs.getInt("ID"), rs.getString("Inhalt"), g,
                        leseBenutzer(rs.getInt("Benutzer")), rs.getInt("bewertung"), hatKommentarBewertet(
                                rs.getInt("ID"), aktBenutzerID), rs.getInt("VaterKommentar"), -1, 0);

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
            conLock.getValue().unlock();
        }

        return komms;
    }

    @Override
    public boolean schreibeKommentar(Kommentar kommentar)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
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
                karteik = leseKarteikarte(vaterKommentar.getKarteikartenID());
            }
            else
                karteik = leseKarteikarte(kommentar.getKarteikartenID());

            if (karteik == null)
                return false;

            schreibeBenachrichtigung(new BenachrNeuerKommentar(kommentar.getErsteller().getId(), karteik, kommentarId),
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
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();

        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            conLock.getValue().unlock();
        }

        return erfolgreich;
    }

    @Override
    public boolean loescheKommentar(int kommentarID)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
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
            conLock.getValue().unlock();
        }

        return erfolgreich;
    }

    @Override
    public boolean bewerteKommentar(int kommentarID, int bewert, int benutzerId)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
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
            conLock.getValue().unlock();
        }

        return erfolgreich;
    }

    @Override
    public boolean hatKommentarBewertet(int kommentarID, int benutzerId)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
        PreparedStatement ps = null;
        boolean erfolgreich = true;

        try
        {
            conMysql.setAutoCommit(false);

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
            conLock.getValue().unlock();
        }

        return erfolgreich;
    }

    @Override
    public void zuVeranstaltungEinschreiben(int veranstaltung, int benutzer, String kennwort, Connection conMysql,
            boolean isAdmin) throws SQLException, DbUniqueConstraintException, DbFalsePasswortException
    {
        Entry<Connection, ReentrantLock> conLock = null;
        boolean transaktion = true;
        if (conMysql == null)
        {
            conLock = getConnection();
            conMysql = conLock.getKey();
            transaktion = false;
        }

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
            if (!transaktion)
                conLock.getValue().unlock();
        }
    }

    @Override
    public boolean vonVeranstaltungAbmelden(int veranstaltung, int benutzer)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

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
            conLock.getValue().unlock();
        }
        return erfolgreich;
    }

    @Override
    public Notiz leseNotiz(int benutzer, int karteikID)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

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

            while (rs.next())
            {
                notiz = new Notiz(rs.getInt("ID"), rs.getString("Inhalt"), rs.getInt("Benutzer"),
                        rs.getInt("KarteikarteID"));

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(ps);
            closeQuietly(rs);
            conLock.getValue().unlock();
        }
        return notiz;
    }

    @Override
    public boolean schreibeNotiz(Notiz notiz)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

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
            conLock.getValue().unlock();
        }
        return erfolgreich;
    }

    @Override
    public boolean bearbeiteNotiz(Notiz notiz)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();

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
            conLock.getValue().unlock();
        }
        return erfolgreich;
    }

    @Override
    public boolean loescheNotiz(int notizID)
    {
        Entry<Connection, ReentrantLock> conLock = getConnection();
        Connection conMysql = conLock.getKey();
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
            conLock.getValue().unlock();
        }
        return erfolgreich;
    }

    @Override
    public void connectKk(int vonKK, int zuKK, BeziehungsTyp typ, Connection conNeo4j) throws SQLException
    {
        boolean verbindungAbbauen = false;
        Entry<Connection, ReentrantLock> conLockNeo4j = null;
        if (conNeo4j == null)
        {
            conLockNeo4j = getConnectionNeo4j();
            conNeo4j = conLockNeo4j.getKey();
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
                conLockNeo4j.getValue().unlock();
        }

    }

    @Override
    public void disconnectKk(int vonKK, int zuKK, Connection conNeo4j) throws SQLException
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
