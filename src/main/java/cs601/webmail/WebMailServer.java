package cs601.webmail;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.auth.AccessAuditFilter;
import cs601.webmail.util.Logger;
import cs601.webmail.util.PropertyExpander;
import org.apache.commons.lang3.ClassPathUtils;
import org.apache.log4j.BasicConfigurator;
import cs601.webmail.pages.DispatchServlet;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

import javax.servlet.DispatcherType;
import java.util.EnumSet;


/**
 * Created by yuanyuan on 10/22/14.
 */
public class WebMailServer implements WrapperListener{

    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();

        String classPath = "";

        try {
            classPath = WebMailServer.class.getResource("").getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Configuration configuration = Configuration.getDefault();

        String staticFilesDir = classPath + "static";
        String logDir = PropertyExpander.expandSystemProperties("${user.home}/webmail/logs");

        // under jsw
        if (staticFilesDir.indexOf("/jsw") > -1) {
            staticFilesDir = System.getProperty("user.dir") + "/res/static";
        }
        Server server = new Server(8080);


        System.out.println("Server starting...");
        System.out.println("----------------------------------------------------");
        System.out.println("Static Dir: " + staticFilesDir);
        System.out.println("Log Dir: " + logDir);
        System.out.println("User Dir: " + System.getProperty("user.dir"));
        System.out.println("Application Work Dir: " + configuration.get(Configuration.WORK_DIR));
        System.out.println("----------------------------------------------------");

        ServletContextHandler context = new
                ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Audit all the request. Log their path and more information to log file.
        FilterHolder auditFilter = new FilterHolder(AccessAuditFilter.class);
        context.addFilter(auditFilter, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));

        // Login checking.
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

        @Override
        public Integer start(String[] strings) {
                System.out.println("wrapper: starting...");

                try {
                        main(strings);
                } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error staring sever, " + e.getMessage());
                        return -1;
                }

                return 0;
        }

        @Override
        public int stop(int i) {
                System.out.println("wrapper: stopping... code=" + i);
                return 0;
        }

        @Override
        public void controlEvent(int event) {
                System.out.println("wrapper: control event, code=" + event);
                if ((event == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT) &&
                        (WrapperManager.isLaunchedAsService())) {
                } else {
                        WrapperManager.stop(0);
                }
        }
}

