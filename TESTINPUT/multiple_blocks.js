/**#
* @valid-duration 5
* @run-once true
* @window-size 2
* @validate /^multiple_blocks1$/sim
* @id 5
#**/

/**#
* @valid-duration 5
* @run-once true
* @window-size 2
* @validate /^multiple_blocks2$/sim
* @id 6
#**/

logger.log("blah", "$id=5");
logger.log("blah", "$id=5");
logger.log("blah", "$id=5");
logger.log("blah", "$id=6");
logger.log("blah", "$id=6");
logger.log("blah", "$id=5");
logger.log("blah", "$id=5");