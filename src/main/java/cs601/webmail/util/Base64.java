package cs601.webmail.util;

/**
 * Created by yuanyuan on 11/12/14.
 */
public class Base64 {

    public static byte[] decodeBase64(final byte[] base64Data) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(base64Data);
    }

}
