package cs601.webmail.pages;

import cs601.webmail.frameworks.web.RequestContext;


/**
 * Created by yuanyuan on 10/23/14.
 */
public class HomePage extends ControllerPage {

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
