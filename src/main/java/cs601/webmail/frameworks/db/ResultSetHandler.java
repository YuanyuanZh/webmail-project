package cs601.webmail.frameworks.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yuanyuan on 10/30/14.
 */
public interface ResultSetHandler<T> {

    public T handle(ResultSet resultSet) throws SQLException;

}
