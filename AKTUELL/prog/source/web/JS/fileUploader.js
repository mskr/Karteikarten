
function uploadFile(file, successFkt, uploadAction, params, beforeFkt, completeFkt) 
{
	var formData = new FormData();
	formData.append('file', file);
	formData.append('action', uploadAction);
	for(var key in params)
		formData.append(key, params[key]);
		
	$.ajax({
		type: "POST",
		url: fileUploadServlet,
		enctype: "multipart/form-data",
		data: formData,
		processData: false,
		contentType: false,
		beforeSend: beforeFkt,
		success: successFkt,
		complete: completeFkt
	});
}