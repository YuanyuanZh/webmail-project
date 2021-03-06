package cs601.webmail;

import cs601.webmail.exception.ConfigurationException;
import cs601.webmail.util.PropertyExpander;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by yuanyuan on 10/27/14.
 */
public class Configuration {

    private static Configuration instance;

    public static final ConfigurationItem DB_PATH = new ConfigurationItem("db.path", "webmail.db");

    @Deprecated
    public static final ConfigurationItem SCAN_PACKAGE = new ConfigurationItem("scan.package", "cs601.webmail") ;

    public static final ConfigurationItem WORK_DIR = new ConfigurationItem("work.dir", "${user.home}/webmail") ;
    public static final ConfigurationItem LOG_DIR = new ConfigurationItem("log.dir", "${user.home}/webmail/logs");


    public static Configuration getDefault() {

        if (instance == null) {
            instance = new Configuration();

            try {
                instance.load();
            } catch (IOException e) {
                throw new IllegalStateException("Load config failed.", e);
            }
        }

        return instance;

    }

    public Configuration() {
    }

    public void load() throws IOException {
        String[] _config =  new String[] {
                PropertyExpander.expandSystemProperties("${user.home}/webmail.config"),
                PropertyExpander.expandSystemProperties("${user.home}/webmail/webmail.config")
        };

        File file = null;

        for (String cfg : _config) {
            if (Constants.DEBUG_MODE)
                System.out.println(String.format("[DEBUG] Trying load config from [%s]", cfg));

            file = new File(cfg);

            if (file.exists()) {
                if (Constants.DEBUG_MODE)
                    System.out.println(String.format("[DEBUG] Load config from [%s]", cfg));
                break;
            }
        }

        Properties properties = new Properties();

        if (file != null && file.exists()) {
            properties.load(new FileInputStream(file));
        }

        this.properties = properties;
    }

    public String get(String configKey) {
        return properties.getProperty(configKey);
    }

    public Object get(ConfigurationItem configurationItem) {
        if (configurationItem == null)
            return null;

        Object ret = properties.get(configurationItem.getKey());

        if (ret instanceof String) {
            return PropertyExpander.expandSystemProperties((String) ret);
        }

        return ret != null ? ret : configurationItem.getDefaultValue();
    }

    public String getString(ConfigurationItem configurationItem) {
        Object obj = get(configurationItem);

        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    public Long getLong(String configKey) {
        String raw = get(configKey);

        if (raw != null && raw.length() > 0) {
            return Long.parseLong(raw);
        }
        return null;
    }

    public Long getLong(ConfigurationItem configurationItem) {
        String raw = getString(configurationItem);

        if (raw != null && raw.length() > 0) {
            return Long.parseLong(raw);
        }
        return null;
    }

    public Integer getInteger(String configKey) {
        String raw = get(configKey);

        if (raw != null && raw.length() > 0) {
            return Integer.parseInt(raw);
        }
        return null;
    }

    public Integer getInteger(ConfigurationItem configurationItem) {
        String raw = getString(configurationItem);

        if (raw != null && raw.length() > 0) {
            return Integer.parseInt(raw);
        }
        return null;
    }

    public Boolean getBoolean(String configKey) {
        String raw = get(configKey);

        if (raw != null && raw.length() > 0) {
            return Boolean.parseBoolean(raw);
        }

        throw new ConfigurationException("Can't get boolean for " + configKey);
    }

    public Boolean getBoolean(ConfigurationItem configurationItem) {
        String raw = getString(configurationItem);

        if (raw != null && raw.length() > 0) {
            return Boolean.parseBoolean(raw);
        }

        throw new ConfigurationException("Can't get boolean for " + configurationItem.getKey());
    }

    private Properties properties;

    static class ConfigurationItem {

        private String key;

        private Object defaultValue;

        ConfigurationItem(String key, Object defaultValue) {
            this.defaultValue = defaultValue;
            this.key = key;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public String getKey() {
            return key;
        }
    }

}
