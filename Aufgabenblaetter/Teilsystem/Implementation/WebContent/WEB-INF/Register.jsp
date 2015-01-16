<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="js/view.js"></script>
<title>RegistrierungSeite</title>
</head>
<body>

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
			<td><input id="matNo" type="text" size="30" maxlength="30" ></td>
		</tr>
		<tr>
			<td>Studiengang :</td>
			<td> 
				<select name="studiengang">
      				<option value="Informatik">Informatik</option>
      				<option value="Medieninformatik">Medieninformatik</option>
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