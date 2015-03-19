/**
 * @author mk
 */

/**
 * Wird von boxhandler.js aufgerufen,
 * nachdem die <div> Container fuer die Startseite angezeigt wurden.
 * Holt die benoetigten Daten per Ajax Call vom Server
 * und traegt sie in die richtigen HTML Elemente ein.
 * Im Falle der Startseite werden die verfuegbaren Studiengaenge
 * geholt und in das <select> Element eingeordnet.
 */
function fillStartseite() {
    $.ajax({
    	type: "POST",
    	url: "BenutzerServlet",
    	data: "action="+actionGetStudiengaenge,
    	success: function(response) {
            var jsonObj = $.parseJSON(response);
            var errCode = jsonObj["error"];
            if(errCode == "noerror") {
                $("#reg_studiengang").empty();
                $("#reg_studiengang").append("<option disabled selected>Studiengang</option>");
            	var studgArr = jsonObj[keyJsonArrResult];
            	for(var studg of studgArr) {
            		$("#reg_studiengang").append("<option>"+studg+"</option>");
            	}
            } else {
            	message(0, buildMessage(errCode));
            }
    	}
    });
}