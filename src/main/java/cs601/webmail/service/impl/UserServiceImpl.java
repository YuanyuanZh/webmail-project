package cs601.webmail.service.impl;

import cs601.webmail.dao.UserDao;
import cs601.webmail.entity.User;
import cs601.webmail.service.UserService;

/**
 * Created by yuanyuan on 10/30/14.
 */
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private UserService userService;

    public UserServiceImpl(){

    }
    public User findByID(long id){

        return userDao.findByID(id);
    }
    public void save(User user){
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
}
