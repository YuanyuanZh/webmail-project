package cs601.webmail.frameworks.mail.util;

/**
 * Created by yuanyuan on 11/13/14.
 */
public class AsciiUtils {

    public static int parseInt(byte[] b, int start, int end, int radix)
            throws NumberFormatException {
        if (b == null)
            throw new NumberFormatException("null");

        int result = 0;
        boolean negative = false;
        int i = start;
        int limit;
        int multmin;
        int digit;

        if (end > start) {
            if (b[i] == '-') {
                negative = true;
                limit = Integer.MIN_VALUE;
                i++;
            } else {
                limit = -Integer.MAX_VALUE;
            }
            multmin = limit / radix;
            if (i < end) {
                digit = Character.digit((char)b[i++], radix);
                if (digit < 0) {
                    throw new NumberFormatException(
                            "illegal number: " + toString(b, start, end)
                    );
                } else {
                    result = -digit;
                }
            }
            while (i < end) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit((char)b[i++], radix);
                if (digit < 0) {
                    throw new NumberFormatException("illegal number");
                }
                if (result < multmin) {
                    throw new NumberFormatException("illegal number");
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new NumberFormatException("illegal number");
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException("illegal number");
        }
        if (negative) {
            if (i > start + 1) {
                return result;
            } else {	/* Only got "-" */
                throw new NumberFormatException("illegal number");
            }
        } else {
            return -result;
        }
    }


    /**
     * Convert the bytes within the specified range of the given byte
     * array into a String. The range extends from <code>start</code>
     * till, but not including <code>end</code>. <p>
     */
    public static String toString(byte[] b, int start, int end) {
        int size = end - start;
        char[] theChars = new char[size];

        for (int i = 0, j = start; i < size; )
            theChars[i++] = (char)(b[j++]&0xff);

        return new String(theChars);
    }

}

