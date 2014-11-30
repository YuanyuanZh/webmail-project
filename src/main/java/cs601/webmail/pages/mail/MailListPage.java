package cs601.webmail.pages.mail;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.db.page.Order;
import cs601.webmail.frameworks.db.page.PageRequest;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;
import cs601.webmail.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import cs601.webmail.frameworks.mail.Address;

/**
 * Created by yuanyuan on 11/7/14.
 */
public class MailListPage extends ControllerPage {

    private static final Logger LOGGER = Logger.getLogger(MailListPage.class);

    public static final String EMPTY_STRING = "";
    public static final String DF_SAME_YEAR = "dd MMM 'at' HH:mm";

    @Override
    public void handleDefaultArgs() {
    }

    @Override
    public void header() {
    }

    @Override
    public void footer() {
    }

    @Override
    public void body() throws Exception {
        RequestContext context = RequestContext.getCurrentInstance();

        MailService mailService = new MailServiceImpl();
        AccountService accountService = new AccountServiceImpl();

        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();

        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();

        User user = (User)session.getAttribute(AuthenticationCheckFilter.LOGIN_SESSION_FLAG);

        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        Account currentAccount = accountService.findSingleByUserId(user.getId());

        if (currentAccount == null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", "Current user don't have any account.");
            return;
        }

        doListMails(req, resp, mailService, currentAccount);
    }

    private void doListMails(HttpServletRequest req,
                             HttpServletResponse resp,
                             MailService mailService,
                             Account currentAccount) {

        String curPage = req.getParameter("page");
        String folder = req.getParameter("folder");
        String sort=req.getParameter("sort");
        String sortDir=req.getParameter("sort_dir");

        //default sort
        Order order=Order.desc("MSGID");

        if(Strings.haveLength(sort)){
            if("Date".equalsIgnoreCase(sort)){
                sort="MSGID";
            }
            else if("Sender".equalsIgnoreCase(sort)){
                sort="MFROM";
            }
            else if("Subject".equalsIgnoreCase(sort)){
                sort="SUBJECT";
            }
            else {
                sort="MSGID";
                sortDir="desc";
            }

            order=("desc".equalsIgnoreCase(sortDir))? Order.desc(sort) : Order.asc(sort);
        }

        PageRequest pageRequest=new PageRequest(order);
        pageRequest.pageSize=getIntParam(req, "page_size", 15);  // PageRequest.DEFAULT_PAGE_SIZE;
        pageRequest.page = curPage != null ? Integer.parseInt(curPage) : 1;

        try {
            Mail.VirtualFolder fd = Mail.VirtualFolder.parseFolder(folder);

            // incorrect folder name
            if (fd == null) {throw new IllegalStateException(String.format("Folder %s not found.", folder));}

            cs601.webmail.frameworks.db.page.Page<Mail> pageResult =
                    mailService.findPage(fd, currentAccount, pageRequest);

            if (pageResult == null || !CollectionUtils.notEmpty(pageResult.getPageList())) {
                resp.addHeader("x-state", "ok");
                resp.addHeader("x-total", "0");
                resp.addHeader("x-position", "0");
                resp.addHeader("x-page", pageRequest.page + EMPTY_STRING);
                resp.addHeader("x-page-size", pageResult.getPageSize() + EMPTY_STRING);
                resp.addHeader("x-folder", folder);
                return;
            }

            List<Mail> mails = pageResult.getPageList();

            for (Mail m : mails) {
                m.setDate(formatDate(m.getDate()));
                m.setFrom(formatFrom(m.getFrom()));
                m.setTo(formatTo(m.getTo()));
            }

            PageTemplate template = new PageTemplate("/velocity/mail_list.vm");
            template.addParam("mails", mails);
            template.addParam("folder", folder);

            StringWriter writer = new StringWriter();
            template.merge(writer);

            resp.addHeader("x-state", "ok");  // careful: we're using HTTP headers instead of JSON to pass info.
            resp.addHeader("x-total", pageResult.getTotal() + EMPTY_STRING);
            resp.addHeader("x-position", pageResult.getPosition() + EMPTY_STRING);
            resp.addHeader("x-page", pageRequest.page + EMPTY_STRING);
            resp.addHeader("x-page-size", pageResult.getPageSize() + EMPTY_STRING);
            resp.addHeader("x-folder", folder);

            // output content
            getOut().print(writer.toString());

        } catch (Exception e) {
            LOGGER.error(e);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-exception", e.getMessage());
        }
    }

    static String formatTo(String to) {
        if (!Strings.haveLength(to)) {
            return to;
        }

        try {
            Address[] a = Address.parseAddresses(to);

            if (a != null && a.length > 0) {
                return a[0].getPersonal() != null ? a[0].getPersonal() : a[0].getAddress();
            }

            return to;
        } catch (cs601.webmail.frameworks.mail.ParseException e) {
            return to;
        }
    }

    static String formatFrom(String from) {
        if (!Strings.haveLength(from)) {
            return from;
        }

        try {
            Address a = Address.parseAddress(from);
            return a != null ? (a.getPersonal() != null ? a.getPersonal() : a.getAddress()) : from;
        } catch (cs601.webmail.frameworks.mail.ParseException e) {
            return from;
        }
    }

    static String formatDate(String date) {
        if (null == date || date.length() == 0)
            return date;

        // Mon, 6 Oct 2014 17:10:40 +0800 (CST)
        // Wed, 10 Aug 2011 01:50:01 +0000
        // 18 Feb 2009 15:48:10 +0800
        try {

            Date _date = DateTimeUtils.parse(date);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(_date);

            Date now = new Date();
            Calendar c2 = Calendar.getInstance();
            c2.setTime(now);

            String DF = DateTimeUtils.DF_DATE_AND_TIME_SHORT;

            // same year
            if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
                DF = DF_SAME_YEAR;
            }
            return DateTimeUtils.format(_date, DF);

        } catch (Exception e) {
            return date;
        }
    }

}
