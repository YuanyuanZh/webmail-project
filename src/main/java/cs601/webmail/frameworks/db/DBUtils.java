package cs601.webmail.frameworks.db;

import cs601.webmail.Constants;
import cs601.webmail.Configuration;
import cs601.webmail.util.ResourceUtils;
import cs601.webmail.util.Strings;
import cs601.webmail.util.Logger;

import java.sql.*;

/**
 * Created by yuanyuan on 10/31/14.
 */
public final class DBUtils {

    private static final Logger LOGGER = Logger.getLogger(DBUtils.class);

    public static Connection generateConnection() {

        Configuration configuration = Configuration.getDefault();

        String dbFile = configuration.getString(Configuration.DB_PATH);

        if (!Strings.haveLength(dbFile)) {
            dbFile = ResourceUtils.getClassPath() + "webmail.db";
        }

        if (Constants.DEBUG_MODE)
            LOGGER.debug("DB file is " + dbFile);

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

    public static void closeConnectionQuietly(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();

                    if (Constants.DEBUG_MODE)
                        LOGGER.debug("connection closed.");
                }
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    // To close ResultSet
    public static void closeResultSetQuietly(ResultSet rs) {
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
