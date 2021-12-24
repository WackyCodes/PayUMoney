package com.wackycodes.payulib;

import androidx.annotation.Nullable;

import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * This Class Created By Shailendra Lodhi on 28-10-2021
 * Visit : https://linktr.ee/wackycodes
 */
public class PayUHashUtil {

    public static String generateHashFromSDK( String hashData, String saltKey,@Nullable String merchantSecretKey) {
        /*
          val vasForMobileSdkHash = HashGenerationUtils.generateHashFromSDK(
            "${binding.etKey.text}|${PayUCheckoutProConstants.CP_VAS_FOR_MOBILE_SDK}|${PayUCheckoutProConstants.CP_DEFAULT}|",
            binding.etSalt.text.toString()
        )
        val paymenRelatedDetailsHash = HashGenerationUtils.generateHashFromSDK(
            "${binding.etKey.text}|${PayUCheckoutProConstants.CP_PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK}|${binding.etUserCredential.text}|",
            binding.etSalt.text.toString()
        )
         * @payment_related_details_for_mobile_sdk =>  <key>|payment_related_details_for_mobile_sdk|<userCredential>|<salt>
         */

        String hashKey = "";
        if ( hashData == null || saltKey == null) {
            return null;
        }
        hashKey = hashData + saltKey;
        if (merchantSecretKey == null){
            return calculateHash(hashKey);
        }else {
            return calculateHmacSha1( hashKey, merchantSecretKey );
        }
    }

    // Calculate Hash!
    public static String calculateHash(String hashString) {
        try {
            StringBuilder hash = new StringBuilder();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
            return hash.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

    private static String calculateHmacSha1(String hashString, String merchantSecretKey ){
        try {
            String type = "HmacSHA1";
            SecretKeySpec secret = new SecretKeySpec(merchantSecretKey.getBytes(), type);
            Mac mac = Mac.getInstance(type);
            mac.init( secret );
            byte[] bytes = mac.doFinal(hashString.getBytes());
            return getHexString(bytes);
        }catch (Exception e){

            return null;
        }

    }
    private static String getHexString( byte[] data ){
        // Create Hex String
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : data) {
            String h = Integer.toHexString(0xFF & aMessageDigest );
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
    }

}
