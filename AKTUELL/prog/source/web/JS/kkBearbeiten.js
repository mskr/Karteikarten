$(document).ready(function(){
	$(".KKbearbeiten").click(function(){
		editKarteikarte($(this));
	});
});

function editKarteikarte(triggerElem){
	id = triggerElem.parent().parent().parent().parent().data("kkid");
	var params = {};
    params[paramId] = id;
    karteikarteJSON = {};
    return ajaxCall(karteikartenServlet, actionGetKarteikarteByID, 
        function(response) {
			karteikarteJSON = response;
			console.log(karteikarteJSON);
		},
        params
    );
}