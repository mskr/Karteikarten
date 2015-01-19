<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page session="true"%>
<jsp:useBean id="error" class="java.lang.String" scope="request" />
<jsp:useBean id="info" class="java.lang.String" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- Laden der CSS-Datei -->
<link rel="stylesheet" type="text/css" href="css/style.css"  media="all" />
<title>Startseite</title>
</head>
<body>
	<%
		if (!error.equals("") || !info.equals("")) {
	%>
	<fieldset>
		<legend>Status</legend>
		<%
			}
		%>
		<div id="errorDiv">
			<%
				if (!error.equals("")) {
			%>
			<p id="errorText">
				<c:out value="${error}"></c:out>
			</p>
			<%
				}
			%>
		</div>

		<div id="infoDiv">
			<%
				if (!info.equals("")) {
			%>
			<p id="infoText">
				<c:out value="${info}"></c:out>
			</p>
			<%
				}
			%>
		</div>
		<%
			if (!error.equals("") || !info.equals("")) {
		%>
	</fieldset>
	<%
		}
	%>
	<!-- Überschrift -->
	<h2 align="center">
		Herzlich Willkommen in LISE <br> Ihrem E-Learning Portal
	</h2>
	<p align="center">
		Benutzer ist angemeldet:
		<c:out value="${sessionScope.UsereMail}"></c:out>
	</p>

	<fieldset id="viewField">
		<table>
			<tr>
				<td><label>Vorname: </label></td>
				<td><c:out value="${user.vorname }"></c:out></td>
			</tr>
			<tr>
				<td><label>Nachname: </label></td>
				<td><c:out value="${user.nachname }"></c:out></td>
			</tr>
			<tr>
				<td><label>Martikelnummer: </label></td>
				<td><c:out value="${user.matrikelnummer }"></c:out></td>
			</tr>
			<tr>
				<td><label>Studiengang: </label></td>
				<td><c:out value="${user.studiengang }"></c:out></td>
			</tr>
		</table>

		<form action="Controller">
			<input id="action" name="action" value="logout" type="hidden">

			<table>
				<tr>
					<td><input name="b_logout" value="Abmelden" type="submit">
					</td>
					<td><input name="b_ch_pw" value="Passwort ändern"
						type="submit"
						onclick="document.getElementById('action').value = 'gotoChangePW'">
					</td>
				</tr>
			</table>
		</form>
	</fieldset>


</body>
</html>