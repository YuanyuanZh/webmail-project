package cs601.webmail.util;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Created by yuanyuan on 11/12/14.
 */
public class MimeUtils {

    // TODO finish this method by your self
    public static String decodeText(String string) {
        try {
            return MimeUtility.decodeText(string);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    // TODO finish this
    public static InputStream decode(InputStream is, String encoding) {
        try {
            return MimeUtility.decode(is, encoding);
        } catch (MessagingException e) {
            throw new IllegalStateException(e);
        }
    }

    // Encode a RFC 822 word token into mail-safe form as per
    // RFC 2047<p>
    public static String encodeWord(String word, String charset, String encoding) throws UnsupportedEncodingException {
        return MimeUtility.encodeWord(word, charset, encoding);
    }

    public static String encodeWord(String word) throws UnsupportedEncodingException {
        return MimeUtility.encodeWord(word, null, null);
    }

    // add quote if needed
    public static String quote(String word, String specials) {
        int len = word.length();
        if (len == 0)
            return "\"\"";	// an empty string is handled specially

	/*
	 * Look for any "bad" characters, Escape and
	 *  quote the entire string if necessary.
	 */
        boolean needQuoting = false;
        for (int i = 0; i < len; i++) {
            char c = word.charAt(i);
            if (c == '"' || c == '\\' || c == '\r' || c == '\n') {
                // need to escape them and then quote the whole string
                StringBuffer sb = new StringBuffer(len + 3);
                sb.append('"');
                sb.append(word.substring(0, i));
                int lastc = 0;
                for (int j = i; j < len; j++) {
                    char cc = word.charAt(j);
                    if ((cc == '"') || (cc == '\\') ||
                            (cc == '\r') || (cc == '\n'))
                        if (cc == '\n' && lastc == '\r')
                            ;	// do nothing, CR was already escaped
                        else
                            sb.append('\\');	// Escape the character
                    sb.append(cc);
                    lastc = cc;
                }
                sb.append('"');
                return sb.toString();
            } else if (c < 040 || c >= 0177 || specials.indexOf(c) >= 0)
                // These characters cause the string to be quoted
                needQuoting = true;
        }

        if (needQuoting) {
            StringBuffer sb = new StringBuffer(len + 2);
            sb.append('"').append(word).append('"');
            return sb.toString();
        } else
            return word;
    }

    private static final boolean foldText = true;

    /**
     * Fold a string at linear whitespace so that each line is no longer
     * than 76 characters, if possible.  If there are more than 76
     * non-whitespace characters consecutively, the string is folded at
     * the first whitespace after that sequence.  The parameter
     * <code>used</code> indicates how many characters have been used in
     * the current line; it is usually the length of the header name. <p>
     *
     * Note that line breaks in the string aren't escaped; they probably
     * should be.
     *
     * @param	used	characters used in line so far
     * @param	s	the string to fold
     * @return		the folded string
     * @since		JavaMail 1.4
     */
    public static String fold(int used, String s) {
        if (!foldText)
            return s;

        int end;
        char c;
        // Strip trailing spaces and newlines
        for (end = s.length() - 1; end >= 0; end--) {
            c = s.charAt(end);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n')
                break;
        }
        if (end != s.length() - 1)
            s = s.substring(0, end + 1);

        // if the string fits now, just return it
        if (used + s.length() <= 76)
            return s;

        // have to actually fold the string
        StringBuffer sb = new StringBuffer(s.length() + 4);
        char lastc = 0;
        while (used + s.length() > 76) {
            int lastspace = -1;
            for (int i = 0; i < s.length(); i++) {
                if (lastspace != -1 && used + i > 76)
                    break;
                c = s.charAt(i);
                if (c == ' ' || c == '\t')
                    if (!(lastc == ' ' || lastc == '\t'))
                        lastspace = i;
                lastc = c;
            }
            if (lastspace == -1) {
                // no space, use the whole thing
                sb.append(s);
                s = "";
                used = 0;
                break;
            }
            sb.append(s.substring(0, lastspace));
            sb.append("\r\n");
            lastc = s.charAt(lastspace);
            sb.append(lastc);
            s = s.substring(lastspace + 1);
            used = 1;
        }
        sb.append(s);
        return sb.toString();
    }

    public static String encodeText(String text) throws UnsupportedEncodingException {
        return MimeUtility.encodeText(text);
    }

    /**
     * Check if the given string contains non US-ASCII characters.
     * @param	s	string
     * @return		ALL_ASCII if all characters in the string
     *			belong to the US-ASCII charset. MOSTLY_ASCII
     *			if more than half of the available characters
     *			are US-ASCII characters. Else MOSTLY_NONASCII.
     */
    public static int checkAscii(String s) {
        int ascii = 0, non_ascii = 0;
        int l = s.length();

        for (int i = 0; i < l; i++) {
            if (nonascii((int)s.charAt(i))) // non-ascii
                non_ascii++;
            else
                ascii++;
        }

        if (non_ascii == 0)
            return ALL_ASCII;
        if (ascii > non_ascii)
            return MOSTLY_ASCII;

        return MOSTLY_NONASCII;
    }

    public static final int ALL_ASCII 		= 1;
    public static final int MOSTLY_ASCII 	= 2;
    public static final int MOSTLY_NONASCII 	= 3;

    static final boolean nonascii(int b) {
        return b >= 0177 || (b < 040 && b != '\r' && b != '\n' && b != '\t');
    }


    private static Hashtable mime2java;
    private static Hashtable java2mime;

    static {
        java2mime = new Hashtable(40);
        mime2java = new Hashtable(10);

        // If we didn't load the tables, e.g., because we didn't have
        // permission, load them manually.  The entries here should be
        // the same as the default javamail.charset.map.
        if (java2mime.isEmpty()) {
            java2mime.put("8859_1", "ISO-8859-1");
            java2mime.put("iso8859_1", "ISO-8859-1");
            java2mime.put("iso8859-1", "ISO-8859-1");

            java2mime.put("8859_2", "ISO-8859-2");
            java2mime.put("iso8859_2", "ISO-8859-2");
            java2mime.put("iso8859-2", "ISO-8859-2");

            java2mime.put("8859_3", "ISO-8859-3");
            java2mime.put("iso8859_3", "ISO-8859-3");
            java2mime.put("iso8859-3", "ISO-8859-3");

            java2mime.put("8859_4", "ISO-8859-4");
            java2mime.put("iso8859_4", "ISO-8859-4");
            java2mime.put("iso8859-4", "ISO-8859-4");

            java2mime.put("8859_5", "ISO-8859-5");
            java2mime.put("iso8859_5", "ISO-8859-5");
            java2mime.put("iso8859-5", "ISO-8859-5");

            java2mime.put("8859_6", "ISO-8859-6");
            java2mime.put("iso8859_6", "ISO-8859-6");
            java2mime.put("iso8859-6", "ISO-8859-6");

            java2mime.put("8859_7", "ISO-8859-7");
            java2mime.put("iso8859_7", "ISO-8859-7");
            java2mime.put("iso8859-7", "ISO-8859-7");

            java2mime.put("8859_8", "ISO-8859-8");
            java2mime.put("iso8859_8", "ISO-8859-8");
            java2mime.put("iso8859-8", "ISO-8859-8");

            java2mime.put("8859_9", "ISO-8859-9");
            java2mime.put("iso8859_9", "ISO-8859-9");
            java2mime.put("iso8859-9", "ISO-8859-9");

            java2mime.put("sjis", "Shift_JIS");
            java2mime.put("jis", "ISO-2022-JP");
            java2mime.put("iso2022jp", "ISO-2022-JP");
            java2mime.put("euc_jp", "euc-jp");
            java2mime.put("koi8_r", "koi8-r");
            java2mime.put("euc_cn", "euc-cn");
            java2mime.put("euc_tw", "euc-tw");
            java2mime.put("euc_kr", "euc-kr");
        }
        if (mime2java.isEmpty()) {
            mime2java.put("iso-2022-cn", "ISO2022CN");
            mime2java.put("iso-2022-kr", "ISO2022KR");
            mime2java.put("utf-8", "UTF8");
            mime2java.put("utf8", "UTF8");
            mime2java.put("ja_jp.iso2022-7", "ISO2022JP");
            mime2java.put("ja_jp.eucjp", "EUCJIS");
            mime2java.put("euc-kr", "KSC5601");
            mime2java.put("euckr", "KSC5601");
            mime2java.put("us-ascii", "ISO-8859-1");
            mime2java.put("x-us-ascii", "ISO-8859-1");
        }
    }

    private static String defaultMIMECharset;
    private static String defaultJavaCharset;

    public static String mimeCharset(String charset) {
        if (java2mime == null || charset == null)
            // no mapping table or charset param is null
            return charset;

        String alias =
                (String)java2mime.get(charset.toLowerCase(Locale.ENGLISH));
        return alias == null ? charset : alias;
    }

    public static String javaCharset(String charset) {
        if (mime2java == null || charset == null)
            // no mapping table, or charset parameter is null
            return charset;

        String alias =
                (String)mime2java.get(charset.toLowerCase(Locale.ENGLISH));
        return alias == null ? charset : alias;
    }

    public static String getDefaultJavaCharset() {
        if (defaultJavaCharset == null) {
	    /*
	     * If mail.mime.charset is set, it controls the default
	     * Java charset as well.
	     */
            String mimecs = null;
            try {
                mimecs = System.getProperty("mail.mime.charset");
            } catch (SecurityException ex) { }	// ignore it
            if (mimecs != null && mimecs.length() > 0) {
                defaultJavaCharset = javaCharset(mimecs);
                return defaultJavaCharset;
            }

            try {
                defaultJavaCharset = System.getProperty("file.encoding",
                        "8859_1");
            } catch (SecurityException sex) {

                class NullInputStream extends InputStream {
                    public int read() {
                        return 0;
                    }
                }
                InputStreamReader reader =
                        new InputStreamReader(new NullInputStream());
                defaultJavaCharset = reader.getEncoding();
                if (defaultJavaCharset == null)
                    defaultJavaCharset = "8859_1";
            }
        }

        return defaultJavaCharset;
    }

    public static String getDefaultMIMECharset() {
        if (defaultMIMECharset == null) {
            try {
                defaultMIMECharset = System.getProperty("mail.mime.charset");
            } catch (SecurityException ex) { }	// ignore it
        }
        if (defaultMIMECharset == null)
            defaultMIMECharset = mimeCharset(getDefaultJavaCharset());
        return defaultMIMECharset;
    }

}

