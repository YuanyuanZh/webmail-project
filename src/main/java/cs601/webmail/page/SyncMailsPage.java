package cs601.webmail.page;

import cs601.webmail.entity.Account;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;
import org.codehaus.jackson.map.ObjectMapper;

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
        AccountService accountService = new AccountServiceImpl();
        MailService mailService = new MailServiceImpl();

        // TODO just for demo
        Account currentAccount = accountService.findById(-1l);

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
