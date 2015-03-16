/**
 * @author mk
 */

$(document).ready(function() {
	$("#login_submit").click(function() {
		var email = $("#login_email").val();
		var pass = $("#login_pass").val();
		console.log("Email="+email+", PW="+pass); //Debug
		$.ajax({
			type: "POST",
			url: "BenutzerServlet",
			data: "action=login"+"&email="+email+"&password="+pass,
			success: function(d) {
				
			}
		});
	});
});