package cs601.webmail.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yuanyuan on 11/6/14.
 */
public interface ResultSetHandler<T> {

    public T handle(ResultSet resultSet) throws SQLException;

}