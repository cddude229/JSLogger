var Logger = function(url){
    return {
        log: function(message, logid, customCallBack){
            if(typeof customCallBack == "function"){
                customCallBack = customCallBack(message, logid);
            } else {
                customCallBack = function(){};
            }
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