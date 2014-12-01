package cs601.webmail.service.impl;

import cs601.webmail.Configuration;
import cs601.webmail.dao.AccountDao;
import cs601.webmail.dao.UserDao;
import cs601.webmail.dao.impl.AccountDaoImpl;
import cs601.webmail.dao.impl.UserDAOImpl;
import cs601.webmail.entity.Account;
import cs601.webmail.frameworks.mail.pop3.Pop3Client;
import cs601.webmail.frameworks.mail.smtp.SMTPClient;
import cs601.webmail.service.AccountService;

import java.io.IOException;
import java.util.List;
import cs601.webmail.util.Logger;
/**
 * Created by yuanyuan on 10/26/14.
 */
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = Logger.getLogger(AccountServiceImpl.class);
    private AccountDao accountDao =new AccountDaoImpl();
    private UserDao userDao=new UserDAOImpl();

    @Deprecated
    public Account findById(Long id) {

        return accountDao.findById(id);
    }

    //setting page
    public Account findSingleByUserId (Long userId) {

        List<Account> accounts = accountDao.findByUserId(userId);

        if (accounts.size() > 0)
            return accounts.get(0);
        else
            return null;
    }

    public void addAccount(Account account){
        if(account==null){
            throw new IllegalStateException("account missing");
        }
        accountDao.save(account);
    }

    public boolean verifyAccount(String emailAddress, String pass,String popServer,int popPort){

        Pop3Client client = null;
        boolean ssl = popPort != 110; // 110 is the default port for plain socket
        try{
            client = Pop3Client.createInstance(popServer,popPort, ssl);
            return client.login(emailAddress, pass);
        } catch (Exception e){
            // any error occurred, return FALSE.
            LOGGER.error(e);
            return false;
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
    public boolean verifySMTPAccount(String emailAddress, String pass,String SMTPServer,int SMTPPort){
        try{
            SMTPClient client=SMTPClient.createInstance(SMTPServer,SMTPPort,true);
            if(!client.login(emailAddress,pass)){
                return false;
            }
        }
        catch (IOException e){}
        return true;
    }

    @Override
    public void save(Account account) {
        accountDao.save(account);
    }

}
