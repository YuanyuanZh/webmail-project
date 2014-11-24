package cs601.webmail.frameworks.mail.pop3;

import cs601.webmail.entity.Mail;
import cs601.webmail.util.MimeUtils;

import java.util.List;

/**
 * Created by yuanyuan on 10/27/14.
 */
public class Pop3Extractor {

    private static final String EMPTY_STRING = "";

    private static final String MESSAGE_ID          = "Message-Id";
    private static final String SUBJECT             = "Subject";
    private static final String FROM                = "From";
    private static final String TO                  = "To";
    private static final String DATE                = "Date";
    private static final String RECEIVED            = "Received";
    private static final String CONTENT_TYPE        = "Content-Type";
    private static final String MIME_VERSION        = "MIME-Version";


    public Mail extractMail(Pop3Message message) {

        if (message == null) {
            return null;
        }

        Mail mail = new Mail();

        mail.setDate(getHeader(message, DATE));
        mail.setFrom(getFromTo(getHeader(message, FROM)));
        mail.setTo(getFromTo(getHeader(message, TO)));
        mail.setSubject(MimeUtils.decodeText(getHeader(message, SUBJECT)));
        mail.setMessageId(getHeader(message, MESSAGE_ID));
        mail.setContentType(getHeader(message, CONTENT_TYPE));

        mail.setContent(message.getBody());

        return mail;
    }

    private String getFromTo(String from) {

        if (from == null || from.length() == 0)
            return from;

        String[] ss = from.split(" ");

        if (ss.length == 1)
            return from;

        String sender = ss[0];
        boolean hasQuote = ss[0].startsWith("\"");

        if (hasQuote) {
            sender = "\"" + MimeUtils.decodeText(sender.replace("\"", "")) + "\"";
        } else {
            sender = MimeUtils.decodeText(sender);
        }

        return sender + " " + ss[1];
    }


    private String getHeader(Pop3Message message, String header) {
        List<String> headers = message.getHeaders().get(header);
        return headers != null && headers.size() > 0 ? headers.get(0) : EMPTY_STRING;
    }

}
