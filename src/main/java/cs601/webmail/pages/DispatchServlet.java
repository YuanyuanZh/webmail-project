package cs601.webmail.pages;

/**
 * Created by yuanyuan on 10/15/14.
 */

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cs601.webmail.Constants;
import cs601.webmail.Configuration;

import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.frameworks.web.RequestContextFactory;
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
        mapping.put("/",HomePage.class);
        mapping.put("/inbox",InboxPage.class);
        mapping.put("/login",LoginPage.class);
        mapping.put("/logout", LogoutPage.class);
        mapping.put("/rest/mail/sync", SyncMailsPage.class);
        mapping.put("/rest/mail/list", MailListPage.class);
        mapping.put("/rest/mail/read", ReadMailPage.class);
        mapping.put("/register", RegisterPage.class);
        mapping.put("/registerNext", RegisterNextPage.class);

        mapping.put("/rest/user/profile", ProfilePage.class);
        mapping.put("/rest/mail/control", MailActionsPage.class);
        mapping.put("/contacts", ContactsPage.class);


    }

    private Configuration configuration;

   // private PersistenceContextFactory persistenceContextFactory;

    @Override
    public void init() throws ServletException {

        configuration = Configuration.getDefault();

       // persistenceContextFactory = new PersistenceContextFactory();
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        String uri = request.getRequestURI();
        Page p = createPage(uri);

        if ( p == null ) {
            response.sendRedirect("/files/error.html");
            return;
        }

        if (Constants.DEBUG_MODE)
            System.out.println("[DEBUG] request URI " + request.getRequestURI());

        try {

            //preparePersistenceContext();

            RequestContextFactory.create(request, response);

            p.generate();

        }
        catch (Exception e) {

            if ("STREAM".equalsIgnoreCase(e.getMessage())) {
                // ignore
            } else {
                e.printStackTrace();
                response.sendRedirect("/files/error.html?code=1000&msg=" + URLEncoder.encode(e.getMessage(), "utf-8"));
            }
        }
        finally {

            RequestContext context = RequestContext.getCurrentInstance();
            if (context != null) {
                context.release();
            }

            //releasePersistenceContext();
        }
     }
    public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException, IOException
    {
        doGet(req, resp);
    }

    public Page createPage(String uri)
    {
        Class pageClass = mapping.get(uri);
        try {

            if (pageClass == null) {
                return null;
            }

            Constructor<Page> ctor = pageClass.getConstructor();
            return ctor.newInstance();
        }
        catch (Exception e) {
            LOGGER.error("Create page failed for :" + uri);
            ErrorManager.instance().error(e);
        }
        return null;
    }

}
