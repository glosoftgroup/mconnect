/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glosoftgroup.mpesa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.glosoftgroup.mpesa.api.Api;
import com.glosoftgroup.mpesa.db.MySQL;
import com.glosoftgroup.mpesa.models.Transaction;
import com.glosoftgroup.mpesa.utils.Logging;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.simple.JSONArray;

/**
 *
 * @author kiburu
 */
public class MpesaWorker {
    /**
     * Logger for this application.
     */
    private transient Logging log;
    /**
     * New transactions array list
     */
    private static ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    /**
     * The string to append before the string being logged.
     */
    private static transient MySQL mysql;
    
    private String logPreString;
    /**
     * Flag to check if current pool is completed.
     */
    private transient boolean isCurrentPoolShutDown = false;
    
    /* constructor class */
    MpesaWorker(Logging logging) {
        log = logging;
        logPreString = " Philips worker class | ";
    }
    /* constructor class */
    MpesaWorker(Logging logging, MySQL mySQL) {
        mysql = mySQL;
        log = logging;
        logPreString = " Philips worker class | ";
    }
    
    /**
     * runDaemon(): starting point for daemon running, it calls doWork method
     */
    public void runDaemon() {
        String logPreString = this.logPreString + "runDaemon() | -1 | ";
        doWork();
        executeTasks();
    }

    /* -- doWork -- */
    private synchronized void doWork() {
        log.info("do work");
//        Api.getInstance(log).getNewTransactions(transactions);
    }
    
    /* -- Sleep-Time -- */
    public void doWait(long t) {
        String logPreString = this.logPreString + "doWait() | -1 | ";
        try {
            Thread.sleep(t);
        } catch (InterruptedException ex) {
            log.error(logPreString
                    + "Thread could not sleep for the specified time");
        }
    }
    
    
    private void executeTasks() {
        
        log.info("execute tasks");
        
        try{
            /*  step 1: make api call to get the new transactions with status 0 */
            JSONArray results = Api.getInstance(log).getNewTransactions();
            log.info(results.toJSONString());
            
            /** 
             *  step 2: on status 0, add the new transactions and send back response with 
             *  transaction_ids and status be 1 for those successfully added else change
             *  status to 0
             */
            
            /**
             *  testing postgres connection 
             *
             */
            Connection conn = mysql.getConnection();
            log.info("Connected to the PostgreSQL server successfully.");
            
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery( "SELECT * FROM userprofile_user");
            while (rs.next())
            {
                log.info(rs.getString("id"));
            }
            rs.close();
            st.close();
            conn.close();
            log.info("End of Connection");
            
        }catch (SQLException ex) {
            log.error(logPreString + "SQLException: " + ex.getMessage());
        }
       
    }
}
