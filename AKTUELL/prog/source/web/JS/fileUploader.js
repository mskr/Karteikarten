
function uploadFile(file, successFkt, uploadAction) 
{
	var formData = new FormData();
	formData.append('file', file);
	formData.append('action', uploadAction);
	$.ajax({
		type: "POST",
		url: fileUploadServlet,
		enctype: "multipart/form-data",
		data: formData,
		processData: false,
		contentType: false,
		success: successFkt
	});
}