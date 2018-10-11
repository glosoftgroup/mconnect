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

//import com.glosoftgroup.mpesa.utils.Props;
import com.glosoftgroup.mpesa.db.MySQL;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.glosoftgroup.mpesa.utils.Logging;


/**
 *
 * @author alex
 * email: alexkiburu@gmail.com
 */
public class MpesaDaemon {

    /**
     * The worker thread that does all the work.
     */
    private transient Thread worker;
    /**
     * Flag to check if the worker thread should run.
     */
    private transient boolean working = false;

//    private static MySQL mySQL;
    /**
     * Logger for this application.
     */
    private static Logging log;
    /**
     * The main run class.
     */
    private static MpesaWorker mpesaWorker;
    /**
     * Properties instance.
     */
//    private static Props props;
    private static MySQL mySQL;

    public MpesaDaemon() {

    }

    public static void init() {
        try {
            
            log = new Logging();
            log.info("Initializing Mpesa daemon...");
            
            /**
             * Tell user on terminal the daemon is running
             */
            System.out.println("Initializing Mpesa daemon..... ");


            try {
//                mySQL = new MySQL(props.getDbHost(), props.getDbPort(),
//                        props.getDbName(), props.getDbUserName(),
//                        props.getDbPassword(), props.getDbPoolName(), 20);
                mySQL = new MySQL("localhost", "5432",
                        "restaurant", "restaurant",
                        "restaurant", "restaurant", 20);
            } catch (Exception ex) {
                Logger.getLogger(MpesaDaemon.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
            //create an object with log, mysql and props passed
            try {
                mpesaWorker = new MpesaWorker(log, mySQL);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }

    public void start() throws Exception {
        working = true;
        worker.start();
        log.info("Starting Mpesa daemon...");
    }

    @SuppressWarnings("SleepWhileInLoop")
    public void stop() throws Exception {
        log.info("Stopping Mpesa daemon...");

        working = false;
        //check current pool shutdown and wait to complete task before shutdown
        
        /**
         * Tell user on terminal the daemon is running
         */
        System.out.println("Mpesa daemon stopped..... ");

    }

    public void destroy() {
        log.info("Destroying Mpesa daemon...");
        log.info("Exiting...");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        init();
        /**
         * Tell user on terminal the daemon is running
         */
        System.out.println("Mpesa daemon running..... ");
        while (true) {
            try {
                mpesaWorker.runDaemon();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

}

