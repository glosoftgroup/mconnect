/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glosoftgroup.mpesa.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * initializes the log files.
 *
 * @author <a href="alexkiburu@gmail.com">Alex Kiburu</a>
 */
@SuppressWarnings({ "FinalClass", "ClassWithoutLogger" })
public final class Logging {
    
    // Constants
    private String HOME_PATH = System.getProperty("user.home");
    private String LOG_PATH = HOME_PATH + 
                            File.separator+"DAEMON" + 
                            File.separator + "Logs"+
                            File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    
    public static final String INFO_LOG_LEVEL = "INFO";
    public static final String ERROR_LOG_LEVEL = "ERROR";
    
    private  String INFO_LOG_FILE = LOG_PATH + File.separator + "info.log";
    private  String ERROR_LOG_FILE = LOG_PATH + File.separator + "error.log";
    
    /**
     * Maximum size of a log file.
     */
    public static final String MAX_LOG_FILE_SIZE = "1024MB";
    /**
     * Maximum number of log files.
     */
    public static final int MAX_NUM_LOGFILES = 4;
    
    Logger log = Logger.getLogger(getClass());
   
    /**
     * Info log.
     */
    private static Logger infoLog;
    /**
     * Error log.
     */
    private static Logger errorLog;
    /**
     * Loaded system properties.
     */

    /**
     * Constructor.
     *
     */
    public Logging() {
        initializeLoggers();
    }

    /**
     * Initialize the log managers.
     */
    @SuppressWarnings({
        "CallToThreadDumpStack",
        "UseOfSystemOutOrSystemErr",
        "CallToPrintStackTrace"
    })
    private void initializeLoggers() {
        
        new File(LOG_PATH).mkdirs();        
        if(!new File(LOG_PATH).exists()){
            
            INFO_LOG_FILE = "Logs" + 
                    File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + 
                    File.separator + "info.log";
            
            ERROR_LOG_FILE = "Logs" + 
                    File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + 
                    File.separator + "error.log";
        }
        
        infoLog = Logger.getLogger("infoLog");
        errorLog = Logger.getLogger("errorLog");

        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern("%d{yyyy MMM dd HH:mm:ss,SSS}: %p : %m%n");

        try {
            
            RollingFileAppender rfaInfoLog = new RollingFileAppender(layout, INFO_LOG_FILE, true);
            rfaInfoLog.setMaxFileSize(MAX_LOG_FILE_SIZE);
            rfaInfoLog.setMaxBackupIndex(MAX_NUM_LOGFILES);

            RollingFileAppender rfaErrorLog = new RollingFileAppender(layout, ERROR_LOG_FILE, true);
            rfaErrorLog.setMaxFileSize(MAX_LOG_FILE_SIZE);
            rfaErrorLog.setMaxBackupIndex(MAX_NUM_LOGFILES);

            infoLog.addAppender(rfaInfoLog);
            errorLog.addAppender(rfaErrorLog);
            
        } catch (IOException ex) {
            
            System.err.println("Failed to initialize loggers... EXITING: "
                    + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
            
        }

        infoLog.setLevel(Level.toLevel(INFO_LOG_LEVEL));
        errorLog.setLevel(Level.toLevel(ERROR_LOG_LEVEL));

    }

    /**
     * Log info messages.
     *
     * @param message the message content
     */
    public void info(final String message) {
        infoLog.info(message);
    }
    
    /**
     * Log info messages.
     *
     * @param message the message content
     * @param exception the caught error
     */  
    public void info(final String message, Throwable exception) {
        infoLog.info(message, exception);
    }

    /**
     * Log debug messages.
     *
     * @param message the message content
     */
    public void debug(final String message) {
        infoLog.debug(message);
    }
    
    /**
     * Log debug messages.
     *
     * @param message the message content
     * @param exception the caught error
     */  
    public void debug(final String message, Throwable exception) {
        infoLog.debug(message, exception);
    }

    /**
     * Log error messages.
     *
     * @param message the message content
     */
    public void error(final String message) {
        errorLog.error(message);
    }
    
    /**
     * Log error messages.
     *
     * @param message the message content
     * @param exception the caught error
     */
    public void error(final String message, Throwable exception) {
        String callerClassName = new Exception().getStackTrace()[1].getClassName();
        String callerMethodName = new Exception().getStackTrace()[1].getMethodName();
        int callerLineNumber = new Exception().getStackTrace()[1].getLineNumber();

        String formatedMessage = String.format(
                "[%s:%s.%d] ", callerClassName, callerMethodName,callerLineNumber);
        
        String msg = formatedMessage + message;
        
        errorLog.error(msg, exception);
    }

    /**
     * Log fatal error messages.
     *
     * @param message the message content
     */
    public void fatal(final String message) {
        errorLog.fatal(message);
    }
    
    /**
     * Log fatal messages.
     *
     * @param message the message content
     * @param exception the caught error
     */ 
    public void fatal(final String message, Throwable exception) {
        errorLog.info(message, exception);
    }
}