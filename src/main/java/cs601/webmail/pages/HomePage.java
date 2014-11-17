package cs601.webmail.pages;

import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.Page;


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
