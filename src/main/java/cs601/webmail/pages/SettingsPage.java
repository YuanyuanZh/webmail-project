package cs601.webmail.pages;

import cs601.webmail.entity.Account;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.ContactService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.ContactServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;

/**
 * Created by yuanyuan on 11/19/14.
 */
public class SettingsPage extends ControllerPage {

    @Override
    public void body() throws Exception {

        RequestContext context = RequestContext.getCurrentInstance();

        ContactService contactService = new ContactServiceImpl();
        AccountService accountService = new AccountServiceImpl();

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

        String method = req.getMethod();
        resp.setContentType("text/html; charset=utf-8");

        if ("get".equalsIgnoreCase(method)) {

            Account account = accountService.findSingleByUserId(user.getId());
            PageTemplate template = new PageTemplate("/velocity/settings.vm");
            template.addParam("account", account);
            StringWriter writer = new StringWriter();
            template.merge(writer);
            resp.setHeader("x-state", "ok");
            getOut().print(writer.toString());
            return;
        }
        else if ("post".equalsIgnoreCase(method)) {

            Account account = restoreEntity(req);

            if (account.getUserId() != user.getId()) {
                resp.setHeader("x-state", "error");
                resp.setHeader("x-msg", "Illegal access.");
                return;
            }

            try {
                accountService.save(account);
                resp.setHeader("x-state", "ok");
            } catch (Exception e) {
                resp.setHeader("x-state", "error");
                resp.setHeader("x-exception", e.getMessage());
            }
            return;
        }

        resp.setHeader("x-state", "error");
        resp.setHeader("x-msg", "METHOD not support");
    }

    private Account restoreEntity(HttpServletRequest req) {

        Account account = new Account();

        account.setId(getLongParam(req, "id"));
        account.setUserId(getLongParam(req, "userId"));

        account.setPopServer(req.getParameter("popServer"));
        account.setPopServerPort(getIntParam(req, "popServerPort"));
        account.setEnableSsl("on".equalsIgnoreCase(req.getParameter("popSSL")));
        account.setEmailUsername(req.getParameter("popAccount"));
        account.setEmailPassword(req.getParameter("popPassword"));

        account.setSmtpServer(req.getParameter("smtpServer"));
        account.setSmtpServerPort(getIntParam(req, "smtpServerPort"));
        account.setEnableSmtpSsl("on".equalsIgnoreCase(req.getParameter("smtpSSL")));
        account.setDisplayName(req.getParameter("displayName"));
        account.setMailSignature(req.getParameter("mailSignature"));

        return account;
    }



}
