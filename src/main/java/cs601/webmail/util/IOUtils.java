package cs601.webmail.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuanyuan on 11/13/14.
 */
public class IOUtils {

    public static byte[] toByteArray(InputStream input) throws IOException {
        return org.apache.commons.io.IOUtils.toByteArray(input);
    }

}