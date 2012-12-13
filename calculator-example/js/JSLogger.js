function Logger(url){
    // Add object properties like this
    this.url = url;
};
// Add methods like this.
Logger.prototype.log = function(error, index){
    $.ajax({
	       type: 'POST',
	       url: this.url,
	       data: {error: error, index: index},
	       contentType: 'application/json; charset=utf-8'
	   });
};