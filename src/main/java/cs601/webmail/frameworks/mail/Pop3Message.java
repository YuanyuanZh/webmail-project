package cs601.webmail.frameworks.mail;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanyuan on 10/28/14.
 */
public class Pop3Message implements Serializable {

    private final Map<String, List<String>> headers;

    private final String body;

    protected Pop3Message(Map<String, List<String>> headers, String body) {
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

