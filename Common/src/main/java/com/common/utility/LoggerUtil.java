package com.common.utility;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerUtil {
    static private FileHandler fileHandler;

    static {
        File file = new File("d:////schedule");
        if (!file.exists()){
            file.mkdirs();
        }
        String fileName = file.getAbsolutePath() + File.separator + "task_error_info" + ".log";
        try {
            fileHandler = new FileHandler(fileName, true);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String log = format.format(new Date(System.currentTimeMillis()));
                    log = log + "\n" + record.getMessage() + "\n";
                    return log  ;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public static void info(String log){
        Logger logger = Logger.getLogger("schedule");
        logger.addHandler(fileHandler);
        logger.info(log);
    }
}