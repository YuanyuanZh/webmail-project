package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.ParseException;
import cs601.webmail.frameworks.mail.util.LineInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyuan on 11/9/14.
 */
public class MultiPackageParser {

    public List<String> parse(InputStream in, String boundary) throws ParseException, IOException {
        return parse(in, boundary, 0);
    }

    /**
     * Parse multipart datagram to several individual data package. After parsed all boundaries,
     * will close the input stream.
     * @param in Input content as a stream
     * @param boundary  Boundary starts with "--"
     * @return One or more part contents
     * @throws ParseException if no content found.
     * @throws IOException input stream not available
     */
    public List<String> parse(InputStream in, String boundary, int offset) throws ParseException, IOException {

        if (boundary == null || boundary.length() == 0)
            throw new IllegalArgumentException("arg boundary missed");

        if (in == null)
            throw new ParseException("input stream not available");

        boundary = "--" + boundary;
        String end_boundary = boundary + "--";

        List<String> ret = new ArrayList<String>();
        LineInputStream reader = new LineInputStream(in);

        String line = null;

        StringBuilder partCache = new StringBuilder();
        int previousBoundary = -1; // -1 not start scan yet
        int idx = offset;

        String preamble = null;

        while ((line = reader.readLine()) != null) {

            idx++;

            // got boundary
            if (boundary.equals(line)) {

                // save preamble and clean cache
                if (previousBoundary == -1) {
                    preamble = partCache.toString();
                    partCache = new StringBuilder();
                }

                previousBoundary = idx;

                // save part and clean cache
                if (partCache.length() > 0) {
                    ret.add(partCache.toString());
                    partCache = new StringBuilder();
                }

                continue;
            }

            // reached boundary end
            if (end_boundary.equals(line)) {

                if (partCache.length() > 0) {
                    ret.add(partCache.toString());
                    partCache = null;
                }
                break;
            }

            partCache.append(line).append("\r\n");
        }

        return ret;
    }

}
