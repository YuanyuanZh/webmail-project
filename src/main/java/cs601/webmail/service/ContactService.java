package cs601.webmail.service;

import cs601.webmail.entity.Contact;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;

import java.util.List;

/**
 * Created by yuanyuan on 11/19/14.
 */
public interface ContactService {

    public List<Contact> findByUser(User user);

    public Page<Contact> findByUser(User user, PageRequest pageRequest);

    /**
     *
     * @param folder all | starred | disabled
     * @param user
     * @param pageRequest
     * @return
     */
    public Page<Contact> findByUserAndFolder(String folder, User user, PageRequest pageRequest);

    Contact findById(long id);

    void save(Contact contact);
}
