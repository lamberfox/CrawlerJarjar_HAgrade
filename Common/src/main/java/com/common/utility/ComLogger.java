package com.common.utility;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;

/**
 * @auther tonyjarjar
 * @create 2017/9/6
 */
public class ComLogger {

    private static Logger logger() {
        // 最原始被调用的堆栈对象
        StackTraceElement caller = findCaller();
        if (null == caller) {
            return Logger.getLogger(ComLogger.class);
        }
        Logger log = Logger.getLogger("[" + caller.getClassName() + "." + caller.getMethodName() + "()] [Line: " + caller.getLineNumber() + "]");
        log.setAdditivity(false);
        try {
            log.addAppender(new ConsoleAppender(new PatternLayout("%r [%p] [%d{yyyy-MM-dd HH:mm:ss}] %c- %m%n")));
            log.addAppender(new FileAppender(new PatternLayout("%r [%p] [%d{yyyy-MM-dd HH:mm:ss}] %c- %m%n"),"F:\\log.txt",true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return log;
    }

    public static StackTraceElement findCaller() {
        // 获取堆栈信息
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        if (null == callStack) return null;

        StackTraceElement caller = null;

        String logClassName = ComLogger.class.getName();
        // 循环遍历到日志类标识
        boolean isEachLogClass = false;

        // 遍历堆栈信息，获取出最原始被调用的方法信息
        for (StackTraceElement s : callStack) {
            // 遍历到日志类
            if (logClassName.equals(s.getClassName())) {
                isEachLogClass = true;
            }

            if (isEachLogClass) {
                if (!logClassName.equals(s.getClassName())) {
                    isEachLogClass = false;
                    caller = s;
                    break;
                }
            }
        }

        return caller;
    }

    //重写log的输出方法。
    public static void trace(String msg) {
        trace(msg, null);
    }

    public static void trace(String msg, Throwable e) {
        logger().trace(msg, e);
    }

    public static void debug(String msg) {
        debug(msg, null);
    }

    public static void debug(String msg, Throwable e) {
        logger().debug(msg, e);
    }

    public static void info(String msg) {
        info(msg, null);
    }

    public static void info(String msg, Throwable e) {
        logger().info(msg, e);
        logger().removeAllAppenders();
    }

    public static void warn(String msg) {
        warn(msg, null);
    }

    public static void warn(String msg, Throwable e) {
        logger().warn(msg, e);
        logger().removeAllAppenders();
    }

    public static void error(String msg) {
        error(msg, null);
    }

    public static void error(String msg, Throwable e) {
        logger().error(msg, e);
        logger().removeAllAppenders();
    }
}
