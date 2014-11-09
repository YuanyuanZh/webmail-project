package cs601.webmail.frameworks.web;

/**
 * Created by yuanyuan on 10/29/14.
 */
public class AjaxResponse {

    public static final String _OK = "ok";

    public static final String _ERR = "error";

    public static AjaxResponse OK = new AjaxResponse(_OK, 0);

    public static AjaxResponse ERR = new AjaxResponse(_ERR, 0);

    public AjaxResponse(String state, int code) {
        this.state = state;
        this.code = code;
    }

    public AjaxResponse(String state, int code, Object data) {
        this.state = state;
        this.code = code;
        this.data = data;
    }

    private String state;

    private int code;

    private Object data;

    public String getState() {
        return state;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
