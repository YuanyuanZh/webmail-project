package cs601.webmail.dao;

import cs601.webmail.entity.Contact;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;

import java.util.List;

/**
 * Created by yuanyuan on 11/17/14.
 */
public interface ContactDao {
    Contact findById(Long id);

    List<Contact> findAll(Long userId);

    List<Contact> findAll();

    /**
     * Find a page result with an extra condition.
     *
     * @param pageRequest Page request.
     * @param condition 'where' block from SQL.
     *                  e.g. "ACCOUNTID = 101 and USERID = 3"
     * @return Paged result.
     */
    Page<Contact> findPageByConditions(PageRequest pageRequest, String condition);

    Contact save(Contact contact);
}
