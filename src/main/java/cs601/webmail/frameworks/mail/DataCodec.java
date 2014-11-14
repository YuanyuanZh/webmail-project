package cs601.webmail.frameworks.mail;

/**
 * Created by yuanyuan on 11/13/14.
 */
public interface DataCodec {

    public Object decode(MimePart part) throws MessagingException;

}
