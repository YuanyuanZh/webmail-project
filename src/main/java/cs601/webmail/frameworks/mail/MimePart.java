package cs601.webmail.frameworks.mail;

/**
 * Created by yuanyuan on 11/11/14.
 */
public interface MimePart extends Part {

    // get from header "Content-Transfer-Encoding"
    public String getEncoding() throws MessagingException;

    // MIME message could have more than one header but have only one name, they shared
    // the same name.
    public String getHeader(String name, String delimiter) throws MessagingException;

}
