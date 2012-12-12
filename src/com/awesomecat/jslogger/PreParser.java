package com.awesomecat.jslogger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreParser {
	
	/**
	 * Allowed flags by our regular expression evaluator
	 */
	public static final String allowedFlags = "gim";
	// TODO: @Chris: Update the allowed flags

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
    public static boolean validRegularExpression(String re) {
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
         * 		NOTE2: Be careful to not ban \* or \+... those are valid escaped characters
         * 
         * 8) At this point, I think everything should be valid and not overly inefficient
         * 9) Make sure it is a valid expression based on the regular expression validator(CHRIS)
         * 
         * 
         * NOTE: What about if they escape that last /?   (i.e. /hi\/  wouldn't be valid)
         */ 
    	if (re != null && !re.isEmpty()) {
    		  return false;
    	}
    	int flag_index=0;
    	for(int i= (re.length()-1);i>=0; i-- ){
    		String cur_char = re.substring(i,i+1);
    		if(!isValidFlag(cur_char)){
    			return false;
    		}
    		if(cur_char == "/"){
    			flag_index = i; //is this right?
    			break;
    		}
    	}
    	String flagless = re.substring(flag_index, flag_index+1);
    	if(flagless.substring(0,1) != "/" 
    		&& flagless.substring(flagless.length()-1,flagless.length()) != "/"
    		&& flagless.substring(flagless.length()-2,flagless.length()-1)!= "\\"){
    		return false;
    	}
    	String no_slash = flagless.substring(1,flagless.length()-1); //is this right?
    	if(no_slash.substring(0,1) != "^" 
    		&& no_slash.substring(no_slash.length()-1,no_slash.length()) != "$"){
    		return false;
    	}
    	String no_anchor = no_slash.substring(1,no_slash.length()-1); //is this right?
    	for(int i= 0;i<no_anchor.length(); i++ ){
    		String cur_char = no_anchor.substring(i,i+1);
    		String before_char = no_anchor.substring(i-1,i);
    		if((cur_char == "+" || cur_char == "*") && before_char!="\\"){
    				return false;
    		}
    		if(cur_char == "{" && before_char!="\\"){
    			//2 bad cases: {,} and {#,}
    			if(no_anchor.substring(i,i+3) == "{,}"){
    				return false;
    			}
    			Pattern p = Pattern.compile("\\{\\d+,\\}");
    			if(matchesPattern(p,no_anchor.substring(i))){ //might be slow
    				return false;
    			}
    		}
    	}
    	return true;
    }
    //ADDED
    private static boolean matchesPattern(Pattern p,String sentence) {
        Matcher m = p.matcher(sentence);

        if (m.find()) {
          return true;
        }

        return false;
      }

}
