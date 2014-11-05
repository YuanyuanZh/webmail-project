package cs601.webmail.MVC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 11/3/14.
 */
public class RequestContextFactory {

    public static RequestContext create(HttpServletRequest request, HttpServletResponse response) {
        RequestContext context = new DefaultRequestContext(request, response);
        context.init();

        RequestContext.setInstance(context);
        return context;
    }

}
