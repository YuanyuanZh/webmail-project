package cs601.webmail.dao;

import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.db.Page;
import cs601.webmail.frameworks.db.PageRequest;
import java.util.List;

/**
 * Created by yuanyuan on 10/25/14.
 */
public interface MailDao {

    public Mail findById(Long id);
    public List<Mail> findAll();
    public Mail findByUID(String uid);
    public Mail save(Mail mail);
    void removeByUID(String uid);
    public List<String> findAllMailUIDs();
    public Page<Mail> findByPage(PageRequest pageRequest, Long accountId, String userId);

    //void insertMail(String MSGID,String SUBJECT,String MFROM,String MTO,String CONTENT,String DATE,Integer uid,Integer aid);

}
