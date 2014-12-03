package cs601.webmail.dao;

import cs601.webmail.entity.Account;

import java.util.List;

/**
 * Created by yuanyuan on 10/27/14.
 */
public interface AccountDao {

    Account findById(Long id);

    List<Account> findByUserId(Long userId);

    List<String> listEmails(Long userid);

    /**
     * Both new inserted and updating are supported.<p>
     *
     * If id of account is null, <code>save</code> will
     * invoke insert method, otherwise, <code>save</code>
     * will invoke update method.
     *
     * @param account Account object.
     */
    void save(Account account);

    void delete(Account account);

    List<Account> listAll();

}