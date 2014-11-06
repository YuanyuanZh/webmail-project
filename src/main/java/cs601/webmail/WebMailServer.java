package cs601.webmail;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.page.DispatchServlet;
import cs601.webmail.util.PropertyExpander;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Created by yuanyuan on 11/3/14.
 */
public class WebMailServer {

    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();

        String classPath = WebMailServer.class.getResource("/").getPath();

//        if ( args.length<2 ) {
//            System.err.println("java cs601.webmail.Server static-files-dir log-dir");
//            System.exit(1);
//        }
//
//        String staticFilesDir = args[0];
//        String logDir = args[1];

        String staticFilesDir = classPath + "static";
        String logDir = PropertyExpander.expandSystemProperties("${user.home}/logs");

        Server server = new Server(8080);

        System.out.println("Server starting...");
        System.out.println("----------------------------------------------------");
        System.out.println("Static Dir: " + staticFilesDir);
        System.out.println("Log Dir: " + logDir);
        System.out.println("----------------------------------------------------");

        ServletContextHandler context = new
                ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        FilterHolder loginFilter = new FilterHolder(AuthenticationCheckFilter.class);
        context.addFilter(loginFilter, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));


        // add a simple Servlet at "/dynamic/*"
        ServletHolder holderDynamic = new ServletHolder("dynamic", DispatchServlet.class);
        context.addServlet(holderDynamic, "/*");

        // add special pathspec of "/home/" content mapped to the homePath
        ServletHolder holderHome = new ServletHolder("static-home", DefaultServlet.class);
        holderHome.setInitParameter("resourceBase", staticFilesDir + "/files");
        holderHome.setInitParameter("dirAllowed","true");
        holderHome.setInitParameter("pathInfoOnly","true");
        context.addServlet(holderHome, "/files/*");

        ServletHolder resourcesHome = new ServletHolder("resources", DefaultServlet.class);
        resourcesHome.setInitParameter("resourceBase", staticFilesDir + "/resources");
        resourcesHome.setInitParameter("dirAllowed","true");
        resourcesHome.setInitParameter("pathInfoOnly","true");
        context.addServlet(resourcesHome, "/resources/*");

        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // It is important that this is last.
        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("resourceBase","/tmp/foo");
        holderPwd.setInitParameter("dirAllowed","true");
        context.addServlet(holderPwd, "/");

        // log using NCSA (common log format)
        // http://en.wikipedia.org/wiki/Common_Log_Format
        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setFilename(logDir + "/yyyy_mm_dd.request.log");
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(true);
        requestLog.setLogCookies(false);
        requestLog.setLogTimeZone("GMT");
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        requestLogHandler.setServer(server);

        server.start();
        server.join();
    }
}


