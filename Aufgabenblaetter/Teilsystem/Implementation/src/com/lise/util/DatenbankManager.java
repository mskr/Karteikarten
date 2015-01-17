package com.lise.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.lise.data.Benutzer;
import com.lise.data.BenutzerEinstellungen;

public class DatenbankManager implements IDatenbankManager
{	
	protected Connection con = null;

	public DatenbankManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException{
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/sopra","root","");
	}

	@Override
	public boolean pruefeLogin(String eMail, String passwort) throws LoginFailedException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean login_erfolgreich = false;
		try{
			con = getConnection();
			ps = con.prepareStatement("SELECT * FROM benutzer WHERE eMail = ?");
			ps.setString(1, eMail);
			rs = ps.executeQuery();
			if(!rs.next())
				throw new LoginFailedException("Login fehlgeschlagen", false, true);
			
			closeQuietly(ps);
			closeQuietly(rs);
			ps = con.prepareStatement("SELECT * FROM benutzer WHERE eMail = ? AND Kennwort = ?");
			ps.setString(1, eMail);
			ps.setString(2, passwort);
			rs = ps.executeQuery();
			if(rs.next())
				login_erfolgreich = true;
			else
				throw new LoginFailedException("Login fehlgeschlagen", true, false);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		} finally{
			closeQuietly(con);
			closeQuietly(ps);
			closeQuietly(rs);
		}

		return login_erfolgreich;
	}

	@Override
	public Benutzer holeBenutzer(String eMail) throws DbUserNotExistsException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Benutzer benutzer = null;
		try{
			con = getConnection();
			ps = con.prepareStatement("SELECT b.ID,b.Vorname,b.Nachname,b.Matrikelnummer,s.Name,b.Kennwort,b.Nutzerstatus,"
					+ "b.GruppeneinladungenErlauben,b.NotifyDiskussionen FROM benutzer AS b JOIN studiengang AS s ON "
					+ "b.Studiengang_id = s.ID WHERE eMail = ?");
			ps.setString(1, eMail);
			rs = ps.executeQuery();
			if(rs.next()){
				benutzer = new Benutzer(rs.getInt("b.ID"),rs.getString("b.Vorname"),rs.getString("b.Nachname"),
						rs.getInt("b.Matrikelnummer"),eMail,rs.getString("s.Name"),rs.getString("b.Kennwort"),
						rs.getString("Nutzerstatus"),new BenutzerEinstellungen(rs.getBoolean("b.GruppeneinladungenErlauben"),
								rs.getString("NotifyDiskussionen")));
			}
			else{
				throw new DbUserNotExistsException("Benutzer ist nicht in der Datenbank vorhanden", eMail);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		} finally{
			closeQuietly(con);
			closeQuietly(ps);
			closeQuietly(rs);
		}

		return benutzer;
	}

	@Override
	public void speichereBenutzer(Benutzer user) throws DbUserStoringException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			con = getConnection();
			ps = con.prepareStatement("SELECT ID FROM studiengang WHERE Name = ?");
			ps.setString(1, user.getStudiengang());
			rs = ps.executeQuery();
			if(rs.next()){			
				int Studiengang_id = rs.getInt("ID");
				closeQuietly(ps);
				ps = con.prepareStatement("INSERT INTO benutzer (Vorname,Nachname,Matrikelnummer,eMail,Studiengang_id,"
						+ "Kennwort,Nutzerstatus,GruppeneinladungenErlauben,"
						+ "NotifyDiskussionen) VALUES(?,?,?,?,?,?,?,?,?)");
				ps.setString(1, user.getVorname());
				ps.setString(2, user.getNachname());
				ps.setInt(3, user.getMatrikelnummer());
				ps.setString(4, user.getEmail());
				ps.setInt(5, Studiengang_id);
				ps.setString(6,user.getPasswort());
				ps.setString(7,String.valueOf(user.getNutzerstatus()));
				ps.setBoolean(8, user.getBenutzereinstellungen().getGruppenEinladungenErlauben());
				ps.setString(9, String.valueOf(user.getBenutzereinstellungen().getNotifyDiskussionen()));
				if(ps.executeUpdate() != 1)
					throw new DbUserStoringException("Fehler beim Speichern des Benutzers aufgetreten", user);
			}
			else{
				DbUserStoringException exc = new DbUserStoringException("Der angegebende Studiengang dieses Benutzers existiert nicht", user);
				exc.studiengangNotSupported = true;
				throw exc;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		} finally{
			closeQuietly(con);
			closeQuietly(ps);
			closeQuietly(rs);
		}
	}	
	
	@Override
	public void bearbeiteBenutzer(Benutzer user) throws DbUserStoringException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			con = getConnection();
			ps = con.prepareStatement("SELECT ID FROM studiengang WHERE Name = ?");
			ps.setString(1, user.getStudiengang());
			rs = ps.executeQuery();
			if(rs.next()){
				int Studiengang_id = rs.getInt("ID");
				closeQuietly(ps);
				ps = con.prepareStatement("UPDATE benutzer SET Vorname=?,Nachname=?,Matrikelnummer=?,eMail=?,Studiengang_id=?,Kennwort=?,"
						+ "Nutzerstatus=?,GruppeneinladungenErlauben=?, NotifyDiskussionen=? WHERE id = ?");
				ps.setString(1, user.getVorname());
				ps.setString(2, user.getNachname());
				ps.setInt(3, user.getMatrikelnummer());
				ps.setString(4, user.getEmail());
				ps.setInt(5, Studiengang_id);
				ps.setString(6,user.getPasswort());
				ps.setString(7,String.valueOf(user.getNutzerstatus()));
				ps.setBoolean(8, user.getBenutzereinstellungen().getGruppenEinladungenErlauben());
				ps.setString(9, String.valueOf(user.getBenutzereinstellungen().getNotifyDiskussionen()));
				ps.setInt(10, user.getId());
				if(ps.executeUpdate() != 1)
					throw new DbUserStoringException("Fehler beim Speichern des Benutzers aufgetreten", user);
			}
			else{
				DbUserStoringException exc = new DbUserStoringException("Der angegebende Studiengang dieses Benutzers existiert nicht", user);
				exc.studiengangNotSupported = true;
				throw exc;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		} finally{
			closeQuietly(con);
			closeQuietly(ps);
			closeQuietly(rs);
		}
	}	

	@Override
	public ArrayList<String> holeStudiengaenge(){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<String> studiengaenge = new ArrayList<String>();
		try{
			con = getConnection();
			ps = con.prepareStatement("SELECT name FROM studiengang");
			rs = ps.executeQuery();
			while(rs.next())
				studiengaenge.add(rs.getString("Name"));
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		} finally{
			closeQuietly(con);
			closeQuietly(ps);
			closeQuietly(rs);
		}
		return studiengaenge;
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
}