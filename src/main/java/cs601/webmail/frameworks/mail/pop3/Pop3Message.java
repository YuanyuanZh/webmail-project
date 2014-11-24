package cs601.webmail.frameworks.mail.pop3;

import cs601.webmail.frameworks.mail.Header;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanyuan on 10/24/14.
 */
public class Pop3Message implements Serializable{

    @Deprecated
    private Map<String, List<String>> headers;

    private Map<String, List<Header>> _headers;

    private String body;

    @Deprecated
    protected Pop3Message(Map<String, List<String>> headers, String body) {
        this.headers = Collections.unmodifiableMap(headers);
        this.body = body;
    }

    public Pop3Message(Map<String, List<Header>> _headers) {
        this._headers = Collections.unmodifiableMap(_headers);
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

}
