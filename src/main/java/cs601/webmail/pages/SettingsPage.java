package cs601.webmail.pages;

import cs601.webmail.Constants;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.UserService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.UserServiceImpl;
import cs601.webmail.util.DigestUtils;
import cs601.webmail.util.EncryptUtils;
import cs601.webmail.util.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import cs601.webmail.util.Logger;

/**
 * Created by yuanyuan on 11/19/14.
 */
public class SettingsPage extends ControllerPage {

    private static final Logger LOGGER = Logger.getLogger(SettingsPage.class);

    @Override
    public void body() throws Exception {

        RequestContext context = RequestContext.getCurrentInstance();

        UserService userService = new UserServiceImpl();
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
                processPasswordChanging(req, userService, user);
                resp.setHeader("x-state", "ok");
            } catch (Exception e) {
                resp.setHeader("x-state", "error");
                resp.setHeader("x-exception", e.getMessage());
                LOGGER.error(e);
            }
            return;
        }

        resp.setHeader("x-state", "error");
        resp.setHeader("x-msg", "METHOD not support");
    }

    private void processPasswordChanging(HttpServletRequest req, UserService userService, User user) {

        String curPwd = req.getParameter("currentPassword");
        String newPwd = req.getParameter("newPassword");
        String newPwd2 = req.getParameter("newPassword2");

        if (!Strings.haveLength(curPwd) &&
                !Strings.haveLength(newPwd) &&
                !Strings.haveLength(newPwd2)) {
            // no current password means user won't change it.
            // just return to ignore.
            return;
        }

        if (!userService.verifyUser(user.getLoginId(), DigestUtils.digestToSHA(curPwd))) {
            throw new IllegalStateException("Current password not correct.");
        }

        if (!Strings.haveLength(newPwd)) {
            throw new IllegalStateException("New password can't be empty");
        }

        if (!newPwd.equals(newPwd2)) {
            throw new IllegalStateException("Confirm password failed.");
        }

        userService.updatePass(user.getId(), DigestUtils.digestToSHA(newPwd));

        LOGGER.debug("Password has been changed for the user [" + user.getLoginId() + "]");
    }


    private Account restoreEntity(HttpServletRequest req) {

        Account account = new Account();

        account.setId(getLongParam(req, "id"));
        account.setUserId(getLongParam(req, "userId"));

        account.setPopServer(req.getParameter("popServer"));
        account.setPopServerPort(getIntParam(req, "popServerPort"));
        account.setEnableSsl("on".equalsIgnoreCase(req.getParameter("popSSL")));
        account.setEmailUsername(req.getParameter("popAccount"));
        account.setEmailPassword(EncryptUtils.encryptToHex(req.getParameter("popPassword"), Constants.DEFAULT_AES_CIPHER_KEY));


        account.setSmtpServer(req.getParameter("smtpServer"));
        account.setSmtpServerPort(getIntParam(req, "smtpServerPort"));
        account.setEnableSmtpSsl("on".equalsIgnoreCase(req.getParameter("smtpSSL")));
        account.setDisplayName(req.getParameter("displayName"));
        account.setMailSignature(req.getParameter("mailSignature"));

        return account;
    }



}
