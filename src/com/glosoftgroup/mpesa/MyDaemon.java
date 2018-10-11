/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glosoftgroup.mpesa;

/**
 *
 * @author kiburu
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import com.glosoftgroup.mpesa.utils.Logging;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

public class MyDaemon implements Daemon, Runnable {
    
    /**
     * The worker thread that does all the work.
     */
    private transient Thread worker;
    /**
     * Flag to check if the worker thread should run.
     */
    private transient boolean working = false;    
    /**
     * Logger for this application.
     */
    private transient Logging log;
    /**
     * The main run class.
     */
    private transient MpesaWorker mw;

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        System.out.println("MyDaemon init...");
        worker = new Thread(this);

        log.info("Initializing Phillips daemon...");
        
        /**
         * Tell user on terminal the daemon is running
         */
        System.out.println("Initializing Phillips daemon..... ");
        
        try{
           /* Make connection to Postgres */
            /* mySQL = new MySQL(props.getDbHost(), props.getDbPort(),
                props.getDbName(), props.getDbUserName(),
                props.getDbPassword(), props.getDbPoolName(), 20);*/
        } catch (Exception ex) {
            Logger.getLogger(MyDaemon.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
        //create an object with log, mysql and props passed
        
        //pw = new PhillipsWorker(props, log, mySQL);
    }

    @Override
    public void start() throws Exception {
        System.out.println("MyDaemon start...");
        working = true;
        worker.start();
        log.info("Starting MyDaemon...");
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void stop() throws Exception {
        System.out.println("MyDaemon stopped..... ");
        log.info("Stopping MyDaemon...");

        working = false;
        //check current pool shutdown and wait to complete task before shutdown
    }

    @Override
    public void destroy() {
        System.out.println("MyDaemon destroy...");
        log.info("Destroying Phillips daemon...");
        log.info("Exiting...");
    }
    
    @Override
    @SuppressWarnings({ "SleepWhileHoldingLock", "SleepWhileInLoop" })
    public void run() {
        
        /**
         * Tell user on terminal the daemon is running
         */
        System.out.println("Phillips daemon running..... ");
        
         while (working) {
            log.info("");
            log.info("");
            
            try {
                mw.runDaemon();
            } catch (Exception ex) {
                log.fatal("Error occured: " + ex.getMessage());
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }            
            
         }
    }
}
