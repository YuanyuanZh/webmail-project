package cs601.webmail.dao;


import cs601.webmail.Constants;
import cs601.webmail.db.DBUtils;
import cs601.webmail.db.QueryRunner;
import cs601.webmail.db.ResultSetHandler;
import cs601.webmail.util.ResourceUtils;
import cs601.webmail.application.Configuration;

import java.sql.*;


/**
 * Created by yuanyuan on 10/27/14.
 */
public abstract class BaseDao{

    private QueryRunner _queryRunner;

    protected QueryRunner getQueryRunner(){
        if(_queryRunner==null){
            _queryRunner =new QueryRunner();
        }
        return _queryRunner;
    }

    //private Connection connection;

    protected Connection getConnection() {

        return DBUtils.generateConnection();

    }
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

    // To close ResultSet
    protected void closeResultSetQuietly(ResultSet rs) {
        DBUtils.closeResultSetQuietly(rs);
    }

    // To close Statement
    protected void closeStatementQuietly(Statement statement) {
        DBUtils.closeStatementQuietly(statement);
    }

}


