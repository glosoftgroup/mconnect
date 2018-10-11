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
        BASE_URL = "http://alexkiburu.pythonanywhere.com/";
    }
    
    public static Endpoint getInstance() {
        return new Endpoint();
    }

    public String getLOGIN() {
        return BASE_URL.concat("/api/auth/token/");
    }

    public String getNewMPESATransactionsURL() {
//        return BASE_URL.concat("/confirmation/");
        return "http://127.0.0.1:8000/mpesa/transactions/api/list/test/?status=0";
    }  
    
    public String getBaseUrl(){
        return BASE_URL;
    }
    
}
