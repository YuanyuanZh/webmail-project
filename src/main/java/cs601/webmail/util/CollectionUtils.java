package cs601.webmail.util;

import java.util.Collection;

/**
 * Created by yuanyuan on 11/12/14.
 */
public class CollectionUtils {

    public static boolean notEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }

}
