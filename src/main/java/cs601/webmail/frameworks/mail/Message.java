package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.impl.ContentType;
import cs601.webmail.frameworks.mail.impl.DataCodecImpl;
import cs601.webmail.frameworks.mail.impl.HeaderTokenizer;
import cs601.webmail.frameworks.mail.util.LineInputStream;
import cs601.webmail.frameworks.mail.util.LineOutputStream;
import cs601.webmail.util.DateTimeUtils;
import cs601.webmail.util.Strings;
import cs601.webmail.util.Logger;

import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by yuanyuan on 11/11/14.
 */
public class Message implements MimePart{

    public static class HeaderNames {
        public static final String Sender = "Sender";
        public static final String From = "From";
        public static final String To = "To";
        public static final String Cc = "Cc";
        public static final String Bcc = "Bcc";
        public static final String NewsGroups = "Newsgroups";
        public static final String Date = "Date";
        public static final String Subject = "Subject";
        public static final String ReplyTo = "Reply-To";
        public static final String ContentTransferEncoding = "Content-Transfer-Encoding";
        public static final String ContentType = "Content-Type";
        public static final String MessageID = "Message-ID";
    }

    private static Logger LOGGER = Logger.getLogger(Message.class);

    // number of this message in this folder
    protected int msgnum = 0;

    protected Headers headers;

    protected byte[] content;

    protected InputStream contentStream;

    protected DataCodec dataCodec;

    protected boolean saved = false;

    protected Object cachedContent;

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

    public Message(byte[] content) {
        this.headers = new Headers();
        this.content = content;
    }

    public Message(Headers headers, byte[] content) {
        this.headers = headers;
        this.content = content;
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
        String raw = getHeader(HeaderNames.Subject, null);

        if (raw == null)
            return null;

        try {
            return MimeUtility.decodeText(MimeUtility.unfold(raw));
        } catch (UnsupportedEncodingException e) {
            return raw;
        }
    }

    /**
     * Set subject using default charset.
     * @param subject Subject string (Unicode)
     */
    public void setSubject(String subject) throws MessagingException {
        setSubject(subject, null);
    }

    public void setSubject(String subject, String charset) throws MessagingException {
        if (subject == null) {
            removeHeader(HeaderNames.Subject);
        } else {
            try {
                setHeader(HeaderNames.Subject, MimeUtility.fold(9,
                        MimeUtility.encodeText(subject, charset, null)));
            } catch (UnsupportedEncodingException e) {
                throw new MessagingException("Charset not supported", e);
            }
        }
    }

    // from header "Date"
    public Date getSentDate() throws MessagingException {
        return parseDate(getHeader(HeaderNames.Date, null));
    }

    // from header "Sender", return null if not found.
    public Address getSender() throws MessagingException {
        Address[] a = getAddressHeader(HeaderNames.Sender);

        if (a == null || a.length == 0)
            return null;

        return a[0];
    }

    public void setSender(Address sender) throws MessagingException {
        if (sender == null) {
            removeHeader(HeaderNames.Sender);
        }
        else {
            setHeader(HeaderNames.Sender, sender.toString());
        }
    }

    // from header "Reply-To"
    public String getReplyTo() throws MessagingException {
        return getHeader("Reply-To", null);
    }

    public Address[] getFrom() throws MessagingException {
        Address[] a = getAddressHeader(HeaderNames.From);

        if (a == null) {
            a = getAddressHeader(HeaderNames.Sender);
        }

        return a;
    }

    public void setFrom(String address) throws MessagingException {
        if (address == null) {
            removeHeader(HeaderNames.From);
        } else {
            setAddressHeader(HeaderNames.From, Address.parseAddresses(address));
        }
    }

    /**
     * Replace the existed From field.
     *
     * @param address One or more address string which separated by comma.
     * @throws MessagingException
     */
    public void setFrom(Address address) throws MessagingException {
        if (address == null) {
            removeHeader(HeaderNames.From);
        } else {
            setHeader(HeaderNames.From, address.toString());
        }
    }

    public Address[] getRecipients(RecipientType type) throws MessagingException {
        if (type == RecipientType.NEWS_GROUPS) {
            throw new IllegalStateException("Not supported NEWSGROUPS");
        } else {
            return getAddressHeader(type.toString());
        }
    }

    public void addRecipients(RecipientType type, String addresses) throws MessagingException {
        if (type == RecipientType.NEWS_GROUPS) {
            throw new IllegalStateException("Not supported NEWSGROUPS");
        } else {
            addAddressHeader(type.toString(), Address.parseAddresses(addresses));
        }
    }

    // Add, of course, would be appended to the existed if have.
    public void addRecipients(RecipientType type, Address[] addresses)
            throws MessagingException {
        if (type == RecipientType.NEWS_GROUPS) {
            throw new IllegalStateException("Not supported NEWSGROUPS");
        } else {
            addAddressHeader(type.toString(), addresses);
        }
    }

