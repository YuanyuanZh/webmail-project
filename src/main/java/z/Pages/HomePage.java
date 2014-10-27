package z.Pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Bill Xiong on 10/23/14.
 */
public class HomePage extends Page {

    public HomePage(HttpServletRequest request, HttpServletResponse response) {
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
        //out.println("Home page");
        // list mail

//        response.sendRedirect("/files/home.html");
        request.getRequestDispatcher("/files/home.html").forward(request, response);
    }

}
