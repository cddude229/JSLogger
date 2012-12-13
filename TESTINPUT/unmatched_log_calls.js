/**#
* @valid-duration 5
* @run-once true
* @window-size 2
* @validate /^unmatched_log_calls$/sim
* @id 5
#**/

logger.log("blah", "$id=5");
logger.log("blah", "$id=6");