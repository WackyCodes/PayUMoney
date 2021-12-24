package com.wackycodes.payumoney;

import static com.wackycodes.payulib.PaymentQueryActivity.KEY_DATA_MODEL;
import static com.wackycodes.payulib.PaymentQueryActivity.KEY_RES_CODE;
import static com.wackycodes.payulib.PaymentQueryActivity.KEY_RES_CODE_CANCELLED;
import static com.wackycodes.payulib.PaymentQueryActivity.KEY_RES_CODE_ERROR;
import static com.wackycodes.payulib.PaymentQueryActivity.KEY_RES_CODE_FAILED;
import static com.wackycodes.payulib.PaymentQueryActivity.KEY_RES_CODE_SUCCESS;
import static com.wackycodes.payulib.PaymentQueryActivity.KEY_RES_ERROR;
import static com.wackycodes.payulib.PaymentQueryActivity.KEY_RES_IS_TXN_INIT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wackycodes.payulib.PayModel;
import com.wackycodes.payulib.PayUResponseModel;
import com.wackycodes.payulib.PaymentQueryActivity;
import com.wackycodes.payumoney.databinding.ActivityMainBinding;

import java.lang.reflect.Type;

public class ActivityTestPayMoney extends AppCompatActivity {

    private ActivityMainBinding payMoneyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_pay_money);
        payMoneyBinding = DataBindingUtil.setContentView( this, R.layout.activity_main);
        payMoneyBinding.buttonPayNow.setOnClickListener( v -> launchActivityForPayResult() );

    }

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

                    //
                    payMoneyBinding.textResponse.setText( "" + error );

                    payMoneyBinding.buttonPayNow.setText("RETRY Payment!");
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

}