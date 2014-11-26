package cs601.webmail.service;

import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;

import java.util.List;

/**
 * Created by yuanyuan on 10/26/14.
 */
public interface MailService {
    void save(Mail mail);

    void save(List<Mail> mails);

    // mark as trashed
    public void trash(Mail mail);

    // mark as deleted
    void delete(Mail mail);

    Mail findById(long id);

    List<Mail> findByFolder(String folder);

    // pull remote mails, and delete locals if necessary
    int syncMails(Account account);

    Page<Mail> findByAccountAndPage(Account account, PageRequest pageRequest);

    Page<Mail> findPage(String folder, Account currentAccount, PageRequest pageRequest);
    Page<Mail> findPage(Mail.VirtualFolder folder, Account account, PageRequest pageRequest);
}
