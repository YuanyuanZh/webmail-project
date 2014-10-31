package z.Pages;

import cs601.webmail.db.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 10/22/14.
 */
public class LoginPage extends Page {
    public LoginPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public void verify() {
        // no-op
    }

    @Override
    public void header() {
        // no-op
    }

    @Override
    public void footer() {
        // no-op
    }

    @Override
    public void body() throws Exception {


    request.getRequestDispatcher("/files/login.html").forward(request, response);}
}