    public void setRecipients(RecipientType type, String addresses) throws MessagingException {
        if (type == RecipientType.NEWS_GROUPS) {
            throw new IllegalStateException("Not supported NEWSGROUPS");
        } else {
            setAddressHeader(type.toString(), Address.parseAddresses(addresses));
        }
    }

    // Set, will overwrite if type was existed.
    public void setRecipients(RecipientType type, Address[] addresses)
            throws MessagingException {

        if (type == null || addresses == null) {
            throw new IllegalArgumentException();
        }

        if (type == RecipientType.NEWS_GROUPS) {
            throw new IllegalStateException("Not supported NEWSGROUPS");
        } else {
            setAddressHeader(type.toString(), addresses);
        }
    }

    private void setAddressHeader(String type, Address[] addresses) throws MessagingException {
        String s = Address.toString(addresses);
        if (s == null) {
            removeHeader(type);
        } else {
            setHeader(type, s);
        }
    }

    private void addAddressHeader(String name, Address[] addresses) throws MessagingException {
        if ( addresses == null || addresses.length == 0) {
            return;
        }

        Address[] cur = getAddressHeader(name);
        Address[] newadd;

        if (cur == null || cur.length == 0) {
            newadd = addresses;
        }
        else {
            newadd = new Address[addresses.length + cur.length];
            System.arraycopy(cur, 0, newadd, 0, cur.length); // copy current
            System.arraycopy(addresses, 0, newadd, cur.length, addresses.length); // copy new
        }
        String s = Address.toString(newadd);

        if (!Strings.haveLength(s)) {
            return;
        }
        setHeader(name, s);
    }

    // Convenience method to get addresses
    private Address[] getAddressHeader(String name)
            throws MessagingException {
        String s = getHeader(name, ",");
        return (s == null) ? null : Address.parseHeader(s);
    }

    public static enum  RecipientType {
        TO("To"),
        CC("Cc"),
        BCC("Bcc"),
        NEWS_GROUPS("Newsgroups");

        private String t;
        RecipientType(String type) {
            this.t = type;
        }

        @Override
        public String toString() {
            return t;
        }
    }


    //--------------------------------------------------- interface methods


    /**
     * from header
     *
     * ExQQ Mail sample
     * Message-ID: <tencent_08F01BDC1E5A7B5E1B1C602A@qq.com>
     *
     * Gmail smaple
     * Message-ID: <CABKsFNsKWvYEJ2BpUG1gRThPMV2WZvHQqfH3ORTgWCh8E8c0Zg@mail.gmail.com>
     *
     */
    public String getMessageID() throws MessagingException {
        return getHeader(HeaderNames.MessageID, null);
    }

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

    public InputStream getContentStream() throws MessagingException {
        if (contentStream != null) {
            if (contentStream.markSupported()) {
                try {
                    contentStream.reset();
                } catch (IOException e) {
                }
            }
            return contentStream;
        }

        if (content != null)
            return new ByteArrayInputStream(content);

        throw new MessagingException("No Message content");
    }

    @Override
    public void setContent(Object obj, String type) throws MessagingException {
        setDataCodec(new DataCodecImpl(obj, type));
    }

    @Override
    public void setText(String text) throws MessagingException {
        throw new IllegalStateException("not impl yet");
    }

    @Override
    public void writeTo(OutputStream os)
            throws MessagingException, IOException {

        if (!saved) {
            saveChanges();
        }

        Iterator itr = headers.getNonMatchedHeaders(new String[]{HeaderNames.Bcc});
        LineOutputStream los = new LineOutputStream(os);

        while (itr.hasNext()) {
            Object next = itr.next();
            if (next != null) {
                los.writeln(next.toString());
            }
        }

        los.writeln(); // split head and body

        if (content == null) {
            // call getContentStream to give subclass a chance to
            // provide the data on demand
            InputStream is = null;
            byte[] buf = new byte[8192];
            try {
                is = getContentStream();
                // now copy the data to the output stream
                int len;
                while ((len = is.read(buf)) > 0)
                    os.write(buf, 0, len);
            } finally {
                if (is != null)
                    is.close();
                buf = null;
            }
        } else {
            los.write(content);
        }
        los.flush();
    }

    protected void saveChanges() throws MessagingException {
        saved = true;
        updateHeaders();
    }

    protected void updateHeaders() throws MessagingException {
        updateHeaders(this);
        setHeader("MIME-Version", "1.0");

        if (cachedContent != null) {
            cachedContent = null;
            content = null;

            if (contentStream != null) {
                try {
                    contentStream.close();
                } catch (IOException e) {
                }
            }
            contentStream = null;
        }
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

    static void updateHeaders(MimePart part) throws MessagingException {
        // TODO update headers
        //throw new IllegalStateException("Not impl yet");
    }

}
