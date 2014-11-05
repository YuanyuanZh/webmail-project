package cs601.webmail.service.impl;

import cs601.webmail.dao.UserDao;
import cs601.webmail.dao.impl.MailDaojdbcImpl;
import cs601.webmail.dao.impl.UserDAOImpl;
import cs601.webmail.entity.User;
import cs601.webmail.service.UserService;
import org.apache.log4j.Logger;

/**
 * Created by yuanyuan on 10/30/14.
 */
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(MailServiceImpl.class);

    public UserServiceImpl() {
        userDao=new UserDAOImpl();
        userService=new UserServiceImpl();
    }

     private UserDao userDao;
     private UserService userService;


    public boolean verifyUser(String loginid,String password){
        if(userDao.LoginIDExist(loginid)&& userDao.getPassword(loginid)==password){
            return true;
        }
        return false;
    }

    public User findByID(long id){

        return userDao.findByID(id);
    }
    public boolean addUser(User user){
        if(user==null){
            throw new IllegalStateException("userDao missed");
        }
        if(userDao.save(user)){
            return true;
        }else return false;

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
