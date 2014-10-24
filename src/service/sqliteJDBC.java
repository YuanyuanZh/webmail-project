package service;

import java.sql.*;

/**
 * Created by yuanyuan on 10/22/14.
 */
public class sqliteJDBC {
    private Connection c = null;
    private final String dbFile = "/Users/yuanyuan/Documents/Github/CS601/yuanyuanzh-webmail/webmail.db";

    public void connectToDB() {
        try {
            //String dbFile="/Users/yuanyuan/Documents/Github/CS601/yuanyuanzh-webmail/webmail.db";
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

    public void insertEmails(String subject, String from, String to, String content, String date, Integer uid, Integer aid, boolean read) throws SQLException {

        String insertString =
                "insert into emails(SUBJECT,MFROM,MTO,CONTENT,DATE,USERSID,ACCOUNTID,READ)values(?,?,?,?,?,?,?,?)";
        PreparedStatement insert = c.prepareStatement(insertString);
        insert.setString(1, subject);
        insert.setString(2, from);
        insert.setString(3, to);
        insert.setString(4, content);
        insert.setString(5, date);
        insert.setInt(6, uid);
        insert.setInt(7, aid);
        insert.setBoolean(8, read);
        int n = insert.executeUpdate();
        if (n != 1) {
            System.err.println("Bad update");
        }
        insert.close();

    }

    public void insertUsers(String logId, String pass, String firstName, String lastName) throws SQLException {
        String insertString = "insert into users(LOGID,PASS,FIRSTNAME,LASTNAME) values(?,?,?,?)";
        PreparedStatement insert = c.prepareStatement(insertString);
        insert.setString(1, logId);
        insert.setString(2, pass);
        insert.setString(3, firstName);
        insert.setString(4, lastName);
        int n = insert.executeUpdate();
        if (n != 1) {
            System.err.println("Bad update");
        }
        insert.close();

    }

    public void insertAccounts(Integer userid, String emailAddress, String pass) throws SQLException {
        String insertString = "insert into accounts(userid,email_address,epass) values(?,?,?)";
        PreparedStatement insert = c.prepareStatement(insertString);
        insert.setInt(1, userid);
        insert.setString(2, emailAddress);
        insert.setString(3, pass);
        int n = insert.executeUpdate();
        if (n != 1) {
            System.err.println("Bad update");
        }
        insert.close();
    }

    public void updateStatement() throws SQLException {

    }

    public void deleteStatement() throws SQLException {

    }

    public void queryStatement() throws SQLException {
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery("select * from users");

    }
}



