package cs601.webmail.frameworks.mail.codec;

/**
 * Created by yuanyuan on 11/13/14.
 */
public interface Codec {

    public int decode(byte[] qp);

    public String decode(byte[] qp, String charset);

    public String encode(String content, String charset);

    public String encode(byte[] content);

}
