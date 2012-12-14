var Logger = function(url){
    return {
        log: function(message, logid, customCallBack){
            customCallBack = customCallBack || function(){};
            $.ajax({
                type: 'GET',
                url: url,
                data: {
                    message: message,
                    logid: logid,
                    jsLogger: 1
                },
                success: customCallBack
                contentType: 'application/json; charset=utf-8',
            });
	    }
    };
};