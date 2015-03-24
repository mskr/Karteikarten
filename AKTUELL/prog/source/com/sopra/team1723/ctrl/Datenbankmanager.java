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
import java.util.List;





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
    public void bearbeiteBenutzer(String alteMail, Benutzer benutzer)
                    throws SQLException, DbUniqueConstraintException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conMysql.prepareStatement("UPDATE benutzer SET eMail=?, Vorname=?,Nachname=?,"
                    + "NotifyKommentare=?, NotifyVeranstAenderung=?,"
                    + "NotifyKarteikartenAenderung=?  WHERE eMail = ?");
            ps.setString(1, benutzer.geteMail());
            ps.setString(2, benutzer.getVorname());
            ps.setString(3, benutzer.getNachname());
            ps.setString(4, benutzer.getNotifyKommentare().name());
            ps.setBoolean(5, benutzer.isNotifyVeranstAenderung());
            ps.setBoolean(6, benutzer.isNotifyKarteikartenAenderung());
            ps.setString(7, alteMail);    
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
    public void bearbeiteBenutzerAdmin(String alteMail, Benutzer benutzer) throws SQLException, DbUniqueConstraintException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conMysql.prepareStatement("UPDATE benutzer SET eMail=?, Vorname=?,Nachname=?,Matrikelnummer=?,Studiengang=?,"
                    + "Nutzerstatus=?, NotifyKommentare=?, NotifyVeranstAenderung=?,"
                    + "NotifyKarteikartenAenderung=?  WHERE eMail = ?");
            ps.setString(1, benutzer.geteMail());
            ps.setString(2, benutzer.getVorname());
            ps.setString(3, benutzer.getNachname());
            ps.setInt(4, benutzer.getMatrikelnummer());
            ps.setString(5, benutzer.getStudiengang());
            ps.setString(6, benutzer.getNutzerstatus().name());
            ps.setString(7, benutzer.getNotifyKommentare().name());
            ps.setBoolean(8, benutzer.isNotifyVeranstAenderung());
            ps.setBoolean(9, benutzer.isNotifyKarteikartenAenderung());
            ps.setString(10, alteMail);    
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
    public boolean loescheBenutzer(String eMail) {
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try{
            ps = conMysql.prepareStatement("DELETE FROM benutzer WHERE eMail=?");
            ps.setString(1, eMail);    
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
    public boolean aendereProfilBild(String eMail, String dateiName)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try{
            ps = conMysql.prepareStatement("UPDATE benutzer SET Profilbild=? WHERE eMail=?");
            ps.setString(1, dateiName);
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
    public Veranstaltung leseVeranstaltung(String veranstTitel) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Veranstaltung veranstaltung = null;
        try{
            ArrayList<String> moderatoren = new ArrayList<String>();
            ps = conMysql.prepareStatement("SELECT m.Benutzer FROM veranstaltung AS v JOIN moderator AS m WHERE v.Titel=?");
            ps.setString(1, veranstTitel);
            rs = ps.executeQuery();
            while(rs.next()){
                moderatoren.add(rs.getString("m.Benutzer"));
            }
            closeQuietly(ps);
            closeQuietly(rs);

            ps = conMysql.prepareStatement("SELECT Beschreibung, Studiengang, Semester, Kennwort, BewertungenErlaubt,"
                    + "ModeratorKarteikartenBearbeiten, Ersteller, Benutzer FROM veranstaltung WHERE Titel = ?");
            ps.setString(1, veranstTitel);
            rs = ps.executeQuery();
            if(rs.next()){
                veranstaltung = new Veranstaltung(veranstTitel,rs.getString("Beschreibung"),rs.getString("Studiengang"),
                        rs.getString("Semester"),rs.getString("Kennwort"),rs.getBoolean("BewertungenErlaubt"),
                        rs.getBoolean("ModeratorKarteikartenBearbeiten"), rs.getString("Ersteller"),
                        moderatoren, rs.getBoolean("KommentareErlaubt"));
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
    
    @Override
    public List<ErgebnisseSuchfeld> durchsucheDatenbank(String suchmuster)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<ErgebnisseSuchfeld> ergebnisse = null;
        try{
            ergebnisse = new ArrayList<ErgebnisseSuchfeld>();
            ps = conMysql.prepareStatement("SELECT Vorname AS Text, ID, 'Benutzer' AS Tabelle FROM Benutzer "
                    + "WHERE levenshtein(=?,Vorname) BETWEEN 0 AND 5 UNION "
                    + "SELECT Nachname AS Text, ID, 'Benutzer' AS Tabelle FROM Benutzer WHERE levenshtein(=?,Nachname) BETWEEN 0 AND 5 UNION"
                    + "SELECT Titel AS Text, ID, 'Veranstaltung' AS Tabelle FROM Benutzer "
                    + "WHERE levenshtein(=?,Titel) BETWEEN 0 AND 5 LIMIT 5");
                  
            ps.setString(1, suchmuster);
            ps.setString(2, suchmuster);
            ps.setString(3, suchmuster);
            rs = ps.executeQuery();
            while(rs.next()){
                ergebnisse.add(new ErgebnisseSuchfeld(rs.getString("Text"), rs.getInt("ID"), rs.getString("Tabelle")));
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
    public boolean schreibeVeranstaltung(Veranstaltung veranst) {


        return false;
    }

    @Override
    public boolean bearbeiteVeranstaltung(Veranstaltung veranst) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean loescheVeranstaltung(String veranstTitel) {
        // TODO Auto-generated method stub
        return false;
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
    public boolean zuVeranstaltungEinschreiben(String veranstTitel, String eMail) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean vonVeranstaltungAbmelden(String veranstTitel, String eMail) {
        // TODO Auto-generated method stub
        return false;
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



}
