package cs601.webmail.page;

/**
 * Created by yuanyuan on 10/15/14.
 */
import cs601.webmail.frameworks.web.AjaxResponse;
import cs601.webmail.frameworks.web.RequestContext;
import org.codehaus.jackson.map.ObjectMapper;
import z.managers.ErrorManager;
import z.misc.VerifyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class Page {

    PrintWriter out;
    int pageNum;

    protected void renderText(String text) {

        if (text == null || text.length() == 0) {
            return;
        }

        getResponse().setContentType("text/plain;charset=UTF-8");
        getResponse().setCharacterEncoding("UTF-8");

        getOut().println(text);
    }

    protected void renderJson(Object respObject) {

        getResponse().setContentType("application/json;charset=UTF-8");
        getResponse().setCharacterEncoding("UTF-8");

        AjaxResponse ajaxResponse = AjaxResponse.OK;

        if (respObject != null) {
            ajaxResponse.setData(respObject);
        }

        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(getOut(), ajaxResponse);
        } catch (IOException e) {
            // ignore
        }
    }

    protected HttpServletResponse getResponse() {
        return RequestContext.getCurrentInstance().getResponse();
    }

    protected HttpServletRequest getRequest() {
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
       // out.println("<html>");
        //out.println("<body>");
    }

    public abstract void body() throws Exception;

    public void footer() {
       // out.println("</body>");
        //out.println("</html>");
    }
}
