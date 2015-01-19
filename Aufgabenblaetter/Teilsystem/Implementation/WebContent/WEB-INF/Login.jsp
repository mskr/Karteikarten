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
<script type="text/javascript" src="js/hash.js"></script>
<title>Anmeldung</title>
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
	<h2 align="center">Login LISE</h2>


	<c:set value="color:black" var="passColor"></c:set>
	<c:if test="${passInvalid}">
		<c:set value="color:red" var="passColor"></c:set>
	</c:if>

	<c:set value="color:black" var="eMailColor"></c:set>
	<c:if test="${eMailInvlaid}">
		<c:set value="color:red" var="eMailColor"></c:set>
	</c:if>

	<form action="Controller">
		
	
	
	
		<!-- Infos zum weiterleiten -->
		<input id="action" name="action" value="login"	type="hidden">
		<input id="hashpw" name="password" value=""	type="hidden">
	
	
		<!-- Eingabefelder -->
		<table align="center">
				<tr>
					<td>
						<input name="user_email" type="text" value="${param.user_email}" placeholder="eMail-Addresse" style="${eMailColor};">
					</td>				
				</tr>
				<tr>
					<td>
						<input id="pw" type="password" value="" placeholder="Passwort"	style="${passColor};">
					</td>
				</tr>
		</table>
	
		
		
		
		<!-- Buttons zum anmelden --> 
		 <table align="center">
				<tr>
					<td>
						<input name="b_submit" value="Anmelden"type="submit" onclick="document.getElementById('hashpw').value = MD5(document.getElementById('pw').value)">
					</td>
					<td>
						<input name="b_register" value="Registrieren"type="submit" onclick="document.getElementById('action').value = 'gotoRegister'">
					</td>			
				</tr>
			</table>
	</form>



</body>
</html>