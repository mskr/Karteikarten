/**
 * @author mk
 */

$(document).ready(function() {
    
    registerSucheGlobalEvent();
    
});

function registerSucheGlobalEvent()
{
    //TODO bei keypress event ausloesen
    $("#suche_global_form").submit(function() {
        $("#suche_ergebnisse").css("display","flex"); // Anstatt  von show()
        $("#sucherg_vn").empty();
        $("#sucherg_benutzer").empty();
        var suchString = $("#suche_global_input").val();
        $.ajax({
            url: suchfeldServlet,
            data: "action=" + actionSucheBenVeranst + "&" +
                  paramSuchmuster + "=" + suchString,
            success: function(response) {
                var jsonObj = response;
                var errCode = jsonObj["error"];
                console.log(jsonObj);
                if(errCode == "noerror")
                {
                    var arrSuchErgebnisse = jsonObj[keyJsonArrSuchfeldResult];
                    fillSuchergebnisse(arrSuchErgebnisse);
                }
                else
                {
                    message(0, buildMessage(errCode));
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
            $("#sucherg_benutzer").append(
                    "<div id='sucherg_benutzer_"+id+"' class='sucherg_benutzer_item'><span class='octicon octicon-person'></span>" + text + " (#"+id+")</div>");
        } else if(klasse == "Veranstaltung") {
            $("#sucherg_vn").append(
                    "<div id='sucherg_vn_"+id+"' class='sucherg_vn_item'><span class='octicon octicon-podium'></span>" + text + " (#"+id+")</div>");
        }
        registerSuchergebnisClickEvent(klasse, id);
    }
}

function registerSuchergebnisClickEvent(klasse, id)
{
    
    //TODO
    // 1) Alle Elemente mit class "sucherg_..._item" selektieren
    // 2) Mit for each durch die Elemente iterieren und id abfragen.
    // 3) Fuer jede id eine URL zusammenbauen
}