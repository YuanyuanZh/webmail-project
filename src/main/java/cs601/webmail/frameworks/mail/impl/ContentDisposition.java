package cs601.webmail.frameworks.mail.impl;

import cs601.webmail.frameworks.mail.ParseException;
import cs601.webmail.util.MimeUtils;
import cs601.webmail.util.Strings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuanyuan on 11/8/14.
 */
public class ContentDisposition {

    // SAMPLE
    //Content-Disposition: attachment; filename="20130321_165734_388.jpg"

    private String disposition;
    private Map<String, String> parameters;

    public ContentDisposition(String content) throws ParseException {

        if (!Strings.haveLength(content)) {
            throw new IllegalArgumentException();
        }

        HeaderTokenizer h = new HeaderTokenizer(content, HeaderTokenizer.MIME);
        HeaderTokenizer.Token tk;

        // First "disposition" ..
        tk = h.next();
        if (tk.getType() != HeaderTokenizer.Token.ATOM)
            throw new ParseException("Expected disposition, got " + tk.getValue());
        disposition = tk.getValue();

        // Then parameters ..
        String rem = h.getRemainder();

        if (Strings.haveLength(rem)) {
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

    public String getDisposition() {
        return disposition;
    }

    public String getParameter(String param) {
        return parameters != null ? parameters.get(param) : null;
    }

    public void setParameter(String key, String val) {
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        }
        this.parameters.put(key, val);
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    @Override
    public String toString() {
        if (disposition == null)
            return "";

        if (parameters == null || parameters.isEmpty())
            return disposition;

        if (parameters == null)
            return disposition;

        StringBuffer sb = new StringBuffer(disposition);

        ParamsBuf buf = new ParamsBuf(sb.length() + 21); // length of "Content-Disposition: " to preserver

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
