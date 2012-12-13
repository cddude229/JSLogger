var Logger = function(url){
	return {
		log: function(message, logid){ 
			$.ajax({
				type: 'GET',
				url: url,
				data: {
					message: message,
					logid: logid,
					jsLogger: 1
				},
				contentType: 'application/json; charset=utf-8'
			});
		}
	}
};