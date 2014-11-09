package cs601.webmail.page;

import cs601.webmail.frameworks.web.RequestContext;

/**
 * Created by yuanyuan on 11/3/14.
 */
public class Register extends Page {

    /*public Register(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }*/

    public void verify() {

        //RequestContext context = RequestContext.getCurrentInstance();
        //HttpServletRequest req = context.getRequest();
        //HttpServletResponse resp = context.getResponse();

        // no-op
    }

    @Override
    public void header() {
        // no-op
    }

    @Override
    public void footer() {
        // no-op
    }

    @Override
    public void body() throws Exception {

        //request.getRequestDispatcher("/files/home.html").forward(request, response);
        //out.println("Home page");
        // list mail

        RequestContext context = RequestContext.getCurrentInstance();

        context.getRequest().getRequestDispatcher("/files/register.html").forward(context.getRequest(), context.getResponse());
    }

}
