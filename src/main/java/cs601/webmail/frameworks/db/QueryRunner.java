package cs601.webmail.frameworks.db;


import cs601.webmail.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yuanyuan on 10/31/14.
 */
public class QueryRunner {

    private static final String[] EMPTY_STRINGS = new String[0];

    private Connection connection;
    private boolean shouldCloseConn = false;

    public QueryRunner(Connection connection) {
        this.connection = connection;
    }

    public QueryRunner() {
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public <T> T query(String sql, ResultSetHandler<T> resultSetHandler, Object[] params) throws SQLException {

        Connection connection = prepareConnection();

        PreparedStatement statement = null;
        ResultSet rs = null;

        if (Constants.DEBUG_MODE)
            System.out.println("[DEBUG] execute sql > " + sql);

        try {
            connection.setAutoCommit(true);
            statement = connection.prepareStatement(sql);

            if (params.length > 0) {
                for (int i = 0, len = params.length; i < len; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }

            rs = statement.executeQuery();

            return resultSetHandler.handle(rs);

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            DBUtils.closeStatementQuietly(statement);
            DBUtils.closeResultSetQuietly(rs);
            releaseConnection(connection);
        }
    }

    public <T> T query(String sql, ResultSetHandler<T> handler) throws SQLException {
        return query(sql, handler, EMPTY_STRINGS);
    }

    protected void releaseConnection(Connection connection) {
        if (shouldCloseConn) {
            DBUtils.closeConnectionQuietly(connection);
            shouldCloseConn = false;
        }
    }

    protected Connection prepareConnection() throws SQLException {
        if (connection != null) {
            return connection;
        } else {
            shouldCloseConn = true;
            return DBUtils.generateConnection();
        }
    }

    public int update(String sql, Object[] params) throws SQLException {

        Connection connection = prepareConnection();

        PreparedStatement statement = null;

        if (Constants.DEBUG_MODE)
            System.out.println("[DEBUG] execute sql > " + sql);

        try {
            connection.setAutoCommit(true);
            statement = connection.prepareStatement(sql);

            if (params.length > 0) {
                for (int i = 0, len = params.length; i < len; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }

            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            DBUtils.closeStatementQuietly(statement);
            releaseConnection(connection);
        }
    }

    public int update(String sql) throws SQLException {
        return update(sql, EMPTY_STRINGS);
    }
}
