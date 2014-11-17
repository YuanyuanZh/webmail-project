package cs601.webmail.pages;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.StringWriter;

/**
 * Created by yuanyuan on 11/15/14.
 */
public class ProfilePage extends Page {

    @Override
    public void body()throws Exception{
        RequestContext context=RequestContext.getCurrentInstance();
        MailService mailService=new MailServiceImpl();
        AccountService accountService=new AccountServiceImpl();

        HttpServletRequest req=context.getRequest();
        HttpServletResponse resp=context.getResponse();

        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session=req.getSession();

        User user=(User)session.getAttribute(AuthenticationCheckFilter.LOGIN_SESSION_FLAG);
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }
        try{
            PageTemplate template=new PageTemplate("/velocity/fragment_profile.vm");
            template.addParam("fullName",user.getFirstName()+" "+user.getLastName());

            StringWriter writer=new StringWriter();
            template.merge(writer);

            resp.addHeader("x-state", "ok");
            getOut().print(writer.toString());

        }catch (Exception e){
            resp.addHeader("x-state", "error");
            resp.addHeader("x-exception", e.getMessage());
        }
    }
}
