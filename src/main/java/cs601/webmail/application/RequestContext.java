package cs601.webmail.application;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;


public abstract class RequestContext {

    private static ThreadLocal<RequestContext> instance = new ThreadLocal<RequestContext>();

    public static RequestContext getCurrentInstance() {
        return instance.get();
    }

    static void setInstance(RequestContext context) {
        instance.set(context);
    }

    // DB methods
    public abstract Connection currentConnection();

    public void init() {}

    public void release() {}


    public abstract HttpServletRequest getRequest();

    public abstract HttpServletResponse getResponse();

}
