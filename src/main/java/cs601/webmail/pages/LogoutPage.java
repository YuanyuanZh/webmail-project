package cs601.webmail.pages;

import cs601.webmail.Constants;
import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.Page;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by yuanyuan on 11/5/14.
 */
public class LogoutPage extends Page {

    @Override
    public void body() throws Exception {

        HttpServletRequest request = RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response = RequestContext.getCurrentInstance().getResponse();

        HttpSession session = request.getSession(true);

        session.invalidate();

        clearCookie(response);

        // go to login again
        response.sendRedirect("/login");
    }

    private void clearCookie(HttpServletResponse response) {
        Cookie rememberMeCookie = new Cookie(AuthenticationCheckFilter.LOGIN_COOKIE_FLAG, "");
        rememberMeCookie.setMaxAge(0);
        rememberMeCookie.setPath("/");

        Cookie sessionCookie = new Cookie(Constants.SESSION_ID, "");
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");

        response.addCookie(rememberMeCookie);
        response.addCookie(sessionCookie);
    }

}

