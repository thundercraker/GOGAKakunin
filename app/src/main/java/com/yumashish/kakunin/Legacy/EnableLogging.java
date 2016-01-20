package com.yumashish.kakunin.Legacy;
import com.google.api.client.http.HttpTransport;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
/**
 * Created by yumashish on 10/30/15.
 */
public class EnableLogging {

    public static void enableLogging() {
        Logger logger = Logger.getLogger(HttpTransport.class.getName());
        logger.setLevel(Level.CONFIG);
        logger.addHandler(new Handler() {

            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }

            @Override
            public void publish(LogRecord record) {
                // default ConsoleHandler will print &gt;= INFO to System.err
                if (record.getLevel().intValue() < Level.INFO.intValue()) {
                    System.out.println(record.getMessage());
                }
            }
        });
    }
}
