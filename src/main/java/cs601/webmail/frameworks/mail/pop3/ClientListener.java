package cs601.webmail.frameworks.mail.pop3;

/**
 * Created by yuanyuan on 11/9/14.
 */
public interface ClientListener {

    public static enum Event {
        LineReceived
    }

    public void onLineReceived(String line);

}
