/**
 * Steuert alle html-5 VideoPlayer
 */

$(document).ready(
	// Dieses Skript muss ganz am Ende des Dokuments stehenn.
	// Stellt sicher, dass das Dokument geladen wurde, bevor das Script ausgefuehrt wird (wie JQuery document.ready)
	function() {
		var controls; // Enthaelt das video_controls-DIV der letzten Interaktion
		var video; // Enthaelt das video Element der letzten Interaktion
		var playPauseBtn; // Enthaelt den Play-Pause Button der letzten Interaktion
		var muteBtn; // usw.
		var fullscreenBtn;
		var suchleiste;
		var volRegler;
		var volReglerValueTmp = 1.0; // Merke Lautstaerke vor dem Stummschalten, um sie wiederherstellen zu koennen.
		// Registriere fuer alle Play Buttons einen Eventlistener.
		var playPauseBtns = document.getElementsByClassName("play_pause");
		var playPauseClickCount = 0;
		for(var i=0; i<playPauseBtns.length; ++i) {
			playPauseBtn = playPauseBtns[i];
			playPauseBtn.addEventListener("click", function() {
				// Erst wenn der User eine Interaktion mit dem Video macht (z.B. auf Play drueckt),
				// kann das Script wissen, welches Video gesteuert werden soll.
				controls = playPauseBtn.parentNode;
				video = controls.previousSibling.previousSibling;
				// Nachdem bekannt ist welches Video gesteuert werden soll,
				// registriere den Eventlistener um die Suchleiste laufen zu lassen.
				if(playPauseClickCount==0) { // Registriere den Eventlistener nur einmal
					video.addEventListener("timeupdate", function() {
						// Wird kontinuierlich aufgerufen, waehrend das Video laeuft.
						var value = (100 / video.duration) * video.currentTime;
						var sl = playPauseBtn.nextSibling.nextSibling;
						sl.value = value;
						var kk_wrapper = controls.parentNode.parentNode.parentNode;
						var timebatch = kk_wrapper.getElementsByClassName("video_timebadge")[0];
						var secg = Math.round(video.currentTime);
						var std = Math.floor(secg/3600);
						var min = Math.floor(secg/60);
						var sec = secg - (min*60);
						if(std<10) std = "0"+std;
						if(min<10) min = "0"+min;
						if(sec<10) sec = "0"+sec;
						var formatted = ""+std+":"+min+":"+sec;
						timebatch.innerHTML = formatted;
					});
					video.addEventListener("ended", function() {
						console.log("Ended!")
						// Reset nach dem Ende des Videos
						playPauseBtn.firstChild.setAttribute("class", "mega-octicon octicon-playback-rewind");
					});
				}
				if(video.paused == true) {
					video.play();
					playPauseBtn.firstChild.setAttribute("class", "mega-octicon octicon-playback-pause");
				} else {
					video.pause();
					playPauseBtn.firstChild.setAttribute("class", "mega-octicon octicon-playback-play");
				}
				++playPauseClickCount;
			});
		}
		// Registriere fuer alle Stumm Buttons einen Eventlistener.
		var muteBtns = document.getElementsByClassName("stumm");
		for(var i=0; i<muteBtns.length; ++i) {
			muteBtn = muteBtns[i];
			muteBtn.addEventListener("click", function() {
				controls = muteBtn.parentNode;
				video = controls.previousSibling.previousSibling;
				if(video.muted == false) {
					video.muted = true;
					muteBtn.firstChild.setAttribute("class", "octicon octicon-mute");
					volRegler = muteBtn.nextSibling.nextSibling;
					volReglerValueTmp = volRegler.value;
					volRegler.value = 0.0;
				} else {
					video.muted = false;
					muteBtn.firstChild.setAttribute("class", "octicon octicon-unmute");
					volRegler.value = volReglerValueTmp;
				}
			});
		}
		// Registriere fuer alle Vollbild Buttons einen Eventlistener.
		var fullscreenBtns = document.getElementsByClassName("vollbild");
		for(var i=0; i<fullscreenBtns.length; ++i) {
			fullscreenBtn = fullscreenBtns[i];
			fullscreenBtn.addEventListener("click", function() {
				controls = fullscreenBtn.parentNode;
				video = controls.previousSibling.previousSibling;
				if(video.requestFullscreen) {
					video.requestFullscreen(); // W3C Standard
				} else if(video.mozRequestFullScreen) {
					video.mozRequestFullScreen(); // Firefox specific
				} else if(video.webkitRequestFullscreen) {
					video.webkitRequestFullscreen(); // Chrome and Safari specific
				}
			});
		}
		// Registriere fuer alle Suchleisten einen Eventlistener.
		var suchleisten = document.getElementsByClassName("suchleiste");
		for(var i=0; i<suchleisten.length; ++i) {
			suchleiste = suchleisten[i];
			suchleiste.addEventListener("change", function() {
				controls = suchleiste.parentNode;
				video = controls.previousSibling.previousSibling;
				// Zeit berechnen zu der gesprungen werden soll
				var time = video.duration * (suchleiste.value / 100);
				// Video auf diese Zeit setzen
				video.currentTime = time;
			});
			suchleiste.addEventListener("mousedown", function() {
				controls = suchleiste.parentNode;
				video = controls.previousSibling.previousSibling;
				video.pause();
			});
			suchleiste.addEventListener("mouseup", function() {
				controls = suchleiste.parentNode;
				video = controls.previousSibling.previousSibling;
				video.play();
				playPauseBtn = suchleiste.previousSibling.previousSibling;
				playPauseBtn.firstChild.setAttribute("class", "mega-octicon octicon-playback-pause");
			});
		}
		// Registriere fuer alle Lautstaerkeregler einen Eventlistener
		var volReglerArray = document.getElementsByClassName("lautstaerke");
		for(var i=0; i<volReglerArray.length; ++i) {
			volRegler = volReglerArray[i];
			volRegler.addEventListener("change", function() {
				controls = volRegler.parentNode;
				video = controls.previousSibling.previousSibling;
				video.volume = volRegler.value;
				muteBtn = volRegler.previousSibling.previousSibling;
				if(volRegler.value == 0.0) {
					muteBtn.firstChild.setAttribute("class", "octicon octicon-mute");
				} else {
					muteBtn.firstChild.setAttribute("class", "octicon octicon-unmute");
				}
			});
		}
		
	}
);