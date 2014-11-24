package cs601.webmail.frameworks.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yuanyuan on 11/11/14.
 */
public interface Part {

    // get MIME type of content
    public String getContentType() throws MessagingException;

    public boolean isMimeType(String mimeType) throws MessagingException;

    public Object getContent() throws MessagingException;

    // get raw content as a stream
    public InputStream getInputStream() throws MessagingException;

    /**
     * @param obj Content object.
     * @param type MIME type.
     */
    public void setContent(Object obj, String type) throws MessagingException;


    public void setText(String text) throws MessagingException;

    public void writeTo(OutputStream os) throws MessagingException, IOException;

    //-------------------------- Header methods

    public String[] getHeader(String name) throws MessagingException;

    public void setHeader(String name, String value) throws MessagingException;

    public void addHeader(String name, String value) throws MessagingException;

    public void removeHeader(String name) throws MessagingException;

}
