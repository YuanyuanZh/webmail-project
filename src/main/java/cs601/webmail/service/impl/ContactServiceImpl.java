package cs601.webmail.service.impl;

import cs601.webmail.dao.ContactDao;
import cs601.webmail.dao.impl.ContactDaoImpl;
import cs601.webmail.entity.Contact;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;
import cs601.webmail.service.ContactService;
import cs601.webmail.util.Strings;

import java.util.List;

/**
 * Created by yuanyuan on 11/19/14.
 */
public class ContactServiceImpl implements ContactService {

    private ContactDao contactDao;

    public ContactServiceImpl() {
        this.contactDao = new ContactDaoImpl();
    }

    @Override
    public Page<Contact> findByUser(User user, PageRequest pageRequest) {
        throw new IllegalStateException("not impl yet"); // TODO
    }

    @Override
    public Page<Contact> findByUserAndFolder(String folder, User user, PageRequest pageRequest) {
        if (!Strings.haveLength(folder)) {
            throw new IllegalArgumentException("folder incorrect");
        }
        if (user == null) {
            throw new IllegalArgumentException("user incorrect");
        }

        if ("all".equalsIgnoreCase(folder)) {
            // include DISABLED and Starred
            String sql = String.format("flag_del = " + Contact.FLAG_NO,
                    Contact.FLAG_DISABLED);
            return contactDao.findPageByConditions(pageRequest, sql);
        }
        else if ("starred".equals(folder)) {
            // include Starred only
            String sql = String.format("flag_del = %d and flag_fav = %d",
                    Contact.FLAG_NO, Contact.FLAG_YES);
            return contactDao.findPageByConditions(pageRequest, sql);
        }
        else if ("disabled".equals(folder)) {
            // include Disabled only
            String sql = String.format("flag_del = %d",
                    Contact.FLAG_DISABLED);
            return contactDao.findPageByConditions(pageRequest, sql);
        }

        throw new IllegalStateException("folder was not supported");
    }

    @Override
    public Contact findById(long id) {
        return contactDao.findById(id);
    }

    @Override
    public void save(Contact contact) {
        contactDao.save(contact);
    }

    @Override
    public List<Contact> findByUser(User user) {
        if (user == null)
            throw new IllegalArgumentException();

        return contactDao.findAll(user.getId());
    }
}
