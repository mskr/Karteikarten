$(document).ready(function(){
	UPLOADIDSET = -1;
//	UPLOAD_ID = -1;
	UPLOADTYPE = "";
	uploadInProgress = false;
	Dropzone.autoDiscover = false;

	myDropzoneConfig = {
		url : fileUploadServlet,
		acceptedFiles : ".jpeg,.jpg,.png,.mp4",
		autoDiscover : false,
		autoProcessQueue : true,
		createImageThumbnails : true,
		previewTemplate : $("#kk_PreviewTemplate").clone().show().prop('outerHTML') ,
		uploadMultiple : false,
//		addRemoveLinks : true,
		maxFiles : 1,

		// Datei annehmen
		accept : function(file, done) {
			var ext = file.name.substr(file.name.lastIndexOf('.') + 1);
			if (ext == "png" || ext == "jpg" || ext == "jpeg" || ext == "mp4") {
				done();
			} else {
				done("Ungültige Datei");
			}
		},
		sending : function(file, xhr, formData) {
			console.log("sending");
			formData.append('file', file);

			var ext = file.name.substr(file.name.lastIndexOf('.') + 1);
			var uploadAction;
			if (ext == "png" || ext == "jpg" || ext == "jpeg") {
				uploadAction = actionUploadKKBild;
			} else if (ext == "mp4") {
				uploadAction = actionUploadKKVideo;
			} else {
				return false;
			}
			formData.append('action', uploadAction);
			uploadInProgress = true;
		},
		success : function(file, response) {
			showInfo("Datei hochgeladen");
			console.log("sucess");

			var ext = file.name.substr(file.name.lastIndexOf('.') + 1);
			UPLOADTYPE = ext;
			UPLOADIDSET = response.strResult;
			uploadInProgress = false;
		},
		maxfilesexceeded : function(file) {
			console.log("max files reached");
			this.removeAllFiles();
			this.addFile(file);
		},
		init : function() {
			this.on("addedfile", function(file) {
				console.log("added file");
			});

			this.on("removedfile", function(file) {
				console.log("removed file");
				UPLOADTYPE = "";
				UPLOADIDSET = -1;
				uploadInProgress = false;
			});
		}
	};
});