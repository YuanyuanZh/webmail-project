package cs601.webmail.pages.mail;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.Page;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class SyncMailsPage extends Page {

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


        Account currentAccount = accountService.findById(user.getId());

        Map model = new HashMap();

        try {
            int updatedCount = mailService.syncMails(currentAccount);

            model.put("state", "ok");
            model.put("update", updatedCount);
        }
        catch (Exception e) {
            e.printStackTrace();
            model.put("state", "err");
            model.put("msg", e.getMessage());
        }

        ObjectMapper om = new ObjectMapper();
        om.writeValue(getOut(), model);
    }

}
