/** @author Matthias
 * 
 */
package com.sopra.team1723.ctrl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.sql.Timestamp;








//import com.mysql.jdbc.authentication.MysqlClearPasswordPlugin;
import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.*;

/**
 * Implementiert die Methoden des @ref IDatenbankmanager. Bietet eine Schnittstelle zur Datenbank.
 */
public class Datenbankmanager implements IDatenbankmanager {
    final int UNIQUE_CONSTRAINT_ERROR = 1062;
    private Connection conMysql = null;
    private Connection conNeo4j = null;
    /**
     * Implementiert die Methoden des @ref IDatenbankmanager. Bietet eine Schnittstelle zur Datenbank.
     * @throws Exception 
     */
    public Datenbankmanager() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conMysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/sopra","root","");
        //conNeo4j = DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
    }

    public void closeQuietly(Connection connection){
        if (null == connection) return;
        try {
            connection.close();
        } catch (SQLException e) {
            // ignored
        }
    }

    protected void closeQuietly(PreparedStatement statement) {
        if (null == statement) return;
        try {
            statement.close();
        } catch (SQLException e) {
            // ignored
        }
    }

    protected void closeQuietly(ResultSet resultSet) {
        if (null == resultSet) return;
        try {
            resultSet.close();
        } catch (SQLException e) {
            // ignored
        }
    }

    /**
     * 
     */

    @Override
    public Benutzer leseBenutzer(String eMail) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Benutzer benutzer = null;
        try{
            ps = conMysql.prepareStatement("SELECT ID,Vorname,Nachname,Profilbild,Matrikelnummer,Studiengang,Kennwort,Nutzerstatus,"
                    + "NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, Profilbild FROM benutzer WHERE eMail = ?");
            ps.setString(1, eMail);
            rs = ps.executeQuery();
            if(rs.next()){
                benutzer = new Benutzer(rs.getInt("ID"), eMail,rs.getString("Vorname"),rs.getString("Nachname"),
                        rs.getInt("Matrikelnummer"),rs.getString("Studiengang"),rs.getString("Kennwort"),
                        Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), 
                        rs.getBoolean("NotifyVeranstAenderung"),rs.getBoolean("NotifyKarteikartenAenderung"),
                        NotifyKommentare.valueOf(rs.getString("NotifyKommentare")),rs.getString("Profilbild"));

                benutzer.setProfilBildPfad(ServletController.dirProfilBilder + rs.getString("Profilbild"));
            }
        } catch (SQLException e) {
            benutzer = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benutzer;
    }

    @Override
    public Benutzer leseBenutzer(int id)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Benutzer benutzer = null;
        try{
            ps = conMysql.prepareStatement("SELECT eMail,Vorname,Nachname,Profilbild,Matrikelnummer,Studiengang,Kennwort,Nutzerstatus,"
                    + "NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, Profilbild FROM benutzer WHERE ID = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                benutzer = new Benutzer(id, rs.getString("eMail"),rs.getString("Vorname"),rs.getString("Nachname"),
                        rs.getInt("Matrikelnummer"),rs.getString("Studiengang"),rs.getString("Kennwort"),
                        Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), 
                        rs.getBoolean("NotifyVeranstAenderung"),rs.getBoolean("NotifyKarteikartenAenderung"),
                        NotifyKommentare.valueOf(rs.getString("NotifyKommentare")),rs.getString("Profilbild"));

                benutzer.setProfilBildPfad(ServletController.dirProfilBilder + rs.getString("Profilbild"));
            }
        } catch (SQLException e) {
            benutzer = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benutzer;
    }

    @Override
    public void schreibeBenutzer(Benutzer benutzer) throws DbUniqueConstraintException, SQLException{
        PreparedStatement ps = null;
        try{
            ps = conMysql.prepareStatement("INSERT INTO benutzer (Vorname,Nachname,Matrikelnummer,eMail,Studiengang,"
                    + "Kennwort, Nutzerstatus, NotifyKommentare, NotifyVeranstAenderung, "
                    + "NotifyKarteikartenAenderung) VALUES(?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, benutzer.getVorname());
            ps.setString(2, benutzer.getNachname());
            ps.setInt(3, benutzer.getMatrikelnummer());
            ps.setString(4, benutzer.geteMail());
            ps.setString(5, benutzer.getStudiengang());
            ps.setString(6,benutzer.getKennwort());
            ps.setString(7,benutzer.getNutzerstatus().name());
            ps.setString(8, benutzer.getNotifyKommentare().name());
            ps.setBoolean(9, benutzer.isNotifyVeranstAenderung());
            ps.setBoolean(10, benutzer.isNotifyKarteikartenAenderung());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            if(UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        } finally{
            closeQuietly(ps);
        }
    }

    @Override
    public void bearbeiteBenutzer(Benutzer benutzer)
            throws SQLException, DbUniqueConstraintException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conMysql.prepareStatement("UPDATE benutzer SET eMail=?, Vorname=?,Nachname=?,"
                    + "NotifyKommentare=?, NotifyVeranstAenderung=?,"
                    + "NotifyKarteikartenAenderung=?  WHERE ID = ?");
            ps.setString(1, benutzer.geteMail());
            ps.setString(2, benutzer.getVorname());
            ps.setString(3, benutzer.getNachname());
            ps.setString(4, benutzer.getNotifyKommentare().name());
            ps.setBoolean(5, benutzer.isNotifyVeranstAenderung());
            ps.setBoolean(6, benutzer.isNotifyKarteikartenAenderung());
            ps.setInt(7, benutzer.getId());    
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            if(UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
    }

    @Override
    public void bearbeiteBenutzerAdmin(Benutzer benutzer) throws SQLException, DbUniqueConstraintException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conMysql.prepareStatement("UPDATE benutzer SET eMail=?, Vorname=?,Nachname=?,Matrikelnummer=?,Studiengang=?,"
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

        } catch (SQLException e) {
            e.printStackTrace();
            if(UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
    }

    @Override
    public boolean loescheBenutzer(int benutzerId) {
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try{
            ps = conMysql.prepareStatement("DELETE FROM benutzer WHERE ID=?");
            ps.setInt(1, benutzerId);    
            if(ps.executeUpdate()!= 1)
                return false;
        } catch(SQLException e){
            erfolgreich = false;
        } finally{
            closeQuietly(ps);
        }
        return erfolgreich;
    }

    @Override
    public void pruefeLogin(String eMail, String passwort) throws SQLException, DbFalseLoginDataException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            System.out.println("DB prueft: email="+eMail+", passwort="+passwort);
            ps = conMysql.prepareStatement("SELECT * FROM benutzer WHERE eMail = ? AND Kennwort = ?");
            ps.setString(1, eMail);
            ps.setString(2, passwort);
            rs = ps.executeQuery();
            if(!rs.next()){
                throw new DbFalseLoginDataException();
            }


        } catch (SQLException e) {
            e.printStackTrace();

            throw e;
        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

    }

    @Override
    public List<String> leseStudiengaenge(){
        ArrayList<String> studiengaenge = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conMysql.prepareStatement("SELECT Name FROM Studiengang");
            rs = ps.executeQuery();
            while(rs.next()){
                studiengaenge.add(rs.getString("Name"));
            }
        } catch (SQLException e){
            studiengaenge = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
        return studiengaenge;
    }
    
    @Override
    public List<String> leseSemester()
    {
        ArrayList<String> semester = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conMysql.prepareStatement("SELECT Name FROM Semester");
            rs = ps.executeQuery();
            while(rs.next()){
                semester.add(rs.getString("Name"));
            }
        } catch (SQLException e){
            semester = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
        return semester;
    }

    @Override
    public boolean passwortAendern(String eMail, String neuesPasswort) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try{
            ps = conMysql.prepareStatement("UPDATE benutzer SET Kennwort=? WHERE eMail=?");
            ps.setString(1, neuesPasswort);
            ps.setString(2, eMail);
            if(ps.executeUpdate()!= 1)
                return false;
        } catch (SQLException e) {
            erfolgreich = false;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
        return erfolgreich;
    }
    @Override
    public boolean aendereProfilBild(int benutzerId, String dateiName)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try{
            ps = conMysql.prepareStatement("UPDATE benutzer SET Profilbild=? WHERE ID=?");
            ps.setString(1, dateiName);
            ps.setInt(2, benutzerId);
            if(ps.executeUpdate()!= 1)
                return false;
        } catch (SQLException e) {
            erfolgreich = false;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
        return erfolgreich;
    }

    @Override
    public Veranstaltung leseVeranstaltung(int id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Veranstaltung veranstaltung = null;
        try{
            ps = conMysql.prepareStatement("SELECT Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt,"
                    + "ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, count(bvz.ID) AS AnzTeilnehmer"
                    + " FROM veranstaltung AS v LEFT OUTER JOIN benutzer_veranstaltung_zuordnung AS bvz "
                    + "ON v.ID = bvz.Veranstaltung WHERE v.ID=?"); 
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                veranstaltung = new Veranstaltung(id,rs.getString("Titel"),rs.getString("Beschreibung"),
                        rs.getString("Semester"),rs.getString("Kennwort"),rs.getBoolean("BewertungenErlaubt"),
                        rs.getBoolean("ModeratorKarteikartenBearbeiten"), leseBenutzer(rs.getInt("Ersteller")),
                        rs.getBoolean("KommentareErlaubt"),rs.getInt("AnzTeilnehmer"));
            }
        } catch (SQLException e) {
            veranstaltung = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return veranstaltung;
    }

//    @Override
//    public List<Veranstaltung> leseAlleVeranstaltungen()
//    {
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        ArrayList<Veranstaltung> veranstaltungen = null;
//        try{
//            veranstaltungen = new ArrayList<Veranstaltung>();
//            ps = conMysql.prepareStatement("SELECT v.ID, Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt, "
//                    + "ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, count(bvz.ID) AS AnzTeilnehmer "
//                    + "FROM veranstaltung AS v LEFT OUTER JOIN benutzer_veranstaltung_zuordnung AS bvz ON "
//                    + "v.ID = bvz.Veranstaltung GROUP BY v.ID"); 
//            rs = ps.executeQuery();
//            while(rs.next()){
//                veranstaltungen.add(new Veranstaltung(rs.getInt("v.ID"),rs.getString("Titel"),rs.getString("Beschreibung"),
//                        rs.getString("Semester"),rs.getString("Kennwort"),rs.getBoolean("BewertungenErlaubt"),
//                        rs.getBoolean("ModeratorKarteikartenBearbeiten"), leseBenutzer(rs.getInt("Ersteller")),
//                        rs.getBoolean("KommentareErlaubt"),rs.getInt("AnzTeilnehmer")));
//            }
//        } catch (SQLException e) {
//            veranstaltungen = null;
//            e.printStackTrace();
//
//        } finally{
//            closeQuietly(ps);
//            closeQuietly(rs);
//        }
//
//        return veranstaltungen;
//    }

    @Override
    public List<Veranstaltung> leseVeranstaltungenStudiengang(String studiengang)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Veranstaltung> veranstaltungen = null;
        try{
            veranstaltungen = new ArrayList<Veranstaltung>();
            ps = conMysql.prepareStatement("SELECT v.ID, Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt, "
                    + "ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, count(bvz.ID) AS AnzTeilnehmer "
                    + "FROM benutzer_veranstaltung_zuordnung AS bvz RIGHT OUTER JOIN veranstaltung AS v ON "
                    + "v.ID = bvz.Veranstaltung JOIN veranstaltung_studiengang_zuordnung AS vsz ON v.ID = vsz.Veranstaltung"
                    + " WHERE vsz.Studiengang =? GROUP BY v.ID"); 
            ps.setString(1, studiengang);
            rs = ps.executeQuery();
            while(rs.next()){
                veranstaltungen.add(new Veranstaltung(rs.getInt("v.ID"),rs.getString("Titel"),rs.getString("Beschreibung"),
                        rs.getString("Semester"),rs.getString("Kennwort"),rs.getBoolean("BewertungenErlaubt"),
                        rs.getBoolean("ModeratorKarteikartenBearbeiten"), leseBenutzer(rs.getInt("Ersteller")),
                        rs.getBoolean("KommentareErlaubt"),rs.getInt("AnzTeilnehmer")));
            }
        } catch (SQLException e) {
            veranstaltungen = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return veranstaltungen;
    }

    @Override
    public List<Veranstaltung> leseVeranstaltungenSemester(String semester)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Veranstaltung> veranstaltungen = null;
        try{
            veranstaltungen = new ArrayList<Veranstaltung>();
            ps = conMysql.prepareStatement("SELECT v.ID, Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt, "
                    + "ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, count(bvz.ID) AS AnzTeilnehmer "
                    + "FROM veranstaltung AS v LEFT OUTER JOIN benutzer_veranstaltung_zuordnung AS bvz ON "
                    + "v.ID = bvz.Veranstaltung WHERE Semester =? GROUP BY v.ID"); 
            ps.setString(1, semester);
            rs = ps.executeQuery();
            while(rs.next()){
                veranstaltungen.add(new Veranstaltung(rs.getInt("v.ID"),rs.getString("Titel"),rs.getString("Beschreibung"),
                        semester,rs.getString("Kennwort"),rs.getBoolean("BewertungenErlaubt"),
                        rs.getBoolean("ModeratorKarteikartenBearbeiten"), leseBenutzer(rs.getInt("Ersteller")),
                        rs.getBoolean("KommentareErlaubt"),rs.getInt("AnzTeilnehmer")));
            }
        } catch (SQLException e) {
            veranstaltungen = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return veranstaltungen;
    }

    @Override
    public List<Veranstaltung> leseVeranstaltungen(int benutzer)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Veranstaltung> veranstaltungen = null;
        try{
            veranstaltungen = new ArrayList<Veranstaltung>();
            ps = conMysql.prepareStatement("SELECT v.ID, Titel, Beschreibung, Semester, Kennwort, BewertungenErlaubt, "
                    + "ModeratorKarteikartenBearbeiten, Ersteller, KommentareErlaubt, count(bvz.ID) AS AnzTeilnehmer "
                    + "FROM veranstaltung AS v LEFT OUTER JOIN benutzer_veranstaltung_zuordnung AS bvz ON "
                    + "v.ID = bvz.Veranstaltung WHERE bvz.Benutzer =? GROUP BY v.ID"); 
            ps.setInt(1, benutzer);
            rs = ps.executeQuery();
            while(rs.next()){
                veranstaltungen.add(new Veranstaltung(rs.getInt("ID"),rs.getString("Titel"),rs.getString("Beschreibung"),
                        rs.getString("Semester"),rs.getString("Kennwort"),rs.getBoolean("BewertungenErlaubt"),
                        rs.getBoolean("ModeratorKarteikartenBearbeiten"), leseBenutzer(rs.getInt("Ersteller")),
                        rs.getBoolean("KommentareErlaubt"),rs.getInt("AnzTeilnehmer")));
            }
        } catch (SQLException e) {
            veranstaltungen = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return veranstaltungen;
    }

    @Override
    public List<Benutzer> leseModeratoren(int veranstaltung)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Benutzer> moderatoren = null;
        try{
            moderatoren = new ArrayList<Benutzer>();
            ps = conMysql.prepareStatement("SELECT b.ID, eMail, Vorname, Nachname, Profilbild, Matrikelnummer, Studiengang, "
                    + "Kennwort, Nutzerstatus, NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung, "
                    + "Profilbild FROM benutzer AS b JOIN moderator AS m ON b.ID = m.Benutzer AND m.Veranstaltung =?"); 
            ps.setInt(1, veranstaltung);
            rs = ps.executeQuery();
            while(rs.next()){
                moderatoren.add(new Benutzer(rs.getInt("b.ID"),rs.getString("eMail"),rs.getString("Vorname"),
                        rs.getString("Nachname"), rs.getInt("Matrikelnummer"),rs.getString("Studiengang"),
                        rs.getString("Kennwort"),Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), 
                        rs.getBoolean("NotifyVeranstAenderung"),rs.getBoolean("NotifyKarteikartenAenderung"),
                        NotifyKommentare.valueOf(rs.getString("NotifyKommentare")),rs.getString("Profilbild")));
            }
        } catch (SQLException e) {
            moderatoren = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return moderatoren;
    }

    @Override
    public List<String> leseStudiengaenge(int veranstaltung)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> studiengaenge = null;
        try{
            studiengaenge = new ArrayList<String>();
            ps = conMysql.prepareStatement("SELECT Studiengang FROM veranstaltung_studiengang_zuordnung "
                    + " WHERE Veranstaltung =?"); 
            ps.setInt(1, veranstaltung);
            rs = ps.executeQuery();
            while(rs.next()){
                studiengaenge.add(rs.getString("Studiengang"));
            }
        } catch (SQLException e) {
            studiengaenge = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return studiengaenge;
    }
    
    @Override
    public Boolean istModerator(int benutzer, int veranstaltung)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ErgebnisseSuchfeld> durchsucheDatenbank(String suchmuster, List<Klassenfeld> suchfelder)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<ErgebnisseSuchfeld> ergebnisse = null;
        try{
            ergebnisse = new ArrayList<ErgebnisseSuchfeld>();
            if(suchfelder != null && suchfelder.size() > 0){
                Iterator<Klassenfeld> it = suchfelder.iterator();
                Klassenfeld aktSuchfeld = it.next();
                String sql = new String("");
                while(it.hasNext()){
                    aktSuchfeld = DatenbankKlassenNamenMapping.matching.get(aktSuchfeld);
                    if (aktSuchfeld == null){
                        System.out.println("Angegebenes Klassenfeld konnte nicht auf ein Feld in der db gematcht werden");
                        return null;
                    }
                    sql = sql + "SELECT "+aktSuchfeld.feld+" AS Text, ID, '"+aktSuchfeld.klasse+"' AS Tabelle, "
                            + "levenshtein('"+suchmuster+"',"+aktSuchfeld.feld+") AS lev FROM "+aktSuchfeld.klasse+" "
                            + " WHERE levenshtein('"+suchmuster+"',"+aktSuchfeld.feld+") BETWEEN 0 and 5 UNION ";
                    aktSuchfeld = it.next();
                }
                aktSuchfeld = DatenbankKlassenNamenMapping.matching.get(aktSuchfeld);
                sql = sql + "SELECT "+aktSuchfeld.feld+" AS Text, ID, '"+aktSuchfeld.klasse+"' AS Tabelle,"
                        + "levenshtein('"+suchmuster+"',"+aktSuchfeld.feld+") AS lev FROM "+aktSuchfeld.klasse+" "
                        + "WHERE levenshtein('"+suchmuster+"',"+aktSuchfeld.feld+") BETWEEN 0 and 5 ORDER BY lev LIMIT 5";

                ps = conMysql.prepareStatement(sql);
                rs = ps.executeQuery();
                while(rs.next()){         
                    ergebnisse.add(new ErgebnisseSuchfeld(rs.getString("Text"),rs.getInt("ID"),
                            DatenbankKlassenNamenMapping.matchTabelleKlasse.get(rs.getString("Tabelle"))));
                }
            }

        } catch (SQLException e) {
            ergebnisse = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
        return ergebnisse;
    }
    @Override
    public boolean angemeldet(int benutzer, int veranstaltung) throws SQLException{
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean angemeldet = false;
        try{
            ps = conMysql.prepareStatement("SELECT ID FROM benutzer_veranstaltung_zuordnung WHERE Benutzer =? AND Veranstaltung =?");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            rs = ps.executeQuery();
            if(rs.next())
                angemeldet = true;
            else
                angemeldet = false;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
        return angemeldet;
    }

    @Override
    public void schreibeVeranstaltung(Veranstaltung veranst) throws SQLException, DbUniqueConstraintException  {
        PreparedStatement ps = null;
        try{
            ps = conMysql.prepareStatement("INSERT INTO veranstaltung (Titel, Beschreibung, Semester, Kennwort, KommentareErlaubt,"
                    + "BewertungenErlaubt, ModeratorKarteikartenBearbeiten, Ersteller) VALUES(?,?,?,?,?,?,?,?)");
            ps.setString(1, veranst.getTitel());
            ps.setString(2, veranst.getBeschreibung());
            ps.setString(3, veranst.getSemester());
            ps.setString(4, veranst.getZugangspasswort());
            ps.setBoolean(5, veranst.isKommentareErlaubt());
            ps.setBoolean(6, veranst.isBewertungenErlaubt());
            ps.setBoolean(7, veranst.isModeratorKarteikartenBearbeiten());
            ps.setInt(8, veranst.getErsteller().getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            if(UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        } finally{
            closeQuietly(ps);
        }


    }

    @Override
    public void bearbeiteVeranstaltung(Veranstaltung veranst) throws SQLException, DbUniqueConstraintException {
        //        PreparedStatement ps = null;
        //        ResultSet rs = null;
        //        try{
        //            ps = conMysql.prepareStatement("UPDATE veranstaltung SET Titel=?, Beschreibung=?, Semester=?,"
        //                    + "Kennwort=?, KommentareErlaubt=?,"
        //                    + "BewertungenErlaubt=?  WHERE eMail = ?");
        //            ps.setString(1, benutzer.geteMail());
        //            ps.setString(2, benutzer.getVorname());
        //            ps.setString(3, benutzer.getNachname());
        //            ps.setString(4, benutzer.getNotifyKommentare().name());
        //            ps.setBoolean(5, benutzer.isNotifyVeranstAenderung());
        //            ps.setBoolean(6, benutzer.isNotifyKarteikartenAenderung());
        //            ps.setString(7, alteMail);    
        //            ps.executeUpdate();
        //
        //        } catch (SQLException e) {
        //            e.printStackTrace();
        //            if(UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
        //                throw new DbUniqueConstraintException();
        //            else
        //                throw e;
        //
        //        } finally{
        //            closeQuietly(ps);
        //            closeQuietly(rs);
        //        }
    }

    @Override
    public boolean loescheVeranstaltung(String veranstTitel) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public List<Benachrichtigung> leseBenachrichtigungen(int benutzer, int limit)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Benachrichtigung> aktBenachrichtigungen = null;
        try{
            aktBenachrichtigungen = new ArrayList<Benachrichtigung>();
            
            ps = conMysql.prepareStatement("SELECT bem.ID AS ID, 'benachrichtigung_einladung_moderator' AS Tabelle,"
                    + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_einladung_moderator AS bem JOIN benachrichtigung AS b ON"
                    + " bem.Benachrichtigung = b.ID WHERE bem.Gelesen = false AND bem.Benutzer =? UNION "
                    
                    + "SELECT bk.ID AS ID, 'benachrichtigung_karteikartenaenderung' AS Tabelle ,"
                    + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_karteikartenaenderung AS bk JOIN benachrichtigung AS b ON"
                    + " bk.Benachrichtigung = b.ID WHERE bk.Gelesen = false AND bk.Benutzer =? UNION "
                    
                    + "SELECT bnk.ID AS ID, 'benachrichtigung_neuer_kommentar' AS Tabelle ,"
                    + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_neuer_kommentar AS bnk JOIN benachrichtigung AS b ON"
                    + " bnk.Benachrichtigung = b.ID WHERE bnk.Gelesen = false AND bnk.Benutzer =? UNION "
                    
                    + "SELECT bpg.ID AS ID, 'benachrichtigung_profil_geaendert' AS Tabelle ,"
                    + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_profil_geaendert AS bpg JOIN benachrichtigung AS b ON"
                    + " bpg.Benachrichtigung = b.ID WHERE bpg.Gelesen = false AND bpg.Benutzer =? UNION "

                    + "SELECT bv.ID AS ID, 'benachrichtigung_veranstaltungsaenderung' AS Tabelle ,"
                    + " b.Erstelldatum AS Erstelldatum FROM benachrichtigung_veranstaltungsaenderung AS bv JOIN benachrichtigung AS b ON"
                    + " bv.Benachrichtigung = b.ID WHERE bv.Gelesen = false AND bv.Benutzer =? "
                    + "ORDER BY Erstelldatum DESC LIMIT ?");
            
            ps.setInt(1, benutzer);
            ps.setInt(2, benutzer);
            ps.setInt(3, benutzer);
            ps.setInt(4, benutzer);
            ps.setInt(5, benutzer);
            ps.setInt(6, limit);
            rs = ps.executeQuery();
            String tabelle;
            int id;
            while (rs.next()){
                tabelle = rs.getString("Tabelle");
                id = rs.getInt("ID");
                if(tabelle.equals("benachrichtigung_einladung_moderator"))
                    aktBenachrichtigungen.add(leseBenachrEinlModerator(id));
                else if(tabelle.equals("benachrichtigung_karteikartenaenderung"))
                    aktBenachrichtigungen.add(leseBenachrKarteikAenderung(id));
                else if(tabelle.equals("benachrichtigung_neuer_kommentar"))
                    aktBenachrichtigungen.add(leseBenachrNeuerKommentar(id));
                else if(tabelle.equals("benachrichtigung_profil_geaendert"))
                    aktBenachrichtigungen.add(leseBenachrProfilGeaendert(id));
                else
                    aktBenachrichtigungen.add(leseBenachrVeranstAenderung(id));
            }
            
            
        } catch(SQLException e){
            aktBenachrichtigungen = null;
            e.printStackTrace();
        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
        return aktBenachrichtigungen;
    }
    
    @Override
    public BenachrEinlModerator leseBenachrEinlModerator(int id)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrEinlModerator benachrichtigung = null;
        try{
            ps = conMysql.prepareStatement("SELECT Inhalt, Erstelldatum, Benutzer, Veranstaltung, Gelesen, Angenommen"
                    + " FROM benachrichtigung_einladung_moderator AS bem JOIN benachrichtigung AS b ON bem.Benachrichtigung"
                    + "= b.ID WHERE bem.ID =?"); 
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                Calendar cal = new GregorianCalendar();
                cal.setTime(rs.getTimestamp("Erstelldatum"));
                benachrichtigung = new BenachrEinlModerator(id, rs.getString("Inhalt"), cal,
                        rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), leseVeranstaltung(rs.getInt("Veranstaltung")),
                        rs.getBoolean("Angenommen"));
            }
        } catch (SQLException e) {
            benachrichtigung = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    @Override
    public BenachrKarteikAenderung leseBenachrKarteikAenderung(int id)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrKarteikAenderung benachrichtigung = null;
        try{
            ps = conMysql.prepareStatement("SELECT Inhalt, Erstelldatum, Benutzer, Karteikarte, Gelesen"
                    + " FROM benachrichtigung_karteikartenaenderung AS bk JOIN benachrichtigung AS b ON bk.Benachrichtigung"
                    + "= b.ID WHERE bk.ID =?"); 
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                Calendar cal = new GregorianCalendar();
                cal.setTime(rs.getDate("Erstelldatum"));
                benachrichtigung = new BenachrKarteikAenderung(id, rs.getString("Inhalt"), cal,
                        rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), leseKarteikarte(rs.getInt("Karteikarte")));
            }
        } catch (SQLException e) {
            benachrichtigung = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    @Override
    public BenachrNeuerKommentar leseBenachrNeuerKommentar(int id)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrNeuerKommentar benachrichtigung = null;
        try{
            ps = conMysql.prepareStatement("SELECT Inhalt, Erstelldatum, Benutzer, Kommentar, Gelesen"
                    + " FROM benachrichtigung_neuer_kommentar AS bnk JOIN benachrichtigung AS b ON bnk.Benachrichtigung"
                    + "= b.ID WHERE bnk.ID =?"); 
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                Calendar cal = new GregorianCalendar();
                cal.setTime(rs.getDate("Erstelldatum"));
                benachrichtigung = new BenachrNeuerKommentar(id, rs.getString("Inhalt"), cal,
                        rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), leseKommentar(rs.getInt("Kommentar")));
            }
        } catch (SQLException e) {
            benachrichtigung = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    @Override
    public BenachrProfilGeaendert leseBenachrProfilGeaendert(int id)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrProfilGeaendert benachrichtigung = null;
        try{
            ps = conMysql.prepareStatement("SELECT Inhalt, Erstelldatum, Benutzer, Admin, Gelesen"
                    + " FROM benachrichtigung_profil_geaendert AS bpg JOIN benachrichtigung AS b ON bpg.Benachrichtigung"
                    + "= b.ID WHERE bpg.ID =?"); 
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                Calendar cal = new GregorianCalendar();
                cal.setTime(rs.getDate("Erstelldatum"));
                benachrichtigung = new BenachrProfilGeaendert(id, rs.getString("Inhalt"), cal,
                        rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), leseBenutzer(rs.getInt("Admin")));
            }
        } catch (SQLException e) {
            benachrichtigung = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }

    @Override
    public BenachrVeranstAenderung leseBenachrVeranstAenderung(int id)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BenachrVeranstAenderung benachrichtigung = null;
        try{
            ps = conMysql.prepareStatement("SELECT Inhalt, Erstelldatum, Benutzer, Veranstaltung, Gelesen"
                    + " FROM benachrichtigung_veranstaltungsaenderung AS bv JOIN benachrichtigung AS b ON bv.Benachrichtigung"
                    + "= b.ID WHERE bv.ID =?"); 
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                Calendar cal = new GregorianCalendar();
                cal.setTime(rs.getDate("Erstelldatum"));
                benachrichtigung = new BenachrVeranstAenderung(id, rs.getString("Inhalt"), cal,
                        rs.getInt("Benutzer"), rs.getBoolean("Gelesen"), leseVeranstaltung(rs.getInt("Veranstaltung")));
            }
        } catch (SQLException e) {
            benachrichtigung = null;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benachrichtigung;
    }
    
    @Override
    public boolean schreibeBenachrichtigung(Benachrichtigung benachrichtigung)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try{
            conMysql.setAutoCommit(false);
            ps = conMysql.prepareStatement("INSERT INTO benachrichtigung (Inhalt, Erstelldatum) VALUES (?,?); ");
            ps.setString(1, benachrichtigung.getInhalt());
            ps.setTimestamp(2, new Timestamp(benachrichtigung.getErstelldaum().getTimeInMillis()));
            ps.executeUpdate();
            closeQuietly(ps);
            
            ps = conMysql.prepareStatement("SELECT LAST_INSERT_ID();");
            rs = ps.executeQuery();
            if(!rs.next())
                return false;
            int id = rs.getInt(1);
            closeQuietly(ps);
            
            if(benachrichtigung instanceof BenachrEinlModerator){
                BenachrEinlModerator bem = (BenachrEinlModerator) benachrichtigung;
                ps = conMysql.prepareStatement("INSERT INTO benachrichtigung_einladung_moderator (Benachrichtigung, Benutzer,"
                        + "Veranstaltung, Gelesen, Angenommen) VALUES (?,?,?,?,?)");
                ps.setInt(1, id);
                ps.setInt(2, bem.getBenutzer());
                ps.setInt(3, bem.getVeranstaltung().getId());
                ps.setBoolean(4, bem.isGelesen());
                ps.setBoolean(5, bem.isAngenommen());
                
            } else if (benachrichtigung instanceof BenachrKarteikAenderung){
                BenachrKarteikAenderung bk = (BenachrKarteikAenderung) benachrichtigung;
                ps = conMysql.prepareStatement("INSERT INTO benachrichtigung_karteikartenaenderung (Benachrichtigung, Benutzer,"
                        + "Karteikarte, Gelesen) VALUES (?,?,?,?)");
                ps.setInt(1, id);
                ps.setInt(2, bk.getId());
                ps.setInt(3, bk.getKarteikarte().getId());
                ps.setBoolean(4, bk.isGelesen());
                
            } else if (benachrichtigung instanceof BenachrNeuerKommentar){
                BenachrNeuerKommentar bnk = (BenachrNeuerKommentar) benachrichtigung;
                ps = conMysql.prepareStatement("INSERT INTO benachrichtigung_neuer_kommentar (Benachrichtigung, Benutzer,"
                        + "Kommentar, Gelesen) VALUES (?,?,?,?)");
                ps.setInt(1, id);
                ps.setInt(2, bnk.getBenutzer());
                ps.setInt(3, bnk.getKommentar().getId());
                ps.setBoolean(4, bnk.isGelesen());   
                
            } else if (benachrichtigung instanceof BenachrProfilGeaendert){
                BenachrProfilGeaendert bpg = (BenachrProfilGeaendert) benachrichtigung;
                ps = conMysql.prepareStatement("INSERT INTO benachrichtigung_profil_geaendert (Benachrichtigung, Benutzer,"
                        + "Admin, Gelesen) VALUES (?,?,?,?)");
                ps.setInt(1, id);
                ps.setInt(2, bpg.getBenutzer());
                ps.setInt(3, bpg.getAdmin().getId());
                ps.setBoolean(4, bpg.isGelesen()); 
                
            } else {
                BenachrVeranstAenderung bv = (BenachrVeranstAenderung) benachrichtigung;
                ps = conMysql.prepareStatement("INSERT INTO benachrichtigung_veranstaltungsaenderung (Benachrichtigung, Benutzer,"
                        + "Veranstaltung, Gelesen) VALUES (?,?,?,?)");
                ps.setInt(1, id);
                ps.setInt(2, bv.getBenutzer());
                ps.setInt(3, bv.getVeranstaltung().getId());
                ps.setBoolean(4, bv.isGelesen());  
            }           
            
            if(ps.executeUpdate() != 1)                
                erfolgreich = false;
            
            conMysql.commit();
                
                
        } catch (SQLException e) {
            try
            {
                conMysql.rollback();
            }
            catch (SQLException e1)
            {
                e1.printStackTrace();
            }
            erfolgreich = false;
            e.printStackTrace();

        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return erfolgreich;
    }


    @Override
    public Karteikarte leseKarteikarte(int karteikID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Karteikarte[] leseKindKarteikarten(int vaterKarteikID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean schreibeKarteikarte(Karteikarte karteik, int vaterKarteikID, int Position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void schreibeErsteKarteikarte(Karteikarte karteik, int sohnKarteikID) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean bearbeiteKarteikarte(Karteikarte karteik) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean loescheKarteikarte(int karteikID) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean bewerteKarteikarte(int karteikID, int bewert, String benutzer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hatKarteikarteBewertet(int karteikID, String benutzer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Kommentar[] leseKommentare(int karteikID, int vaterKID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean schreibeKommentar(Kommentar kommentar) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean bearbeiteKommentar(Kommentar kommentar) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean loescheKommentar(int kommentarID) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean bewerteKommentar(int kommentarID, int bewert, String benutzer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hatKommentarBewertet(int kommentarID, String benutzer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean istModerator(Veranstaltung veranst, String benutzerMail) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void zuVeranstaltungEinschreiben(int veranstaltung, int benutzer, String kennwort) throws SQLException, 
    DbUniqueConstraintException, DbFalsePasswortException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conMysql.prepareStatement("SELECT Kennwort FROM veranstaltung WHERE ID =?");
            ps.setInt(1, veranstaltung);
            rs = ps.executeQuery();
            if(rs.next()){
                // TODO rs.getString("Kennwort") kann null sein obwohl die Veranstaltung ein Kennwort hat.
                if(rs.getString("Kennwort") != null && rs.getString("Kennwort").equals(kennwort) == false)
                    throw new DbFalsePasswortException();
            }
            closeQuietly(ps);    
            
            ps = conMysql.prepareStatement("INSERT INTO benutzer_veranstaltung_zuordnung (Benutzer, Veranstaltung)"
                    + "VALUES(?,?)");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            ps.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
            if(UNIQUE_CONSTRAINT_ERROR == e.getErrorCode())
                throw new DbUniqueConstraintException();
            else
                throw e;
        } catch(DbFalsePasswortException e){
            e.printStackTrace();
            throw e;
        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }
    }

    @Override
    public boolean vonVeranstaltungAbmelden(int veranstaltung, int benutzer) {
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try{
            ps = conMysql.prepareStatement("DELETE FROM benutzer_veranstaltung_zuordnung WHERE Benutzer=? AND Veranstaltung=?");
            ps.setInt(1, benutzer);
            ps.setInt(2, veranstaltung);
            if(ps.executeUpdate() != 1)
                erfolgreich = false;
        } catch(SQLException e){
            erfolgreich = false;
            e.printStackTrace();
        } finally {
           closeQuietly(ps);
        }
        return erfolgreich;
    }

    @Override
    public Notiz[] leseNotizen(String erstellerEMail, int karteikID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean schreibeNotiz(Notiz notiz) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean bearbeiteNotiz(Notiz notiz) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean loescheNotiz(int notizID) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean rolleZuweisen(String eMail, Nutzerstatus status) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Kommentar leseKommentar(int id)
    {
        // TODO Auto-generated method stub
        return null;
    }




}
