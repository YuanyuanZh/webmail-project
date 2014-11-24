package cs601.webmail.auth;

import cs601.webmail.util.Strings;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by yuanyuan on 11/6/14.
 */
public class AuthenticationCheckFilter implements Filter {

    public static final String LOGIN_SESSION_FLAG = "webmail.login";
    public static final String LOGIN_COOKIE_FLAG = "webmail_login";

    private static final String[] WHITE_LIST = {
        "/resources/", "/login", "/logout", "/register", "/rest/"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession();

        try {
            checkLogin(req, session);

            chain.doFilter(request, response);

        } catch (UnauthenticatedException e) {

            String rememberMe = readRememberMe(req);

            if (Strings.haveLength(rememberMe))
                resp.sendRedirect("/login?username=" + rememberMe);
            else
                resp.sendRedirect("/login");

            return;
        }
    }

    private String readRememberMe(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (LOGIN_COOKIE_FLAG.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void checkLogin(HttpServletRequest req, HttpSession session) throws UnauthenticatedException {
        Object sessionFlag = session.getAttribute(LOGIN_SESSION_FLAG);

        String uri = req.getRequestURI();

        for (String prefix : WHITE_LIST) {
            if (uri.startsWith(prefix)) {
                return;
            }
        }

        if (sessionFlag == null) {
            throw new UnauthenticatedException();
        }
    }

    @Override
    public void destroy() {

    }
}
