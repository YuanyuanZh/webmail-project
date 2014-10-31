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
    public Account findById(Long aid){
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs= null;

        try{
            statement=conn.prepareStatement("select * from emails where AID=?");
            statement.setLong(1,aid);
            rs = statement.executeQuery();

        }catch (SQLException e){
            throw new DaoException();
        }finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }
        return null;
    }

   public List<String> listEmails(){
       Connection conn=getConnection();
       PreparedStatement statement=null;
       ResultSet rs= null;

       try{
           statement=conn.prepareStatement("select * from accounts");
           rs=statement.executeQuery();
           List<String> accounts=new ArrayList<String>();
           while (rs.next()){
               Account account=handleRowMapping(rs);
               accounts.add(account.getEmailUsername());
           }
          return accounts;
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
}
