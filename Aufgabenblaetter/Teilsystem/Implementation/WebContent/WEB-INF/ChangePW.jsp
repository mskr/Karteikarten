<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="error" class="java.lang.String" scope="request" />
<jsp:useBean id="info" class="java.lang.String" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>PasswortAenderungSeite</title>
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
<h2 align="center">Änderung für Ihr Kennwort</h2>


<p align="center">
		Benutzer ist angemeldet:
		<c:out value="${sessionScope.UsereMail}"></c:out>
	</p>
<form action="Controller">
<!-- Eingabefelder -->
	<table  align="center">
		<tr>
			<td>altes Passwort: *</td>
			<td><input name="oldPassword" type="text" size="30" maxlength="30" placeholder="Ihr derzeitiges Passwort"></td>
		</tr>
		<tr>
			<td>neues Passwort: * </td>
			<td><input id="NewPw"name="newPassword" type="text" size="30" maxlength="30" placeholder="kryptische Zeichen"></td>
		</tr>
		<tr>
			<td>neues Kennwort wiederholen: * </td>
			<td><input id="repNewPw" name="repNewPw" type="text" size="30" maxlength="30" placeholder="kryptische Zeichen"></td>
		</tr>
		
	</table>	

<!-- Buttons zur Anmeldung und zum PW Rest-->			
			<input id="action" name="action" type="hidden"	value=""></input>
				
			<table align="center">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><input name="b_pw_save" type="submit" value="neues Passwort speichern" onclick="document.getElementById('action').value = 'changePW'"></input></td>
					<td><input name="b_pw_cancel" type="submit" value="Passwortänderung abbrechen"></input></td>				
				</tr>
			</table>
		</form>


</body>
</html>