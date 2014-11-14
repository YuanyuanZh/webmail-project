package cs601.webmail.frameworks.mail;

import cs601.webmail.util.Strings;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import cs601.webmail.frameworks.mail.util.LineInputStream;

/**
 * Created by yuanyuan on 11/13/14.
 */
public class Headers {

    private static final Logger LOGGER = Logger.getLogger(Headers.class);

    private List headers;

    private InnerHeader lastInsertHeader = null;

    public Headers() {
        headers = new InnerList(this);
        headers.add(new InnerHeader("Return-Path", null));
        headers.add(new InnerHeader("Received", null));
        headers.add(new InnerHeader("Resent-Date", null));
        headers.add(new InnerHeader("Resent-From", null));
        headers.add(new InnerHeader("Resent-Sender", null));
        headers.add(new InnerHeader("Resent-To", null));
        headers.add(new InnerHeader("Resent-Cc", null));
        headers.add(new InnerHeader("Resent-Bcc", null));
        headers.add(new InnerHeader("Resent-Message-Id", null));
        headers.add(new InnerHeader("Date", null));
        headers.add(new InnerHeader("From", null));
        headers.add(new InnerHeader("Sender", null));
        headers.add(new InnerHeader("Reply-To", null));
        headers.add(new InnerHeader("To", null));
        headers.add(new InnerHeader("Cc", null));
        headers.add(new InnerHeader("Bcc", null));
        headers.add(new InnerHeader("Message-Id", null));
        headers.add(new InnerHeader("In-Reply-To", null));
        headers.add(new InnerHeader("References", null));
        headers.add(new InnerHeader("Subject", null));
        headers.add(new InnerHeader("Comments", null));
        headers.add(new InnerHeader("Keywords", null));
        headers.add(new InnerHeader("Errors-To", null));
        headers.add(new InnerHeader("MIME-Version", null));
        headers.add(new InnerHeader("Content-Type", null));
        headers.add(new InnerHeader("Content-Transfer-Encoding", null));
        headers.add(new InnerHeader("Content-MD5", null));
        headers.add(new InnerHeader(":", null));
        headers.add(new InnerHeader("Content-Length", null));
        headers.add(new InnerHeader("Status", null));
    }

    public Headers(InputStream is) throws MessagingException {
        headers = new InnerList(40, this);
        readHeaders(this, is);
    }

    public void appendToLastHeader(String line) {
        if (lastInsertHeader != null) {
            lastInsertHeader.line = lastInsertHeader.line + line;
        }
    }

    static class InnerList extends ArrayList {

        Headers headers;

        InnerList(int initialCapacity, Headers headers) {
            super(initialCapacity);
            this.headers = headers;
        }

        InnerList(Headers headers) {
            this.headers = headers;
        }

        @Override
        public void add(int index, Object element) {
            headers.lastInsertHeader = (InnerHeader) element;
            super.add(index, element);
        }

        @Override
        public boolean add(Object o) {
            headers.lastInsertHeader = (InnerHeader) o;
            return super.add(o);
        }
    }

    // for placeholder only
    protected final class InnerHeader extends Header {

        protected String line;

        public InnerHeader(String line) {
            super("", "");

            int i = line.indexOf(":");
            if (i < 0) { // not found
                name = line.trim();
            } else {
                name = line.substring(0, i).trim();
            }

            this.line = line;
        }

        public InnerHeader(String attribute, String value) {
            super(attribute, "");
            if (value != null) {
                line = attribute + ": " + value;
            } else {
                line = null;
            }
        }

        public String getValue() {
            if (!Strings.haveLength(line)) {
                return line;
            }

            int i = line.indexOf(":");

            if (i < 0)
                return line;

            return line.substring(i + 2);
        }
    }

    /**
     * Get all the headers for this head name, returned as an array.
     * @param name header name
     * @return
     */
    public String[] getHeader(String name) {
        Iterator it = headers.iterator();
        List v = new ArrayList();

        while (it.hasNext()) {
            InnerHeader h = (InnerHeader) it.next();
            if (name.equalsIgnoreCase(h.getName()) && h.line != null) {
                v.add(h.getValue());
            }
        }

        if (v.size() == 0)
            return null;

        String[] r = new String[v.size()];
        r = (String[]) v.toArray(r);
        return r;
    }

