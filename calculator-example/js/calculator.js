/**#
 * @valid-duration 5000
 * @run-once false
 * @window-size 1
 * @validate /^could not eval: [A-Za-z0-9\+\*\-]{1,10}$/sm
 * @id error
 #**/

/**#
 * @valid-duration 5000
 * @run-once false
 * @window-size 1
 * @validate /^user entered: [A-Za-z0-9\+\*\-]{1,10}$/sm
 * @id userInput
 #**/

var logger = Logger("http://localhost:8080/");
function pushButton(buttonValue) {
    if (buttonValue == 'C') {
        document.getElementById('screen').value = '';
    }
    else {
        document.getElementById('screen').value += buttonValue;
    }
}
function calculate(equation) {
    try {
        var answer = eval(equation);
	    logger.log("user entered: " + equation, "$id=userInput");
    } catch(ex) {
        logger.log("could not eval: " + equation, "$id=error");
    }
    document.getElementById('screen').value = answer;
}
