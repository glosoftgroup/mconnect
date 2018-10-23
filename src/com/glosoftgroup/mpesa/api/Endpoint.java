/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glosoftgroup.mpesa.api;

/**
 *
 * @author root
 */
public class Endpoint {
    private String BASE_URL;
     
    public Endpoint() {
        /* change the BASE_URL accordingly */
        BASE_URL = "http://localhost:8090/";
    }
    
    public static Endpoint getInstance() {
        return new Endpoint();
    }

    public String getLOGIN() {
        return BASE_URL.concat("/api/auth/token/");
    }

    public String getNewMPESATransactionsURL() {
        /**
         *  This endpoint is used to poll new mpesa transactions with
         *  a status of 0
         */
        return "http://localhost:8090/api/payments/?status=0";
    }
    
    public String getTransactionStatusCallbackURL() {
        /**
         *  This endpoint is used to send the status transaction which
         *  determines if the transactions will be polled again or not
         */
        return "http://localhost:8090/api/payments/";
    }
    
    public String getBaseUrl(){
        return BASE_URL;
    }
    
}
