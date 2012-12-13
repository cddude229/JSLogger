import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.awesomecat.jslogger.servlet.LoggerFilter;
import com.awesomecat.jslogger.servlet.PreParseFilter;

public class LaunchTestServer {

    public static void main(String[] args) throws Exception {
        System.out.println("'cause I can");
        Server server = new Server(8080);
        
        int options = ServletContextHandler.NO_SECURITY;
        ServletContextHandler context = new ServletContextHandler(server, "/", options);

        FilterHolder filter = new FilterHolder(LoggerFilter.class);
        FilterHolder filter2 = new FilterHolder(PreParseFilter.class);
        context.addFilter(filter, "/*", null);
        context.addFilter(filter2, "/*", null);
 
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
 
        resource_handler.setResourceBase(".");
        
        context.addServlet("DummyServlet", "/*");
        
        server.start();
        System.out.println("'cause I can");
        server.join();
    }
}
