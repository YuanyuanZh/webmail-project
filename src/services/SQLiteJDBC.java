package services;

import java.sql.*;

public class SQLiteJDBC
{
    public static void main( String args[] )
    {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:webmail.db");
            Statement statement = c.createStatement();
            ResultSet rs = statement.executeQuery("select * from users");
            while( rs.next() ) {
                // read the result set
                System.out.println("name = " + rs.getString("Firstname"));
                System.out.println("id = " + rs.getInt("uid"));
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}