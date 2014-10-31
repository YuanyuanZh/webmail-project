package cs601.webmail.service.impl;

import cs601.webmail.dao.AccountDao;
import cs601.webmail.dao.UserDao;
import cs601.webmail.entity.Account;
import cs601.webmail.service.AccountService;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by yuanyuan on 10/26/14.
 */
public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao;
    private UserDao userDao;


    public Account findById(Long id) {
        Account account = new Account();

        account.setId(3l);
        account.setUserId(2l);

        return account;
    }

    public void addAccount(Account account){
        if(account==null){
            throw new IllegalStateException("account missing");
        }

        accountDao.save(account);
    }

    public List<String> emailAccount(){

        return accountDao.listEmails();
    }
}
