
function subnewpw(){
	document.getElementById('action').value = 'changePW'
	document.getElementById('newhashpw').value = MD5(document.getElementById('NewPw').value)
	document.getElementById('newrephashpw').value = MD5(document.getElementById('repNewPw').value)
}