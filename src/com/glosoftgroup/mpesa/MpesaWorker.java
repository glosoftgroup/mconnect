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
import java.sql.SQLException;
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
        logPreString = " Mpesa worker class | ";
    }
    /* constructor class */
    MpesaWorker(Logging logging, MySQL mySQL) {
        mysql = mySQL;
        log = logging;
        logPreString = " Mpesa worker class | ";
    }
    
    /**
     * runDaemon(): starting point for daemon running, it calls doWork method
     */
    public void runDaemon() {
        String logPreString = this.logPreString + "runDaemon() | -1 | ";
        
        doWork();
        
        try{
            executeTasks();
        }catch(SQLException ex){
            log.error(logPreString + "SQLException: " + ex.getMessage());
            System.out.println(ex);
        }
    }

    /* -- doWork -- */
    private synchronized void doWork() {
        log.info("do work");
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
    
    /**
     *  Gets all the new transactions and inserts them to the database.
     * 
     *  Tasks executed:
     *       - Gets new Transactions using an endpoint
     *       - Inserts the transactions to the database
     *       - Sends the status of the insertion process with the
     *         transaction ids to an endpoint
     */
    public void executeTasks() throws SQLException{
        
        JSONArray results = Api.getInstance(log).getNewTransactions();        
        
        if(results.isEmpty())return;
        
        JSONArray ids = new JSONArray();

        /* get all the ids */
        results.stream().forEach(obj->{
            ids.add( ((JSONObject) obj).get("id").toString() );
        });
            
        Connection conn = mysql.getConnection();
        int[] result = null;
        String insertToDBtransacStatus = "success";
        /* this query brings error if same data exists, but the second one ignores the error but does not insert
        String QUERY = "INSERT INTO mpesa_transactions_mpesatransactions ("
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
        */
        
        String QUERY = "INSERT INTO mpesa_transactions_mpesatransactions ("
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
                    + "created_at, updated_at) SELECT * FROM (SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW()) "
                    + "AS tmp WHERE NOT EXISTS ("
                    + "SELECT trans_id FROM mpesa_transactions_mpesatransactions WHERE trans_id = ?) LIMIT 1";
        try {
                PreparedStatement stmt = conn.prepareStatement(QUERY);
                conn.setAutoCommit(false);	
                results.stream().forEach(obj->{
                    try{
                        JSONObject jsonObj = (JSONObject) obj;
                        stmt.setString(1,jsonObj.get("msisdn").toString());
                        stmt.setString(2,jsonObj.get("first_name").toString());
                        stmt.setString(3,jsonObj.get("middle_name").toString());
                        stmt.setString(4,jsonObj.get("last_name").toString());
                        stmt.setString(5,jsonObj.get("trans_time").toString());
                        stmt.setString(6,jsonObj.get("trans_id").toString());
                        stmt.setString(7,jsonObj.get("trans_amount").toString());
                        stmt.setString(8,jsonObj.get("org_account_balance").toString());
                        stmt.setString(9,jsonObj.get("invoice_number").toString());
                        stmt.setString(10,jsonObj.get("bill_ref_number").toString());
                        stmt.setString(11,jsonObj.get("third_party_transid").toString());
                        stmt.setString(12,jsonObj.get("business_short_code").toString());
                        stmt.setString(13,jsonObj.get("transaction_type").toString());
                        stmt.setInt(14,Integer.parseInt(jsonObj.get("status").toString()));
                        stmt.setString(15,jsonObj.get("trans_id").toString());
                        stmt.addBatch();
                    }catch (SQLException ex) {
                        log.info("error in results stream loop ", ex);
                    }
                });
                
                result = stmt.executeBatch();
                conn.commit();
                stmt.close();
                conn.close();
                System.out.println("End of Connection");
        } catch (SQLException e) {
                insertToDBtransacStatus = "error";
                log.error("SQLException ", e);
                conn.rollback();
                e.printStackTrace();
        }finally{
                if(conn!=null)
                        conn.close();
        }
        
        int rs = result == null ? 0 : result.length;
        log.info("Number of rows affected: "+ rs);
        
        /* send the ids and the status of the transactions */
        sendTransactionInsertStatus(ids, insertToDBtransacStatus);
    }
    
    /**
     *  This method sends the status of the inserted transactions
     * 
     *  @param ids JSONArray: contains all transactions ids
     *  @param status String: contains the error or success status of the transaction
     */
    public void sendTransactionInsertStatus(JSONArray ids, String status){
        /* create a new JSON object for posting */
        JSONObject postObject = new JSONObject();
        postObject.put("transaction_ids", ids);
        postObject.put("status", status);
        
        /* post transaction status and the get the return boolean value */
        Boolean isPosted = Api.getInstance(log).postTransactionStatus(postObject);
        
        if(!isPosted){
            log.error("Error posting the status of the inserted to db transactions");
        }else{
            log.info("success posting the status of the db insert transactions ");
        }
        
    }
}
