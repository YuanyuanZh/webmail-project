package cs601.webmail.dao.impl;

import cs601.webmail.dao.AccountDao;
import cs601.webmail.dao.BaseDao;
import cs601.webmail.dao.DaoException;
import cs601.webmail.frameworks.db.QueryRunner;
import cs601.webmail.entity.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yuanyuan on 10/29/14.
 */
public class AccountDaojdbcImpl extends BaseDao implements AccountDao{

   public void save(Account account){
        if(account==null){
            throw new IllegalArgumentException();
        }
       QueryRunner qr = getQueryRunner();
       try {

           int row = qr.update("insert into accounts(userid,email_address,epass) values(?,?,?)",
                   new Object[]{account.getUserId(), account.getEmailUsername(), account.getEmailPassword()});

           if (row != 1) {
               //return false;
               throw new IllegalStateException("Save account failed.");
           }
       } catch (SQLException e) {
           throw new DaoException(e);
       }
   }

    public void delete(Account account){
        if(account==null){
            throw new IllegalArgumentException();
        }
        QueryRunner qr = getQueryRunner();
        try {

            int row = qr.update("DELETE FROM accounts WHERE AID=? and USERID=?",
                    new Object[]{account.getId(), account.getUserId()});

            if (row != 1) {
                //return false;
                throw new IllegalStateException("delete account failed.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    public Account findById(Long userid,Long aid){
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs= null;

        try{
            statement=conn.prepareStatement("select * from accounts where AID=? and USERID=?");
            statement.setLong(1,aid);
            statement.setLong(1,userid);
            rs = statement.executeQuery();

            Account account=new Account();
            account=handleRowMapping(rs);
            return account;

        }catch (SQLException e){
            throw new DaoException();
        }finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

    }

    public List<String> listEmails(String userid){
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs= null;

        try{
            statement=conn.prepareStatement("select EMAIL_ADDRESS from accounts where USERID=?");
            statement.setString(1,userid);
            rs=statement.executeQuery();
            List<String> emails=new ArrayList<String>();
            while (rs.next()){
                //Account account=handleRowMapping(rs);
                emails.add(rs.getString("EMAIL_ADDRESS"));
            }
            return emails;
        }catch (SQLException e){
            throw new DaoException();
        }finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

    }

    private Account handleRowMapping(ResultSet rs) throws SQLException {
        Account account=new Account();

        account.setId(rs.getLong("AID"));
        account.setUserId(rs.getString("USERID"));
        account.setEmailUsername(rs.getString("EMAIL_ADDRESS"));
        account.setEmailPassword(rs.getString("EPASS"));

        return account;
    }
}
