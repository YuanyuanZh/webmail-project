package cs601.webmail.dao;

/**
 * Created by yuanyuan on 10/26/14.
 */


public interface MailDao {

    void insertMail(Integer MSGID,String SUBJECT,String MFROM,String MTO,String CONTENT,String DATE,Integer uid,Integer aid);

}