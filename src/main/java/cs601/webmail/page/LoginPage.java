package cs601.webmail.page;

import cs601.webmail.MVC.RequestContext;

/**
 * Created by yuanyuan on 10/22/14.
 */
public class LoginPage extends Page{
    public void verify() {
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

        RequestContext context = RequestContext.getCurrentInstance();

        context.getRequest().getRequestDispatcher("/files/login.html").forward(context.getRequest(), context.getResponse());
    }
}