    /**
     * Get all the headers for this head name, returned as a single string which joined by
     * delimiter. Return the first header if no delimiter provided or only one header
     * existed. Return <code>null</code> if no headers here for this name.
     * @param name the name of header
     * @param delimiter delimiter
     * @return the values for all headers with this name, or null if none.
     */
    public String getHeader(String name, String delimiter) {
        String[] s = getHeader(name);

        if (s == null)
            return null;

        if (s.length == 1 || delimiter == null)
            return s[0];

        return Strings.join(s, delimiter);
    }


    /**
     * replace the existed value of have with the value. will remove all headers for this name
     * but first.
     * @param name header name
     * @param value header value
     */
    public void setHeader(String name, String value) {
        boolean found = false;

        for (int i = 0, len = headers.size(); i < len; i++) {
            InnerHeader h = (InnerHeader) headers.get(i);

            // matched
            if (name.equalsIgnoreCase(h.getName())) {

                if (!found) {

                    int colonPos;

                    // have colon
                    if (h.line != null && (colonPos = h.line.indexOf(":")) > -1) {
                        // append value to existed
                        h.line = h.line.substring(0, colonPos + 1) + " " + value;
                    }
                    // have no colon
                    else {
                        h.line = name + ": " + value;
                    }
                    found = true;
                }
                else {
                    headers.remove(i);
                    i--;
                    len--;
                }
            }
        }

        if (!found) {
            addHeader(name, value);
        }
    }

    // add, append or prepend
    public void addHeader(String name, String value) {
        int pos = headers.size();
        boolean addReverse =
                name.equalsIgnoreCase("Received") ||
                        name.equalsIgnoreCase("Return-Path");

        if (addReverse)
            pos = 0;

        for (int i = headers.size() - 1; i >= 0; i--) {

            InnerHeader h = (InnerHeader) headers.get(i);

            // matched
            if (name.equals(h.getName())) {

                if (addReverse) { // top of header list
                    pos = i;
                }
                else {
                    headers.add(i + 1, new InnerHeader(name, value));
                    return;
                }
            }

            if (!addReverse && h.getName().equals(":")) {
                pos = i;
            }
        }

        headers.add(pos, new InnerHeader(name, value));
    }

    /**
     * Add an RFC822 header line to the header store.
     * If the line starts with a space or tab (a continuation line),
     * add it to the last header line in the list.  Otherwise,
     * append the new header line to the list.  <p>
     *
     * Note that RFC822 headers can only contain US-ASCII characters
     *
     * @param	line	raw RFC822 header line
     */
    public void addHeaderLine(String line) {
        try {
            char c = line.charAt(0);
            if (c == ' ' || c == '\t') {
                InnerHeader h = (InnerHeader) headers.get(headers.size() - 1);
                h.line += "\r\n" + line;
            } else
                headers.add(new InnerHeader(line));
        } catch (StringIndexOutOfBoundsException e) {
            // line is empty, ignore it
            return;
        } catch (NoSuchElementException e) {
            // XXX - list is empty?
        }
    }

    // remove all headers for this name
    public void removeHeader(String name) throws MessagingException{
        for (int i = headers.size() - 1; i >= 0; i--) {

            InnerHeader h = (InnerHeader) headers.get(i);

            // matched
            if (name.equals(h.getName())) {
                h.line = null; // just set to null, don't need to really remove.
            }
        }
    }

    /**
     * Return all the header lines as an iterator of Strings.
     */
    public Iterator getAllHeaders() { // TODO impl it
        throw new IllegalStateException("NOT IMPL");
    }


    static void readHeaders(Headers headers, InputStream is) throws MessagingException {
        LineInputStream reader = new LineInputStream(is);

        String line;
        String headerName = null;
        String headerValue;

        try {
            // process headers
            while ((line = reader.readLine()) != null && line.length() != 0) {

                if (line.startsWith(" ") || line.startsWith("\t")) {
                    headers.appendToLastHeader(line);
                    continue;
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

        } catch (IOException e) {
            throw new MessagingException("Error to parse headers", e);
        }
    }

}

