package cs601.webmail.pages.mail;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;
import cs601.webmail.util.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyuan on 11/28/14.
 */
public class MailSearchPage extends ControllerPage{

    private static final Logger LOGGER= Logger.getLogger(MailSearchPage.class);

    @Override
    public void body() throws Exception{
        RequestContext context=RequestContext.getCurrentInstance();
        AccountService accountService=new AccountServiceImpl();

        HttpServletRequest req=context.getRequest();
        HttpServletResponse resp=context.getResponse();

        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session=req.getSession();

        try {

            User user = (User) session.getAttribute(
                    AuthenticationCheckFilter.LOGIN_SESSION_FLAG);

            if (user == null) {
                throw new IllegalStateException("Illegal request without user in session.");
            }

            Account currentAccount = accountService.findSingleByUserId(user.getId());

            if (currentAccount == null) {
                throw new IllegalStateException("Current user don't have any account.");
            }

            doSearch(req, resp, user, currentAccount);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-exception", e.getMessage());
        }
    }

    private void doSearch(HttpServletRequest req,HttpServletResponse resp,User user, Account currentAccount)throws IOException{

        MailService mailService=new MailServiceImpl();

        String term=req.getParameter("term");
        String kw=req.getParameter("kw");

        String folder= Mail.VirtualFolder.inbox.getSystemFolder();

        List<Mail> mails=new ArrayList<Mail>();

        try{
            mails=mailService.searchByTerm(currentAccount,term,kw);
        }catch (Exception e){
            LOGGER.error(e);
        }

        if(mails==null||mails.size()==0){
            resp.addHeader("x-state","ok");
            resp.addHeader("x-total","0");
            return;
        }

        //process to render
        for(Mail m:mails){
            m.setDate(MailListPage.formatDate(m.getDate()));
            m.setFrom(MailListPage.formatFrom(m.getFrom()));
            m.setTo(MailListPage.formatTo(m.getTo()));
        }

        PageTemplate template=new PageTemplate("/velocity/mail_list.vm");
        template.addParam("mails",mails);
        template.addParam("folder",folder);

        StringWriter writer=new StringWriter();
        template.merge(writer);

        resp.addHeader("x-state","ok");
        resp.addHeader("x-total", mails.size() + "");
        resp.addHeader("x-folder", folder);

        getOut().print(writer.toString());
    }



}
