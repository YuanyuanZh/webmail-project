package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.impl.HeaderTokenizer;
import cs601.webmail.util.MimeUtils;
import cs601.webmail.util.Strings;

import java.io.UnsupportedEncodingException;


public class Address {

    // The personal name
    protected String personal;

    /**
     * The RFC 2047 encoded version of the personal name.<p>
     */
    protected String encodedPersonal;

    // email address
    protected String address;

    public Address() {
    }

    public String getPersonal() {
        if (personal != null)
            return personal;

        if (encodedPersonal != null) {
            try {
                personal = MimeUtils.decodeText(encodedPersonal);
                return personal;
            } catch (Exception ex) {
                // 1. ParseException: either its an unencoded string or
                //	it can't be parsed
                // 2. UnsupportedEncodingException: can't decode it.
                return encodedPersonal;
            }
        }
        return null;
    }

    public void setPersonal(String name, String charset) throws UnsupportedEncodingException {
        personal = name;

        if (name != null)
            encodedPersonal = MimeUtils.encodeWord(name, charset, null);
        else
            encodedPersonal = null;
    }

    public void setPersonal(String name) throws UnsupportedEncodingException {
        personal = name;

        if (name != null)
            encodedPersonal = MimeUtils.encodeWord(name);
        else
            encodedPersonal = null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        if (!Strings.haveLength(encodedPersonal) && Strings.haveLength(personal)) {
            try {
                encodedPersonal = MimeUtils.encodeWord(personal);
            } catch (UnsupportedEncodingException e) {
            }
        }

        StringBuilder sb = new StringBuilder();

        if (encodedPersonal != null) {
            sb.append(quotePhrase(encodedPersonal)).append(" ");
        }

        sb.append("<").append(address).append(">");
        return sb.toString();
    }


    /**
     * Converter Address array to single head line.
     *
     * @param addresses Address array.
     * @return A comma separated string of addresses.
     */
    public static String toString(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }

        int used = 0;
        StringBuffer sb = new StringBuffer();

        for (int i = 0, len = addresses.length; i < len; i++) {
            if (i > 0) {
                sb.append(", ");
                used += 2;
            }

            String s = addresses[i].toString();
            int l1 = lengthOfFirstSegment(s);

            if (used + l1 > 76) {
                sb.append("\r\n\t"); // new line start with TAB
                used = 8;
            }

            sb.append(s);
            used = lengthOfLastSegment(s, used);
        }

        return sb.toString();
    }

    /* Return the length of the first segment within this string.
     * If no segments exist, the length of the whole line is returned.
     */
    private static int lengthOfFirstSegment(String s) {
        int pos;
        if ((pos = s.indexOf("\r\n")) != -1)
            return pos;
        else
            return s.length();
    }

    /*
     * Return the length of the last segment within this string.
     * If no segments exist, the length of the whole line plus
     * <code>used</code> is returned.
     */
    private static int lengthOfLastSegment(String s, int used) {
        int pos;
        if ((pos = s.lastIndexOf("\r\n")) != -1)
            return s.length() - pos - 2;
        else
            return s.length() + used;
    }

    public String toUnicodeString() {
        String p = getPersonal();

        StringBuilder sb = new StringBuilder();

        if (p != null) {
            sb.append(quotePhrase(p)).append(" ");
        }

        sb.append("<").append(address).append(">");
        return sb.toString();
    }

    public static Address[] parseHeader(String addresses) throws AddressException {
        return AddressParser.parse(addresses, true, true);
    }

    public static Address[] parseAddresses(String rfc822Address) throws ParseException {
        return AddressParser.parse(rfc822Address, true, false);
    }

    // e.g. "Foo Bar" <foo.bar@fb.com>
    public static Address parseAddress(String rfc822Address) throws ParseException {
        if (!Strings.haveLength(rfc822Address)) {
            throw new IllegalArgumentException();
        }

        Address[] aa = AddressParser.parse(rfc822Address, true, false);

        if (aa != null && aa.length > 0) {
            return aa[0];
        }
        return null;
    }


    private static final String rfc822phrase =
            HeaderTokenizer.RFC822.replace(' ', '\0').replace('\t', '\0');

    private static String quotePhrase(String phrase) {
        int len = phrase.length();
        boolean needQuoting = false;

        for (int i = 0; i < len; i++) {
            char c = phrase.charAt(i);
            if (c == '"' || c == '\\') {
                // need to escape them and then quote the whole string
                StringBuffer sb = new StringBuffer(len + 3);
                sb.append('"');
                for (int j = 0; j < len; j++) {
                    char cc = phrase.charAt(j);
                    if (cc == '"' || cc == '\\')
                        // Escape the character
                        sb.append('\\');
                    sb.append(cc);
                }
                sb.append('"');
                return sb.toString();
            } else if ((c < 040 && c != '\r' && c != '\n' && c != '\t') ||
                    c >= 0177 || rfc822phrase.indexOf(c) >= 0)
                // These characters cause the string to be quoted
                needQuoting = true;
        }

        if (needQuoting) {
            StringBuffer sb = new StringBuffer(len + 2);
            sb.append('"').append(phrase).append('"');
            return sb.toString();
        } else
            return phrase;
    }

}
