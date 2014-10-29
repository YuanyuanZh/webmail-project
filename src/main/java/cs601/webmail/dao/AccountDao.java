package cs601.webmail.dao;

import cs601.webmail.entity.Account;

import java.util.List;

/**
 * Created by yuanyuan on 10/27/14.
 */
public interface AccountDao {

    Account findById(Long id);

    List<Account> listAll();

    void save(Account account);

}
