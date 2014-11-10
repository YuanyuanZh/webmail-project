package cs601.webmail.page;

import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.MailServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;

/**
 * Created by yuanyuan on 11/8/14.
 */
public class ReadMailPage extends Page{
    public void body()throws Exception{
        HttpServletRequest request= RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response=RequestContext.getCurrentInstance().getResponse();

        String id=request.getParameter("id");
        response.setContentType("text/html;charset=utf-8");

        if(id==null||id.length()==0){
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Invalid mail id");
            return;
        }

        MailService mailService=new MailServiceImpl();
        Mail mail=mailService.findById(Long.parseLong(id));

        if(mail==null){
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Mail not found");
            return;
        }

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

    }
}
