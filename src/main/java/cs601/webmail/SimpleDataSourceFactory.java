package cs601.webmail;

import cs601.webmail.application.Configurer;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by yuanyuan on 10/27/14.
 */
public class SimpleDataSourceFactory {

    public DataSource create() {
        String dbPath = (String) Configurer.getDefault().get(Configurer.DB_PATH);

        if (dbPath == null
                || dbPath.length() == 0) {
            throw new IllegalStateException("DB path from Config File was incorrect.");
        }

        if (Constants.DEBUG_MODE)
            System.out.println("[DEBUG] will test DB path [" + dbPath + "]");

        File dbFile = new File(dbPath);

        if (!dbFile.exists() || !dbFile.canRead()) {
            throw new IllegalStateException("DB file not found or can't be access");
        }

        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl("jdbc:sqlite:" + dbPath);

        validate(ds);

        if (Constants.DEBUG_MODE)
            System.out.println("[DEBUG] set JDBC url to [" + ds.getUrl() + "]");


        return ds;
    }

    private void validate(SQLiteDataSource ds) {
        Connection conn = null;

        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Data Source not available.", e);
        } finally {

            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                // ignore
            }
        }
    }

}
