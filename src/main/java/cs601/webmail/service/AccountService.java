package cs601.webmail.service;

import cs601.webmail.entity.Account;

import java.util.List;

/**
 * Created by yuanyuan on 10/26/14.
 */
public interface AccountService {
    Account findById(Long id);

    void addAccount(Account account);
    List<String> emailAccount();
}
