package cs601.webmail.pages;

import cs601.webmail.frameworks.web.RequestContext;
/**
 * Created by yuanyuan on 11/19/14.
 */

public class ContactsPage extends Page {

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

        context.getRequest().getRequestDispatcher("/files/contacts.html").forward(context.getRequest(), context.getResponse());
    }


}
