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

logger.log("blah1", "$id=5");
logger.log("blah2", "$id=5");
logger.log("blah3", "$id=5");
logger.log("blah4", "$id=6");
logger.log("blah5", "$id=6");
logger.log("blah6", "$id=5");
logger.log("blah7", "$id=5");