package cs601.webmail.frameworks.mail.smtp;

import cs601.webmail.frameworks.mail.Headers;
import cs601.webmail.frameworks.mail.Message;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuanyuan on 11/20/14.
 */
public class SMTPMessage extends Message {

    private String rcptTo;

    private String mailFrom;



    public SMTPMessage(InputStream in) throws IOException {
        super(in);
    }

    public SMTPMessage(int msgnum, InputStream in) throws IOException {
        super(msgnum, in);
    }

    public SMTPMessage(Headers headers, byte[] content) {
        super(headers, content);
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getRcptTo() {
        return rcptTo;
    }

    public void setRcptTo(String rcptTo) {
        this.rcptTo = rcptTo;
    }
}

