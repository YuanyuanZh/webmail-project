package cs601.webmail.page;

import cs601.webmail.MVC.RequestContext;
import cs601.webmail.entity.User;
import cs601.webmail.service.UserService;
import cs601.webmail.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 11/4/14.
 */
public class RegisterResponse extends Page {

    @Override
    public void verify() {
        UserService userService=new UserServiceImpl();
        try {
            String username = getRequest().getParameter("LoginID");
            String password = getRequest().getParameter("Password");
            String firstName = getRequest().getParameter("FirstName");
            String lastName = getRequest().getParameter("LastName");
            String message="LoginID already exist";
            if(userService.LoginIDExist(username)){
                getRequest().setAttribute("error",message);
                getResponse().sendRedirect("/files/login.html");
            }
            User user=new User();
            user.setLoginId(username);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            if(userService.addUser(user)){
                getResponse().sendRedirect("");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

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
    public void body() throws Exception{

    }
}
