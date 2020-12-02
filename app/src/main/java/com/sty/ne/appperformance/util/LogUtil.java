package com.sty.ne.appperformance.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Author: tian
 * @UpdateDate: 2020/11/30 8:56 PM
 */
public class LogUtil {

    public static int level = 3;

    private static final String FILE_FORMAT = "yyyyMMdd";

    private static final String MESSAGE_FORMAT = "MM-dd HH:mm:ss.ms";

    private static final String SUFFIX = ".log";

    private static final String SEPARATOR = ".";

    private static String logPath;

    private static final ThreadLocal<DateFormat> messageFormat = new ThreadLocal<DateFormat>();

    private static final ThreadLocal<DateFormat> fileFormat = new ThreadLocal<DateFormat>();

    private static final Executor logger = Executors.newSingleThreadExecutor();

    public static void init(String logPath, boolean isDebug, final String buildTag) {
        LogUtil.logPath = logPath;
        if (isDebug) {
            LogUtil.level = Log.VERBOSE;
        } else {
            LogUtil.level = Log.INFO;
        }
        logger.execute(new Runnable() {

            @Override
            public void run() {
                File dir = new File(LogUtil.logPath);
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if (files.length <= 5) {
                        return;
                    }
                    Arrays.sort(files, new Comparator<File>() {

                        public int compare(File f1, File f2) {
                            long diff = f1.lastModified() - f2.lastModified();
                            if (diff > 0) {
                                return 1;
                            } else if (diff == 0) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }

                        public boolean equals(Object obj) {
                            return true;
                        }

                    });
                    try {
                        for (int i = files.length - 1; i > 5; i--) {
                            files[i].delete();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
        i("Application init", buildTag);
    }

    private static final DateFormat messageFormat() {
        DateFormat format = messageFormat.get();
        if (format == null) {
            format = new SimpleDateFormat(MESSAGE_FORMAT, Locale.getDefault());
            messageFormat.set(format);
        }
        return format;
    }

    private static final DateFormat fileFormat() {
        DateFormat format = fileFormat.get();
        if (format == null) {
            format = new SimpleDateFormat(FILE_FORMAT, Locale.getDefault());
            fileFormat.set(format);
        }
        return format;
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable tr) {
        o(Log.INFO, tag, msg, tr);
    }

    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable tr) {
        o(Log.VERBOSE, tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        o(Log.ERROR, tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable tr) {
        o(Log.DEBUG, tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable tr) {
        o(Log.WARN, tag, msg, tr);
    }

    public static void o(int priority, String tag, String msg) {
        o(priority, tag, msg, null);
    }

    public static void o(final int priority, final String tag, final String msg, final Throwable tr) {
        final String time = messageFormat().format(new Date());
        final long threadId = Thread.currentThread().getId();
        logger.execute(new Runnable() {

            @Override
            public void run() {
                Log.println(priority, tag, threadId + "/" + msg + '\n' + Log.getStackTraceString(tr));
                if (level <= priority) {
                    outMessage(tag, time, msg, tr);
                }
            }
        });
    }

    private static void outMessage(String tag, String time, String msg, Throwable tr) {
        outMessage("", tag, time, msg, tr);
    }

    private static void outMessage(String cat, String tag, String time, String msg, Throwable tr) {
        if (TextUtils.isEmpty(logPath)) {
            return;
        }
        outputToFile(formatMessage(tag, time, msg, tr), getLogFilePath(cat));
    }

    private static String formatMessage(String tag, String time, String msg, Throwable tr) {
        StringBuilder sb = new StringBuilder();
        // time
        sb.append(time);
        sb.append(": ");
        // tag
        sb.append(tag);
        sb.append(": ");
        // message
        sb.append(msg);
        sb.append("\n");
        // Throwable
        if (tr != null) {
            sb.append(Log.getStackTraceString(tr));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static boolean outputToFile(String message, String path) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean written = false;
        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(path, true));
            fw.write(message);
            fw.flush();
            fw.close();
            written = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return written;
    }

    /*package*/
    static String getLogFileName(String cat) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileFormat().format(new Date()));
        if (!TextUtils.isEmpty(cat)) {
            sb.append(SEPARATOR);
            sb.append(cat);
        }
        sb.append(SUFFIX);
        return sb.toString();
    }

    private static String getLogFilePath(String cat) {
        File dir = new File(logPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return logPath + "/" + getLogFileName(cat);
    }
}