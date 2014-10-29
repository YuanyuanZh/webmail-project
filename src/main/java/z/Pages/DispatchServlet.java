package z.Pages;

/**
 * Created by yuanyuan on 10/15/14.
 */

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Logger;
import z.managers.ErrorManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispatchServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DispatchServlet.class);

    public static Map<String,Class> mapping = new HashMap<String, Class>();
    static {
        mapping.put("/", HomePage.class);
        mapping.put("/inbox", InboxPage.class);
        //mapping.put("/register",register.class");
        //mapping.put("/edit_profile",edit_profile.class");
        //mapping.put("/search",search.class");
        //mapping.put("/register",register.class");
    }


    /*static ServiceManager serviceManager;

    @Override
    public void init() throws ServletException {

        // init service container
        if (serviceManager == null) {
            serviceManager = ServiceManagerFactory.create();
        }

    }

   /* public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        String uri = request.getRequestURI();
        Page p = createPage(uri, request, response);
        if ( p == null ) {
            response.sendRedirect("/files/error.html");
            return;
        }
        //response.setContentType("text/html");

        RequestContextFactory.create(request, response, serviceManager);

        try {
            p.generate();
        }
        finally {
            RequestContext context = RequestContext.getCurrentInstance();
            if (context != null) {
                context.release();
            }
        }
     }*/
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        String uri = request.getRequestURI();
        Page p = createPage(uri, request, response);
        if ( p==null ) {
            response.sendRedirect("/files/error.html");
            return;
        }
        response.setContentType("text/html");
        p.generate();
    }

    public Page createPage(String uri,
                           HttpServletRequest request,
                           HttpServletResponse response)
    {
        Class pageClass = mapping.get(uri);
        try {

            Constructor<Page> ctor = pageClass.getConstructor(HttpServletRequest.class,
                    HttpServletResponse.class);
            return ctor.newInstance(request, response);
        }
        catch (Exception e) {
            LOGGER.error("Create page failed for :" + uri);
            ErrorManager.instance().error(e);
        }
        return null;
    }

}
