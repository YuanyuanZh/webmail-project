package cs601.webmail.dao;

import cs601.webmail.entity.Contact;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;

import java.util.List;
/**
 * Created by yuanyuan on 11/19/14.
 */
public interface ContactDao {

    Contact findById(Long id);
    List<Contact> findAll(Long userId);
    List<Contact> findAll();

    Page<Contact> findPageByConditions(PageRequest pageRequest, String condition);

    Contact save(Contact contact);
}
