package cs601.webmail.pages.contact;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.Contact;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.db.page.Order;
import cs601.webmail.frameworks.db.page.PageRequest;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.ContactService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.ContactServiceImpl;
import cs601.webmail.util.CollectionUtils;
import cs601.webmail.util.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yuanyuan on 11/17/14.
 */
public class ContactListPage extends ControllerPage {

    public static final String EMPTY_STRING = "";

    @Override
    public void body() throws Exception {
        RequestContext context = RequestContext.getCurrentInstance();

        AccountService accountService = new AccountServiceImpl();
        ContactService contactService = new ContactServiceImpl();

        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();

        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();

        User user = (User)session.getAttribute(AuthenticationCheckFilter.LOGIN_SESSION_FLAG);

        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        String id = req.getParameter("id");

        // render single item
        if (Strings.haveLength(id)) {
            doRenderSingle(req, resp, contactService, user, id);
            return;
        }

        // list all according on folder
        doListContacts(req, resp, contactService, user);
    }

    private void doRenderSingle(HttpServletRequest req, HttpServletResponse resp, ContactService contactService, User user, String id) {

        Contact contact = contactService.findById(Long.parseLong(id));

        if (contact == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setHeader("x-state", "error");
            resp.setHeader("x-msg", "Contact not found.");
            return;
        }

        if (contact.getUserId() != user.getId()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setHeader("x-state", "error");
            resp.setHeader("x-msg", "Illegal access.");
            return;
        }

        try {
            PageTemplate template = new PageTemplate("/velocity/contact_list.vm");
            template.addParam("items", Arrays.asList(contact));
            StringWriter writer = new StringWriter();
            template.merge(writer);

            resp.addHeader("x-state", "ok");

            getOut().print(writer.toString());

        } catch (IOException e) {
            resp.setHeader("x-state", "error");
            resp.setHeader("x-exception", e.getMessage());
        }
    }

    private void doListContacts(HttpServletRequest req, HttpServletResponse resp,
                                ContactService contactService, User user) {

        String curPage = req.getParameter("page");
        String folder = req.getParameter("folder");

        // incorrect folder name
        // use 'all' as default
        if (!"all".equals(folder) && !"starred".equals(folder)
                && !"disabled".equals(folder)) {
            folder = "all";
        }

        PageRequest pageRequest = new PageRequest(Order.desc("id"));
        pageRequest.pageSize = 15; // PageRequest.DEFAULT_PAGE_SIZE;
        pageRequest.page = curPage != null ? Integer.parseInt(curPage) : 1;

        try {

            cs601.webmail.frameworks.db.page.Page<Contact> pageResult
                    = contactService.findByUserAndFolder(folder, user, pageRequest);

            if (pageResult == null || !CollectionUtils.notEmpty(pageResult.getPageList())) {
                resp.addHeader("x-state", "ok");
                resp.addHeader("x-total", "0");
                resp.addHeader("x-position", "0");
                resp.addHeader("x-page", pageRequest.page + EMPTY_STRING);
                resp.addHeader("x-page-size", pageResult.getPageSize() + EMPTY_STRING);
                resp.addHeader("x-folder", folder);
                return;
            }

            List<Contact> contacts = pageResult.getPageList();

            PageTemplate template = new PageTemplate("/velocity/contact_list.vm");
            template.addParam("items", contacts);
            template.addParam("folder", folder);

            StringWriter writer = new StringWriter();
            template.merge(writer);

            resp.addHeader("x-state", "ok");  // careful: we're using HTTP headers instead of JSON to pass info.
            resp.addHeader("x-total", pageResult.getTotal() + EMPTY_STRING);
            resp.addHeader("x-position", pageResult.getPosition() + EMPTY_STRING);
            resp.addHeader("x-page", pageRequest.page + EMPTY_STRING);
            resp.addHeader("x-page-size", pageResult.getPageSize() + EMPTY_STRING);
            resp.addHeader("x-folder", folder);

            // output content
            getOut().print(writer.toString());

        } catch (Exception e) {
            resp.addHeader("x-state", "error");
            resp.addHeader("x-exception", e.getMessage());
        }
    }

}
