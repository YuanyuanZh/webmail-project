package cs601.webmail.pages.mail;

import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.mail.Address;
import cs601.webmail.frameworks.mail.Message;
import cs601.webmail.frameworks.mail.MessagingException;
import cs601.webmail.frameworks.mail.MimeContent;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.ControllerPage;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;
import cs601.webmail.util.*;
import cs601.webmail.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuanyuan on 11/6/14.
 */
public class ReadMailPage extends ControllerPage {

    private static final Logger LOGGER = Logger.getLogger(ReadMailPage.class);
    private static final String[] MX_FILE_SUBFIX = new String[] {".txt", ".mx", ".mx.txt"};
    private static final SimpleDateFormat MAIL_SDF = new SimpleDateFormat("MM/dd yyyy HH:mm");

    @Override
    public void body() throws Exception {

        HttpServletRequest request = RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response = RequestContext.getCurrentInstance().getResponse();

        String id = request.getParameter("id");

        if (!Strings.haveLength(id)) {
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Invalid mail ID");
            return;
        }

        User user;

        try {
            user = checkUserLogin(request, response);
        } catch (NotAuthenticatedException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        renderByRawFile(request, response, user, id);
    }

    /**
     * Parse raw file and wrapped into a java object in real time.
     *
     * @param user
     * @param id Identity of mail object.
     */
    private void renderByRawFile(HttpServletRequest request, HttpServletResponse response, User user, String id) throws IOException {
        AccountService accountService = new AccountServiceImpl();
        MailService mailService = new MailServiceImpl();

        Mail mail = mailService.findById(Long.parseLong(id));
        Map<String, Object> extraParams = new HashMap<String, Object>();

        if (mail == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Mail entry not found");
            return;
        }

        // mark as READ
        if (mail.getFlagUnread() > 0) {
            mail.setFlagUnread(0);
            mail.setFlagNew(0);
            mailService.save(mail);
        }

        Account currentAccount = accountService.findSingleByUserId(user.getId());

        if (currentAccount == null) {
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Sorry, your account was not available.");
            return;
        }

        Mail.VirtualFolder folder=Mail.VirtualFolder.parseFolder(mail.getFolder());
        String uid = mail.getUid();
        String messageId = mail.getMessageId();

        //try UID(compatible for legacy version)
        String rawFilePath=ResourceUtils.resolveMailFolderPath(currentAccount.getEmailUsername(), folder);
        File rawFile=null;

        if (uid!=null){
            rawFile = findMailFile(rawFilePath + File.separator + DigestUtils.digestToSHA(uid));
        }

        //try Message-ID

        if(rawFile==null|| !rawFile.exists()){
            rawFile=findMailFile(rawFilePath+File.separator+cleanMessageID(messageId));
        }

        // raw file can neither be found nor be readable
        if (rawFile == null || !rawFile.exists() || !rawFile.canRead()) {
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Mail raw file not found");
            return;
        }

        Message message = null;
        Exception exception = null;
        String originalContentType = null;
        Object msgContent = null;

        response.setContentType("text/html; charset=utf-8");
        // default template is text
        PageTemplate template = new PageTemplate("/velocity/mail_in_text.vm");

        try {
            message = new Message(new FileInputStream(rawFile));

            originalContentType = message.getContentType();

            mail.setSubject(MimeUtils.decodeText(message.getSubject()));
            mail.setFrom(MimeUtils.decodeText(getFromField(message)));
            mail.setUid(uid);
            Date _date = message.getSentDate();
            mail.setDate(_date != null ? MAIL_SDF.format(_date) : null);

            extraParams.put("mail_from", getPlainAddresses(message.getFrom()));
            extraParams.put("mail_to", getPlainAddresses(message.getRecipients(Message.RecipientType.TO)));
            extraParams.put("mail_cc", getPlainAddresses(message.getRecipients(Message.RecipientType.CC)));

            // try to get content
            msgContent = message.getContent();
        } catch (IOException e) {
            exception = e;
        } catch (MessagingException e) {
            exception = e;
        }

        if (msgContent instanceof MimeContent) {
            MimeContent mc = (MimeContent) msgContent;
            mail.setContent(mc.getContent());
            mail.setContentType(mc.getContentType());

            if ("text/html".equalsIgnoreCase(mc.getContentType())) {
                template = new PageTemplate("/velocity/mail_in_html.vm");
            }
        }
        else if (msgContent instanceof String) {
            mail.setContent((String) msgContent);
        }
        else {
            mail.setContent(msgContent != null ? msgContent.toString() : null);
        }

        // render a warning cause got exception (content type not be supported)
        if (exception != null) {
            template = new PageTemplate("/velocity/mail_in_html.vm");
            response.addHeader("x-exception", exception.getMessage());

            StringBuilder sb = new StringBuilder("<div class=\"alert alert-danger\" role=\"alert\">");
            sb.append("<strong>Sorry!</strong> We're now can't read this mail, and only support " +
                    "text/plain and text/html and multipart/alternative.");
            sb.append("</div>");

            mail.setContent(sb.toString());
            mail.setContentType(originalContentType);
        }

        template.addParam("mail", mail);
        template.addParam("folder", folder != null ? folder.getSystemFolder() : "");
        template.addParam("mail_content_type", originalContentType);
        template.addParams(extraParams);

        StringWriter writer = new StringWriter();
        template.merge(writer);

        response.addHeader("x-state", "ok");
        response.addHeader("x-Content-Type", originalContentType);
        getOut().print(writer.toString());
    }
    private String cleanMessageID(String messageId) {
        if (!Strings.haveLength(messageId)) {
            return null;
        }
        return messageId.replace("<", "").replace(">", "");
    }

    private File findMailFile(String filename) {
        File ret = null;
        for (String subfix : MX_FILE_SUBFIX) {
            ret = new File(filename + subfix);

            if (Constants.DEBUG_MODE)
                LOGGER.debug("Testing mx mail @" + ret.getAbsolutePath());

            if (ret.exists() && ret.canRead()) {
                break;
            }
        }
        return ret;
    }

    private String getPlainAddresses(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (Address a : addresses) {
            if (idx++ > 0) {
                sb.append(",");
            }
            sb.append(a.getAddress());
        }
        return  sb.toString();
    }


    private String getFromField(Message message) throws MessagingException {
        Address[] a = message.getFrom();
        if (a == null || a.length == 0) {
            return null;
        }
        return Address.toString(a);
    }

    @Deprecated
    private void renderById(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {

        MailService mailService = new MailServiceImpl();
        Mail mail = mailService.findById(Long.parseLong(id));

        if (mail == null) {
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Mail not found");
            return;
        }

        // mark as READ
        if (mail.getFlagUnread() > 0) {
            mail.setFlagUnread(0);
            mail.setFlagNew(0);
            mailService.save(mail);
        }

        PageTemplate template = new PageTemplate("/velocity/mail_in_text.vm");
        template.addParam("mail", mail);

        decodeMail(mail);

        StringWriter writer = new StringWriter();
        template.merge(writer);

        response.addHeader("x-state", "ok");
        getOut().print(writer.toString());
    }

    @Deprecated
    private void decodeMail(Mail mail) {

        String contentType = mail.getContentType();
        String content = mail.getContent();

        // decode multipart/alternative
        if (Strings.haveLength(contentType) && contentType.toLowerCase().startsWith("multipart/alternative")) {
            if (Strings.haveLength(content)) {
            }
        }
    }

}
