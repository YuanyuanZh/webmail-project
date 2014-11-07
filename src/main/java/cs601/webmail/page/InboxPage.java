package cs601.webmail.page;

import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.db.PageRequest;
import cs601.webmail.frameworks.db.Order;
import cs601.webmail.MVC.RequestContext;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;
import cs601.webmail.util.DateTimeUtils;

import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by yuanyuan on 10/20/14.
 */
public class InboxPage extends Page{

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

        RequestContext context = RequestContext.getCurrentInstance();

        MailService mailService = new MailServiceImpl();
        AccountService accountService = new AccountServiceImpl();

        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();

        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Map model = new HashMap();

        // TODO just for demo
        Account currentAccount = accountService.findById(-1l);

        String curPage = req.getParameter("page");

        PageRequest pageRequest = new PageRequest(Order.desc("MSGID"));
        pageRequest.pageSize = PageRequest.DEFAULT_PAGE_SIZE;
        pageRequest.page = curPage != null ? Integer.parseInt(curPage) : 1;

        try {
            cs601.webmail.frameworks.db.Page<Mail> pageResult
                    = mailService.findByAccountAndPage(currentAccount, pageRequest);

            model.put("state", "ok");

            if (pageResult != null) {

                model.put("total", pageResult.getTotal());
                model.put("position", pageResult.getPosition());
                model.put("pageSize", pageResult.getPageSize());

                List<Mail> mails = pageResult.getPageList();

                for (Mail m : mails) {
                    m.setDate(formatDate(m.getDate()));
                }

                model.put("messages", mails);
            }
        } catch (Exception e) {

            model.put("state", "error");
            model.put("msg", e.getMessage());
        }

        ObjectMapper om = new ObjectMapper();
        om.writeValue(getOut(), model);
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

    private String getFromTo(String from) throws UnsupportedEncodingException {

        if (from == null || from.length() == 0)
            return from;

        String[] ss = from.split(" ");

        if (ss.length == 1)
            return from;

        String sender = ss[0];
        boolean hasQuote = ss[0].startsWith("\"");

        if (hasQuote) {
            sender = "\"" + sender.replace("\"", "") + "\"";
        } else {
            sender = sender;
        }

        return sender + " " + ss[1];
    }

    private String getHeaderValue(List<String> headerValues) {
        if (headerValues == null || headerValues.size() == 0)
            return null;

        return headerValues.get(0);
    }

}
