package z.service;

import java.sql.*;

/**
 * Created by yuanyuan on 10/22/14.
 */
public class sqliteJDBC {
    private Connection c = null;
    private Statement statement;
    private final String dbFile="/Users/yuanyuan/Documents/Github/CS601/yuanyuanzh-webmail/webmail.db";

    public void connectToDB(){
        try {
            //String dbFile="/Users/yuanyuan/Documents/Github/CS601/yuanyuanzh-webmail/webmail.db";
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");

    }

    public void mailInsertStatement(String MSGID,String SUBJECT,String MFROM,String MTO,String CONTENT,String DATE,Integer uid,Integer aid)throws SQLException{
        String insertString =
                "insert into emails (MSGID,SUBJECT,MFROM,MTO,CONTENT,DATE,USERSID,ACCOUNTID) values(?,?,?,?,?,?,?,?)";
        PreparedStatement insert = c.prepareStatement(insertString);
        insert.setString(1,MSGID);
        insert.setString(2,SUBJECT);
        insert.setString(3,MFROM);
        insert.setString(4,MTO);
        insert.setString(5,CONTENT);
        insert.setString(6,DATE);
        insert.setInt(7, uid);
        insert.setInt(8, aid);
        int n = insert.executeUpdate();
        if ( n!=1 ) {
            System.err.println("Bad update");
        }
        insert.close();

    }
    public void updateStatement()throws SQLException{

    }
    public void deleteStatement()throws SQLException{

    }
    public void queryStatement()throws SQLException{
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery("select * from users");

    }
    public static void main( String args[] )
        {
            sqliteJDBC db1=new sqliteJDBC();
            db1.connectToDB();

        }
    }

