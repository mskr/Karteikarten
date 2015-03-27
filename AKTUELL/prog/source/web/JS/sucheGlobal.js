/**
 * @author mk
 */

$(document).ready(function() {
    registerSucheGlobalEvent();
});

function registerSucheGlobalEvent()
{
    $("#suche_global_form").submit(function() {
        $("#suche_ergebnisse").show();
        var suchString = $("#suche_global_input").val();
        $.ajax({
            url: suchfeldServlet,
            data: "action =" + actionSucheBenVeranst + "&" +
                  suchmuster + "=" + suchString,
            success: function() {
                var jsonObj = response;
                var errCode = jsonObj["error"];
                if(errCode == "noerror")
                {
                    var arrSuchErgebnisse = jsonObj[keyJsonArrSuchfeldResult];
                    fillSuchergebnisse(arrSuchErgebnisse);
                }
            }
        });
        event.preventDefault();
    });
}

function fillSuchergebnisse(arrSuchErgebnisse)
{
    for(var i in arrSuchErgebnisse)
    {
        var jsonSuchErgebnis = arrSuchErgebnisse[i];
        var text = jsonSuchErgebnis[keyJsonSuchfeldErgText];
        var klasse = jsonSuchErgebnis[keyJsonSuchfeldErgKlasse];
        var id = jsonSuchErgebnis[keyJsonSuchfeldErgId];
        console.log("Suchfeld: klasse="+klasse+", text="+text+", id="+id);
        if(klasse == "Benutzer") {
            $("#sucherg_benutzer").append("<div id='sucherg_benutzer_"+id+"'>" + text + "</div>");
        } else if(klasse == "Veranstaltung") {
            $("#sucherg_vn").append("<div id='sucherg_vn_"+id+"'>" + text + "</div>");
        }
        registerSuchergebnisClickEvent(klasse, id);
    }
}

function registerSuchergebnisClickEvent(klasse, id)
{
    
}