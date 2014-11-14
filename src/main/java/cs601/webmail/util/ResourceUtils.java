package cs601.webmail.util;

import java.io.File;
import cs601.webmail.Configuration;

/**
 * Created by yuanyuan on 10/25/14.
 */
public class ResourceUtils {

    public static String getClassPath() {
        return ResourceUtils.class.getResource("/").getPath();
    }

    public static String getWorkDir() {
        return Configuration.getDefault().getString(Configuration.WORK_DIR);
    }


    public static String getRawMailStorePath(String username) {

        StringBuilder sb = new StringBuilder(getWorkDir());
        sb.append(File.separator).append("raw");
        sb.append(File.separator);

        String usernameMD = DigestUtils.digestToSHA(username);
        sb.append(usernameMD);

        return sb.toString();
    }


}
