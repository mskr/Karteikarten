/**
 * @author mk
 */

function buildKarteikarte(karteikarteJson)
{
    var kkId = karteikarteJson[paramId],
        kkTitel = karteikarteJson[paramTitel];
        kkType = karteikarteJson[paramType];
        kkVeranstaltung = karteikarteJson[paramVeranstaltung];
        kkInhalt = karteikarteJson[paramInhalt];
        kkBewertung = karteikarteJson[paramBewertung];
        kkAenderungsdatum = karteikarteJson[paramAenderungsdatum];
    var kkHtmlStr = 
        "<div id='kk_"+kkId+"_wrapper' class='kk_wrapper'>" +
            "<div class='kk_votes'>" +
                "<a class='kk_voteup'><span class='mega-octicon octicon-triangle-up'></span></a>" +
                "<div class='kk_votestat'></div>" +
                "<a class='kk_votedown'><span class='mega-octicon octicon-triangle-down'></span></a>" +
            "</div>" +
            "<div class='kk_optionen'>" +
                "<a class='option permalink tiptrigger tipabove' data-tooltip='Link kopieren'>" +
                    "<span class='octicon octicon-link'></span>" +
                "</a>" +
                "<a class='option loeschen tiptrigger tipabove' data-tooltip='Karteikarte löschen'>" +
                    "<span class='octicon octicon-trashcan'></span>" +
                "</a>" +
                "<a class='option einfuegen tiptrigger tipabove' data-tooltip='Karteikarte unter dieser Karteikarte einfügen'>" +
                    "<span class='octicon octicon-plus'></span>" +
                "</a>" +
                "<a class='option attribute attr_popup_open tiptrigger tipabove' data-tooltip='Attribute ändern'>" +
                    "<span class='octicon octicon-checklist'></span>" +
                "</a>" +
            "</div>" +
            "<div class='kk_notizen' contenteditable></div>";
    
    if(kkType == paramKkText)
    {
        kkHtmlStr +=
            "<div class='kk inhalt_text' contenteditable></div>";
    }
    else if(kkType == paramKkBild)
    {
        kkHtmlStr +=
            "<div class='kk inhalt_bild' contenteditable></div>";
    }
    else if(kkType == paramKkVideo)
    {
        kkHtmlStr +=
            "<div class='kk inhalt_video' contenteditable></div>";
    }
            
    kkHtmlStr +=
            "<div class='kk_kommbox'>" +
                "<div class='kk_kommheader'><span class='octicon octicon-comment-discussion'></span> Kommentare (<span class='kk_kommzaehler'></span>)</div>" +
                "<div class='kk_kommerstellen'>" +
                    "<textarea placeholder='Neues Thema beginnen...'></textarea>" +
                "</div>" +
            "</div>" +
        "</div>";
    fillKarteiKarte($(kkHtmlStr),karteikarteJson);
}