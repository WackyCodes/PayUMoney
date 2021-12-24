package com.wackycodes.payulib;


import java.io.Serializable;

@SuppressWarnings("serial")
public class PayModel implements Serializable {
    public PayModel() {
    }

    //Payment Param

    private boolean isProduction = true; // -- Not Mandatory!
    private String amount;  // Mandatory!
    private String productInfo; // Mandatory!
    private String key; // Mandatory!
    private String saltKey; // Mandatory!
    private String userPhone; // -- Not Mandatory!
    private String transactionId;// Mandatory!
    private String userName; // Mandatory!
    private String userEmail;// Mandatory!
    private String sUrl; // Mandatory!
    private String fUrl; // Mandatory!
    private String userCredential; // -- Not Mandatory!
    private String merchantAccessKey; // It's Same as key!


    private String udf1 = "udf1";
    private String udf2 = "udf2";
    private String udf3 = "udf3";
    private String udf4 = "udf4";
    private String udf5 = "udf5";

    // ---- For SI ------
    private boolean isFreeTrial = false;
//    private String amount;  // Mandatory!
    private String billingCurrency = "INR";
    private String startDate; // "2021-12-31"
    private String endDate; // "2021-12-31"
    private String remarks;


    public boolean isProduction() {
        return isProduction;
    }

    public void setProduction(boolean production) {
        isProduction = production;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSaltKey() {
        return saltKey;
    }

    public void setSaltKey(String saltKey) {
        this.saltKey = saltKey;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserFirstName() {
        String firstName = userName;
        if (userName != null){
            firstName = userName.split(" ")[0];
        }
        if (firstName != null && !firstName.equals("")){
            return firstName;
        }else if ( userName != null && !userName.equals("") ){
            return userName;
        }else return null;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getsUrl() {
        return sUrl;
    }

    public void setsUrl(String sUrl) {
        this.sUrl = sUrl;
    }

    public String getfUrl() {
        return fUrl;
    }

    public void setfUrl(String fUrl) {
        this.fUrl = fUrl;
    }

    public String getUserCredential() {
        if (userCredential == null){
            // "key"+":john@yopmail.com"
            userCredential = merchantAccessKey + ":" + userEmail;
        }
        return merchantAccessKey + ":" + userEmail;
    }

    public void setUserCredential(String userCredential) {
        this.userCredential = userCredential;
    }

    public String getMerchantAccessKey() {
        return merchantAccessKey;
    }

    public void setMerchantAccessKey(String merchantAccessKey) {
        this.merchantAccessKey = merchantAccessKey;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

    public String getUdf4() {
        return udf4;
    }

    public void setUdf4(String udf4) {
        this.udf4 = udf4;
    }

    public String getUdf5() {
        return udf5;
    }

    public void setUdf5(String udf5) {
        this.udf5 = udf5;
    }

    //---------------------------------------------------


    public boolean isFreeTrial() {
        return isFreeTrial;
    }

    public void setFreeTrial(boolean freeTrial) {
        isFreeTrial = freeTrial;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBillingCurrency() {
        return billingCurrency;
    }

    public void setBillingCurrency(String billingCurrency) {
        this.billingCurrency = billingCurrency;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
