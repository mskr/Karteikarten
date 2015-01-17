
function subnewpw(){
	document.getElementById('newhashpw').value = MD5(document.getElementById('NewPw').value);
	document.getElementById('newrephashpw').value = MD5(document.getElementById('repNewPw').value);
	
	if (document.getElementById('newhashpw').value != document.getElementById('newrephashpw').value){
		alert("Neues Kennwort und wiederholtes Passwort sind unterschiedlich! Bitte geben Sie diese erneut ein!");
		document.getElementById('action').value = '';
	}
		  
	if (document.getElementById('newhashpw').value == document.getElementById('newrephashpw').value){
		document.getElementById('action').value = 'changePW';
		document.getElementById('oldhashpw').value = MD5(document.getElementById('oldPassword').value);
		document.getElementById('changePWFormular').submit();
	}
		
}