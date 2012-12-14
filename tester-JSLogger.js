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
                success:function(data){
		    $("div#results").prepend("<div>"+"message: "+message+",\t\t"+"logId: "+logid+",\t\t"+"server-accept?: "+data+"</div");  
		},
                contentType: 'application/json; charset=utf-8',
            });
	    }
    };
};