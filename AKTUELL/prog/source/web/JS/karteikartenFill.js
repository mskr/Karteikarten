/**
 * 
 */
var emptyKarteiKarteDOM;
var sampleJSON1 = {
		paramId: 5,
		paramTitel: "Potenzgesetz I",
		paramType : "TEXT",
		paramInhalt : "dies ist ein beispieltext, hier könnten interessante informationen stehen. hier ist das leider nicht der fall.",
		paramVeranstaltung : "Analysis I ",
		paramAenderungsdatum : "2015-05-01 00:00:00",
		paramBewertung: 4
	
}
var sampleJSON2 = {
		paramId: 1,
		paramTitel: "Potenzgesetz I",
		paramType : "BILD",
		paramInhalt : "",
		paramVeranstaltung : "Analysis I ",
		paramAenderungsdatum : "2015-05-01 00:00:00",
		paramBewertung: 7
	
}
var sampleJSON3 = {
		paramId: 7,
		paramTitel: "Potenzgesetz I",
		paramType : "VIDEO",
		paramInhalt : "",
		paramVeranstaltung : "Analysis I ",
		paramAenderungsdatum : "2015-05-01 00:00:00",
		paramBewertung: -5
	
}

$(document).ready(function(){
	emptyKarteiKarteTEXT = $("#textKarteikarteTest").clone();
	emptyKarteiKarteTEXT.css("display","block");
	
	
	emptyKarteiKarteBILD = $("#bildKarteikarteTest").clone();
	emptyKarteiKarteBILD.css("display","block");
	
	emptyKarteiKarteVIDEO = $("#videoKarteikarteTest").clone();
	emptyKarteiKarteVIDEO.css("display","block");

	fillKarteiKarte(emptyKarteiKarteTEXT, sampleJSON1);
	fillKarteiKarte(emptyKarteiKarteBILD, sampleJSON2);
	fillKarteiKarte(emptyKarteiKarteVIDEO, sampleJSON3);
	
});

fillKarteiKarte = function(domElem, json){
	//set Rating
	domElem = domElem.clone();
	domElem.attr("id",json["paramId"]);
	domElem.find(".kk_votestat").html(json["paramBewertung"]);
	
	// detect type and add content
	switch (json["paramType"]) {
    case "TEXT":
    	domElem.find(".inhalt_text").html(json["paramInhalt"]);
    	break;
    case "BILD":
    	image = $(document.createElement("img"));
    	image.attr("src","files/images/"+json["paramId"]+".png");
    	domElem.find(".inhalt_bild").html(image);
    	break;
    case "VIDEO":
    	video = $(document.createElement("video"));
    	video.addClass("kk_video");
    	video.attr("autobuffer","");
    	video.attr("autoplay","");
    	video.attr("controls","");
    	video.append("<source src='files/videos/"+json["paramId"]+".mp4' type='video/mp4'></source>");
    	video.append("<source src='files/videos/"+json["paramId"]+".ogg' type='video/ogg'></source>");
    	video.append("Your browser does not support the video tag.");
    	domElem.find(".inhalt_video").html(video);
    	break;
	}
	
	$("#kk_all").append(domElem);
	
}