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
import cs601.webmail.pages.contact.*;
import cs601.webmail.pages.mail.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cs601.webmail.ErrorManager;
import cs601.webmail.util.Logger;

public class DispatchServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DispatchServlet.class);

    public static Map<String,Class> mapping = new HashMap<String, Class>();

    static {
        mapping.put("/", HomePage.class);
        mapping.put("/contacts", ContactsPage.class);

        mapping.put("/register", RegisterPage.class);
        mapping.put("/login", LoginPage.class);
        mapping.put("/logout", LogoutPage.class);
        mapping.put("/register", RegisterPage.class);
        mapping.put("/registerNext", RegisterNextPage.class);

        mapping.put("/rest/user/profile", ProfilePage.class);
        mapping.put("/rest/user/settings", SettingsPage.class);


        mapping.put("/rest/mail/list", MailListPage.class);
        mapping.put("/rest/mail/read", ReadMailPage.class);
        mapping.put("/rest/mail/sync", SyncMailsPage.class);
        mapping.put("/rest/mail/control", MailActionsPage.class);
        mapping.put("/rest/mail/send", MailSendPage.class);
        mapping.put("/rest/mail/search", MailSearchPage.class);

        mapping.put("/rest/contact/list", ContactListPage.class);
        mapping.put("/rest/contact/read", ContactReadPage.class);
        mapping.put("/rest/contact/edit", ContactEditPage.class);
        mapping.put("/rest/contact/create", ContactCreatePage.class);
        mapping.put("/rest/contact/control", ContactActionsPage.class);
        mapping.put("/rest/contacts.json", ContactListJsonPage.class);

    }

    private Configuration configuration;



    @Override
    public void init() throws ServletException {

        configuration = Configuration.getDefault();


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {

        if (Constants.DEBUG_MODE)
            LOGGER.debug("[DEBUG] request URI " + request.getRequestURI());

        String uri = request.getRequestURI();
        ControllerPage p = createPage(uri);

        if ( p == null ) {
            response.sendRedirect("/files/error.html");
            return;
        }

        try {

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
        }
     }



    public ControllerPage createPage(String uri)
    {
        Class pageClass = mapping.get(uri);
        try {

            if (pageClass == null) {
                return null;
            }

            Constructor<ControllerPage> ctor = pageClass.getConstructor();
            return ctor.newInstance();
        }
        catch (Exception e) {
            LOGGER.error("Create page failed for :" + uri);
            ErrorManager.instance().error(e);
        }
        return null;
    }

}
