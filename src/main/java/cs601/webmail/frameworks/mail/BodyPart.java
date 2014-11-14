package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.impl.DataCodecImpl;
import cs601.webmail.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * Created by yuanyuan on 11/13/14.
 */
public class BodyPart implements MimePart {

    protected Headers headers;

    protected byte[] content;

    protected DataCodec dataCodec;

    public BodyPart(InputStream in) throws MessagingException {

        if (in == null) {
            throw new MessagingException("InputStream missed");
        }

        if (!(in instanceof ByteArrayInputStream) &&
                !(in instanceof BufferedInputStream))
            in = new BufferedInputStream(in);

        headers = new Headers(in);
        dataCodec = new DataCodecImpl();
        parse(in);
    }

    private synchronized void parse(InputStream in) throws MessagingException {
        try {
            content = IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new MessagingException("Error reading input stream.", e);
        } finally {
            if (in.markSupported()) {
                try {
                    in.reset();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public DataCodec getDataCodec() {
        return dataCodec;
    }

    public void setDataCodec(DataCodec dataCodec) {
        this.dataCodec = dataCodec;
    }

    @Override
    public String getContentType() throws MessagingException {
        return getHeader("Content-Type", null);
    }

    @Override
    public boolean isMimeType(String mimeType) throws MessagingException {
        return Message.isMimeType(this, mimeType);
    }

    @Override
    public Object getContent() throws MessagingException {
        if (dataCodec != null) {
            return dataCodec.decode(this);
        }
        return null;
    }

    @Override
    public InputStream getInputStream() throws MessagingException {
        if (content != null) {
            return new ByteArrayInputStream(content);
        }
        throw new MessagingException("Content of BodyPart not available");
    }

    @Override
    public void setContent(Object obj, String type) throws MessagingException {

    }

    @Override
    public void setText(String text) throws MessagingException {
        throw new IllegalStateException("not impl yet");
    }

    @Override
    public String[] getHeader(String name) throws MessagingException {
        return headers.getHeader(name);
    }

    @Override
    public void setHeader(String name, String value) throws MessagingException {
        headers.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) throws MessagingException {
        headers.addHeader(name, value);
    }

    @Override
    public void removeHeader(String name) throws MessagingException {
        headers.removeHeader(name);
    }

    @Override
    public String getEncoding() throws MessagingException {
        return Message.getEncoding(this);
    }

    @Override
    public String getHeader(String name, String delimiter) throws MessagingException {
        return headers.getHeader(name, delimiter);
    }
}

