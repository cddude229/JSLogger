import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class Test extends AbstractHandler {
    static Logger logger = Logger.getLogger(Test.class);

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Hello World</h1>");
        logger.info(request.getPathInfo());
    }

    public static void main(String[] args) throws Exception {
        // Set up a simple configuration that logs on the console.
        BasicConfigurator.configure();

        Server server = new Server(8080);
        server.setHandler(new Test());

        server.start();
        server.join();
    }
}
