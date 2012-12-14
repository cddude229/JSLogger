/**#
* @valid-duration 5
* @run-once false
* @window-size 2
* @validate /^generate valid message$/sim
* @id validMessage
#**/

/**#
* @valid-duration 5
* @run-once false
* @window-size 2
* @validate /^message [0-9]{1,4}$/sim
* @id loopMessage
#**/

/**#
* @valid-duration 5
* @run-once false
* @window-size 2
* @validate /^a{5121}$/sim
* @id largeMessage
#**/


$(function(){
  var logger = new Logger("http://localhost:8080/");
  $("button#valid-message").click(function(){
    logger.log("generate valid message", "$id=validMessage");
    return false;								       
  });
  $("button#loop-message").click(function(){
    for(var i=0; i<5000; i++){
	logger.log("message "+val, "$id=loopMessage");	
    }
    return false;								       
  });
      
  $("button#large-message").click(function(){
    var message = "";
    for(var i=0; i<5121; i++){
      message+="a";
    }
    logger.log(message, "$id=largeMessage");	
    return false;								       
  });
      
      
  $("button#submit").click(function(){
    $.ajax({
       type: 'GET',
       url: "http://localhost:8080/",
       data: {
	   message: $("textarea#message-input").val(),
	   logid: $("textarea#logId-input").val(),
	   jsLogger: 1
       },
       contentType: 'application/json; charset=utf-8',
       success:function(data){
	   var message = $("textarea#message-input").val();
	   var logId = $("textarea#logId-input").val();
	   $("div#results").prepend("<div>"+"message: "+message+",\t\t"+"logId: "+logId+",\t\t"+"server-accept?: "+data+"</div");
       }	       
     });
    return false;				
  });
});