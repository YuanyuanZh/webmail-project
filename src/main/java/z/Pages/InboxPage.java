package z.Pages;

import cs601.webmail.mail.Pop3Message;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.MailServiceImpl;
import cs601.webmail.util.DateTimeUtils;
import cs601.webmail.mail.Pop3Client;
import cs601.webmail.entity.MailSummary;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by yuanyuan on 10/20/14.
 */
public class InboxPage extends Page {

    public InboxPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

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
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");


        MailService mailService = new MailServiceImpl();

        System.out.println("mailserice got ok? " + (mailService != null));

        Map model = new HashMap();
        model.put("state", "ok");

        Pop3Client client = Pop3Client.createInstance();

        try {
            System.out.println("Number of new emails: " + client.getNumberOfNewMessages());

            int totalCount = client.getNumberOfNewMessages();
            List<Pop3Message> messages = client.getMessages(-10);

            List<MailSummary> mailSummaries = new ArrayList<MailSummary>();

            if (messages != null && messages.size() > 0) {
                for (int i = messages.size() - 1; i >=0 ; i--) {
                    Pop3Message message = messages.get(i);
                    MailSummary mailSummary = new MailSummary();

                    mailSummary.setMessageIndex(i);

                    String subject = getHeaderValue(message.getHeaders().get("Subject"));
                    mailSummary.setSubject(subject);
                    mailSummary.setFrom(getFromTo(getHeaderValue(message.getHeaders().get("From"))));
                    mailSummary.setTo(getFromTo(getHeaderValue(message.getHeaders().get("To"))));

                    String date = getHeaderValue(message.getHeaders().get("Date"));
                    date = formatDate(date);
                    mailSummary.setDate(date);

                    mailSummaries.add(mailSummary);
                }
            }

            model.put("total", totalCount);
            model.put("position", totalCount);
            model.put("stepSize", -10);
            model.put("messages", mailSummaries);

        } finally {
            client.close();
        }

        ObjectMapper om = new ObjectMapper();
        om.writeValue(getOut(), model);
    }

    private String formatDate(String date) {
        if (null == date || date.length() == 0)
            return date;

        // Mon, 6 Oct 2014 17:10:40 +0800 (CST)
        // Wed, 10 Aug 2011 01:50:01 +0000
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
            sender =sender;
        }

        return sender + " " + ss[1];
    }

    private String getHeaderValue(List<String> headerValues) {
        if (headerValues == null || headerValues.size() == 0)
            return null;

        return headerValues.get(0);
    }

}
