package cs601.webmail.frameworks.mail.smtp;

import cs601.webmail.frameworks.mail.Address;
import cs601.webmail.frameworks.mail.Message;
import cs601.webmail.frameworks.mail.MessagingException;
import cs601.webmail.frameworks.mail.SocketClient;
import cs601.webmail.frameworks.mail.util.LineInputStream;
import cs601.webmail.util.Strings;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by yuanyuan on 11/16/14.
 */
public class SMTPClient extends SocketClient{

    static final String newLine = "\r\n";

    private static final String SUCCESS = "250";

    public SMTPClient(boolean sslEnabled) {
        super(sslEnabled);
    }

    @Override
    public void login(String username, String password) throws IOException {

        String domain = username;

        if (username.indexOf("@") > -1) {
            username = username.substring(0, username.indexOf("@"));
            domain = domain.substring(domain.indexOf("@") + 1);
        }

        String line = sendCommand("HELO " + domain);

        if (!Strings.haveLength(line) || !line.startsWith(SUCCESS)) {
            throw new IOException("HELO failed");
        }

        do {
            if (line.startsWith("250 ")) {
                // get response end
                break;
            }
        } while ((line = readResponseLine()) != "" && line.startsWith(SUCCESS));

        if (isDebug())
            System.out.println("[DEBUG] starting login...");

        line = sendCommand("AUTH LOGIN");

        if (!line.startsWith("334 ")) {
            throw new IOException("can't continue to login. echo back:" + line);
        }

        // send base64 encoded username
        line = sendCommand(Base64.encodeBase64String(username.getBytes()));

        if (!line.startsWith("334 ")) {
            throw new IOException("can't continue to login (failed to send username). echo back:" + line);
        }

        // send base64 encoded password
        line = sendCommand(Base64.encodeBase64String(password.getBytes()));

        if (!line.startsWith("235 ")) {
            throw new IOException("can't continue to login (failed to send password). echo back:" + line);
        }

        // if reached here, that means authentication was passed. Go ahead to send mail.
    }

    @Override
    public void logout() throws IOException {
        sendCommand("QUIT");
    }

    public void send(String mailFrom, String rcptTo, Iterator<String> headers, Iterator<String> msgContent) throws IOException {

        String respLine = sendCommand("MAIL From:<" + mailFrom + ">");

        if (!respLine.startsWith(SUCCESS)) {
            throw new IOException("Can't send mail. echo back:" + respLine);
        }

        respLine = sendCommand("RCPT To:<" + rcptTo + ">");

        if (!respLine.startsWith(SUCCESS)) {
            throw new IOException("Can't send mail. echo back:" + respLine);
        }

        respLine = sendCommand("DATA");

        if (!respLine.startsWith("354 ")) {
            throw new IOException("Can't send mail. echo back:" + respLine);
        }

        // start to sending

        while (headers.hasNext()) {
            writer.write(headers.next());
            writer.write(newLine);
        }

        // message headers end
        // message body begin
        writer.write(newLine);

        while (msgContent.hasNext()) {
            writer.write(msgContent.next());
            writer.write(newLine);
        }

        writer.write(newLine);
        writer.write(newLine);

        // finish content sending with dot
        respLine = sendCommand(".");

        if (!respLine.startsWith(SUCCESS)) {
            throw new IOException("Mail send failed. echo back:" + respLine);
        }

    }

    // See RFC 821
    // http://tools.ietf.org/html/rfc821
    public void send(Message message) throws IOException, MessagingException {

        if (!(message instanceof SMTPMessage)) {
            throw new IllegalArgumentException("Not a SMTP message");
        }

        Address[] addresses;
        SMTPMessage smtpMessage = (SMTPMessage) message;

        addresses = smtpMessage.getFrom();

        if (addresses == null || addresses.length == 0) {
            throw new MessagingException("No From found.");
        }

        String respLine = sendCommand("MAIL From:<" + addresses[0].getAddress() + ">");

        if (!respLine.startsWith(SUCCESS)) {
            throw new IOException("Can't send mail. echo back:" + respLine);
        }

//        // we could have multi receivers
//        String rcptTo = smtpMessage.getRcptTo();
//        String[] rcpts = rcptTo.indexOf(",") > -1 ?
//                rcptTo.split(",") :
//                new String[] {rcptTo};
//
//        for (String rcpt : rcpts) {
//            respLine = sendCommand("RCPT To:<" + Strings.trim(rcpt) + ">");
//
//            if (!respLine.startsWith(SUCCESS)) {
//                throw new IOException("Can't send mail. echo back:" + respLine);
//            }
//        }



        // deal with <code>RCPT To</code>

        addresses = smtpMessage.getRecipients(Message.RecipientType.TO);

        if (addresses == null || addresses.length == 0) {
            throw new MessagingException("No TO recipient found.");
        }

        for (Address address : addresses) {
            respLine = sendCommand("RCPT To:<" + address.getAddress() + ">");

            if (!respLine.startsWith(SUCCESS)) {
                throw new IOException("Can't send mail. echo back:" + respLine);
            }
        }

        // deal with <code>RCPT Cc</code>
        addresses = smtpMessage.getRecipients(Message.RecipientType.CC);

        if (addresses != null && addresses.length > 0) {
            for (Address address : addresses) {
                respLine = sendCommand("RCPT To:<" + address.getAddress() + ">");

                if (!respLine.startsWith(SUCCESS)) {
                    throw new IOException("Can't send mail. echo back:" + respLine);
                }
            }
        }

        // deal with <code>RCPT Bcc</code>
        addresses = smtpMessage.getRecipients(Message.RecipientType.BCC);

        if (addresses != null && addresses.length > 0) {
            for (Address address : addresses) {
                respLine = sendCommand("RCPT To:<" + address.getAddress() + ">");

                if (!respLine.startsWith(SUCCESS)) {
                    throw new IOException("Can't send mail. echo back:" + respLine);
                }
            }
        }


        respLine = sendCommand("DATA");

        if (!respLine.startsWith("354 ")) {
            throw new IOException("Can't send mail. echo back:" + respLine);
        }

        // available to send data
        ByteArrayOutputStream encodedMail = new ByteArrayOutputStream();
        message.writeTo(encodedMail);

        LineInputStream lin = new LineInputStream(new ByteArrayInputStream(encodedMail.toByteArray()));

        String cline = null;
        while ((cline = lin.readLine()) != null) {
            writer.write(cline);
            writer.write(newLine);
        }

        writer.write(newLine);
        writer.write(newLine);

        // finish content sending with dot
        respLine = sendCommand(".");

        if (!respLine.startsWith(SUCCESS)) {
            throw new IOException("Mail send failed. echo back:" + respLine);
        }

    }


}
