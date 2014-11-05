package cs601.webmail.dao.impl;

import cs601.webmail.dao.AccountDao;
import cs601.webmail.dao.BaseDao;
import cs601.webmail.dao.DaoException;
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

   public List<String> listEmails(Long userid){
       Connection conn=getConnection();
       PreparedStatement statement=null;
       ResultSet rs= null;

       try{
           statement=conn.prepareStatement("select EMAIL_ADDRESS from accounts where USERID=?");
           statement.setLong(1,userid);
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
        account.setUserId(rs.getLong("USERID"));
        account.setEmailUsername(rs.getString("EMAIL_ADDRESS"));
        account.setEmailPassword(rs.getString("EPASS"));

        return account;
    }

    public void save(Account account){
        if(account==null){
            throw new IllegalArgumentException();
        }
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs= null;

        try{
            statement =conn.prepareStatement("insert into accounts(aid,userid,email_address,epass values(?,?,?,?)");
            statement.setLong(1,account.getId());
            statement.setLong(2,account.getUserId());
            statement.setString(3,account.getEmailUsername());
            statement.setString(4,account.getEmailPassword());

            int row=statement.executeUpdate();
            if(row!=1){
                throw new IllegalArgumentException("save account failed");
            }

        }catch (SQLException e){
            throw new DaoException();
        }finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

    }
    public void delete(Account account){
        if(account==null){
            throw new IllegalArgumentException();
        }
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs= null;
        try{
            statement =conn.prepareStatement("DELETE FROM accounts WHERE AID=? and USERID=?");

            statement.setLong(1, account.getId());
            statement.setLong(2, account.getUserId());

            int row=statement.executeUpdate();
            if(row!=1){
                throw new IllegalArgumentException("delete account failed");
            }

        }catch (SQLException e){
            throw new DaoException();
        }finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }
    }
}
