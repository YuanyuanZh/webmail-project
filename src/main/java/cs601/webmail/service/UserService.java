package cs601.webmail.service;

import cs601.webmail.entity.User;

/**
 * Created by yuanyuan on 10/30/14.
 */
public interface UserService {
    User findByID(long id);
    boolean verifyUser(String loginid,String password);
    boolean addUser(User user);
    void updatePass(long id,String password);
    boolean LoginIDExist(String LOGID);
}
