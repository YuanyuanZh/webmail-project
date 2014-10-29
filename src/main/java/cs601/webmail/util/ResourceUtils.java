package cs601.webmail.util;

import java.io.File;

/**
 * Created by yuanyuan on 10/25/14.
 */
public class ResourceUtils {

    public static String getClassPath() {
        return ResourceUtils.class.getResource("/").getPath();
    }

}
