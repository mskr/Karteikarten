/**
 * @author mk
 */

$(document).ready(function() {
    
    $('#vn_erstellen_bt').click(function() {
        
        popupFenster(
            $("#vn_erstellen_popup_overlay"), 
            [$('#vn_popup_close'),$("#vn_erzeugen_cancel"),$("#vn_erzeugen_ok")],
            function() {
                var editor = $('#vn_beschr_input').ckeditorGet();
                editor.destroy();
            },
            $("#vn_titel_input")
        );
        
        $("#vn_beschr_input").ckeditor();
        
    });
    
});




var selectedModList = {};
var modSuchTimer;
function registerVeranstErzeugeHandler() {
    
    $("#vn_erzeugen_ok").click(function(event) {
        var dialog = $("#vn_erstellen_popup");
        var titel = dialog.find("#vn_titel_input").val(),
            semester = dialog.find("#vn_erstellen_auswahl_semester").val(),
            studiengang = dialog.find("#vn_erstellen_auswahl_studiengang").val(),
            beschr = dialog.find("#vn_beschr_input").val(),
            moderatorenKkBearbeiten = dialog.find("input[name=vn_bearbeitenMode_radiogb]:checked").val() != "Nur ich",
            passw = dialog.find("#vn_pass_input").val(),
            kommentareErlaubt = dialog.find("#vn_komm_erlaubt").is(':checked'),
            bewertungenErlaubt = dialog.find("#vn_bew_erlaubt").is(':checked');
        
        var moderatorenIDs = [];
        
        for( var key in selectedModList)
        {
            moderatorenIDs.push(selectedModList[key][paramId]);
        }
        
        // Fehlerprüfung
        if(titel == "" || beschr == "")
        {
            showError("Bitte geben Sie mindestens einen Titel und eine Beschreibung an!");
            return;
        }
    
        var params = {};
        params[paramTitel] = escape(titel);
        params[paramSemester] = escape(semester);
        params[paramStudiengang] = escape(studiengang);
        params[paramBeschr] = escape(beschr);
        params[paramModeratorKkBearbeiten] = moderatorenKkBearbeiten;
        params[paramKommentareErlauben] = kommentareErlaubt;
        params[paramBewertungenErlauben] = bewertungenErlaubt;
        params[paramPasswort] = escape(passw);
        params[paramModeratoren] = moderatorenIDs;
        
        var ajax = ajaxCall(veranstaltungServlet,
                actionErstelleVeranst,
                function(response) {
                    showInfo("Veranstaltung \""+ titel +"\"wurde erfolgreich erzeugt.");
                    fillVeranstaltungsliste();  
                },
                params
            );
    });
    
    // Triggert das eigene Enter-Event wenn key 13 gedrückt wurde
    $("#vn_mod_input").keyup(function(e)
    {
        if(e.keyCode == 37) // Pfeil links
        {
            // TODO 
        }
        else if(e.keyCode == 38) // Pfeil hoch
        {
            // TODO
        }
        else if(e.keyCode == 39) // Pfeil rechts
        {
            // TODO
        }
        else if(e.keyCode == 40) // Pfeil runter
        {
            // TODO
        }
        else if(e.keyCode == 13) // ENTER
        {
            // TODO
            $("#vn_mod_vorschlag").children().first().trigger("click");
        }
        else if(e.keyCode == 27) // ESC
        {
            $("#vn_mod_input").val("");
            $("#vn_mod_input").trigger("keyup");
        }
        // Falls etwas eingegeben wurde suchen, sonst Feld leeren
        else if($("#vn_mod_input").val() != "")
        {
            clearTimeout(modSuchTimer);
            modSuchTimer = setTimeout(function(){
                var params = {};
                params[paramSuchmuster] = $("#vn_mod_input").val();
                ajaxCall(suchfeldServlet,
                    actionSucheBenutzer,
                    function(response) 
                    {
                        // Alle Suchergebnisse entfernen
                        $("#vn_mod_vorschlag").children().remove();
                        // Neue Suchergebnisse holen
                        var res = response[keyJsonArrResult];
                        // Falls welche existieren
                        if(res.length>0)
                        {
                            for(var i in res)
                            {
                                var x = $("<a>" + res[i][paramVorname] + " " + res[i][paramNachname] + "</a>");
    
                                (function(benutzer) 
                                        {
                                    // Click handler für den Sucheintrag
                                    x.click(function()
                                            {
                                        addItemToList(selectedModList, $("#vn_mod_list"), 
                                                benutzer[paramVorname] + " " + benutzer[paramNachname], 
                                                benutzer, undefined,undefined);
    
                                        $("#vn_mod_input").val("");
                                        $("#vn_mod_vorschlag").slideUp(100);
    
                                            });
                                        })(res[i]);
    

                                $("#vn_mod_vorschlag").append(x);
                                $("#vn_mod_vorschlag").append("<br>");
                            }
                            $("#vn_mod_vorschlag").slideDown(100);
                        }
                        else
                            $("#vn_mod_vorschlag").slideUp(100);

                    },
                    params
                );

            },400);
        }
        else
            $("#vn_mod_vorschlag").fadeOut();
    });
}

