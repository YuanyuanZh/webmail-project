package cs601.webmail.page;

import cs601.webmail.MVC.RequestContext;


/**
 * Created by Bill Xiong on 10/23/14.
 */
public class HomePage extends Page {

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

        context.getRequest().getRequestDispatcher("/files/home.html").forward(context.getRequest(), context.getResponse());
    }

}
