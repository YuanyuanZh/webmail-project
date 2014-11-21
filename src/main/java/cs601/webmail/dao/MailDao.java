package cs601.webmail.dao;

import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;
import java.util.List;

/**
 * Created by yuanyuan on 10/25/14.
 */
public interface MailDao {

    public Mail findById(Long id);

    public Mail findByUID(String uid);

    public List<Mail> findAll();

    public Page<Mail> findByPage(PageRequest pageRequest, Long accountId, Long userId);

    public Mail save(Mail mail);

    // UID
    public List<String> findAllMailUIDs();

//    void insertMail(String MSGID,String SUBJECT,String MFROM,String MTO,String CONTENT,String DATE,Integer uid,Integer aid);

    public void removeByUID(String uid);

    /**
     * Find a page result with an extra condition.
     *
     * @param pageRequest Page request.
     * @param condition 'where' block from SQL.
     *                  e.g. "ACCOUNTID = 101 and USERID = 3"
     * @return Paged result.
     */
    public Page<Mail> findPageByConditions(PageRequest pageRequest, String condition);
    List<String> findMailUIDs(Long accountId);
}
