package cs601.webmail.pages.contact;

import cs601.webmail.entity.Contact;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.ContactService;
import cs601.webmail.service.impl.ContactServiceImpl;
import cs601.webmail.util.Strings;
import java.io.StringWriter;
import cs601.webmail.frameworks.web.PageTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yuanyuan on 11/17/14.
 */
public class ContactReadPage extends ControllerPage {

    @Override
    public void body() throws Exception {
        HttpServletRequest request = RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response = RequestContext.getCurrentInstance().getResponse();

        String id = request.getParameter("id");

        if (!Strings.haveLength(id)) {
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Invalid contact ID");
            return;
        }

        User user;

        try {
            user = checkUserLogin(request, response);
        } catch (NotAuthenticatedException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        renderContact(request, response, user, id);
    }

    private void renderContact(HttpServletRequest request, HttpServletResponse response, User user, String id) throws IOException {

        ContactService contactService = new ContactServiceImpl();
        Contact contact = contactService.findById(Long.parseLong(id));

        if (contact == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Contact not found");
            return;
        }

        // check if this contact entity belongs to the user
        // which was retrieved from session or not.
        if (contact.getUserId() != user.getId()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Illegal request");
            return;
        }

        PageTemplate template = new PageTemplate("/velocity/contact_read.vm");
        template.addParam("item", contact);
        StringWriter writer = new StringWriter();
        template.merge(writer);

        response.setContentType("text/html; charset=utf-8");
        response.addHeader("x-state","ok");

        getOut().print(writer.toString());
    }

}
