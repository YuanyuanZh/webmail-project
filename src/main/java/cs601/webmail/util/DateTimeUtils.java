package cs601.webmail.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yuanyuan on 10/25/14.
 */
public class DateTimeUtils {

    public static final String DF_DATE = "yyyy-MM-dd";
    public static final String DF_TIME = "HH:mm:ss";
    public static final String DF_DATE_AND_TIME = DF_DATE + " " + DF_TIME;
    public static final String DF_DATE_AND_TIME_US = "dd MMM,yyyy HH:mm:ss";
    public static final String DF_DATE_AND_TIME_SHORT = "dd MMM,yyyy HH:mm";

    static final SimpleDateFormat DF = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z (zzz)", Locale.ENGLISH);
    static final SimpleDateFormat DF_WITHOUT_ZZZ = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    static final SimpleDateFormat DF_WITHOUT_ZZZ_DAY = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    static final SimpleDateFormat DF_WITHOUT_Z = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);


    static final SimpleDateFormat[] SUPPORTED_DF =
            new SimpleDateFormat[] {DF, DF_WITHOUT_ZZZ, DF_WITHOUT_ZZZ_DAY, DF_WITHOUT_Z};

    static Map<String, SimpleDateFormat> OUT_DF_CACHE;

    static {
        TimeZone tz = TimeZone.getTimeZone("CST");
        DF.setTimeZone(tz);

        OUT_DF_CACHE = new HashMap<String, SimpleDateFormat>();
        OUT_DF_CACHE.put(DF_DATE_AND_TIME_US, new SimpleDateFormat(DF_DATE_AND_TIME_US));
        OUT_DF_CACHE.put(DF_DATE_AND_TIME, new SimpleDateFormat(DF_DATE_AND_TIME));
    }

    public static Date parse(String source) {
        if (source == null || source.length() == 0)
            return null;

        Date ret = null;

        for (SimpleDateFormat df : SUPPORTED_DF) {
            try {
                ret = df.parse(source);

                if (ret != null)
                    break;
            } catch (Exception e) {
                continue;
            }
        }

        return ret;
    }

    /**
     * @see #parse(String source)  Recommended.
     */
    public static Date parseDate(String dateString) throws ParseException {

        if (dateString == null || dateString.length() == 0)
            return null;

        boolean hasZZZ = dateString.indexOf("CST") > -1 || dateString.indexOf("UTC") > -1;
        boolean hasDay = dateString.indexOf(",") > -1;

        if (hasDay && hasZZZ) {
            return DF.parse(dateString);
        }
        else if (hasDay) {
            return DF_WITHOUT_ZZZ.parse(dateString);
        }
        else {
            return DF_WITHOUT_ZZZ_DAY.parse(dateString);
        }
    }

    @Deprecated
    public static Date parseCSTDate(String cstDate) throws ParseException {

        if (cstDate == null || cstDate.length() == 0)
            return null;

        return DF.parse(cstDate);
    }

    public static String format(Date date, String pattern) {

        if (date == null)
            throw new IllegalArgumentException();

        if (pattern == null || pattern.length() == 0)
            throw new IllegalArgumentException();

        SimpleDateFormat df = OUT_DF_CACHE.get(pattern);

        if (df == null) {
            df = new SimpleDateFormat(pattern);
            OUT_DF_CACHE.put(pattern, df);
        }

        return df.format(date);
    }

}
