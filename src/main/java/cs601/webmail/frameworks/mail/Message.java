package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.impl.ContentType;
import cs601.webmail.frameworks.mail.impl.DataCodecImpl;
import cs601.webmail.frameworks.mail.impl.HeaderTokenizer;
import cs601.webmail.frameworks.mail.util.LineInputStream;
import cs601.webmail.util.DateTimeUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by yuanyuan on 11/13/14.
 */
public class Message implements MimePart{

    private static Logger LOGGER = Logger.getLogger(Message.class);

    // number of this message in this folder
    protected int msgnum = 0;

    protected Headers headers;

    protected byte[] content;

    protected DataCodec dataCodec;

    public Message(InputStream in) throws IOException {
        headers = new Headers();
        dataCodec = new DataCodecImpl();
        parse(in);
    }

    public Message(int msgnum, InputStream in) throws IOException {
        this.msgnum = msgnum;

        headers = new Headers();
        dataCodec = new DataCodecImpl();
        parse(in);
    }

    private void parse(InputStream in) throws IOException {
        LineInputStream reader = new LineInputStream(in);

        String line;
        String headerName = null;
        String headerValue;

        // process headers
        while ((line = reader.readLine())!= null && line.length() > 0) {

            if (line.startsWith(" ") || line.startsWith("\t")) {
                headers.appendToLastHeader(line);
                continue; //no process of multiline headers
            }

            int colonPosition = line.indexOf(":");

            // no colon
            if (colonPosition == -1) {
                LOGGER.debug("ignore header line: " + line);
                continue;
            }

            headerName = line.substring(0, colonPosition);

            if (line.length() > colonPosition + 2) {
                headerValue = line.substring(colonPosition + 2);
            } else {
                headerValue = "";
            }

            headers.addHeader(headerName, headerValue);
        }

        // process body
        StringBuilder bodyBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null && !line.equals(".")) {
            bodyBuilder.append(line + "\n");
        }

        // get body
        content = bodyBuilder.toString().getBytes();
    }

    public int getMessageNum() {
        return msgnum;
    }

    public DataCodec getDataCodec() {
        return dataCodec;
    }

    public void setDataCodec(DataCodec dataCodec) {
        this.dataCodec = dataCodec;
    }

    // from header "Subject"
    public String getSubject() throws MessagingException {
        return (getHeader("Subject", null));
    }

    // from header "Date"
    public Date getSentDate() throws MessagingException {
        return parseDate(getHeader("Date", null));
    }

    // from header "From"
    public String[] getFrom() throws MessagingException {
        return getHeader("From");
    }

    // from header "Sender", return null if not found.
    public String getSender() throws MessagingException {
        return getHeader("Sender", null);
    }

    // from header "Reply-To"
    public String getReplyTo() throws MessagingException {
        return getHeader("Reply-To", null);
    }


    //--------------------------------------------------- interface methods

    @Override
    public String getContentType() throws MessagingException {
        String s = getHeader("Content-Type", null);
        if (s == null) {
            return "text/plain";
        }
        return s;
    }

    @Override
    public boolean isMimeType(String mimeType) throws MessagingException {
        return isMimeType(this, mimeType);
    }

    @Override
    public Object getContent() throws MessagingException {
        return getDataCodec().decode(this);
    }

    @Override
    public String getEncoding() throws MessagingException {
        return getEncoding(this);
    }

    @Override
    public InputStream getInputStream() throws MessagingException {
        if (content != null) {
            return new ByteArrayInputStream(content);
        }
        throw new MessagingException("Message have not content found!");
    }

    @Override
    public void setContent(Object obj, String type) throws MessagingException {
        throw new IllegalStateException("not impl yet");
    }

    @Override
    public void setText(String text) throws MessagingException {
        throw new IllegalStateException("not impl yet");
    }

    public String getHeader(String name, String delimiter) throws MessagingException {
        return headers.getHeader(name, delimiter);
    }

    @Override
    public String[] getHeader(String name) throws MessagingException {
        return headers.getHeader(name);
    }

    @Override
    public void setHeader(String name, String value) throws MessagingException {
        headers.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) throws MessagingException {
        headers.addHeader(name, value);
    }

    @Override
    public void removeHeader(String name) throws MessagingException {
        headers.removeHeader(name);
    }


    // extract encoding from MIME header
    static String getEncoding(MimePart part) throws MessagingException {
        String s = part.getHeader("Content-Transfer-Encoding", null);

        if (s == null)
            return null;

        s = s.trim();	// get rid of trailing spaces
        // quick check for known values to avoid unnecessary use
        // of tokenizer.
        if (s.equalsIgnoreCase("7bit") || s.equalsIgnoreCase("8bit") ||
                s.equalsIgnoreCase("quoted-printable") ||
                s.equalsIgnoreCase("binary") ||
                s.equalsIgnoreCase("base64"))
            return s;


        // Tokenize the header to obtain the encoding (skip comments)
        HeaderTokenizer h = new HeaderTokenizer(s, HeaderTokenizer.MIME);

        HeaderTokenizer.Token tk;
        int tkType;

        for (;;) {
            tk = h.next(); // get a token
            tkType = tk.getType();
            if (tkType == HeaderTokenizer.Token.EOF)
                break; // done
            else if (tkType == HeaderTokenizer.Token.ATOM)
                return tk.getValue();
            else // invalid token, skip it.
                continue;
        }
        return s;
    }

    /**
     * Accept time from as follow:
     *
     * <ul>
     *     <li>Mon, 6 Oct 2014 17:10:40 +0800 (CST)</li>
     *     <li>Wed, 10 Aug 2011 01:50:01 +0000</li>
     *     <li>18 Feb 2009 15:48:10 +0800</li>
     * </ul>
     *
     * @param date Date string from mail header.
     * @return Date object.
     */
    static Date parseDate(String date) {
        if (null == date || date.length() == 0)
            return null;

        try {
            return DateTimeUtils.parseDate(date);
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    static boolean isMimeType(MimePart part, String mimeType)
            throws MessagingException {
        // XXX - lots of room for optimization here!
        try {
            ContentType ct = new ContentType(part.getContentType());
            return ct.match(mimeType);
        } catch (ParseException ex) {
            return part.getContentType().equalsIgnoreCase(mimeType);
        }
    }

}

