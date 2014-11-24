package cs601.webmail.frameworks.mail.util;


import cs601.webmail.util.ByteUtils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LineOutputStream extends FilterOutputStream {

    private static byte[] newline;

    static {
        newline = new byte[2];
        newline[0] = (byte)'\r';
        newline[1] = (byte)'\n';
    }

    public LineOutputStream(OutputStream out) {
        super(out);
    }

    public void writeln(String s) throws IOException {
        byte[] bytes = ByteUtils.getBytes(s);
        out.write(bytes);
        out.write(newline);
    }

    public void writeln() throws IOException {
        out.write(newline);
    }
}
