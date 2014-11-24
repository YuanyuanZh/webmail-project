package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.impl.ContentType;

/**
 * Created by yuanyuan on 11/12/14.
 */
public class MimeContent {

    // MIME type
    private String contentType;

    private String charset;

    private String content;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
