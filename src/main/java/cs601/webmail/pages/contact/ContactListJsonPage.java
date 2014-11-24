package cs601.webmail.pages.contact;

import cs601.webmail.entity.Contact;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.db.page.Order;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.ContactService;
import cs601.webmail.service.impl.ContactServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanyuan on 11/18/14.
 */
public class ContactListJsonPage extends ControllerPage {

    @Override
    public void body() throws Exception {
        RequestContext context = RequestContext.getCurrentInstance();

        ContactService contactService = new ContactServiceImpl();

        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();

        User user = null;

        try {
            user = checkUserLogin(req, resp);
        } catch (NotAuthenticatedException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        doBuildContactsJson(req, resp, contactService, user);
    }

    private void doBuildContactsJson(HttpServletRequest req, HttpServletResponse resp,
                                     ContactService contactService, User user) {

        PageRequest pr = new PageRequest(Order.asc("full_name"));
        pr.pageSize = Integer.MAX_VALUE;
        pr.page = 1;

        // find all in one page
        Page<Contact> page = contactService.findByUserAndFolder("all", user, pr);

        List<Contact> contacts = page.getPageList();

        resp.setContentType("application/json; charset=utf-8");
        resp.setHeader("x-state", "ok");


        // just render an empty array
        if (contacts == null || contacts.size() == 0) {
            getOut().write("[]");
            getOut().flush();
            return;
        }

//        Collections.sort(contacts, new Comparator<Contact>() {
//            @Override
//            public int compare(Contact o1, Contact o2) {
//                if (o1 == null || o2 == null) {
//                    return 0;
//                }
//                if (o1.getFullName() != null) {
//                    return o1.getFullName().compareTo(o2.getFullName());
//                }
//                return 0;
//            }
//        });

        StringBuilder sb = new StringBuilder("[");

        Iterator<Contact> itr = contacts.iterator();

        while (itr.hasNext()) {
            Contact contact = itr.next();

            sb.append("{")
                    .append("\"name\":\"")
                    .append(contact.getFullName())
                    .append("\",")
                    .append("\"email\":\"")
                    .append(contact.getEmail())
                    .append("\"}");

            if (itr.hasNext()) {
                sb.append(",");
            }
        }

        sb.append("]");

        getOut().write(sb.toString());
        getOut().flush();
    }

}
