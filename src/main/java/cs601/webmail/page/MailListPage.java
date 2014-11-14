package cs601.webmail.page;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.db.Order;
import cs601.webmail.frameworks.db.PageRequest;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;
import cs601.webmail.util.DateTimeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.*;

/**
 * Created by yuanyuan on 11/8/14.
 */
public class MailListPage extends Page {

    public static final String EMPTY_STRING="";

    @Override
    public void body() throws Exception {



        RequestContext context = RequestContext.getCurrentInstance();

        MailService mailService = new MailServiceImpl();
        AccountService accountService = new AccountServiceImpl();

        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();

        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();

        User user=(User)session.getAttribute(AuthenticationCheckFilter.LOGIN_SESSION_FLAG);


        Account currentAccount = accountService.findById(user.getId());

        String curPage = req.getParameter("page");

        PageRequest pageRequest = new PageRequest(Order.desc("MSGID"));
        pageRequest.pageSize = PageRequest.DEFAULT_PAGE_SIZE;
        pageRequest.page = curPage != null ? Integer.parseInt(curPage) : 1;

        try {
            cs601.webmail.frameworks.db.Page<Mail> pageResult
                    = mailService.findByAccountAndPage(currentAccount, pageRequest);

            if (pageResult == null||!(pageResult.getPageList()!=null&&pageResult.getPageList().size()>0)) {

                resp.addHeader("x-state","ok");
                resp.addHeader("x-total", "0");
                resp.addHeader("x-position", "0");
                resp.addHeader("x-page-size", pageResult.getPageSize() + EMPTY_STRING);
                return;
            }

                List<Mail> mails = pageResult.getPageList();

                for (Mail m : mails) {
                    m.setDate(formatDate(m.getDate()));
                }

                PageTemplate template=new PageTemplate("/velocity/mail_list.vm");
                template.addParam("mails",mails);

                StringWriter writer=new StringWriter();
                template.merge(writer);

                resp.addHeader("x-state", "ok");  // using HTTP headers instead of JSON to pass info.
                resp.addHeader("x-total", pageResult.getTotal() + EMPTY_STRING);
                resp.addHeader("x-position", pageResult.getPosition() + EMPTY_STRING);
                resp.addHeader("x-page-size", pageResult.getPageSize() + EMPTY_STRING);

                getOut().println(writer.toString());


        } catch (Exception e) {

            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", e.getMessage());
        }

    }

    private String formatDate(String date) {
        if (null == date || date.length() == 0)
            return date;

        // Mon, 6 Oct 2014 17:10:40 +0800 (CST)
        // Wed, 10 Aug 2011 01:50:01 +0000
        // 18 Feb 2009 15:48:10 +0800
        try {

            Date _date = DateTimeUtils.parseDate(date);

            return DateTimeUtils.format(_date, DateTimeUtils.DF_DATE_AND_TIME_SHORT);
        } catch (ParseException e) {
            return date;
        }
    }


}
