function Logger(url){
    // Add object properties like this
    this.url = url;
};
// Add methods like this.
Logger.prototype.log = function(message, logid){
    $.ajax({
	       type: 'GET',
	       url: this.url,
	       data: {message: message, logid: logid, jsLogger:1},
	       contentType: 'application/json; charset=utf-8'
	   });
};