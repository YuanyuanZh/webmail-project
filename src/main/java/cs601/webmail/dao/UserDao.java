package cs601.webmail.dao;

import cs601.webmail.entity.User;

/**
 * Created by yuanyuan on 10/30/14.
 */
public interface UserDao {

    User findByID(long id);

    void save(User user);
    /**
     * Update new password for the user.
     * @param id User's UID.
     * @param password New password that in hashed format.
     */
    void updatePass(long id,String password);

    boolean LoginIDExist(String LOGID);

    String getPassword(String LOGID);

    User findUserByLogId(String logId);
}