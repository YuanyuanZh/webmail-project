package cs601.webmail;

/**
 * Created by yuanyuan on 10/27/14.
 */

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class Message implements Serializable {

    private final Map<String, List<String>> headers;

    private final String body;

    protected Message(Map<String, List<String>> headers, String body) {
        this.headers = Collections.unmodifiableMap(headers);
        this.body = body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

}
