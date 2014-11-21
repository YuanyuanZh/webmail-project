package cs601.webmail.pages.mail;

import cs601.webmail.auth.AuthenticationCheckFilter;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.entity.User;
import cs601.webmail.frameworks.mail.Message;
import cs601.webmail.frameworks.mail.MessagingException;
import cs601.webmail.frameworks.mail.MimeContent;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.Page;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.AccountServiceImpl;
import cs601.webmail.service.impl.MailServiceImpl;
import cs601.webmail.util.DigestUtils;
import cs601.webmail.util.MimeUtils;
import cs601.webmail.util.ResourceUtils;
import cs601.webmail.util.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by yuanyuan on 11/8/14.
 */
public class ReadMailPage extends Page {

    public void body()throws Exception{
        HttpServletRequest request= RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response=RequestContext.getCurrentInstance().getResponse();

        String id=request.getParameter("id");


        if(Strings.haveLength(id)){
            renderByRawFile(request,response,id);
        }else {
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Invalid mail id");
            return;
        }
    }

    private void renderByRawFile(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {
        AccountService accountService = new AccountServiceImpl();
        MailService mailService = new MailServiceImpl();

        Mail mail = mailService.findById(Long.parseLong(id));

        if (mail == null) {
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

        HttpSession session = request.getSession();

        User user=(User)session.getAttribute(AuthenticationCheckFilter.LOGIN_SESSION_FLAG);

        Account currentAccount = accountService.findById(user.getId());

        String uid = mail.getUid();

        String rawFilePath = ResourceUtils.getRawMailStorePath(currentAccount.getEmailUsername());
        File rawFile = new File(rawFilePath + File.separator + DigestUtils.digestToSHA(uid) + ".txt");

        // raw file can neither be found nor be readable
        if (!rawFile.exists() || !rawFile.canRead()) {
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Mail raw file not found");
            return;
        }

        Message message = null;
        Exception exception = null;
        String originalContentType = null;
        Object msgContent = null;

        try {
            message = new Message(new FileInputStream(rawFile));

            originalContentType = message.getContentType();

            mail.setSubject(MimeUtils.decodeText(message.getSubject()));
            mail.setFrom(Strings.join(message.getFrom(), ", "));
            mail.setUid(uid);
            mail.setDate(message.getSentDate() != null ? message.getSentDate().toString() : null);

            // try to get content
            msgContent = message.getContent();
        } catch (IOException e) {
            exception = e;
        } catch (MessagingException e) {
            exception = e;
        }

        response.setContentType("text/html; charset=utf-8");

        // default template is text
        PageTemplate template = new PageTemplate("/velocity/mail_in_text.vm");

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

        StringWriter writer = new StringWriter();
        template.merge(writer);

        response.addHeader("x-state", "ok");
        response.addHeader("x-Content-Type", originalContentType);
        getOut().print(writer.toString());
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
