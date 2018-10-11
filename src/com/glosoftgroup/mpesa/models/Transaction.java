package com.glosoftgroup.mpesa.models;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.json.simple.JSONObject;

/**
 *
 * @author babaviz
 */
public class Transaction {
    
    private String TransID;
    private String TransTime;
    private String BusinessShortCode;
    private String TransactionType;
    private String InvoiceNumber;
    private String TransAmount;
    private String ThirdPartyTransID;
    private String OrgAccountBalance;
    private String BillRefNumber;
    private String MSISDN;
    private String FirstName;
    private String MiddleName;
    private String LastName;

    public Transaction(
            String TransID, String TransTime, 
            String BusinessShortCode, 
            String TransactionType, String InvoiceNumber, String TransAmount, 
            String ThirdPartyTransID, String OrgAccountBalance, 
            String BillRefNumber, String MSISDN,
            String FirstName, 
            String MiddleName, 
            String LastName) 
    {
        
        this.TransID = TransID;
        this.TransTime = TransTime;
        this.BusinessShortCode = BusinessShortCode;
        this.TransactionType = TransactionType;
        this.InvoiceNumber = InvoiceNumber;
        this.TransAmount = TransAmount;
        this.ThirdPartyTransID = ThirdPartyTransID;
        this.OrgAccountBalance = OrgAccountBalance;
        this.BillRefNumber = BillRefNumber;
        this.MSISDN = MSISDN;
        this.FirstName = FirstName;
        this.MiddleName = MiddleName;
        this.LastName = LastName;
    
    }

    public String getTransID() {
        return TransID;
    }

    public void setTransID(String TransID) {
        this.TransID = TransID;
    }

    public String getTransTime() {
        return TransTime;
    }

    public void setTransTime(String TransTime) {
        this.TransTime = TransTime;
    }

    public String getBusinessShortCode() {
        return BusinessShortCode;
    }

    public void setBusinessShortCode(String BusinessShortCode) {
        this.BusinessShortCode = BusinessShortCode;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String TransactionType) {
        this.TransactionType = TransactionType;
    }

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setInvoiceNumber(String InvoiceNumber) {
        this.InvoiceNumber = InvoiceNumber;
    }

    public String getTransAmount() {
        return TransAmount;
    }

    public void setTransAmount(String TransAmount) {
        this.TransAmount = TransAmount;
    }

    public String getThirdPartyTransID() {
        return ThirdPartyTransID;
    }

    public void setThirdPartyTransID(String ThirdPartyTransID) {
        this.ThirdPartyTransID = ThirdPartyTransID;
    }

    public String getOrgAccountBalance() {
        return OrgAccountBalance;
    }

    public void setOrgAccountBalance(String OrgAccountBalance) {
        this.OrgAccountBalance = OrgAccountBalance;
    }

    public String getBillRefNumber() {
        return BillRefNumber;
    }

    public void setBillRefNumber(String BillRefNumber) {
        this.BillRefNumber = BillRefNumber;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String MiddleName) {
        this.MiddleName = MiddleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }
 
    
    
    public static Transaction fromJSON(JSONObject jSONObject){
        Transaction transaction = new Transaction(
                jSONObject.get("TransID").toString(),
                jSONObject.get("TransTime").toString(),
                jSONObject.get("BusinessShortCode").toString(),
                jSONObject.get("TransactionType").toString(),
                jSONObject.get("InvoiceNumber").toString(),
                jSONObject.get("TransAmount").toString(),
                jSONObject.get("ThirdPartyTransID").toString(),
                jSONObject.get("OrgAccountBalance").toString(),
                jSONObject.get("BillRefNumber").toString(),
                jSONObject.get("MSISDN").toString(),
                jSONObject.get("FirstName").toString(),
                jSONObject.get("MiddleName").toString(),
                jSONObject.get("LastName").toString()
        );
        
        return transaction;
    }
}

