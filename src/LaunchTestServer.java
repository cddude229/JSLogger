import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.awesomecat.jslogger.servlet.LoggerFilter;
import com.awesomecat.jslogger.servlet.PreParseFilter;

public class LaunchTestServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        
        int options = ServletContextHandler.NO_SECURITY;
        ServletContextHandler context = new ServletContextHandler(server, "/", options);

        FilterHolder filter = new FilterHolder(LoggerFilter.class);
        FilterHolder filter2 = new FilterHolder(PreParseFilter.class);
        context.addFilter(filter, "/*", null);
        context.addFilter(filter2, "/*.js", null);
 
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
 
        resource_handler.setResourceBase(".");
 
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
        
        server.setHandler(handlers);
        
        server.start();

        server.join();
    }
}
