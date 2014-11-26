package cs601.webmail.pages;


import cs601.webmail.Constants;
import cs601.webmail.entity.Account;

import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.service.AccountService;

import cs601.webmail.service.UserService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.UserServiceImpl;
import cs601.webmail.entity.User;
import cs601.webmail.util.EncryptUtils;

import javax.servlet.http.HttpSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yuanyuan on 11/11/14.
 */
public class RegisterNextPage extends ControllerPage {
    @Override
    public void body() throws Exception {

        HttpServletRequest request = RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response = RequestContext.getCurrentInstance().getResponse();
        HttpSession session = request.getSession(false);

        String method = request.getMethod();

        if ("get".equalsIgnoreCase(method)) {

            // Not found reg flag in session, that means the visitor does not get started to reg at all.
            if (session == null || session.getAttribute(RegisterPage.REGISTER_STEP_ATTR) == null) {
                response.sendRedirect("/register?error=103");
                return;
            }

            request.getRequestDispatcher("/files/registerNext.html").forward(request, response);
        } else {
            if (session == null || session.getAttribute(RegisterPage.REGISTERING_USER_ATTR) == null) {
                response.sendRedirect("/register?error=103");
                return;
            }

            doRegister(request, response, (User) session.getAttribute(RegisterPage.REGISTERING_USER_ATTR));
        }
    }

    private void doRegister(HttpServletRequest request, HttpServletResponse response, User registeringUser) throws IOException {
        Account account=new Account();
        AccountService accountService = new AccountServiceImpl();
        UserService userService=new UserServiceImpl();

        String emailAccount=request.getParameter("emailAddress");
        String emailPassword=request.getParameter("emailPassword");
        String popServer=request.getParameter("popServer");
        int popPort=Integer.parseInt(request.getParameter("popPort"));
        String SMTPServer=request.getParameter("SMTPServer");
        int SMTPPort=Integer.parseInt(request.getParameter("SMTPPort"));

        if(userService.LoginIDExist(registeringUser.getLoginId())){

            if(accountService.verifyAccount(emailAccount,emailPassword,popServer,popPort)
                    &&accountService.verifySMTPAccount(emailAccount,emailPassword,SMTPServer,SMTPPort)){

                account.setUserId(registeringUser.getId());
                account.setEmailUsername(emailAccount);
                account.setEmailPassword(EncryptUtils.encryptToHex(emailPassword, Constants.DEFAULT_AES_CIPHER_KEY));
                account.setPopServer(popServer);
                account.setPopServerPort(popPort);
                account.setEnableSsl(true);
                account.setSmtpServer(SMTPServer);
                account.setSmtpServerPort(SMTPPort);
                account.setEnableSmtpSsl(true);
                accountService.addAccount(account);

                HttpSession session = request.getSession(true);
                session.removeAttribute(RegisterPage.REGISTER_STEP_ATTR);
                session.removeAttribute(RegisterPage.REGISTERING_USER_ATTR);
                response.sendRedirect("/login?logId=" + registeringUser.getLoginId());

            }else {
                response.sendRedirect("/registerNext?error=302");
            }
        }else {
            response.sendRedirect("/registerNext?error=301");
        }
    }
}

