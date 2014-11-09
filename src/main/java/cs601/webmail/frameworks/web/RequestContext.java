package cs601.webmail.frameworks.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 11/3/14.
 */
public abstract class RequestContext {

    private static ThreadLocal<RequestContext> instance = new ThreadLocal<RequestContext>();

    public static RequestContext getCurrentInstance() {
        return instance.get();
    }

    static void setInstance(RequestContext context) {
        instance.set(context);
    }

    public void init() {}

    public void release() {}

    public abstract HttpServletRequest getRequest();

    public abstract HttpServletResponse getResponse();

}
