package cs601.webmail.util;

import cs601.webmail.Configuration;

import java.io.File;
import cs601.webmail.entity.Mail;

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

    // Calculate path to store raw file.<p>
    // save raw content to {WorkDir}/raw/accountId/MD5-16(mailAccount)/uid.dat
    // for example: /Users/foobar/webmail/raw/2/abcdef12345467890/cDebdfdafd0122.dat
    @Deprecated
    public static String getRawMailStorePath(String username) {

        StringBuilder sb = new StringBuilder(getWorkDir());
        sb.append(File.separator).append("raw");
        sb.append(File.separator);

        String usernameMD = DigestUtils.digestToSHA(username);
        sb.append(usernameMD);

        return sb.toString();
    }
    // e.g.
    //  ${user.home}/webmail/mails/abdda123434245152151243412444/sent
    //  ${user.home}/webmail/mails/abdda123434245152151243412444/inbox

    public static String resolveMailFolderPath(String mailAddress, String folder) {

        StringBuilder sb = new StringBuilder(getWorkDir());

        sb.append(File.separator).append("mails");
        sb.append(File.separator);

        String usernameMD = DigestUtils.digestToSHA(mailAddress);
        sb.append(usernameMD);

        sb.append(File.separator);
        sb.append(folder);

        return sb.toString();
    }
    public static String resolveMailFolderPath(String mailAddress, Mail.VirtualFolder folder) {
        return resolveMailFolderPath(mailAddress, folder.getSystemFolder());
        }

}


