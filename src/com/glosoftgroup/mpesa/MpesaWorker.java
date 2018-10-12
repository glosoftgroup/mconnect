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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
            /**
             *  fetch the new transactions
             *  if failure of any kind post a response with the payload and the status
             *  enclose the code inside a try catch block to get the error
             */
            
            
            /**  step 1: make api call to get the new transactions from the remote
             *   endpoint with status 0 */
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
            
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery( "SELECT * FROM userprofile_user");
//            while (rs.next())
//            {
//                log.info(rs.getString("id"));
//            }
            
            
            /**
             * insert into the db
             */
            
            /**
             *  Insert only once if it exists
             *  INSERT INTO table_listnames (name, address, tele)
                SELECT * FROM (SELECT 'Rupert', 'Somewhere', '022') AS tmp
                WHERE NOT EXISTS (
                    SELECT name FROM table_listnames WHERE name = 'Rupert'
                ) LIMIT 1;
             */
            String query = "INSERT INTO mpesa_transactions_mpesatransactions ("
                    + "msisdn, "
                    + "first_name, " 
                    + "middle_name, " 
                    + "last_name, "
                    + "trans_time, " 
                    + "trans_id, "
                    + "trans_amount, " 
                    + "org_account_balance, "
                    + "invoice_number, "
                    + "bill_ref_number, "
                    + "third_party_transid, "
                    + "business_short_code, " 
                    + "transaction_type, "
                    + "status, "
                    + "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            PreparedStatement ps = conn.prepareStatement(query);
            
            results.stream().forEach(obj->{
                try{
                    JSONObject jsonObj = (JSONObject) obj;
                    ps.setString(1,jsonObj.get("msisdn").toString());
                    ps.setString(2,jsonObj.get("first_name").toString());
                    ps.setString(3,jsonObj.get("middle_name").toString());
                    ps.setString(4,jsonObj.get("last_name").toString());
                    ps.setString(5,jsonObj.get("trans_time").toString());
                    ps.setString(6,jsonObj.get("trans_id").toString());
                    ps.setString(7,jsonObj.get("trans_amount").toString());
                    ps.setString(8,jsonObj.get("org_account_balance").toString());
                    ps.setString(9,jsonObj.get("invoice_number").toString());
                    ps.setString(10,jsonObj.get("bill_ref_number").toString());
                    ps.setString(11,jsonObj.get("third_party_transid").toString());
                    ps.setString(12,jsonObj.get("business_short_code").toString());
                    ps.setString(13,jsonObj.get("transaction_type").toString());
                    ps.setInt(14,Integer.parseInt(jsonObj.get("status").toString()));
                    ps.addBatch();
                }catch (SQLException ex) {
                    log.info("error inserting into db", ex);
                }
            }); 
            ps.executeBatch();
            ps.close();
            /* -- end of inserting -- */
//            rs.close();
//            st.close();
            conn.close();
            log.info("End of Connection");
            
        }catch (SQLException ex) {
            /**
             * Response: {
                           succeeded: [2,3,4],
                           failed: [5,6,7],
                           error: "db error"
                         }
             */
            log.error(logPreString + "SQLException: " + ex.getMessage());
        }
        System.out.println("end of iniserting query");
       
    }
}
