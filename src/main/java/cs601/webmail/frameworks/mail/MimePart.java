package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.MessagingException;
import cs601.webmail.frameworks.mail.Part;

/**
 * Created by yuanyuan on 11/13/14.
 */
public interface MimePart extends Part {

    // get from header "Content-Transfer-Encoding"
    public String getEncoding() throws MessagingException;

    // MIME message could have more than one header but have only one name, they shared
    // the same name.
    public String getHeader(String name, String delimiter) throws MessagingException;

}
