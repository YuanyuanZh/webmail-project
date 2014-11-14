package cs601.webmail.frameworks.mail.codec;

/**
 * Created by yuanyuan on 11/13/14.
 */
public class NoSuchCodecException extends CodecException {

    public NoSuchCodecException() {
    }

    public NoSuchCodecException(String message) {
        super(message);
    }

    public NoSuchCodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchCodecException(Throwable cause) {
        super(cause);
    }

    public NoSuchCodecException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}