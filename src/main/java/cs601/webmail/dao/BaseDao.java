package cs601.webmail.dao;


import cs601.webmail.Constants;
import cs601.webmail.util.ResourceUtils;
import cs601.webmail.application.Configuration;

import java.sql.*;


/**
 * Created by yuanyuan on 10/27/14.
 */
public abstract class BaseDao{

    //private Connection connection;

    protected Connection getConnection() {

        Configuration configuration = Configuration.getDefault();

        String dbFile= configuration.getString(Configuration.DB_PATH);

        if (dbFile == null || dbFile.length() == 0) {
            dbFile = ResourceUtils.getClassPath() + "webmail.db";
        }

        if (Constants.DEBUG_MODE)
            System.out.println("[DEBUG] DB file is " + dbFile);

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBC driver missed", e);
        }
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException e) {
            throw new IllegalStateException("create connection failed.", e);
        }

    }
    protected int count(String tableName) {

        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;

        String sql = String.format("select count(*) from %s", tableName);

        try {
            statement = conn.prepareStatement(sql);

            rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

        throw new IllegalStateException("Can't get row count for table [" + tableName + "]");
    }

    // To close ResultSet
    protected void closeResultSetQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    // To close Statement
    protected void closeStatementQuietly(Statement statement) {
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


   /* private QueryRunner _queryRunner;*/

    /**
     * @deprecated  user EntityManager instead
     */
    //@Deprecated
    /*protected QueryRunner getQueryRunner() {
        if (_queryRunner  == null) {

            RequestContext context = RequestContext.getCurrentInstance();

            if (context == null) {
                SQLiteDataSource ds = new SQLiteDataSource();
                ds.setUrl("jdbc:sqlite:webmail.db");
                _queryRunner = new QueryRunner(ds);
            }
            else {
                // TODO need to add context support, not this mock block
                SQLiteDataSource ds = new SQLiteDataSource();
                ds.setUrl("jdbc:sqlite:webmail.db");
                _queryRunner = new QueryRunner(ds);
                System.out.println("[WARNING] no request context found");
            }
        }
        return _queryRunner;
    }*/




