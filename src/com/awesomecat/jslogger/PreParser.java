package com.awesomecat.jslogger;

public class PreParser {
	
	/**
	 * Allowed flags by our regular expression evaluator
	 */
	private static final String allowedFlags = "gim";

	/**
	 * Is the potential flag valid?
	 * @param potentialFlag The length 1 string to check
	 * @return True if valid, false otherwise
	 */
	public static boolean isValidFlag(String potentialFlag){
		return potentialFlag.length() == 1 && allowedFlags.indexOf(potentialFlag) >= 0;
	}

	/**
	 * Determines if a given regular expression is strict enough to meet out requirements.
	 * @param re Format as: /^ABC, 123$/gim
	 * @return
	 */
    public boolean validRegularExpression(String re) {
        // TODO: Write our regular expression evaluator
        /*
         * Steps before building:
         * 1) We need to determine which flags we support (i.e. /hi/gim, the "gim" part)
         * 
         * During evaluation:
         * 1) If null, return false
         * 2) Strip off valid flags from the end. If invalid flag is found, return false
         * 3) Strip off the / at the start and / at the end.
         * 4) If first character is not start anchor (^), return false
         * 5) If last character is not end anchor ($), return false
         * 6) Strip anchors.  At this point, we have our entire regular expression to validate.
         * 7) We need to not allow huge wildcards... i.e. we should ban the use of *, +, and {#,} (this last one leaves open arbitrarily long messages)
         * 		NOTE: We don't ban {,#} because they are willingly implementing a max limit
         * 
         * 8) At this point, I think everything should be valid and not overly inefficient
         * 
         * 
         * 
         * NOTE: What about if they escape that last /?   (i.e. /hi\/  wouldn't be valid)
         */ 
        return false;
    }

}
