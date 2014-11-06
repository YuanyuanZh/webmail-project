package cs601.webmail.db;

import cs601.webmail.Constants;
import cs601.webmail.application.Configuration;
import cs601.webmail.util.ResourceUtils;

import java.sql.*;

/**
 * Created by yuanyuan on 11/6/14.
 */
public final class DBUtils {
    public static Connection generateConnection(){
        Configuration configuration= Configuration.getDefault();
        String dbFile=configuration.getString(Configuration.DB_PATH);

        if(dbFile!=null && dbFile.length()!=0){
            dbFile= ResourceUtils.getClassPath()+"webmail.db";
        }
        if(Constants.DEBUG_MODE){
            System.out.println("\"[DEBUG] DBUtils: DB file is \" + dbFile");
        }
        try{
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e){
            throw new IllegalArgumentException("JDBC driver missed",e);
        }
        try{
            return DriverManager.getConnection("jdbc:sqlite:" + dbFile);

        }catch (SQLException e){
            throw new IllegalArgumentException("create connection failed.");
        }
    }

    public static void closeConnectionQuietly(Connection connection){
        if(connection!=null){
            try{
                if(!connection.isClosed()){
                    connection.close();
                    if (Constants.DEBUG_MODE)
                        System.out.println("[DEBUG] DBUtils: close connection invoked");
                }
            }catch (SQLException e){

            }
        }
    }

    public static void closeResultSetQuietly(ResultSet rs){
        if(rs !=null){
            try{
                if(!rs.isClosed()){
                    rs.close();
                }
            }catch (SQLException e){

            }
        }
    }
    public static void closeStatementQuietly(Statement statement) {
        if (statement != null) {
            try {
                if (!statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
