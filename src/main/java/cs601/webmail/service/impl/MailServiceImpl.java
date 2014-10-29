package cs601.webmail.service.impl;

import cs601.webmail.dao.MailDao;
import cs601.webmail.entity.Mail;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import org.picocontainer.annotations.Inject;

import java.util.List;

/**
 * Created by yuanyuan on 10/25/14.
 */
public class MailServiceImpl implements MailService {

    public MailServiceImpl() {
    }

    @Inject
    private MailDao mailDao;

    @Inject
    private AccountService accountService;

    @Override
    public void save(Mail mail) {
        if (mailDao == null) {
            throw new IllegalStateException("MailDao missed");
        }
    }

    @Override
    public void save(List<Mail> mails) {}

    @Override
    public void delete(Mail mail) {}

    @Override
    public Mail findById(int id) { return null; }

    @Override
    public List<Mail> findByFolder(String folder) {
        return null;
    }

    public void syncMails() {

    }

}
