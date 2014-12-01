package cs601.webmail.pages;

/**
 * Created by yuanyuan on 10/15/14.
 */
import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.ErrorManager;
import cs601.webmail.exception.VerifyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public abstract class ControllerPage {

    PrintWriter out;
    int pageNum;


    protected String getStringParam(HttpServletRequest req, String param) {
        return req.getParameter(param);
    }

    protected String getStringParam(HttpServletRequest req, String param, String def) {
        String val = req.getParameter(param);
        return val == null ? def : val;
    }

    protected Long getLongParam(HttpServletRequest req, String param) {
        String val = req.getParameter(param);
        return val == null ? null : Long.parseLong(val);
    }

    protected Long getLongParam(HttpServletRequest req, String param, long def) {
        String val = req.getParameter(param);
        return val == null ? def : Long.parseLong(val);
    }

    protected Integer getIntParam(HttpServletRequest req, String param) {
        String val = req.getParameter(param);
        return val == null ? null : Integer.parseInt(val);
    }

    protected Integer getIntParam(HttpServletRequest req, String param, int def) {
        String val = req.getParameter(param);
        return val == null ? def : Integer.parseInt(val);
    }

    protected Boolean getBooleanParam(HttpServletRequest req, String param) {
        String val = req.getParameter(param);
        return val == null ? null : Boolean.valueOf(val);
    }

    protected Boolean getBooleanParam(HttpServletRequest req, String param, boolean def) {
        String val = req.getParameter(param);
        return val == null ? def : Boolean.valueOf(val);
    }

    protected User checkUserLogin(HttpServletRequest req, HttpServletResponse resp)
            throws NotAuthenticatedException {

        HttpSession session = req.getSession(true);

        User user = (User)session.getAttribute(AuthenticationCheckFilter.LOGIN_SESSION_FLAG);

        if (user == null) {
            throw new NotAuthenticatedException();
        }

        return user;
    }

    protected void renderText(String text) {

        if (text == null || text.length() == 0) {
            return;
        }

        getResponse().setContentType("text/plain;charset=UTF-8");
        getResponse().setCharacterEncoding("UTF-8");

        getOut().println(text);
    }

    @Deprecated
    protected void renderJson(Object respObject) {


    }

    private HttpServletResponse getResponse() {
        return RequestContext.getCurrentInstance().getResponse();
    }

    private HttpServletRequest getRequest() {
        return RequestContext.getCurrentInstance().getRequest();
    }

    protected PrintWriter getOut() {
        if (out == null) {
            try {
                out = getResponse().getWriter();
            }
            catch (IOException ioe) {
                ErrorManager.instance().error(ioe);
            }
        }
        return out;
    }

    public void verify() throws VerifyException {
        // handle default args like page number, etc...
        // verify that arguments make sense
        // implemented by subclass typically
        // VerifyException is a custom Exception subclass
    }

    public void handleDefaultArgs() {
        // handle default args like page number, etc...
        String pageStr = getRequest().getParameter("page");
        if ( pageStr!=null ) {
            pageNum = Integer.valueOf(pageStr);
        }
    }

    public void generate() {
        handleDefaultArgs();
        try {
            verify(); // check args before generation
            header();
            body();
            footer();
        }
        catch (VerifyException ve) {
            try {
                getResponse().sendRedirect("/files/error.html");
            }
            catch (IOException ioe) {
                ErrorManager.instance().error(ioe);
            }
        }
        catch (Exception e) {
            ErrorManager.instance().error(e);
        }
        finally {
            PrintWriter writer = getOut();
        }
    }

    public void header() {
//        out.println("<html>");
//        out.println("<body>");
    }

    public abstract void body() throws Exception;

    public void footer() {
//        out.println("</body>");
//        out.println("</html>");
    }
}
