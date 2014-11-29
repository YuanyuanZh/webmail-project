package cs601.webmail.util;

import cs601.webmail.Configuration;
import cs601.webmail.Constants;
import org.apache.commons.io.FileUtils;
import sun.reflect.Reflection;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Created by yuanyuan on 11/25/14.
 */
public class Logger {

    private static final String DEFAULT_LOG_FILE = "webmail";
    private static final String DEFAULT_LOG_SUBFIX= ".%g.log";
    public static final String EMPTY_STRING = "";

    private static final int ONE_MB = 1024 * 1024;

    public static Logger getLogger(Class clz) {
        return getLogger(clz.getName());
    }

    // populate a child logger from `rt` logger
    // which was inherited all the settings from `rt`.
    public static Logger getLogger(String className) {
        return getLogger("rt", className);
    }

    protected static Logger getLogger(String loggerName, String clsName) {
        initLogger();

        java.util.logging.Logger log = java.util.logging.Logger.getLogger(loggerName + "." + clsName);

        return new Logger(log);
    }

    // populate a child logger from `access` logger
    // which was inherited all the settings from `access`.
    public static Logger getAccessLogger() {
        Class caller = Reflection.getCallerClass();
        return getLogger("access", caller.getName());
    }

    private synchronized static void initLogger() {

        if (initialized) {
            return;
        }

        Configuration cfg = Configuration.getDefault();
        String wordDir = cfg.getString(Configuration.WORK_DIR);

        // Try to write log to one of these paths.
        // test them one by one.
        String[] paths = new String[] {
                cfg.getString(Configuration.LOG_DIR),
                "/var/log/webmail",
                wordDir + "/logs"
        };

        File logPath = null;

        for (String p : paths) {

            if (!Strings.haveLength(p)) {
                continue;
            }

            logPath = new File(p);

            if (!logPath.exists()) {
                try {
                    FileUtils.forceMkdir(logPath);
                } catch (IOException e) {
                    // can't get this dir, skip
                    continue;
                }
            }

            // skip this if can't write in
            if (!logPath.canWrite()) {
                continue;
            }

            if (logPath.isDirectory() && logPath.canWrite()) {
                break;
            }
        }

        if (logPath == null || !logPath.isDirectory() || !logPath.canWrite()) {
            throw new IllegalStateException("Log path not available.");
        }

        if (Constants.DEBUG_MODE)
            System.out.println("[DEBUG] Logger: log path is " + logPath.getAbsolutePath());

        // create `rt` logger instance
        java.util.logging.Logger rtLogger = java.util.logging.Logger.getLogger("rt"); // run time logger;

        FileHandler fh = null;

        try {
            fh = new FileHandler(logPath.getAbsolutePath()
                    + File.separator
                    + DEFAULT_LOG_FILE + DEFAULT_LOG_SUBFIX , 100 * ONE_MB, 10, true);
        } catch (IOException e) {
            throw new IllegalStateException("Can't create rt logger", e);
        }

        fh.setLevel(Levels.ALL);
        fh.setFormatter(new MyLogFormatter());

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new MyLogFormatter());

        rtLogger.addHandler(ch);
        rtLogger.addHandler(fh);
        rtLogger.setUseParentHandlers(false);
        rtLogger.setLevel(Level.ALL);

        // create `access` logger instance
        java.util.logging.Logger accessLogger = java.util.logging.Logger.getLogger("access"); // access logger;
        try {
            fh = new FileHandler(logPath.getAbsolutePath()
                    + File.separator
                    + DEFAULT_LOG_FILE + ".access" + DEFAULT_LOG_SUBFIX , 100 * ONE_MB, 10, true);
        } catch (IOException e) {
            throw new IllegalStateException("Can't create access logger", e);
        }

        fh.setLevel(Levels.INFO);
        fh.setFormatter(new MyLogFormatter());
        accessLogger.setUseParentHandlers(false);
        accessLogger.addHandler(fh);

        initialized = true;
    }

    static boolean initialized = false;

    private Logger(java.util.logging.Logger delegate) {
        this.delegate = delegate;
    }

    private java.util.logging.Logger delegate;

    public void log(String log) {
        delegate.log(Levels.LOG, log);
    }

    public void log(String log, Throwable throwable) {
        delegate.log(Levels.LOG, log, throwable);
    }

    public void debug(String log) {
        delegate.log(Levels.DEBUG, log);
    }

    public void debug(String log, Throwable throwable) {
        delegate.log(Levels.DEBUG, log, throwable);
    }

    public void info(String log) {
        delegate.log(Level.INFO, log);
    }

    public void info(String log, Throwable throwable) {
        delegate.log(Level.INFO, log, throwable);
    }

    public void error(Throwable e) {
        delegate.log(Levels.ERROR, EMPTY_STRING, e);
    }

    public void error(String log) {
        delegate.log(Levels.ERROR, log);
    }

    public void error(String log, Throwable throwable) {
        delegate.log(Levels.ERROR, log, throwable);
    }

    // ONLY for access logger
    public void access(String log) {
        delegate.log(Levels.ACCESS, log);
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    public static class MyLogFormatter extends Formatter {

        public MyLogFormatter() {
        }

        @Override
        public String format(LogRecord record) {
            Date date = new Date();

            String d = SDF.format(date);

            String className = record.getLoggerName();
            int dotPos = className.indexOf(".");
            if (dotPos > -1) {
                className = className.substring(dotPos + 1);
            }

            StringBuffer sb=new StringBuffer();

            // if LOG, than don't print DATE and Level info,
            // just pure MESSAGE
            if (record.getLevel().getName().equals(Levels.LOG.getName())) {
                sb.append(record.getMessage()+"\n");
            } else {
                String clsn = className == null ? record.getSourceClassName() : className;
                sb.append("[" + d + "]"  + "[" + record.getLevel() + "]" + clsn+ ":" + record.getMessage()+"\n");
            }

            if(record.getThrown()!=null){
                StringWriter writer=new StringWriter();
                PrintWriter pw=new PrintWriter(writer);
                record.getThrown().printStackTrace();
                sb.append(writer.toString()).append("\n");
            }

            return sb.toString();
        }
    }

    private static class Levels extends Level {
        protected Levels(String name, int value) {
            super(name, value);
        }

        public static Level LOG = new Levels("LOG", 2000);
        public static Level DEBUG = new Levels("DEBUG", 2100);
        public static Level ERROR = new Levels("ERROR", 2200);
        public static Level ACCESS = new Levels("ACCESS", 3000);
    }

}

