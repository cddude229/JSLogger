package com.awesomecat.jslogger;

import java.io.*;
import java.util.Scanner;

public class JavaScriptFilePreParser {
	/**
	 * Takes the specified file and will evaluate it
	 * @param file The file to parse
	 * @param mapper The session mapper that we use to obtain IDs
	 * @return The now parsed JavaScript file to be rendered to the user 
	 * @throws FileNotFoundException
	 */
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
	public static String evaluateString(String content, SessionMapper mapper){
		// TODO: @Aaron Write our file parser
		return "";
	}

}
