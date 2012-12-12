package com.awesomecat.jslogger;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.awesomecat.jslogger.storage.Expression;
import com.awesomecat.jslogger.storage.HashMapStore;

public class JavaScriptFilePreParser {
	/**
	 * Takes the specified file and will evaluate it
	 * @param file The file to parse
	 * @param mapper The session mapper that we use to obtain IDs
	 * @return The now parsed JavaScript file to be rendered to the user 
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args){
		String content = 
			"/**# \n" +
			"* @run-once true \n" +
			"* @window-size 2 \n" +
			"* @validate /^hello-world$/sim \n" +
			"* @id 5" +
			"#**/" +
			"logger.log(\"blah\", \"$id=5\")";
		SessionMapper mapper = new SessionMapper(5, new HashMapStore());
		System.out.println(evaluateString(content, mapper));
		
	}
	
	public static String evaluateFile(File file, SessionMapper mapper) throws FileNotFoundException {
		if(file.exists() == false) throw new RuntimeException("File does not exist.");
		if(file.isDirectory() == true) throw new RuntimeException("File is a directory.");
		if(file.canRead() == false) throw new RuntimeException("Must be able to read the file.");

	    StringBuilder text = new StringBuilder();
	    String NL = System.getProperty("line.separator");
	    Scanner scanner = new Scanner(new FileInputStream(file), "UTF-8");
	    try {
	    	while (scanner.hasNextLine()){
	    		text.append(scanner.nextLine() + NL);
	    	}
	    } finally {
	    	scanner.close();
	    }
		return evaluateString(text.toString(), mapper);
	}

	/**
	 * Will evaluate a string and add the expressions + associated IDs to mapper
	 * @param content
	 * @param mapper
	 * @return
	 */
	public static String extract(String str_id, String comment_block){
		Pattern p = Pattern.compile("\\s*\\* "+str_id+"\\s([^\\s]+)", Pattern.DOTALL);
		Matcher regexMatcher = p.matcher(comment_block);
		String output = "";
		while (regexMatcher.find()) {
			System.out.println(str_id + " "+ regexMatcher.group(1));
			output = regexMatcher.group(1);
		} 
		return output;
	}
	public static String evaluateString(String content, SessionMapper mapper){
		// TODO: @Aaron Write our file parser
		ArrayList<String> comment_blocks = new ArrayList<String>();
		//grabs comment values
		Pattern p1 = Pattern.compile("/\\*\\*#(.*?)#\\*\\*/", Pattern.DOTALL);
		Matcher regexMatcher1 = p1.matcher(content);
		while (regexMatcher1.find()) {
			System.out.println("Comment---------------");
			System.out.println(regexMatcher1.group(1));
			comment_blocks.add(regexMatcher1.group(1));
		} 
		//deletes comments
		Pattern p2 = Pattern.compile("/\\*\\*#.*?#\\*\\*/", Pattern.DOTALL);
		Matcher regexMatcher2 = p2.matcher(content);
		String new_content = regexMatcher2.replaceAll("");
		//parses comments
		System.out.println("parsing---------------");
		ArrayList<Integer> val_dur_list = new ArrayList<Integer>();
		ArrayList<String> express_list = new ArrayList<String>();
		ArrayList<Boolean> run_once_list = new ArrayList<Boolean>();
		ArrayList<Integer> window_list = new ArrayList<Integer>();
		ArrayList<String> id_list = new ArrayList<String>();
		for(int i=0; i<comment_blocks.size(); i++){
			String comment_block = comment_blocks.get(i);
			val_dur_list.add(0);
			express_list.add(extract("@validate", comment_block).trim());
			run_once_list.add(Boolean.parseBoolean(extract("@run-once", comment_block).trim()));
			window_list.add(Integer.parseInt(extract("@window-size", comment_block).trim()));
			id_list.add(extract("@id", comment_block).trim());
		}
		
		ArrayList<String> new_id_list = new ArrayList<String>();
		for(int i=0; i<id_list.size(); i++){
			Expression expression = new Expression(val_dur_list.get(i),express_list.get(i),
					run_once_list.get(i),window_list.get(i));
			new_id_list.add(mapper.registerExpressionAndGetAssociatedId(expression));
		}
		//update new_content
		Pattern p3 = Pattern.compile("$id=([^\\s]+)", Pattern.DOTALL);
		Matcher regexMatcher3 = p3.matcher(new_content);
		if (regexMatcher3.find()) {
		    System.out.println(regexMatcher3.group(1));
		}
		String new_content2 = regexMatcher3.replaceAll("");
		return new_content;
	}

}
