package com.lise.ctrl;

import java.io.IOException;
import java.util.Date;

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
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Controller() {
		super();
		datenbankmanager = new DatenbankManager();
		benutzerverwaltung = new Benutzerverwaltung(datenbankmanager);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// Aktuelle Session holen oder neue Session erstellen.
		HttpSession session = request.getSession();

		// Was will der Benutzer tun?
		String action = request.getParameter("action");
		
		// Bisschen platz zwischen den log meldungen
		System.out.println();
		System.out.println();
		
		// Prüfen ob benutzer angemeldet ist
		if(benutzerverwaltung.istEingeloggt(session))
		{
			System.out.println("Benutzer ist eingeloggt: " + session.getAttribute("UsereMail"));
			Date lastAccessed = new Date(session.getLastAccessedTime());
			System.out.println("Er war zuletzt aktiv am " + lastAccessed);
			
			
			// erneueter anmeldeversuch?
			// TODO kann man eigentlich auch weglassen oder?
//			if(!isEmpty(action) && action.equals("login"))
//			{
//				System.out.println("Erneueter Anmeldeversuch! Benutzer ist schon angemeldet.");
//
//				// Weiter zu startseite
//				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/View.jsp");
//				dispatcher.forward(request,response);
//			}
			// Abmeldevorgang?
			if(!isEmpty(action) && action.equals("logout"))
			{
				if(benutzerverwaltung.abmelden(session))
					System.out.println("Abmeldung erfolgreich");
				else
					// TODO kann das überhaupt auftreten?
					System.out.println("Fehler beim abmelden! Benutzer schon abgemeldet.");

				// Weiterleitung
				response.sendRedirect("/DurchstichLISE/Controller");
			}
			else
			{
				System.out.println("Keine gültige Aktion gewählt. Weiter zur Startseite.");
				// Weiter zu startseite
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/View.jsp");
				dispatcher.forward(request,response);
			}
		}
		// Registrierung?
		else if(!isEmpty(action) && action.equals("register"))
		{
			System.out.println("Registrierungsvorgang gestartet.");
			// Benutzerobjekt zusammenbauen
			// TODO Prüfen ob werte richtig übergeben wurden
			Benutzer user = new Benutzer();
			user.seteMail(request.getParameter("eMail"));
			user.setVorname(request.getParameter("vorname"));
			user.setNachname(request.getParameter("nachname"));
			user.setPasswort(request.getParameter("pass"));
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

				// Weiter zu login seite
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/Login.jsp");
				dispatcher.forward(request,response);
			} 
			catch (DbUserStoringException e) 
			{
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
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/Login.jsp");
				dispatcher.forward(request,response);
			}
		}
		else if( !isEmpty(action) && action.equals("login"))
		{
			System.out.println("Niemand eingeloggt. Loginvorgang gestartet.");
			
			String eMail = request.getParameter("eMail");
			String pass = request.getParameter("pass");
			
			if(!isEmpty(eMail) && !isEmpty(pass))
			{
				// Anmelden
				try {
					benutzerverwaltung.anmelden(eMail, pass, session);
					
					// Wenn erfolgreich, dann weiter
//					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/View.jsp");
//					dispatcher.forward(request,response);
					System.out.println("Anmeldung erfolgreich. Weiterleitung.");
					response.sendRedirect("/DurchstichLISE/Controller");
				} 
				catch (LoginFailedException e) 
				{
					System.out.println("Fehler beim Anmeldevorgang.");
					// Fehler beim anmelen
					// Übergeben was falsch war. eMail und passwort stehen noch in der URL
					request.setAttribute("passInvalid", e.passwortWrong);
					request.setAttribute("eMailInvlaid", e.eMailWrong);

					// Weiter zu login seite
					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/Login.jsp");
					dispatcher.forward(request,response);
				}
			}
			else
			{
				System.out.println("Keine Login-Informationen angegeben.");
				// Weiter zu login seite
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/Login.jsp");
				dispatcher.forward(request,response);
			}
		}
		else
		{
			System.out.println("Niemand ist eingeloggt. Gehe zu Login.");

			// Weiter zu login seite
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/Login.jsp");
			dispatcher.forward(request,response);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}	
	
	public boolean passwortAendern(Benutzer user, String altesPasswort, String neuesPasswort) {
		return false;
		
	}
	
	private boolean isEmpty(String s) {
		if(s == null || s == "")
			return true;
		return false;
	}
}