package cs601.webmail.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuanyuan on 11/8/14.
 */
public class ByteUtils {

    public static byte[] getBytes(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte [] buf;


        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        }
        else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        return buf;
    }

    public static byte[] getBytes(String s) {
        char [] chars= s.toCharArray();
        int size = chars.length;
        byte[] bytes = new byte[size];

        for (int i = 0; i < size;)
            bytes[i] = (byte) chars[i++];
        return bytes;
    }
}
