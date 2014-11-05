package cs601.webmail.page;

import cs601.webmail.MVC.RequestContext;
import cs601.webmail.service.UserService;
import cs601.webmail.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 11/3/14.
 */
public class LoginResponse extends Page {

    //HttpServletRequest req;
    //HttpServletResponse response;
    UserService userService=new UserServiceImpl();
    /*public LoginResponse(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }*/


    public void verify() {
        try {
            String username = getRequest().getParameter("login");
            String password = getRequest().getParameter("Password");
            String message="username or password invalid";
            //HttpSession session=req.getSession();
            //String u=session.getAttribute("user");
                if(userService.verifyUser(username,password)){
                   getResponse().sendRedirect("/");
                   System.out.println("**********");
                }
                else{
                    getRequest().setAttribute("error",message);
                   getResponse().sendRedirect("/login");
                    System.out.println("&&&&&&&&&&&");

                }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void header() {
        // no-op
    }

    @Override
    public void footer() {
        // no-op
    }

    /*@Override
    public void body() throws Exception {
        /*try {
            String username = req.getParameter("LoginID");
            String password = req.getParameter("Password");
            String message="username or password invalid";
            HttpSession session=req.getSession();
            //String u=session.getAttribute("user");
            if (!username.equals("") && !password.equals("")) {
                if(userService.verifyUser(username)){

                    resp.sendRedirect("/files/home.html");
                }
                else{
                    req.setAttribute("error",message);
                    resp.sendRedirect("/files/login.html");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //RequestContext context = RequestContext.getCurrentInstance();

        //context.getRequest().getRequestDispatcher("/files/home.html").forward(context.getRequest(), context.getResponse());
    //}*/

    public void body() throws Exception{

    }
}
