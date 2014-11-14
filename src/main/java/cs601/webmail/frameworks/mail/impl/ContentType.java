package cs601.webmail.frameworks.mail.impl;

import cs601.webmail.frameworks.mail.ParseException;
import cs601.webmail.util.MimeUtils;
import cs601.webmail.util.Strings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuanyuan on 11/12/14.
 */
public class ContentType {

    private String primaryType;

    private String subType;

    private Map<String, String> parameters;

    public ContentType(String primaryType, String subType, Map<String, String> parameters) {
        this.primaryType = primaryType;
        this.subType = subType;
        this.parameters = parameters;
    }

    /**
     * Type must be in format like "primary/sub"
     *
     * @param type type: sample multipart/*
     * @throws cs601.webmail.frameworks.mail.ParseException
     */
    public ContentType(String type) throws ParseException {

        if (!Strings.haveLength(type)) {
            throw new IllegalArgumentException();
        }

        HeaderTokenizer h = new HeaderTokenizer(type, HeaderTokenizer.MIME);
        HeaderTokenizer.Token tk;


        // First "type" ..
        tk = h.next();
        if (tk.getType() != HeaderTokenizer.Token.ATOM)
            throw new ParseException("Expected MIME type, got " +
                    tk.getValue());
        primaryType = tk.getValue();

        // The '/' separator ..
        tk = h.next();
        if ((char)tk.getType() != '/')
            throw new ParseException("Expected '/', got " + tk.getValue());

        // Then "subType" ..
        tk = h.next();
        if (tk.getType() != HeaderTokenizer.Token.ATOM)
            throw new ParseException("Expected MIME subtype, got " +
                    tk.getValue());
        subType = tk.getValue();

        // Finally parameters ..
        String rem = h.getRemainder();
        if (rem != null) {
            createParams(rem);
        }
    }

    private void createParams(String rem) throws ParseException {
        this.parameters = new HashMap<String, String>();

        HeaderTokenizer h = new HeaderTokenizer(rem, HeaderTokenizer.MIME);
        HeaderTokenizer.Token tk = null;

        String pk = null;
        String pv = null;

        do {
            tk = h.next();

            if (tk.getType() == HeaderTokenizer.Token.EOF)
                break;

            // normal char
            if (tk.getType() != HeaderTokenizer.Token.ATOM) {
                if (tk.getValue().equals(";"))
                    continue;
            }

            if (tk.getType() == HeaderTokenizer.Token.ATOM && pk == null) {
                pk = tk.getValue();
                continue;
            }

            if ((tk.getType() == HeaderTokenizer.Token.ATOM ||
                    tk.getType() == HeaderTokenizer.Token.QUOTEDSTRING) && pk != null) {
                pv = tk.getValue();

                parameters.put(pk, pv);
                pk = null;
                pv = null;
                continue;
            }

        } while (tk.getType() != HeaderTokenizer.Token.EOF);
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getSubType() {
        return subType;
    }

    public String getBaseType() {
        return primaryType + "/" + subType;
    }

    public boolean match(ContentType cType) {
        if (cType == null)
            return false;

        if (!cType.primaryType.equalsIgnoreCase(this.primaryType))
            return false;

        String sType = cType.getSubType();

        if (subType.charAt(0) == '*' || sType.charAt(0) == '*')
            return true;

        if (!subType.equalsIgnoreCase(sType))
            return false;

        return true;
    }

    // type:  sample  text/html  or  application/json
    public boolean match(String type) {
        try {
            return match(new ContentType(type));
        } catch (ParseException e) {
            return false;
        }
    }

    public String getParameter(String name) {
        return parameters != null ? parameters.get(name) : "";
    }

    public void setParameter(String key, String val) {
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        }
        parameters.put(key, val);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getBaseType());

        if (parameters == null) {
            return sb.toString();
        }

        // length of "Content-Type: " to preserver
        ParamsBuf buf = new ParamsBuf(sb.length() + 14);

        Iterator it = parameters.keySet().iterator();
        while (it.hasNext()) {
            String k = (String) it.next();
            buf.addNV(k, (parameters.get(k)));
        }

        sb.append(buf.toString());

        return sb.toString();
    }

    private String quote(String s) {
        return MimeUtils.quote(s, HeaderTokenizer.MIME);
    }

    // 76 characters at max for each line
    static class ParamsBuf {

        ParamsBuf(int used) {
            this.used = used;
        }

        private int used;	// keep track of how much used on current line
        private StringBuffer sb = new StringBuffer();

        // Quote a parameter value token if required.
        private static String quote(String value) {
            return MimeUtils.quote(value, HeaderTokenizer.MIME);
        }

        public void addNV(String name, String value) {
            value = quote(value);
            sb.append("; ");
            used += 2;
            int len = name.length() + value.length() + 1;
            if (used + len > 76) { // overflows ...
                sb.append("\r\n\t"); // .. start new continuation line
                used = 8; // account for the starting <tab> char
            }
            sb.append(name).append('=');
            used += name.length() + 1;
            if (used + value.length() > 76) { // still overflows ...
                // have to fold value
                String s = MimeUtils.fold(used, value);
                sb.append(s);
                int lastlf = s.lastIndexOf('\n');
                if (lastlf >= 0)	// always true
                    used += s.length() - lastlf - 1;
                else
                    used += s.length();
            } else {
                sb.append(value);
                used += value.length();
            }
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

}

