import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dummy content serving servlet.  Just sends everything as output
 * THIS IS NOT SECURE AND IS USED PURELY AS AN EXAMPLE
 * @author chris
 *
 */
public class DummyServlet extends HttpServlet {
	private static final long serialVersionUID = 3491061636959911534L;
	
	private String getPath(HttpServletRequest req) {
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();
		return "." + servletPath + (pathInfo == null?"":pathInfo);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String path = getPath(req);
		File file = new File(path);
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
	    
	    resp.getWriter().write(text.toString());
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}