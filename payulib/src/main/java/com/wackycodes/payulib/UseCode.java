package com.wackycodes.payulib;

public class UseCode {

/**
 *
 * This Library is used to make payment of payUmoney gateway -> with Zero code !
 *
 * Just Use below code in your activity or fragment!
 */

    /** TODO: Copy this code and uncomment!!



     private PayModel payModel;
     private PayUResponseModel payUResponseModel;

     // Create Launcher Var for Payment!
    private ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->  {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    int resCode = data.getIntExtra( KEY_RES_CODE, -1 );
                    boolean isInitTxn = data.getBooleanExtra(KEY_RES_IS_TXN_INIT, false );
                    String error = data.getStringExtra(KEY_RES_ERROR);
                    Log.e("RESPONSE", "CODE : " + resCode +"  : " + isInitTxn);

                    switch (resCode){
                        case KEY_RES_CODE_SUCCESS:
                        case KEY_RES_CODE_FAILED:
                            Gson gson = new Gson();
                            Type type = new TypeToken<PayUResponseModel>() {}.getType();
                            payUResponseModel = gson.fromJson(
                                    data.getStringExtra(PaymentQueryActivity.KEY_RES_OBJ), type);

                            // TODO : Next Process on Success or Failed!

                            break;
                        case KEY_RES_CODE_CANCELLED: /// Show Cancel Msg
                        case KEY_RES_CODE_ERROR: // Show Error!
                            AlertDialog.Builder builder = new AlertDialog.Builder( this );
                            builder.setCancelable(false);
                            builder.setTitle(""+ error);
                            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).show();
                            break;
                        default:
                            break;
                    }
                }
            });



     // Call Whenever User Wants to pay!
     private void launchActivityForPayResult(){
         // TODO: Assign @payModel

         // Finally Start Activity!
         Intent intent = new Intent(this, PaymentQueryActivity.class);
         // put Data
         intent.putExtra( KEY_DATA_MODEL, payModel );
         paymentLauncher.launch(intent);
     }




    */

}