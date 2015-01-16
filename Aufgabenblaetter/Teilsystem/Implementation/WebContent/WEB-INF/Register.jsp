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
<script type="text/javascript" src="js/view.js"></script>
<title>RegistrierungSeite</title>
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
<h2 align="center">Registrierung zu LISE</h2>

<form action="Controller">
<!-- Eingabefelder -->
	<table  align="center">
		<tr>
			<td>Vorname: *</td>
			<td><input name="prename" type="text" size="30" maxlength="30"></td>
		</tr>
		<tr>
			<td>Nachname: * </td>
			<td><input name="surname" type="text" size="30" maxlength="30" ></td>
		</tr>
		<tr>
			<td>Martikelnummer: * </td>
			<td><input id="matNo" name="matNo" type="text" size="30" maxlength="30" ></td>
		</tr>
		<tr>
			<td>Studiengang :</td>
			<td> 
				<select name="studiengang" id="studiengang" >
						<c:forEach var="st" items="${studiengaenge}">
							<option value="${st}"><c:out value="${st}"></c:out></option>		
						</c:forEach>
				</select> 
 			</td>
		</tr>
		<tr>
			<td>email-Adresse: * </td>
			<td><input name="user_email" type="text" size="30" maxlength="40" ></td>
		</tr>
		<tr>
			<td>Passwort: * </td>
			<td><input name="password" type="password" size="30" maxlength="40" ></td>
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
					<td><input name="b_register" type="submit" value="Registrierung abschließen" onclick="document.getElementById('action').value = 'register'"></input></td>
					<td><input name="b_reg_cancel" type="submit" value="Registrierung abbrechen"  onclick="document.getElementById('action').value = 'login'"></input></td>				
				</tr>
			</table>
		</form>


</body>
</html>