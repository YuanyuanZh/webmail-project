package cs601.webmail.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by yuanyuan on 10/30/14.
 */
public abstract class Strings {

    public static boolean haveLength(String string) {
        return string != null && string.length() > 0;
    }

    public static String join(String[] strings, String joinWith) {

        if (strings == null || strings.length == 0)
            return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {

            if (i > 0) {
                sb.append(joinWith);
            }

            sb.append(strings[i]);
        }

        return sb.toString();
    }

    public static String join(Collection<String> strings, String joinWith) {

        if (strings == null || strings.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();

        for (Iterator<String> it = strings.iterator(); it.hasNext(); ) {
            sb.append(it.next());

            if (it.hasNext()) {
                sb.append(joinWith);
            }
        }

        return sb.toString();
    }

    public static String trim(String rcpt) {
        return rcpt != null ? rcpt.trim() : rcpt;
    }
}
