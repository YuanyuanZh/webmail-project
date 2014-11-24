package cs601.webmail.frameworks.mail.smtp;

import cs601.webmail.frameworks.mail.Headers;
import cs601.webmail.frameworks.mail.Message;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuanyuan on 11/16/14.
 */
public class SMTPMessage extends Message {

    @Deprecated
    private String mailFrom;

    public SMTPMessage(InputStream in) throws IOException {
        super(in);
    }

    public SMTPMessage(int msgnum, InputStream in) throws IOException {
        super(msgnum, in);
    }

    public SMTPMessage(byte[] content) {
        super(content);
    }

    public SMTPMessage(Headers headers, byte[] content) {
        super(headers, content);
    }

    @Deprecated
    public String getMailFrom() {
        return mailFrom;
    }

    @Deprecated
    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

}
