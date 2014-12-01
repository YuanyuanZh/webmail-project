package cs601.webmail.pages.mail;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cs601.webmail.util.Logger;

/**
 * Created by yuanyuan on 10/28/14.
 */
public class SyncMailsPage extends ControllerPage {

    private static Logger LOGGER = Logger.getLogger(SyncMailsPage.class);

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
        HttpServletRequest request = RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response = RequestContext.getCurrentInstance().getResponse();
        HttpSession session = request.getSession();
        AccountService accountService = new AccountServiceImpl();
        MailService mailService = new MailServiceImpl();

        User user=(User)session.getAttribute(AuthenticationCheckFilter.LOGIN_SESSION_FLAG);


        Account currentAccount = accountService.findSingleByUserId(user.getId());
        response.setContentType("text/html; charset=utf-8");

        try {
            int updatedCount = mailService.syncMails(currentAccount);

            response.setHeader("x-state", "ok");
            response.setHeader("x-update", updatedCount + "");
        }

        catch (Exception e) {
            LOGGER.error(e);
            response.setHeader("x-state", "error");
            response.setHeader("x-exception", e.getMessage());
        }
    }

}
