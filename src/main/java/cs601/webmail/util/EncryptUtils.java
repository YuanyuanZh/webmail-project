package cs601.webmail.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yuanyuan on 11/13/14.
 */
public class EncryptUtils {

    public static final String KEY_ALGORITHM = "AES";

    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * Decrypt by AES algorithm.
     * @param data Byte array which you want to decrypt.
     * @param key Length of key could be 128 or 192 or 256 (bit).
     *            If use ASCII character, count should be 16 or 24 or 32 chars.
     * @return Original data.
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        Key k = toKey(key);
        byte[] ret = null;
        Exception exception = null;

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, k);

            ret = cipher.doFinal(data);

        } catch (NoSuchAlgorithmException e) {
            exception = e;
        } catch (NoSuchPaddingException e) {
            exception = e;
        } catch (InvalidKeyException e) {
            exception = e;
        } catch (BadPaddingException e) {
            exception = e;
        } catch (IllegalBlockSizeException e) {
            exception = e;
        }

        if (exception != null) {
            throw new IllegalStateException("Error to encrypt", exception);
        }

        return ret;
    }

    /**
     * Encrypt with AES algorithm.
     * @param data Byte array which you want to encrypt.
     * @param key Length of key could be 128 or 192 or 256 (bit).
     *            If use ASCII character, count should be 16 or 24 or 32 chars.
     * @return Byte array which was encrypted.
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        Key k = toKey(key);
        byte[] ret = null;
        Exception exception = null;

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, k);

            ret = cipher.doFinal(data);

        } catch (NoSuchAlgorithmException e) {
            exception = e;
        } catch (NoSuchPaddingException e) {
            exception = e;
        } catch (InvalidKeyException e) {
            exception = e;
        } catch (BadPaddingException e) {
            exception = e;
        } catch (IllegalBlockSizeException e) {
            exception = e;
        }

        if (exception != null) {
            throw new IllegalStateException("Error to encrypt", exception);
        }

        return ret;
    }

    /**
     * Decrypt data from a HEX wrapped string.
     * @param data HEX string whom contains encrypted data.
     * @param key Key in string. Attention: length should be one of 16 / 24 / 32.
     * @return Original data in string.
     */
    public static String decryptFromHex(String data, String key) {
        checkKey(key);
        try {
            byte[] dataBytes = Hex.decodeHex(data.toCharArray());

            return new String(decrypt(dataBytes, key.getBytes()));

        } catch (DecoderException e) {
            throw new IllegalStateException("Error decryptFromHex", e);
        }
    }

    /**
     * Wrap byte array that encrypted from original data to HEX string.
     * @param data Original data in string format.
     * @param key Key in string. Attention: length should be one of 16 / 24 / 32.
     * @return Wrapped hex string from encrypted byte array.
     */
    public static String encryptToHex(String data, String key) {
        checkKey(key);
        byte[] result = encrypt(data.getBytes(), key.getBytes());
        return Hex.encodeHexString(result);
    }

    private static Key toKey(byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }

    private static void checkKey(String key) {
        if (key == null || key.length() == 0)
            throw new IllegalArgumentException("No key provided.");

        char[] keyChars = key.toCharArray();

        // this rule just for AES
        if (!(keyChars.length == 16 ||
                keyChars.length == 24 ||
                keyChars.length == 32)) {
            throw new IllegalArgumentException("Key length not in 16 or 24 or 32!");
        }
    }

}

