package cs601.webmail.service.impl;

import cs601.webmail.Configuration;
import cs601.webmail.dao.AccountDao;
import cs601.webmail.dao.UserDao;
import cs601.webmail.dao.impl.AccountDaoImpl;
import cs601.webmail.dao.impl.UserDAOImpl;
import cs601.webmail.entity.Account;
import cs601.webmail.frameworks.mail.pop3.Pop3Client;
import cs601.webmail.service.AccountService;

import java.io.IOException;
import java.util.List;
/**
 * Created by yuanyuan on 10/26/14.
 */
public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao =new AccountDaoImpl();
    private UserDao userDao=new UserDAOImpl();


    /*public Account findById(Long id) {
        Account account = new Account();

        account.setId(3l);
        account.setUserId(1l);

        return account;
    }*/
    Account account = new Account();

    public Account findById(Long id) {  //FIXME must to impl this method in production environment.
        /*Account account = new Account();

        account.setId(3l);
        account.setUserId(2l);

        account.setEmailUsername(Configuration.getDefault().get("email"));
        account.setEmailPassword(Configuration.getDefault().get("password"));

        account.setPopServer(Configuration.getDefault().get("pop"));
        account.setPopServerPort(Configuration.getDefault().getInteger("pop.port"));
        account.setEnableSsl(Configuration.getDefault().getBoolean("pop.ssl"));*/

        return accountDao.findById(id);
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
