package com.wackycodes.payulib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUBillingCycle;
import com.payu.base.models.PayUPaymentParams;
import com.payu.base.models.PayUSIParams;
import com.payu.base.models.PayuBillingLimit;
import com.payu.base.models.PayuBillingRule;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.custombrowser.Bank;
import com.payu.custombrowser.PayUWebChromeClient;
import com.payu.custombrowser.PayUWebViewClient;

import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class PaymentQueryActivity extends AppCompatActivity {

    public static final String KEY_DATA_MODEL = "KEY_DATA_MODEL";

    public static final String KEY_RES_OBJ = "KEY_RES_OBJ";
    public static final String KEY_RES_IS_TXN_INIT = "KEY_RES_IS_TXN_INIT";
    public static final String KEY_RES_ERROR = "KEY_RES_ERROR";

    public static final String KEY_RES_CODE = "KEY_RES_CODE";
    public static final int KEY_RES_CODE_CANCELLED = 100;
    public static final int KEY_RES_CODE_SUCCESS = 200;
    public static final int KEY_RES_CODE_FAILED = 300;
    public static final int KEY_RES_CODE_ERROR = 400;

    private String merchantAccessKey = "lbMgCF";
    private String merchantSecretKey = "lbMgCF";

    //Test Key and Salt
    private String testKey = "gtKFFx";
    private String testSalt = "wia56q6O";

    //Prod Key and Salt
    private String prodKey = "3TnMpV";
    private String prodSalt = "g0nGFe03";

    private String surl = "https://ko-fi.com/wackycodes";
    private String furl = "https://payuresponse.firebaseapp.com/failure";

    private PayModel payModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payquery);

        payModel = (PayModel) getIntent().getSerializableExtra(KEY_DATA_MODEL);
//        responseListener =(PayUResponseListener) getIntent().getSerializableExtra( KEY_LISTENER );
        if (isValidData()){
            setPayUParams();
        }else{
            // Something is missing!
//            sendResponseData(KEY_RES_CODE_ERROR, "", true, "Something is missing!");
            setPyUTest();
        }

    }

    private void sendResponseData(int responseCode, String payuResponse, boolean onTxnCancel_isTxnInitiated, String errorResponse) {
        /**
         * @Document
         * int responseCode : Show the response Type
         * Object response : On Success And failed Data Response
         * boolean onTxnCancel_isTxnInitiated : Cancel Transaction , To Check Whether txn initiated or not!
         * ErrorResponse errorResponse : Error Response.
         */
        Log.e("RESPONSE", "Response : " + errorResponse );

        Intent intent = new Intent();
        intent.putExtra(KEY_RES_CODE, responseCode);
        intent.putExtra(KEY_RES_IS_TXN_INIT, onTxnCancel_isTxnInitiated);
        intent.putExtra(KEY_RES_OBJ, payuResponse);
        intent.putExtra(KEY_RES_ERROR, errorResponse);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void showLog(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e("PaymentQuery", "Response : " + msg);
    }

    /**
     * Step 2: Build the Payment Params
     */

    /**
     * For Recurring Payments(SI)
     * If you are integrating SI, then generate below payment params additionally
     */
    private PayUSIParams getPayUSiDetails() {
        PayUSIParams siDetails = new PayUSIParams.Builder()
                .setIsFreeTrial(payModel.isFreeTrial()) //set it to true for free trial. Default value is false
                .setBillingAmount(payModel.getAmount())
                .setBillingCycle(PayUBillingCycle.ONCE)
                .setBillingCurrency(payModel.getBillingCurrency())
                .setBillingInterval(1)
                .setPaymentStartDate(payModel.getStartDate())
                .setPaymentEndDate(payModel.getEndDate()) // "2021-12-31"
                .setBillingRule(PayuBillingRule.MAX)
                .setBillingLimit(PayuBillingLimit.ON)
                .setRemarks(payModel.getRemarks())
                .build();

        return siDetails;
    }

    /**
     * Additional Params..
     */
    private void setPayUParams() {

        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, payModel.getUdf1());
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, payModel.getUdf2());
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, payModel.getUdf3());
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, payModel.getUdf4());
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, payModel.getUdf5());

        //Below params should be passed only when integrating Multi-currency support
        // Please pass your own Merchant Access Key below as provided by your Key Account Manager at PayU.
        additionalParams.put(PayUCheckoutProConstants.CP_MERCHANT_ACCESS_KEY, payModel.getMerchantAccessKey());

        // Passing Static Hash !
        String hashData1 = payModel.getKey() + "|" + PayUCheckoutProConstants.CP_VAS_FOR_MOBILE_SDK + "|" + PayUCheckoutProConstants.CP_DEFAULT + "|";
        String hashData2 = payModel.getKey() + "|" + PayUCheckoutProConstants.CP_PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK + "|" + payModel.getUserCredential() + "|";

        additionalParams.put(PayUCheckoutProConstants.CP_VAS_FOR_MOBILE_SDK,
                PayUHashUtil.generateHashFromSDK(hashData1, payModel.getSaltKey(), null));
        additionalParams.put(PayUCheckoutProConstants.CP_PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK,
                PayUHashUtil.generateHashFromSDK(hashData2, payModel.getSaltKey(), null));

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(payModel.getAmount())
//                .setIsProduction(payModel.isProduction())
                .setProductInfo(payModel.getProductInfo())
                .setKey(payModel.getKey())
                .setPhone(payModel.getUserPhone())
                .setTransactionId(payModel.getTransactionId())
                .setFirstName(payModel.getUserFirstName())
                .setEmail(payModel.getUserEmail())
                .setSurl(payModel.getsUrl())
                .setFurl(payModel.getfUrl())
                .setUserCredential(payModel.getUserCredential())
                .setAdditionalParams(additionalParams);
