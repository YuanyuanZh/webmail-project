package cs601.webmail.dao.impl;

import cs601.webmail.dao.DaoException;
import cs601.webmail.dao.UserDao;
import cs601.webmail.db.QueryRunner;
import cs601.webmail.db.ResultSetHandler;
import cs601.webmail.entity.User;
import cs601.webmail.dao.BaseDao;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yuanyuan on 10/30/14.
 */
public class UserDAOImpl extends BaseDao implements UserDao {

    public User findByID(long id) {

        QueryRunner qr = getQueryRunner();
        try {
            return qr.query("select * from users where UID=?", new ResultSetHandler<User>() {
                @Override
                public User handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return handleRowMapping(resultSet);
                    }
                    return null;
                }
            }, new Object[]{id});
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private User handleRowMapping(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getLong("UID"));
        user.setLoginId(rs.getString("LOGID"));
        user.setPassword(rs.getString("PASS"));
        user.setFirstName(rs.getString("FirstName"));
        user.setLastName(rs.getString("LastName"));

        return user;
    }

    public void save(User user) {

        QueryRunner qr = getQueryRunner();
        try {

            int row = qr.update("insert into users(UID,LOGID,PASS,FIRSTNAME,LASTNAME) values(?,?,?,?)",
                    new Object[]{user.getId(), user.getLoginId(), user.getFirstName(), user.getLastName()});

            if (row != 1) {
                //return false;
                throw new IllegalStateException("Save entity failed.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    public void updatePass(long id, String password) {

        QueryRunner qr = getQueryRunner();
        try {

            int row = qr.update("UPDATE USERS SET PASS = '?' WHERE ID = ?", new Object[]{id, password});

            if (row != 1) {
                //return false;
                throw new IllegalStateException("update password failed.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean LoginIDExist(String LOGID) {

        QueryRunner qr = getQueryRunner();
        try {
            int row = qr.query("SELECT Count(*) FROM USERS WHERE LOGID=?", new ResultSetHandler<Integer>() {
                @Override
                public Integer handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                    return -1;
                }
            }, new Object[]{LOGID});
            if (row > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public String getPassword(String LOGID) {

        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select pass from users where LOGID=?", new ResultSetHandler<String>() {
                @Override
                public String handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return resultSet.getString("PASS");
                    }
                    return null;
                }
            }, new Object[]{LOGID});

        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }
}
