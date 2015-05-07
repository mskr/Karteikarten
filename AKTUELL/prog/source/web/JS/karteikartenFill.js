/**
 * 
 */
var emptyKarteiKarteDOM;
var sampleJSON1 = {};
sampleJSON1[paramId] = 5;
sampleJSON1[paramTitel] = "Potenzgesetz I";
sampleJSON1[paramType] = "TEXT";
sampleJSON1[paramInhalt] = "dies ist ein beispieltext, hier könnten interessante informationen stehen. hier ist das leider nicht der fall.";
sampleJSON1[paramVeranstaltung] = "Analysis I ";
sampleJSON1[paramAenderungsdatum] = "2015-05-01 00:00:00";
sampleJSON1[paramBewertung] = 4;


var sampleJSON2 = {};
sampleJSON2[paramId] = 1;
sampleJSON2[paramTitel] = "Potenzgesetze II";
sampleJSON2[paramType] = "BILD";
sampleJSON2[paramInhalt] = "";
sampleJSON2[paramVeranstaltung] = "Analysis I ";
sampleJSON2[paramAenderungsdatum] = "2015-05-01 00:00:00";
sampleJSON2[paramBewertung] = 9;


var sampleJSON3 = {};
sampleJSON3[paramId] = 7;
sampleJSON3[paramTitel] = "Potenzgesetze IIi";
sampleJSON3[paramType] = "VIDEO";
sampleJSON3[paramInhalt] = "";
sampleJSON3[paramVeranstaltung] = "Analysis I ";
sampleJSON3[paramAenderungsdatum] = "2015-05-01 00:00:00";
sampleJSON3[paramBewertung] = -5;


$(document).ready(function(){
	
	buildKarteikarte(sampleJSON1);
	buildKarteikarte(sampleJSON2);
	buildKarteikarte(sampleJSON3);
});

fillKarteiKarte = function(domElem, json){
	//set Rating
	
	domElem = domElem.clone();
	domElem.find(".kk_votestat").html(json[paramBewertung]);
	
	// detect type and add content
	switch (json[paramType]) {
    case "TEXT":
    	console.log(json[paramInhalt]);
    	domElem.find(".inhalt_text").html(json[paramInhalt]);
    	break;
    case "BILD":
    	image = $(document.createElement("img"));
    	image.attr("src","files/images/"+json[paramId]+".png");
    	domElem.find(".inhalt_bild").html(image);
    	break;
    case "VIDEO":
    	video = $(document.createElement("video"));
    	video.addClass("kk_video");
    	video.attr("autobuffer","");
//    	video.attr("autoplay",""); 
    	video.attr("controls","");
    	video.append("<source src='files/videos/"+json[paramId]+".mp4' type='video/mp4'></source>");
    	video.append("<source src='files/videos/"+json[paramId]+".ogg' type='video/ogg'></source>");
    	video.append("Your browser does not support the video tag.");
    	domElem.find(".inhalt_video").html(video);
    	break;
	}
	
	$("#kk_all").append(domElem);
	
}