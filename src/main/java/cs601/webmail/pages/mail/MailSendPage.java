package cs601.webmail.pages.mail;

import cs601.webmail.entity.Account;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.mail.Address;
import cs601.webmail.frameworks.mail.Message;
import cs601.webmail.frameworks.mail.MessagingException;
import cs601.webmail.frameworks.mail.smtp.SMTPClient;
import cs601.webmail.frameworks.mail.smtp.SMTPMessage;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.ContactService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.ContactServiceImpl;
import cs601.webmail.util.Strings;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class MailSendPage extends ControllerPage {

    private static final AtomicLong MESSAGE_SEED = new AtomicLong(0);

    public static final String CLRF = "\r\n";
    public static final String DEFAULT_CHARSET = "utf-8";

    @Override
    public void body() throws Exception {

        RequestContext context = RequestContext.getCurrentInstance();

        ContactService contactService = new ContactServiceImpl();

        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();

        User user = null;

        try {
            user = checkUserLogin(req, resp);
        } catch (NotAuthenticatedException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        String method = req.getMethod();

        if ("post".equalsIgnoreCase(method)) {
            try {
                doSendMail(req, resp, user);
                resp.addHeader("x-state", "ok");
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.addHeader("x-state", "error");
                resp.addHeader("x-exception", e.getMessage());
            }
        }
    }

    private void doSendMail(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException, MessagingException {

        AccountService accountService = new AccountServiceImpl();

        Account account = accountService.findSingleByUserId(user.getId());

        if (account == null) {
            throw new IllegalStateException("Account not available.");
        }

        String[] values = null;
        String subject = getStringParam(req, "subject");
        String content = getStringParam(req, "content");

        StringBuffer contentBuf = new StringBuffer();

        // append text from request
        contentBuf.append(content);

        // append mail signature if requested & have
        if (getStringParam(req, "appendSignature", "off").equalsIgnoreCase("on") &&
                Strings.haveLength(account.getMailSignature())) {
            contentBuf.append(CLRF).append(CLRF);
            contentBuf.append(account.getMailSignature());
        }

        byte[] contentBytes = null;

        try {
            contentBytes = contentBuf.toString().getBytes(DEFAULT_CHARSET);
        } catch (Exception e) {
            contentBytes = contentBuf.toString().getBytes();
        }

        SMTPMessage msg = new SMTPMessage(Base64.encodeBase64Chunked(contentBytes));

        msg.setHeader(Message.HeaderNames.ContentType, "text/plain; charset=" + DEFAULT_CHARSET);
        msg.setHeader(Message.HeaderNames.ContentTransferEncoding, "base64");
        msg.setFrom(getFromAddress(account)); // Mail From

        // Param `To`, required
        if (req.getParameter("to") != null) {
            msg.addRecipients(Message.RecipientType.TO,
                    req.getParameter("to")); // Mail TO
        }
        else if ((values = req.getParameterValues("to[]")) != null) {
            for (String v : values) {
                if (Strings.haveLength(v))
                    msg.addRecipients(Message.RecipientType.TO, v);
            }
        }
        else {
            throw new IllegalArgumentException("'TO' couldn't be missed.");
        }

        // Param `Cc`, optional
        if (req.getParameter("cc") != null) {
            msg.addRecipients(Message.RecipientType.CC,
                    req.getParameter("cc")); // Mail CC
        }
        else if ((values = req.getParameterValues("cc[]")) != null) {
            for (String v : values) {
                if (Strings.haveLength(v))
                    msg.addRecipients(Message.RecipientType.CC, v);
            }
        }

        // Param `Bcc`, optional
        if (req.getParameter("bcc") != null) {
            msg.addRecipients(Message.RecipientType.BCC,
                    req.getParameter("bcc")); // Mail BCC
        }
        else if ((values = req.getParameterValues("bcc[]")) != null) {
            for (String v : values) {
                if (Strings.haveLength(v))
                    msg.addRecipients(Message.RecipientType.BCC, v);
            }
        }

        // plain UTC date string
        msg.setHeader(Message.HeaderNames.Date, new Date().toString());

        msg.setHeader(Message.HeaderNames.MessageID, generateMessageID(account.getEmailUsername()));

        msg.setSubject(subject, "utf-8");

        SMTPClient client = prepareSender(account);

        try {
            client.send(msg);
        } catch (Exception e) {
            throw new MessagingException(e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private Address getFromAddress(Account account) throws UnsupportedEncodingException {
        if (account == null || account.getEmailUsername() == null)
            return null;

        Address address = new Address();
        address.setAddress(account.getEmailUsername());

        if (Strings.haveLength(account.getDisplayName())) {
            address.setPersonal(account.getDisplayName());
        }

        return address;
    }

    private SMTPClient prepareSender(Account account) throws IOException {
        String host = account.getSmtpServer();
        int port = account.getSmtpServerPort();
        boolean ssl = account.isEnableSmtpSsl();

        String username = account.getEmailUsername();
        String password = account.getEmailPassword();

        SMTPClient client = new SMTPClient(ssl);
        client.setDebug(true);
        client.connect(host, port);
        client.login(username, password);

        return client;
    }

    /**
     * Detail about Message-Id, plz see RFC822
     *
     * @param senderAddress Supposed to be a email format string. Such as 'foo@bar.com'
     * @return A string in this format "<" + nanoTime + "." + seed + "@" + domain + ">"
     *      e.g. <143123421.32@aixmail.com>
     */
    public String generateMessageID(String senderAddress) {
        if (!Strings.haveLength(senderAddress)) {
            throw new IllegalArgumentException();
        }

        String domain = senderAddress.substring(senderAddress.indexOf("@"));

        StringBuilder sb = new StringBuilder();
        sb.append("<").append(System.currentTimeMillis()).append(".")
                .append(MESSAGE_SEED.incrementAndGet())
                .append(domain)
                .append(">");

        return sb.toString();
    }

}
