package com.lise.ctrl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lise.data.Benutzer;

/**
 * Servlet implementation class Wochenplaner
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Controller() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession session = request.getSession();
		
		
		//Ansicht generieren
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/View.jsp");
				dispatcher.forward(request,response);

	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
	}

	public void anmelden(String eMail, String passwort) {
		
	}
	
	public void registrieren(Benutzer user) {
		
	}
	
	public boolean passwortAendern(Benutzer user, String altesPasswort, String neuesPasswort) {
		return false;
		
	}
}