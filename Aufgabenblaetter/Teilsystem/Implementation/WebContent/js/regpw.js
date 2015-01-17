

function regPWhash(){
	document.getElementById('action').value = 'register'
	document.getElementById('reghashpw').value = MD5(document.getElementById('password').value)
}