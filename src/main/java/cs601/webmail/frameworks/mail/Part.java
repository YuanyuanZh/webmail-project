package cs601.webmail.frameworks.mail;

import java.io.InputStream;

/**
 * Created by yuanyuan on 11/13/14.
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


    //-------------------------- Header methods

    public String[] getHeader(String name) throws MessagingException;

    public void setHeader(String name, String value) throws MessagingException;

    public void addHeader(String name, String value) throws MessagingException;

    public void removeHeader(String name) throws MessagingException;

}

