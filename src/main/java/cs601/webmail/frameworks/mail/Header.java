package cs601.webmail.frameworks.mail;

/**
 * Created by yuanyuan on 11/13/14.
 */
public class Header {

    protected String name;

    protected String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = this.name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", value=" + value + "]";
    }
}