//                .setPayUSIParams( getPayUSiDetails() );

        PayUPaymentParams payUPaymentParams = builder.build();
        /*
        return PayUPaymentParams.Builder().setAmount(binding.etAmount.text.toString())
            .setIsProduction(binding.radioBtnProduction.isChecked)
            .setKey(binding.etKey.text.toString())
            .setProductInfo("Macbook Pro")
            .setPhone(binding.etPhone.text.toString())
            .setTransactionId(System.currentTimeMillis().toString())
            .setFirstName("Abc")
            .setEmail(email)
            .setSurl(binding.etSurl.text.toString())
            .setFurl(binding.etFurl.text.toString())
            .setUserCredential(binding.etUserCredential.text.toString())
            .setAdditionalParams(additionalParamsMap)
            .setPayUSIParams(siDetails)
            .build()
         */

        // Finally init Payment..!
        initPayment(payUPaymentParams);
    }

    private void setPyUTest( ){
        payModel = new PayModel();
        payModel.setSaltKey( testSalt );
        payModel.setMerchantAccessKey( testKey );
        payModel.setKey( testKey );
        payModel.setProduction( false );

//        payModel.setSaltKey( prodSalt );
//        payModel.setMerchantAccessKey( prodKey );
//        payModel.setKey( prodKey );
//        payModel.setProduction( true );
//        payModel.setsUrl( "https://secure.payu.in/_payment" );

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount("1")
                .setIsProduction(payModel.isProduction())
                .setKey(payModel.getKey())
                .setProductInfo("Macbook Pro")
                .setPhone("7999597410")
                .setTransactionId(String.valueOf(System.currentTimeMillis()))
                .setFirstName("Wacky")
                .setEmail("wackycodes@gmail.com")
                .setSurl(surl)
                .setFurl(furl)
                .setUserCredential( payModel.getKey() + ":wackycodes@gmail.com" );
//                .setAdditionalParams(additionalParamsMap)
//                .setPayUSIParams(siDetails);

        // Finally init Payment..!
        initPayment(builder.build());
    }

    /**
     * Initiate Payment...!
     */
    private void initPayment(PayUPaymentParams payUPaymentParams) {
        PayUCheckoutPro.open(
                this,
                payUPaymentParams,
                new PayUCheckoutProListener() {

                    @Override
                    public void onPaymentSuccess(Object response) {
                        //Cast response object to HashMap
                        HashMap<String, Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
//                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                        showLog("Payment Success : " + payuResponse);
                        sendResponseData(KEY_RES_CODE_SUCCESS, payuResponse, true, "Transaction Success!");
                    }

                    @Override
                    public void onPaymentFailure(Object response) {
                        //Cast response object to HashMap
                        HashMap<String, Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
//                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                        showLog("Payment Failed : " + payuResponse);
                        sendResponseData(KEY_RES_CODE_FAILED, payuResponse, true, "Transaction failed!");
                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
                        showLog("Payment Cancelled ! ");
                        sendResponseData(KEY_RES_CODE_CANCELLED, "", isTxnInitiated, "Your transaction has been cancelled!");
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                        showLog("Payment Failed ! Error : " + errorMessage);
                        sendResponseData(KEY_RES_CODE_ERROR, "", true, errorMessage);
                    }

//                    @Override
//                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
//                        //For setting webview properties, if any. Check Customized Integration section for more details on this
//                        showLog("WebView : " + o );
//                        if (webView != null) {
//                            webView.setWebChromeClient(new CheckoutProWebChromeClient((Bank)o));
//                        }
//                    }
                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        showLog("WebView : " + o );
                        if (webView != null) {
                            webView.setWebViewClient(new CheckoutProWebViewClient((Bank)o, payModel.getMerchantAccessKey() ));
                        }
                    }


                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                            // Do not generate hash from local,
                            // it needs to be calculated from server side only.
                            // TODO : Here, hashString contains hash created from your server side.
                            //  calculate SDH-512 hash using hashData and salt
                            String hash = "hashString";
                            if (hashName.equalsIgnoreCase(PayUCheckoutProConstants.CP_LOOKUP_API_HASH)) {
                                //: Calculate HmacSHA1 hash using the hashData and merchant secret key ....
                                hash = PayUHashUtil.generateHashFromSDK(
                                        hashData,
                                        payModel.getSaltKey(), payModel.getMerchantAccessKey()
                                );
                            } else {
                                hash = PayUHashUtil.generateHashFromSDK(
                                        hashData,
                                        payModel.getSaltKey(), null
                                );
                            }

                            HashMap<String, String> dataMap = new HashMap<>();
                            dataMap.put(hashName, hash);
                            hashGenerationListener.onHashGenerated(dataMap);
                        }
                    }
                }
        );

    }


    // Set Web

    /**
     * Step 3: Hash Generation for Checkout Pro SDK
     * Always generate the hashes on your server. Do not generate the hashes locally in your app as it will compromise the security of the transactions.
     * <p>
     * Static Hashes (Mandatory) – These hashes can be passed to SDK during integration and do not change between transactions.
     * Dynamic Hashes (Mandatory) – These hashes must be generated at runtime for each transaction and will vary based on the transaction parameters.
     *
     * @Documentation for Hash Formula
     * @payment_related_details_for_mobile_sdk =>  <key>|payment_related_details_for_mobile_sdk|<userCredential>|<salt>
     * @payment =>    <key>|<txnid>|<amount>|<productinfo>|<firstname>|<email>|<udf1>|<udf2>|<udf3>|<udf4>|<udf5>||||||<salt
     * <p>
     * After setting the values in above formula generate sha512 over it and pass the same in additional param.
     */

    /*

    E/FAILED: payuResponse :

    {
       "id":14179145592,
       "mode":"UPI",
       "status":"failure",
       "unmappedstatus":"userCancelled",
       "key":"3TnMpV",
       "txnid":"1635400973922",
       "transaction_fee":"1.00",
       "amount":"1.00",
       "discount":"0.00",
       "additional_charges":"0.59",
       "addedon":"2021-10-28 11:37:02",
       "productinfo":"My Products",
       "firstname":"Rahul",
       "email":"wacky@gmail.com",
       "phone":"8602619647",
       "udf1":"udf1",
       "udf2":"udf2",
       "udf3":"udf3",
       "udf4":"udf4",
       "udf5":"udf5",
       "hash":"0126af33534babb1a1dfce2042da04fade958d58900a03e5a6d26c11a28318f8787414994103b5993d6ef6d765f303091093f39301e50db51a22c670a8b42c3c",
       "field1":"8602619647@ybl",
       "field2":"130166767849",
       "field3":"8602619647@ybl",
       "field4":"Mr SHAILENDRA  LODHI SO JODHAN SINGH LODHI",
       "field5":"AXI9eb3c9a6a1fe4d50ba80be1cb4126055",
       "field7":"TRANSACTION DECLINED BY CUSTOMER|ZA",
       "field9":"Transaction declined by customer|Completed Using Callback",
       "payment_source":"payu",
       "PG_TYPE":"UPI-PG",
       "bank_ref_no":"130166767849",
       "ibibo_code":"UPI",
       "error_code":"E905",
       "Error_Message":"Transaction declined",
       "is_seamless":1,
       "surl":"https://payuresponse.firebaseapp.com/success",
       "furl":"https://payuresponse.firebaseapp.com/failure"
    }



     merchantResponse :

    {
       "mihpayid":"14179145592",
       "mode":"UPI",
       "status":"failure",
       "unmappedstatus":"userCancelled",
       "key":"3TnMpV",
       "txnid":"1635400973922",
       "amount":"1.00",
       "discount":"0.00",
       "additionalCharges":"0.59",
       "net_amount_debit":"0.00",
       "addedon":"2021-10-28 11:37:02",
       "productinfo":"My Products",
       "firstname":"Rahul",
       "lastname":"",
       "address1":"",
       "address2":"",
       "city":"",
       "state":"",
       "country":"",
       "zipcode":"",
       "email":"wacky@gmail.com",
       "phone":"8602619647",
       "udf1":"udf1",
       "udf2":"udf2",
       "udf3":"udf3",
       "udf4":"udf4",
       "udf5":"udf5",
       "udf6":"",
       "udf7":"",
       "udf8":"",
       "udf9":"",
       "udf10":"",
       "hash":"0126af33534babb1a1dfce2042da04fade958d58900a03e5a6d26c11a28318f8787414994103b5993d6ef6d765f303091093f39301e50db51a22c670a8b42c3c",
       "field1":"8602619647@ybl",
       "field2":"130166767849",
       "field3":"8602619647@ybl",
       "field4":"Mr SHAILENDRA  LODHI SO JODHAN SINGH LODHI",
       "field5":"AXI9eb3c9a6a1fe4d50ba80be1cb4126055",
       "field6":"",
       "field7":"TRANSACTION DECLINED BY CUSTOMER|ZA",
       "field8":"",
       "field9":"Transaction declined by customer|Completed Using Callback",
       "payment_source":"payu",
       "PG_TYPE":"UPI-PG",
       "bank_ref_num":"130166767849",
       "bankcode":"UPI",
       "error":"E905",
       "error_Message":"Transaction declined",
       "device_type":"1"
    }

    //------  NEFTRTGS : Response --------------------------------------------------------------------

    {
       "id":"14180081413",
       "mode":"NEFTRTGS",
       "status":"failure",
       "unmappedstatus":"failed",
       "key":"3TnMpV",
       "txnid":"1635411715921",
       "transaction_fee":"1.00",
       "amount":"1.00",
       "addedon":"2021-10-28 14:32:01",
       "productinfo":"My Products",
       "firstname":"Rahul",
       "email":"wacky@gmail.com",
       "phone":"8602619647",
       "udf1":"udf1",
       "udf2":"udf2",
       "udf3":"udf3",
       "udf4":"udf4",
       "udf5":"udf5",
       "hash":"5803157839df5336f49524c5d75f71341039aad2286c150454ed51a62c6e3085b9456965c75f103e4f0c814eea79b0a61ae4640d873ffd646e589a3a36492f26",
       "field9":"The details posted are incorrect so redirecting to merchant site.",
       "payment_source":"payu",
       "ibibo_code":"EFTAXIS",
       "error_code":"E1101",
       "Error_Message":"Transaction failed due to invalid params shared by the merchant",
       "is_seamless":1,
       "surl":"https://payuresponse.firebaseapp.com/success",
       "furl":"https://payuresponse.firebaseapp.com/failure"
    }
     */

    //---------------------------------------------------------------------------------
    private boolean isValidData() {
        String errorMessage = " is empty or Missing! ";

        if (payModel == null){
            return false;
        }

        if (!isNotEmpty( payModel.getAmount() )){
            showLog( "Amount " + errorMessage );
            return false;
        }
        if (!isNotEmpty( payModel.getProductInfo() )){
            showLog( "Product Info " + errorMessage );
            return false;
        }
        if (!isNotEmpty( payModel.getKey() )){
            showLog( "key " + errorMessage );
            return false;
        }
        if (!isNotEmpty( payModel.getSaltKey() )){
            showLog( "saltKey " + errorMessage );
            return false;
        }
        if (!isNotEmpty( payModel.getTransactionId() )){
            showLog( "transactionId " + errorMessage );
            return false;
        }
        if (!isNotEmpty( payModel.getUserFirstName() )){
            showLog( "userName " + errorMessage );
            return false;
        }
        if (!isNotEmpty( payModel.getUserEmail() )){
            showLog( "userEmail " + errorMessage );
            return false;
        }
        if (!isNotEmpty( payModel.getsUrl() )){
            showLog( "sUrl " + errorMessage );
            return false;
        }
        if (!isNotEmpty( payModel.getfUrl() )){
            showLog( "fUrl " + errorMessage );
            return false;
        }

        return true;
    }
    private boolean isNotEmpty(String val) {
        if (val == null || val.trim().equals("")) {
            return false;
        } else
            return true;
    }


    //----------------------------------------------------------------------------------------------
    class CheckoutProWebChromeClient extends PayUWebChromeClient {
        public CheckoutProWebChromeClient(Bank bank){
            super(bank);
        }
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }
    class CheckoutProWebViewClient extends PayUWebViewClient {
        public CheckoutProWebViewClient(Bank bank, String merchantKey){
            super(bank, merchantKey);
        }
    }

}
