package cs601.webmail.dao.impl;

import cs601.webmail.Configuration;
import cs601.webmail.dao.AccountDao;
import cs601.webmail.dao.BaseDao;
import cs601.webmail.dao.DaoException;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.db.QueryRunner;
import cs601.webmail.entity.Account;
import cs601.webmail.frameworks.db.ResultSetHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yuanyuan on 10/29/14.
 */
public class AccountDaoImpl extends BaseDao implements AccountDao{

    public void save(Account account){
        if(account==null){
            throw new IllegalArgumentException();
        }
        QueryRunner qr = getQueryRunner();
        try {

            int row = qr.update("insert into accounts(userid,email_address,epass,POP_SERVER,POP_SERVER_PORT,ENABLE_SSL) values(?,?,?,?,?,?)",
                    new Object[]{account.getUserId(), account.getEmailUsername(), account.getEmailPassword(),account.getPopServer(),account.getPopServerPort(),account.isEnableSsl()});

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

    public List<Account> listAll() {
        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select * from ACCOUNTS", new ResultSetHandler<List<Account> >() {
                @Override
                public List<Account>  handle(ResultSet resultSet) throws SQLException {
                    return handleRowsMapping(resultSet);
                }
            });
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Account findById(Long userid){
        if(userid==null){
            throw new IllegalArgumentException();
        }
        QueryRunner qr = getQueryRunner();
        try {
            return qr.query("select * from Accounts where USERID=?", new ResultSetHandler<Account>() {
                @Override
                public Account handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return handleRowMapping(resultSet);
                    }
                    return null;
                }
            }, new Object[]{userid});
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    public List<String> listEmails(Long userid){
        Connection conn=getConnection();
        PreparedStatement statement=null;
        ResultSet rs= null;

        try{
            statement=conn.prepareStatement("select EMAIL_ADDRESS from accounts where USERID=?");
            statement.setLong(1, userid);
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
        account.setPopServer(rs.getString("POP_SERVER"));
        account.setPopServerPort(rs.getInt("POP_SERVER_PORT"));
        account.setEnableSsl(rs.getBoolean("ENABLE_SSL"));

        return account;
    }
    private List<Account> handleRowsMapping(ResultSet rs) throws SQLException {
        List<Account> accounts = new ArrayList<Account>();

        while (rs.next()) {
            accounts.add(handleRowMapping(rs));
        }

        return accounts;
    }
}
