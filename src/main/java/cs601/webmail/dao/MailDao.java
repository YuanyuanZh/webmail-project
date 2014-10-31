package cs601.webmail.dao;

import cs601.webmail.entity.Mail;
import cs601.webmail.db.Page;
import cs601.webmail.db.PageRequest;
import java.util.List;

/**
 * Created by yuanyuan on 10/25/14.
 */
public interface MailDao {

    Mail findByAId(Long id);

    List<Mail> findAll();

    Mail save(Mail mail);
    void removeByUID(String uid);
    List<String> findAllMailUIDs();

    Page<Mail> findByPage(PageRequest pageRequest, Long accountId, Long userId);

    //void insertMail(String MSGID,String SUBJECT,String MFROM,String MTO,String CONTENT,String DATE,Integer uid,Integer aid);

}
