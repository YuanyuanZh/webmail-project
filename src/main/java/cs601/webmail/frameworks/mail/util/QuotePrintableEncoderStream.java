package cs601.webmail.frameworks.mail.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;


/**
 * This class implements a QP Decoder.
 *
 * Created by yuanyuan on 11/12/14.
 */
public class QuotePrintableEncoderStream extends FilterInputStream {

    protected byte[] ba = new byte[2];
    protected int spaces = 0;

    /**
     * Create a Quoted Printable decoder that decodes the specified
     * input stream.
     *
     * @param in the input stream
     */
    public QuotePrintableEncoderStream(InputStream in) {
        super(new PushbackInputStream(in, 2)); // pushback of size=2
    }

    public int read() throws IOException {
        if (spaces > 0) {
            // We have cached space characters, return one
            spaces--;
            return ' ';
        }

        int c = in.read();

        if (c == ' ') {
            // Got space, keep reading till we get a non-space char
            while ((c = in.read()) == ' ')
                spaces++;

            if (c == '\r' || c == '\n' || c == -1)
                // If the non-space char is CR/LF/EOF, the spaces we got
                // so far is junk introduced during transport. Junk 'em.
                spaces = 0;
            else {
                // The non-space char is NOT CR/LF, the spaces are valid.
                ((PushbackInputStream) in).unread(c);
                c = ' ';
            }
            return c; // return either <SPACE> or <CR/LF>
        } else if (c == '=') {
            // QP Encoded atom. Decode the next two bytes
            int a = in.read();

            if (a == '\n') {
        /* Hmm ... not really confirming QP encoding, but lets
		 * allow this as a LF terminated encoded line .. and
		 * consider this a soft linebreak and recurse to fetch
		 * the next char.
		 */
                return read();
            } else if (a == '\r') {
                // Expecting LF. This forms a soft linebreak to be ignored.
                int b = in.read();
                if (b != '\n')
		    /* Not really confirming QP encoding, but
		     * lets allow this as well.
		     */
                    ((PushbackInputStream) in).unread(b);
                return read();
            } else if (a == -1) {
                // Not valid QP encoding, but we be nice and tolerant here !
                return -1;
            } else {
                ba[0] = (byte) a;
                ba[1] = (byte) in.read();
                try {
                    return AsciiUtils.parseInt(ba, 0, 2, 16);
                } catch (NumberFormatException nex) {
		    /*
		    System.err.println(
		     	"Illegal characters in QP encoded stream: " +
		     	ASCIIUtility.toString(ba, 0, 2)
		    );
		    */

                    ((PushbackInputStream) in).unread(ba);
                    return c;
                }
            }
        }
        return c;
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        int i, c;
        for (i = 0; i < len; i++) {
            if ((c = read()) == -1) {
                if (i == 0) // At end of stream, so we should
                    i = -1; // return -1 , NOT 0.
                break;
            }
            buf[off + i] = (byte) c;
        }
        return i;
    }

    public long skip(long n) throws IOException {
        long skipped = 0;
        while (n-- > 0 && read() >= 0)
            skipped++;
        return skipped;
    }

    public boolean markSupported() {
        return false;
    }

    public int available() throws IOException {
        // This is bogus ! We don't really know how much
        // bytes are available *after* decoding
        return in.available();
    }

}

