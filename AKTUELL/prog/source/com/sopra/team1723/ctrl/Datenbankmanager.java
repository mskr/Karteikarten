/** @author Matthias
 * 
 */
package com.sopra.team1723.ctrl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.*;

import com.sopra.team1723.data.*;

/**
 * Implementiert die Methoden des @ref IDatenbankmanager. Bietet eine Schnittstelle zur Datenbank.
 */
public class Datenbankmanager implements IDatenbankmanager {
    private Connection conMysql = null;
    private Connection conNeo4j = null;
    /**
     * Implementiert die Methoden des @ref IDatenbankmanager. Bietet eine Schnittstelle zur Datenbank.
     * @throws Exception 
     */
    public Datenbankmanager() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conMysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/sopra","root","");
        conNeo4j = DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
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
            ps = conMysql.prepareStatement("SELECT Vorname,Nachname,Matrikelnummer,Studiengang,Kennwort,Nutzerstatus,"
                    + "NotifyKommentare, NotifyVeranstAenderung, NotifyKarteikartenAenderung FROM benutzer WHERE eMail = ?");
            ps.setString(1, eMail);
            rs = ps.executeQuery();
            if(rs.next()){
                benutzer = new Benutzer(eMail,rs.getString("Vorname"),rs.getString("Nachname"),
                        rs.getInt("Matrikelnummer"),rs.getString("Studiengang"),rs.getString("Kennwort"),
                        Nutzerstatus.valueOf(rs.getString("Nutzerstatus")), 
                        rs.getBoolean("NotifyVeranstAenderung"),rs.getBoolean("NotifyKarteikartenAenderung"),
                        NotifyKommentare.valueOf(rs.getString("NotifyDiskussionen")));
            }
        } catch (SQLException e) {
            benutzer = null;
            e.printStackTrace();
            System.out.println(e);
        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return benutzer;
    }

    @Override
    public boolean schreibeBenutzer(Benutzer benutzer) {
        PreparedStatement ps = null;
        boolean erfolgreich = true;
        try{
                ps = conMysql.prepareStatement("INSERT INTO benutzer (Vorname,Nachname,Matrikelnummer,eMail,Studiengang,"
                        + "Kennwort,Nutzerstatus, NotifyKommentare, NotifyVeranstAenderung"
                        + "notifyKarteikartenAenderung) VALUES(?,?,?,?,?,?,?,?,?,?)");
                ps.setString(1, benutzer.getVorname());
                ps.setString(2, benutzer.getNachname());
                ps.setInt(3, benutzer.getMatrikelnummer());
                ps.setString(4, benutzer.geteMail());
                ps.setString(5, benutzer.getStudiengang());
                ps.setString(6,benutzer.getKennwort());
                ps.setString(7,String.valueOf(benutzer.getNutzerstatus()));
                ps.setString(8, String.valueOf(benutzer.getNotifyKommentare()));
                ps.setBoolean(9, benutzer.isNotifyVeranstAenderung());
                ps.setBoolean(10, benutzer.isNotifyKarteikartenAenderung());
            
        } catch (SQLException e) {
            erfolgreich = false;
            e.printStackTrace();
        } finally{
            closeQuietly(ps);
        }
        return erfolgreich;
    }

    @Override
    public boolean bearbeiteBenutzer(Benutzer benutzer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean loescheBenutzer(String eMail) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean pruefeLogin(String eMail, String passwort) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erfolgreich = true;
        try{
            ps = conMysql.prepareStatement("SELECT * FROM benutzer WHERE eMail = ? AND Kennwort = ?");
            ps.setString(1, eMail);
            ps.setString(2, passwort);
            rs = ps.executeQuery();
            if(!rs.next())
                erfolgreich = false;

        } catch (SQLException e) {
            erfolgreich = false;
            e.printStackTrace();
            System.out.println(e);
        } finally{
            closeQuietly(ps);
            closeQuietly(rs);
        }

        return erfolgreich;
    }

    @Override
    public boolean passwortAendern(String eMail, String neuesPasswort) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Veranstaltung leseVeranstaltung(String veranstTitel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean schreibeVeranstaltung(Veranstaltung veranst) {
        // TODO Auto-generated method stub
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