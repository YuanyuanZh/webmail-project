package cs601.webmail.dao;

import cs601.webmail.entity.Mail;

import java.util.List;

/**
 * Created by yuanyuan on 10/25/14.
 */
public interface MailDao {

    Mail findById(Long id);

    List<Mail> findAll();

    Mail save(Mail mail);

    void insertMail(String MSGID,String SUBJECT,String MFROM,String MTO,String CONTENT,String DATE,Integer uid,Integer aid);

}
