package cs601.webmail.mail;

/**
 * Created by yuanyuan on 10/28/14.
 */
public class Pop3MessageInfo {

    public int number;
    public int size;
    public String identifier;

    public Pop3MessageInfo(int num, int size) {
        this.number = num;
        this.size = size;
    }

    public Pop3MessageInfo(int num, String identifier) {
        this.number = num;
        this.identifier = identifier;
        this.size = -1;
    }
}
