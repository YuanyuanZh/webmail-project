package cs601.webmail.dao;

import cs601.webmail.dao.DaoException;
import cs601.webmail.frameworks.db.DBUtils;
import cs601.webmail.frameworks.db.QueryRunner;
import cs601.webmail.frameworks.db.ResultSetHandler;
import cs601.webmail.util.Strings;

import java.sql.*;

/**
 * Created by yuanyuan on 10/29/14.
 */
public abstract class BaseDao {

    private QueryRunner _queryRunner;

    protected QueryRunner getQueryRunner() {
        if (_queryRunner == null) {
            _queryRunner = new QueryRunner();
        }
        return _queryRunner;
    }

    protected Connection getConnection() {
        return DBUtils.generateConnection();
    }

    // Return how many rows are there in the table
    protected int count(String tableName) {

        QueryRunner qr = getQueryRunner();

        String sql = String.format("select count(*) from %s", tableName);

        try {
            return qr.query(sql, new ResultSetHandler<Integer>() {
                @Override
                public Integer handle(ResultSet rs) throws SQLException {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    return -1;
                }
            });
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    protected int count(String tableName, String extraConditions) {

        QueryRunner qr = getQueryRunner();

        String sql = String.format("select count(*) from %s", tableName);

        if (Strings.haveLength(extraConditions)) {
            sql = sql + " where " + extraConditions;
        }

        try {
            return qr.query(sql, new ResultSetHandler<Integer>() {
                @Override
                public Integer handle(ResultSet rs) throws SQLException {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    return -1;
                }
            });
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    // Close ResultSet & ignore exception
    protected void closeResultSetQuietly(ResultSet rs) {
        DBUtils.closeResultSetQuietly(rs);
    }

    // Close Statement & ignore exception
    protected void closeStatementQuietly(Statement statement) {
        DBUtils.closeStatementQuietly(statement);
    }

}
