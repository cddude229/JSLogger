import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.awesomecat.jslogger.servlet.LoggerFilter;

public class LaunchTestServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        
        int options = ServletContextHandler.NO_SECURITY;
        ServletContextHandler context = new ServletContextHandler(server, "/", options);

        FilterHolder filter = new FilterHolder(LoggerFilter.class);
        context.addFilter(filter, "/*", null);
        
        context.addServlet("HelloServlet", "/");
        
        server.start();

        server.join();
    }
}
