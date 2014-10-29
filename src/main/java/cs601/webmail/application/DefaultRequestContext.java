package cs601.webmail.application;

import cs601.webmail.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


class DefaultRequestContext extends RequestContext {

    DefaultRequestContext(HttpServletRequest _request, HttpServletResponse _response) {
        this._request = _request;
        this._response = _response;

    }

    private HttpServletRequest _request;

    private HttpServletResponse _response;

    private Connection connection;

    //private ServiceManager serviceManager;

    @Override
    public void init() {
        if (connection == null) {
            connection = generateConnection();
        }
    }

    private Connection generateConnection() {
        String dbFile= ResourceUtils.getClassPath() + "webmail.db";
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

    @Override
    public void release() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
    }

    @Override
    public HttpServletRequest getRequest() {
        return _request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return _response;
    }


    @Override
    public Connection currentConnection() {
        return connection;
    }

}
