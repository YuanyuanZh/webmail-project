package cs601.webmail.application;

import cs601.webmail.Constants;
import cs601.webmail.util.PropertyExpander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by yuanyuan on 10/27/14.
 */
public class Configurer {

    static Configurer instance;

    public static Configurer getDefault() {

        if (instance == null) {
            instance = new Configurer();

            try {
                instance.load();
            } catch (IOException e) {
                throw new IllegalStateException("Load config failed.", e);
            }
        }

        return instance;

    }

    public static final ConfigurationItem DB_PATH = new ConfigurationItem("db.path", "webmail.db");

    public Configurer() {
    }

    public void load() throws IOException {
        String[] _config =  new String[] {
                PropertyExpander.expandSystemProperties("${user.home}/webmail.config"),
                PropertyExpander.expandSystemProperties("${user.home}/webmail/webmail.config")
        };

        File file = null;

        for (String cfg : _config) {
            file = new File(cfg);

            if (file.exists()) {
                if (Constants.DEBUG_MODE)
                    System.out.println(String.format("[INFO] Load config from [%s]", cfg));
                break;
            }
        }

        Properties properties = new Properties();

        if (file != null && file.exists()) {
            properties.load(new FileInputStream(file));
        }

        this.properties = properties;
    }

    public Object get(String configKey) {
        return properties.get(configKey);
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
