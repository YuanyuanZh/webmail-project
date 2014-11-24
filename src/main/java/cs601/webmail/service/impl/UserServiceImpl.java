package cs601.webmail.service.impl;

import cs601.webmail.dao.UserDao;

import cs601.webmail.dao.impl.UserDAOImpl;
import cs601.webmail.entity.User;
import cs601.webmail.service.UserService;
import org.apache.log4j.Logger;

public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(MailServiceImpl.class);

    UserDao userDao= new UserDAOImpl();

    public boolean verifyUser(String logId,String password){
        if(userDao.LoginIDExist(logId)&& userDao.getPassword(logId).equals(password)){
            return true;
        }
        return false;
    }
    public User findUserByLogId(String logId){
        return userDao.findUserByLogId(logId);
    }

    public User findByID(long id){

        return userDao.findByID(id);
    }
    public void addUser(User user){
        if(user==null){
            throw new IllegalStateException("userDao missed");
        }
        userDao.save(user);

    }
    public void updatePass(long id,String password){

        if(password==null){
            throw new IllegalStateException("password missed");
        }
        userDao.updatePass(id,password);
    }
    public boolean LoginIDExist(String LOGID){
        return userDao.LoginIDExist(LOGID);
    }
}
