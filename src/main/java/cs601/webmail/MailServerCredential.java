package cs601.webmail;

/**
 * Created by yuanyuan on 11/3/14.
 */

import cs601.webmail.util.PropertyExpander;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by yuanyuan on 10/24/14.
 *
 * @deprecated User @{Configurer} instead
 */
@Deprecated
public class MailServerCredential {

    private String email;

    private String password;

    private String popServer;

    // TCP 110, SSL 995
    private String popPort;

    private boolean sslEnabled = false;

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public String getPopPort() {
        return popPort;
    }

    public void setPopPort(String popPort) {
        this.popPort = popPort;
    }

    public String getPopServer() {
        return popServer;
    }

    public void setPopServer(String popServer) {
        this.popServer = popServer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static MailServerCredential getDefault() {

        String[] _config =  new String[] {
                PropertyExpander.expandSystemProperties("${user.home}/webmail.config"),
                PropertyExpander.expandSystemProperties("${user.home}/webmail/webmail.config")
        };

        File file = null;

        for (String cfg : _config) {
            file = new File(cfg);

            if (file.exists()) {
                System.out.println(String.format("[INFO] Load config from [%s]", cfg));
                break;
            }
        }

        MailServerCredential ret = new MailServerCredential();

        if (file.exists()) {

            try {
                List<String> lines = FileUtils.readLines(file);

                for (String line : lines) {

                    if (line == null || line.length() == 0 || line.startsWith("#")) {
                        continue; //skip it
                    }

                    int colonPos = line.indexOf("=");
                    String propName = line.substring(0, colonPos);
                    String propValue = line.substring(colonPos + 1);

                    if (propName.equalsIgnoreCase("email")) {
                        ret.setEmail(propValue);
                    }
                    else if (propName.equalsIgnoreCase("password")) {
                        ret.setPassword(propValue);
                    }
                    else if (propName.equalsIgnoreCase("pop")) {
                        ret.setPopServer(propValue);
                    }
                    else if (propName.equalsIgnoreCase("pop.port")) {
                        ret.setPopPort(propValue);
                    }
                    else if (propName.equalsIgnoreCase("pop.ssl")) {
                        ret.setSslEnabled("true".equalsIgnoreCase(propValue));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

}
