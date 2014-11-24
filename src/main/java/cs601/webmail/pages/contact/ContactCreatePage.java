package cs601.webmail.pages.contact;

import cs601.webmail.entity.Contact;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.ContactService;
import cs601.webmail.service.impl.ContactServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by yuanyuan on 11/18/14.
 */
public class ContactCreatePage extends ControllerPage {

    @Override
    public void body() throws Exception {

        RequestContext context = RequestContext.getCurrentInstance();

        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();
        User user;

        try {
            user = checkUserLogin(req, resp);
        } catch (NotAuthenticatedException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        String method = req.getMethod();
        Exception exception = null;

        try {

            // render editing template
            if ("get".equalsIgnoreCase(method)) {
                doRenderTemplate(req, resp, user);
                return;
            }
            else if ("post".equalsIgnoreCase(method)) {
                doSaveContact(req, resp, user);
                return;
            }

        } catch (Exception e) {
            exception = e;
        }

        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        resp.setHeader("x-state", "error");
        resp.setHeader("x-msg", "only  GET and POST are supported");

        if (exception != null) {
            resp.setHeader("x-exception", exception.getMessage());
        }

    }

    private void doSaveContact(HttpServletRequest req, HttpServletResponse resp, User user) {
        ContactService contactService = new ContactServiceImpl();

        try {
            Contact contact = restoreEntity(req);

            contact.setUserId(user.getId());

            contactService.save(contact);

            resp.addHeader("x-state", "ok");

        } catch (Exception e) {
            resp.addHeader("x-state", "error");
            resp.addHeader("x-exception", e.getMessage());
            return;
        }
    }

    private Contact restoreEntity(HttpServletRequest req) {
        Contact contact = new Contact();

        contact.setFullName(req.getParameter("fullName"));
        contact.setAddress(req.getParameter("address"));
        contact.setZipcode(req.getParameter("zipcode"));
        contact.setPhone(req.getParameter("phone"));
        contact.setEmail(req.getParameter("email"));

        return contact;
    }

    private void doRenderTemplate(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        PageTemplate template = new PageTemplate("/velocity/contact_edit.vm");
        StringWriter writer = new StringWriter();
        template.merge(writer);
        resp.addHeader("x-state", "ok");
        resp.setContentType("text/html; charset=utf-8");
        getOut().print(writer.toString());
    }

}
