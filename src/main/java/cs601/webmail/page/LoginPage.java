package cs601.webmail.page;

import cs601.webmail.Constants;
import cs601.webmail.MVC.RequestContext;
import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by yuanyuan on 10/22/14.
 */
public class LoginPage extends Page{
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

        HttpServletRequest request=RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response=RequestContext.getCurrentInstance().getResponse();

        String method = request.getMethod();
        if("get".equalsIgnoreCase(method))
        {
            request.getRequestDispatcher("/files/login.html").forward(request,response);
        }
        else {
            doLogin(request,response);
        }
    }
    public void doLogin(HttpServletRequest request,HttpServletResponse response)throws IOException{
        HttpSession session = request.getSession(true);

        String username= request.getParameter("username");
        String password= request.getParameter("password");
        String rememberMe = request.getParameter("remember_me");

        if (username.length()!=0 && "demo".equalsIgnoreCase(username)
                &&password.length()!=0 && "demo".equalsIgnoreCase(password)){
            User user=new User();
            user.setId(999l);
            user.setFirstName("Demo");
            user.setLastName("omed");
            session.setAttribute(AuthenticationCheckFilter.LOGIN_SESSION_FLAG,user);
            if ("on".equals(rememberMe)) {
                             writeRememberMe(response, session, username);
                          }
            response.sendRedirect(Constants.DEFAULT_HOME);

        }
        else {
            response.sendRedirect("/login?error=101");
        }
    }
    private void writeRememberMe(HttpServletResponse response, HttpSession session, String username) {
        Cookie cookie = new Cookie(AuthenticationCheckFilter.LOGIN_COOKIE_FLAG, username);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.REMEMBER_DURATION_IN_SECONDS); // 14 days
        response.addCookie(cookie);

        String sessionId = session.getId();
        Cookie cookieSessionId = new Cookie(Constants.SESSION_ID,  sessionId);
        cookieSessionId.setPath("/");
        cookieSessionId.setMaxAge(Constants.REMEMBER_DURATION_IN_SECONDS); // 14 days
        response.addCookie(cookieSessionId);
    }
}
