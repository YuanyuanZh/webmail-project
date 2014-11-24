package cs601.webmail.frameworks.mail.util;

import java.util.Properties;

/**
 * Created by yuanyuan on 11/9/14.
 */
public class PropUtils {

    // get property from system as a Boolean
    public static boolean getBooleanSystemProperty(String name, boolean def) {
        try {
            return getBoolean(getProp(System.getProperties(), name), def);
        } catch (SecurityException sex) {
            // fall through...
        }

        /*
         * If we can't get the entire System Properties object because
         * of a SecurityException, just ask for the specific property.
         */
        try {
            String value = System.getProperty(name);
            if (value == null)
                return def;
            if (def)
                return !value.equalsIgnoreCase("false");
            else
                return value.equalsIgnoreCase("true");
        } catch (SecurityException sex) {
            return def;
        }
    }

    /**
     * Get the value of the specified property.
     * If the "get" method returns null, use the getProperty method,
     * which might cascade to a default Properties object.
     */
    private static Object getProp(Properties props, String name) {
        Object val = props.get(name);
        if (val != null)
            return val;
        else
            return props.getProperty(name);
    }

    /**
     * Interpret the value object as a boolean,
     * returning def if unable.
     */
    private static boolean getBoolean(Object value, boolean def) {
        if (value == null)
            return def;
        if (value instanceof String) {
	    /*
	     * If the default is true, only "false" turns it off.
	     * If the default is false, only "true" turns it on.
	     */
            if (def)
                return !((String)value).equalsIgnoreCase("false");
            else
                return ((String)value).equalsIgnoreCase("true");
        }
        if (value instanceof Boolean)
            return ((Boolean)value).booleanValue();
        return def;
    }

}
