package cs601.webmail.application;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RequestContextFactory {

    public static RequestContext create(HttpServletRequest request, HttpServletResponse response) {
        RequestContext context = new DefaultRequestContext(request, response);
        RequestContext.setInstance(context);
        return context;
    }

}
