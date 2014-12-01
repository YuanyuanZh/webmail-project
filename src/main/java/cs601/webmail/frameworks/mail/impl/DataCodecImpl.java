package cs601.webmail.frameworks.mail.impl;

import cs601.webmail.frameworks.mail.*;
import cs601.webmail.frameworks.mail.util.QuotePrintableEncoderStream;
import cs601.webmail.util.Base64;
import cs601.webmail.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.List;

/**
 * Created by yuanyuan on 11/11/14.
 */
public class DataCodecImpl implements DataCodec {

    protected Object content;

    protected String contentType;

    public DataCodecImpl() {
    }

    public DataCodecImpl(Object obj, String type) {
        this.content = obj;
        this.contentType = type;
    }

    // Any type what we supports now, return HTML!
    @Override
    public Object decode(MimePart part) throws MessagingException {

        if (part == null) {
            throw new IllegalArgumentException();
        }

        ContentType cType = new ContentType(part.getContentType());
        String encoding = part.getEncoding();


        if (cType.match("text/plain")) {
            MimeContent mc = new MimeContent();
            mc.setContent(decodeString(part.getInputStream(), cType, encoding));
            mc.setContentType("text/plain");
            mc.setCharset(cType.getParameter("charset"));
            return mc;
        }
        else if (cType.match("text/html")) {
            MimeContent mc = new MimeContent();
            mc.setContent(decodeString(part.getInputStream(), cType, encoding));
            mc.setContentType("text/html");
            mc.setCharset(cType.getParameter("charset"));
            return mc;
        }
        else if (cType.match("multipart/alternative")) {

            String boundary = cType.getParameter("boundary");

            if (boundary == null) {
                throw new MessagingException("No boundary found from ContentType");
            }

            List<String> parts = null;

            MultiPackageParser mParser = new MultiPackageParser();

            try {
                parts = mParser.parse(part.getInputStream(), boundary);

                // Well, nothing found. actually, it wasn't possible I guess
                if (parts == null || parts.size() == 0) {
                    return null;
                }

                // Choose the first one from list as the default strategy.
                // Just ignore other alternative options.
                InputStream contentStream = new ByteArrayInputStream(parts.get(0).getBytes());
                BodyPart subBodyPart = new BodyPart(contentStream);

                ContentType subContentType = new ContentType(subBodyPart.getContentType());

                Object content = subBodyPart.getContent();

                if (content instanceof MimeContent) {
                    return content;
                } else {

                    // Supposed to be text/plain
                    MimeContent mc = new MimeContent();
                    mc.setContent(content != null ? content.toString() : null);
                    mc.setContentType("text/plain");
                    mc.setCharset(subContentType.getParameter("charset"));

                    return mc;
                }
            } catch (IOException e) {
                throw new MessagingException("Error parse multipart/alternative", e);
            }
        }

        throw new MessagingException(String.format(
                "Content type [%s] was not supported yet!", cType.getBaseType()));
    }

    private String decodeString(InputStream inputStream, ContentType cType, String encoding) throws MessagingException {
        String charset = cType.getParameter("charset");

        if (charset == null)
            charset = "iso-8859-1";

        byte[] buf = null;

        try {
            buf = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new MessagingException(e);
        } finally {
            if (inputStream.markSupported()) {
                try {
                    inputStream.reset();
                } catch (IOException e) {
                    //
                }
            }
        }

        if ("base64".equalsIgnoreCase(encoding)) {
            byte[] tbuf = Base64.decodeBase64(buf);
            try {
                return new String(tbuf, charset);
            } catch (UnsupportedEncodingException e) {
                return new String(tbuf);
            }
        }
        else if ("quoted-printable".equalsIgnoreCase(encoding)) {

            QuotePrintableEncoderStream qpds = new QuotePrintableEncoderStream(inputStream);

            try {
                byte[] content = IOUtils.toByteArray(qpds);

                return new String(content, charset);

            } catch (UnsupportedEncodingException e) {
                throw new MessagingException("Can't decode Quoted-Printable in this charset: " + charset, e);
            } catch (IOException e) {
                throw new MessagingException("Can't decode Quoted-Printable", e);
            }
        }
        // return original text is fine.
        else if ("7bit".equalsIgnoreCase(encoding) || "8bit".equalsIgnoreCase(encoding)) {
            try {
                return new String(buf, charset);
            } catch (UnsupportedEncodingException e) {
                return new String(buf);
            }
        }
        else {
            // just return what passed in.
            return new String(buf);
        }
    }

    public String encode() throws MessagingException {

        return "";
    }

}
