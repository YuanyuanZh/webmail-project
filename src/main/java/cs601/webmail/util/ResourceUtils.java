package cs601.webmail.util;

import cs601.webmail.Configuration;


/**
 * Created by yuanyuan on 10/25/14.
 */
public final class ResourceUtils {

    public static String getClassPath() {
        return ResourceUtils.class.getResource("/").getPath();
    }

    public static String getWorkDir() {
        return Configuration.getDefault().getString(Configuration.WORK_DIR);
    }


}


