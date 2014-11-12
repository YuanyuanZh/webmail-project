package cs601.webmail.page;


import cs601.webmail.entity.Account;

import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.service.AccountService;

import cs601.webmail.service.UserService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.UserServiceImpl;
import cs601.webmail.util.DigestUtils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yuanyuan on 11/11/14.
 */
public class RegisterNextPage extends Page {
    @Override
    public void body() throws Exception {

        HttpServletRequest request = RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response = RequestContext.getCurrentInstance().getResponse();

        String method = request.getMethod();

        if ("get".equalsIgnoreCase(method)) {
            request.getRequestDispatcher("/files/registerNext.html").forward(request, response);
        } else {
            doRegister(request, response);
        }
    }

    private void doRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Account account=new Account();
        AccountService accountService = new AccountServiceImpl();
        UserService userService=new UserServiceImpl();
        String username=request.getParameter("username");
        String emailAccount=request.getParameter("emailAddress");
        String emailPassword=request.getParameter("emailPassword");
        String popServer=request.getParameter("popServer");
        int popPort=Integer.parseInt(request.getParameter("popPort"));
        if(userService.LoginIDExist(username)){
            if(accountService.verifyAccount(emailAccount,emailPassword,popServer,popPort)){
                account.setUserId(username);
                account.setEmailUsername(emailAccount);
                account.setEmailPassword(DigestUtils.digestToSHA(emailPassword));
                accountService.addAccount(account);
                response.sendRedirect("/login");

            }else {
                response.sendRedirect("/registerNext?error=302");
            }
        }else {
            response.sendRedirect("/registerNext?error=301");
        }


    }
}
