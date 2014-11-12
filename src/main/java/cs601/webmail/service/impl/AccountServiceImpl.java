package cs601.webmail.service.impl;

import cs601.webmail.dao.AccountDao;
import cs601.webmail.dao.UserDao;
import cs601.webmail.dao.impl.AccountDaojdbcImpl;
import cs601.webmail.dao.impl.UserDAOImpl;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.mail.Pop3Client;
import cs601.webmail.service.AccountService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yuanyuan on 10/26/14.
 */
public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao =new AccountDaojdbcImpl();
    private UserDao userDao=new UserDAOImpl();


    public Account findById(Long id) {
        Account account = new Account();

        account.setId(3l);
        account.setUserId("");

        return account;
    }

    public void addAccount(Account account){
        if(account==null){
            throw new IllegalStateException("account missing");
        }

        accountDao.save(account);
    }

    public boolean verifyAccount(String emailAddress, String pass,String popServer,int popPort){
        try{
        Pop3Client client = Pop3Client.createInstance(popServer,popPort,true);
        if(!client.login(emailAddress, pass)){
            return false;
        }
        }catch (IOException e){}

       return true;
    }

    public List<String> emailAccount(Account account){

        return accountDao.listEmails(account.getUserId());
    }
    public  void deleteAccount(Account account){

    }
}
