/**#
* @valid-duration 5
* @run-once true
* @window-size 2
* @validate /^generate valid message$/sim
* @id validMessage
#**/

$(function(){
  logger = new Logger("http://localhost:8080/");
  $("button#valid-message").click(function(){
    logger.log("generate valid message", "$id=validMessage");				
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
	   $("div#results").prepend("<div>"+"message: "+message+",\n"+"logId: "+logId+",\n"+"server-accept?: "+data+"</div");
       }	       
     });
    return false;				
  });
});