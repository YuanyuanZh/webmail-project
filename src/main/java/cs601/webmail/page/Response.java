package cs601.webmail.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 11/4/14.
 */
public abstract class Response {

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public Response(HttpServletRequest req,HttpServletResponse resp)
    {
        this.request=req;
        this.response=resp;
    }
//    private HttpServletResponse getResponse() {
//        return RequestContext.getCurrentInstance().getResponse();
//    }

//    private HttpServletRequest getRequest() {
//        return RequestContext.getCurrentInstance().getRequest();
//    }


    public abstract String verify();
}
