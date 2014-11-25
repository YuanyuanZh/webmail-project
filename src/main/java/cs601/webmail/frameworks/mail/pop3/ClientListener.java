package cs601.webmail.frameworks.mail.pop3;

/**
 * Created by yuanyuan on 11/9/14.
 */
public interface ClientListener {
    public static enum EventType {
        LineRead,
        LineWrite
    }

    public void onEvent(EventType eventType, Object eventData);

    /**
     * Return <code>true</code> if this listener can deal with the event.
     * @param eventType Event type.
     * @return Return <code>false</code> if couldn't.
     */
    public boolean isAccepted(EventType eventType);

}
