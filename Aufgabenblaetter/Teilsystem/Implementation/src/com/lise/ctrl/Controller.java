package com.lise.ctrl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lise.data.Benutzer;
import com.lise.data.Benutzerverwaltung;
import com.lise.data.IBenutzerverwaltung;
import com.lise.util.DatenbankManager;
import com.lise.util.DbUserStoringException;
import com.lise.util.IDatenbankManager;
import com.lise.util.LoginFailedException;

/**
 * Servlet implementation class Wochenplaner
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {

	IBenutzerverwaltung benutzerverwaltung;
	IDatenbankManager datenbankmanager;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// URL-Namen für die Weiterleitung
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final String startseiteURL = "/WEB-INF/View.jsp";
	private final String loginURL = "/WEB-INF/Login.jsp";
	private final String registerURL = "/WEB-INF/Register.jsp";
	private final String passwChURL = "/WEB-INF/chPassw.jsp";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// URL-Parameter-Namen
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ---------------- Allgemeine Parameter ---------------------
	// eMail-Adresse
	private final String eMailParam = "eMail";
	// Aktuelle Aktion
	private final String actionParam = "action";
	// Fehlermeldungen. Weitere Informationen zu den Fehlern siehe Quelltext. Es wird noch gesetzt, was für Eingaben falsch waren.
	private final String errorParam = "error";
	// Informationen
	private final String infoParam = "info";

	// ---------------- Spezifische Parameter ---------------------
	// Für Login usw.
	private final String passwortParam = "pass";
	// Für passwort ändern
	private final String oldPassParam = "altesPasswort";
	private final String newPassParam = "neuesPasswort";

	// Mögliche Actions
	private final String actionOptionLogin = "login";
	private final String actionOptionLogout = "logout";
	private final String actionOptionChangePW = "changePW";
	private final String actionOptionRegister = "register";
	
	// Befehle, die den Seitenwechsel einleiten
	private final String actionOptionGoToRegister = "gotoRegister";
	private final String actionOptionGoToChangePW = "gotoChange";



	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Controller() {
		super();
		// DatenbankManager erzeugen
		datenbankmanager = new DatenbankManager();
		// Benutzerverwaltung erzeugen
		benutzerverwaltung = new Benutzerverwaltung(datenbankmanager);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// Aktuelle Session holen oder neue Session erstellen.
		HttpSession session = request.getSession();

		// Bisschen platz zwischen den log meldungen
		System.out.println();
		System.out.println();
		
		// verarbeitet Anfrage
		actionDispatcher(request, response, session);
	}

	/**
	 * Verarbeitet ankommende Anfragen unter Berücksichtigung der gewählten "action" und ob der Benutzer angemeldet ist
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void actionDispatcher(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
			throws ServletException, IOException
	{
		// Was will der Benutzer tun?
		String action = request.getParameter(actionParam);
		
		// Prüfen ob Benutzer angemeldet ist
		if(benutzerverwaltung.istEingeloggt(session))
		{
			System.out.println("Benutzer ist eingeloggt: " + session.getAttribute("UsereMail"));
			// Erzeuge hübschen Date-String
			Date lastAccessed = new Date(session.getLastAccessedTime());
			SimpleDateFormat sf = new SimpleDateFormat();
			System.out.println("Zuletzt aktiv am " + sf.format(lastAccessed));

			// Abmeldevorgang?
			if(!isEmpty(action) && action.equals(actionOptionLogout))
			{
				logout(request,response, session);
			}
			// Wechsle zu PW ändern Seite
			else if(!isEmpty(action) && action.equals(actionOptionGoToChangePW))
			{
				System.out.println("Wechsle zu ChangePW-Seite.");
				request.setAttribute("user", benutzerverwaltung.getBenutzer(session));
				
				// Weiter zu startseite
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(passwChURL);
				dispatcher.forward(request,response);
			}
			
			// Passwortänderung
			else if(!isEmpty(action) && action.equals(actionOptionChangePW))
			{
				
				aenderePasswort(request, response, session);
			}
			else
			{
				System.out.println("Keine gültige Aktion gewählt. Weiter zur Startseite.");
				// Weiter zu startseite
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(startseiteURL);
				dispatcher.forward(request,response);
			}
		}
		// Wechseln zur Register-Seite?
		else if(!isEmpty(action) && action.equals(actionOptionGoToRegister))
		{
			System.out.println("Wechsle zu Register-Seite.");
			
			// Weiter zu startseite
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(registerURL);
			dispatcher.forward(request,response);
		}
		// Registrierung?
		else if(!isEmpty(action) && action.equals(actionOptionRegister))
		{
			registrieren(request, response);
		}
		else if( !isEmpty(action) && action.equals(actionOptionLogin))
		{
			login(request, response, session);
		}
		else
		{
			request.setAttribute(infoParam, "Niemand eingeloggt.");
			System.out.println("Niemand ist eingeloggt. Gehe zu Login.");

			// Weiter zu Login Seite
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(loginURL);
			dispatcher.forward(request,response);
		}


	}

	/**
	 * Ändert das Passwort eines Benutzers und leitet ihn entsprechend weiter
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void aenderePasswort(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		

		System.out.println("Ändere Passwort.");
		
		// Daten holen
		String altesPassw = request.getParameter(oldPassParam);
		String neuesPassw = request.getParameter(newPassParam);

		if(!isEmpty(altesPassw) && !isEmpty(neuesPassw))
		{
			if(!benutzerverwaltung.passwortAendern(session, altesPassw, neuesPassw))
			{
				request.setAttribute(errorParam, "Fehler beim ändern des Passworts.");
				System.out.println("Fehler beim ändern des Passworts.");
			}
			else
			{
				request.setAttribute(infoParam, "Passwort wurde erfolgreich geändert.");
				System.out.println("Passwort wurde erfolgreich geändert.");
			}
			// Weiter zur alten Seite
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(passwChURL);
			dispatcher.forward(request,response);
		}
		else
		{
			request.setAttribute(errorParam, "Altes oder neues Passwort wurde nicht angegeben.");
			System.out.println("Altes oder neues Passwort wurde nicht angegeben.");
			// Weiter zur alten Seite
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(passwChURL);
			dispatcher.forward(request,response);
		}
	}

	/**
	 * Loggt den Benutzer ein und leitet weiter.
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 * @throws ServletException
	 */
	private void login(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException, ServletException {
		System.out.println("Niemand eingeloggt. Loginvorgang gestartet.");

		String eMail = request.getParameter(eMailParam);
		String pass = request.getParameter(passwortParam);

		if(!isEmpty(eMail) && !isEmpty(pass))
		{
			// Anmelden
			try {
				benutzerverwaltung.anmelden(eMail, pass, session);
				System.out.println("Anmeldung erfolgreich. Weiterleitung.");
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(startseiteURL);
				dispatcher.forward(request,response);
			} 
			catch (LoginFailedException e) 
			{
				if(e.passwortWrong && e.eMailWrong)
					request.setAttribute(errorParam, "Fehler beim Anmeldevorgang. EMail und Passwort falsch.");
				else if(e.passwortWrong )
					request.setAttribute(errorParam, "Fehler beim Anmeldevorgang. Passwort falsch.");
				else if(e.eMailWrong )
					request.setAttribute(errorParam, "Fehler beim Anmeldevorgang. eMail existert nicht.");
				else
					request.setAttribute(errorParam, "Fehler beim Anmeldevorgang. Fehler unbekannt");

				System.out.println("Fehler beim Anmeldevorgang.");
				// Fehler beim anmelen
				// Übergeben was falsch war. eMail und passwort stehen noch in der URL
				request.setAttribute("passInvalid", e.passwortWrong);
				request.setAttribute("eMailInvlaid", e.eMailWrong);

				// Weiter zu login seite
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(loginURL);
				dispatcher.forward(request,response);
			}
		}
		else
		{
			request.setAttribute(infoParam, "Keine Login-Informationen angegeben.");
			System.out.println("Keine Login-Informationen angegeben.");
			// Weiter zu login seite
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(loginURL);
			dispatcher.forward(request,response);
		}
	}

	/**
	 * Loggt den Benutzer aus und leitet weiter
	 * @param response
	 * @param session
	 * @throws IOException
	 * @throws ServletException 
	 */
	private void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
		if(benutzerverwaltung.abmelden(session)){
			System.out.println("Abmeldung erfolgreich");
		}
		else{
			// TODO kann das überhaupt auftreten?
			System.out.println("Fehler beim abmelden! Benutzer schon abgemeldet.");
		}

		// Weiterleitung
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(loginURL);
		dispatcher.forward(request,response);
	}

	/**
	 * Registriert den Benutzer und leitet ihn bei Erfolg zum Login weiter.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrieren(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	IOException {
		System.out.println("Registrierungsvorgang gestartet.");
		// Benutzerobjekt zusammenbauen
		// TODO Prüfen ob werte richtig übergeben wurden
		Benutzer user = new Benutzer();
		user.seteMail(request.getParameter(eMailParam));
		user.setVorname(request.getParameter("vorname"));
		user.setNachname(request.getParameter("nachname"));
		user.setPasswort(request.getParameter(passwortParam));
		user.setStudiengang(request.getParameter("studiengang"));
		String matrikelNummer = request.getParameter("martrikelnummer");
		if(matrikelNummer == null)
			matrikelNummer="1";
		user.setMatrikelnummer(Integer.parseInt(matrikelNummer));

		try 
		{
			// registrieren
			benutzerverwaltung.registrieren(user);
			System.out.println("Registrieren erfolgreich.");
			request.setAttribute(infoParam, "Registrieren erfolgreich.");

			// Weiter zu login seite
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(loginURL);
			dispatcher.forward(request,response);
		} 
		catch (DbUserStoringException e) 
		{
			request.setAttribute(errorParam, "Registrieren fehlgeschlagen.");
			System.out.println("Registrieren fehlgeschlagen.");
			// Bei Fehler, die informationen an GUI weiterleiten
			request.setAttribute("eMailInvalid", e.eMailInvalid);
			request.setAttribute("martrikelnummerInvalid", e.martrikelnummerInvalid);
			request.setAttribute("nachnameInvalid", e.nachnameInvalid);
			request.setAttribute("nutzerstatusInvalid", e.nutzerstatusInvalid);
			request.setAttribute("passwortInvalid", e.passwortInvalid);
			request.setAttribute("studiengangNotSupported", e.studiengangNotSupported);
			request.setAttribute("vornameInvalid", e.vornameInvalid);

			// Weiter zu login seite
			// TODO Registrieren Seeite
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(registerURL);
			dispatcher.forward(request,response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}	

	private boolean isEmpty(String s) {
		if(s == null || s == "")
			return true;
		return false;
	}
}