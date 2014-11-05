package cs601.webmail.dao.impl;

import cs601.webmail.dao.DaoException;
import cs601.webmail.dao.UserDao;
import cs601.webmail.entity.User;
import cs601.webmail.dao.BaseDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yuanyuan on 10/30/14.
 */
public class UserDAOImpl extends BaseDao implements UserDao {

    public User findByID(long id){

        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs=null;
        try {
            statement = conn.prepareStatement("select * from users where UID=?");

            statement.setLong(1, id);

            rs = statement.executeQuery();

            if (rs.next()) {
                return handleRowMapping(rs);
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

        return null;

    }
    private User handleRowMapping(ResultSet rs) throws SQLException {
        User user=new User();

        user.setId(rs.getLong("UID"));
        user.setLoginId(rs.getString("LOGID"));
        user.setPassword(rs.getString("PASS"));
        user.setFirstName(rs.getString("FirstName"));
        user.setLastName(rs.getString("LastName"));

        return user;
    }
    public boolean save(User user){

        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs=null;
        try {
            statement = conn.prepareStatement("insert into users(UID,LOGID,PASS,FIRSTNAME,LASTNAME) values(?,?,?,?");

            statement.setLong(1, user.getId());
            statement.setString(2,user.getLoginId());
            statement.setString(3,user.getFirstName());
            statement.setString(4,user.getLastName());

            rs = statement.executeQuery();
            int rows = statement.executeUpdate();

            if (rows != 1) {
                return false;
                //throw new IllegalStateException("Save user failed.");

            }else{return true;}

        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }


    }

    public void updatePass(long id,String password){
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs=null;
        try {
            statement = conn.prepareStatement("UPDATE USERS SET PASS = '?' WHERE ID = ?");
            statement.setString(1, password);
            statement.setLong(2, id);

            rs = statement.executeQuery();
            int n = statement.executeUpdate();

            if (n != 1) {
                throw new IllegalStateException("update password failed.");
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

    }
    public boolean LoginIDExist(String LOGID){
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs=null;
        try {
            statement = conn.prepareStatement("SELECT * FROM USERS WHERE LOGIN=?");
            statement.setString(1, LOGID);
            rs = statement.executeQuery();
            if(rs!=null){
                return true;
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }
        return false;
    }

    public String getPassword(String LOGID){
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs=null;
        try {
            statement = conn.prepareStatement("select pass from users where LOGID=?");

            statement.setString(1, LOGID);

            rs = statement.executeQuery();

            if(rs==null){
                return null;
            }
            else return rs.getString("PASS");

        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

    }
}
