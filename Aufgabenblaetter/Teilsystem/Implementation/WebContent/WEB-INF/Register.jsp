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
<script type="text/javascript" src="js/hash.js"></script>
<script type="text/javascript" src="js/regpw.js"></script>
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
			<td><input name="prename" type="text" size="30" maxlength="30" placeholder="Max" value="${user.vorname}"></td>
		</tr>
		<tr>
			<td>Nachname: * </td>
			<td><input name="surname" type="text" size="30" maxlength="30" placeholder="Mustermann" value="${user.nachname}"></td>
		</tr>
		<tr>
			<td>Martikelnummer: * </td>
			<td><input id="matNo" name="matNo" type="text" size="30" maxlength="30" placeholder="123456" value="${user.matrikelnummer}"></td>
		</tr>
		<tr>
			<td>Studiengang :</td>
			<td> 
				<select name="studiengang" id="studiengang" >			
						<c:forEach var="st" items="${studiengaenge}">
							<c:choose>
								<c:when test="${st != user.studiengang}">
									<option value='${st}'><c:out value="${st}"></c:out></option>		
								</c:when>
								<c:otherwise>
									<option value='${st}' selected="selected"><c:out value="${st}"></c:out></option>
								</c:otherwise>
							</c:choose>		
						</c:forEach>
				</select> 
 			</td>
		</tr>
		<tr>
			<td>email-Adresse: * </td>
			<td><input name="user_email" type="text" size="30" maxlength="40" placeholder="eMail-Addresse" value="${user.email}"></td>
		</tr>
		<tr>
			<td>Passwort: * </td>
			<td><input id="password" type="password" size="30" maxlength="40" placeholder="kryptische Zeichen" value=""></td>
		</tr>
	</table>	

<!-- Buttons zur Anmeldung und zum PW Rest-->			
			<input id="action" name="action" type="hidden"	value=""></input>
			<input id="reghashpw" name="reghashpassword" value="" type="hidden">
				
			<table align="center">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><input name="b_register" type="submit" value="Registrierung abschließen" onclick="regPWhash()"></input></td>
					<td><input name="b_reg_cancel" type="submit" value="Registrierung abbrechen"></input></td>				
				</tr>
			</table>
		</form>


</body>
</html>