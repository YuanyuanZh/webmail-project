package cs601.webmail.pages;

import cs601.webmail.util.DigestUtils;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.service.UserService;
import cs601.webmail.service.impl.UserServiceImpl;
import cs601.webmail.util.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by yuanyuan on 11/10/14.
 */
public class RegisterPage extends ControllerPage {
    public static final String REGISTERING_USER_ATTR = "user.registering";
    public static final String REGISTER_STEP_ATTR = "user.register.step";
    public static final String REGISTER_STEP_ONE = "1";
    public static final String REGISTER_STEP_TWO = "2";

    @Override
    public void body() throws Exception {

        HttpServletRequest request = RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response = RequestContext.getCurrentInstance().getResponse();

        String method = request.getMethod();

        // just forward to login page
        if ("get".equalsIgnoreCase(method)) {
            request.getRequestDispatcher("/files/register.html").forward(request, response);
        }
        else {
            doReg(request, response);
        }
    }

    private void doReg(HttpServletRequest request, HttpServletResponse response)throws IOException{
        UserService userService=new UserServiceImpl();

        String firstName= request.getParameter("first");
        String lastName= request.getParameter("last");
        String username= request.getParameter("loginID");
        String password1= request.getParameter("password1");
        String password2= request.getParameter("password2");

        if(Strings.haveLength(password1)&Strings.haveLength(password2)&&password1.equals(password2)){
            if(!userService.LoginIDExist(username)){
                User user=new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setLoginId(username);
                user.setPassword(DigestUtils.digestToSHA(password1));
                userService.addUser(user);
                HttpSession session = request.getSession(true);

                User registerUser=userService.findUserByLogId(username);

                session.setAttribute(REGISTER_STEP_ATTR, REGISTER_STEP_ONE); // mark as registration has been started
                session.setAttribute(REGISTERING_USER_ATTR, registerUser); // save user for next step

                response.sendRedirect("/registerNext?logId=" + username);

                //response.sendRedirect("/registerNext");

            }else {
                response.sendRedirect("/register?error=202");
            }

        }else {
            response.sendRedirect("/register?error=201");
        }


    }
}