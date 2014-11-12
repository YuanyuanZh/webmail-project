package cs601.webmail.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 * Created by yuanyuan on 11/11/14.
 */
public final class DigestUtils {

    public static String digestToSHA(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");

            return new String(Hex.encodeHex(md.digest(origin.getBytes())));

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can't calculate path for raw mail.", e);
        }
    }

}

