package cs601.webmail.service;

import cs601.webmail.db.Page;
import cs601.webmail.db.PageRequest;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;

import java.util.List;

/**
 * Created by yuanyuan on 10/26/14.
 */
public interface MailService {
    void save(Mail mail);

    void save(List<Mail> mails);

    void delete(Mail mail);

    Mail findById(int id);

    List<Mail> findByFolder(String folder);
    int syncMails(Account account);

    Page<Mail> findByAccountAndPage(Account account, PageRequest pageRequest);
}
