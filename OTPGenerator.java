package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OTPGenerator extends AsyncTask<Void, Void, String> {

    private static final int TIME_STEP = 30;
    private static final int CODE_DIGITS = 6;
    private static final String TAG = "OTPGenerator";

    private OTPListener mListener;

    public OTPGenerator(OTPListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            // 시크릿 키 생성
            String secretKey = generateSecretKey();

            // 현재 시간 획득
            long currentTime = System.currentTimeMillis();
            long extraTime = currentTime % 30000;

            // OTP 생성
            long time = currentTime - extraTime;
            return generateOTP(secretKey, time);
        } catch (Exception e) {
            Log.e(TAG, "Error generating OTP: " + e.getMessage());
            return null;
        }
    }

    private static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.encodeToString(bytes, Base64.URL_SAFE);
    }

    private static String generateOTP(String secretKey, long time) throws NoSuchAlgorithmException, InvalidKeyException {
        long timeWindow = time / (TIME_STEP * 1000);
        String secretKeyString = secretKey;
        String dataString = Long.toString(timeWindow) + secretKeyString;
        byte[] hash = hmacSha256(dataString.getBytes(), secretKey.getBytes());
        return extractOTP(hash);
    }

    private static byte[] hmacSha256(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

    private static String extractOTP(byte[] hash) {
        int offset = hash.length - CODE_DIGITS;
        int truncatedHash = 0;
        for (int i = offset; i < hash.length; i++) {
            truncatedHash = (truncatedHash << 8) | (hash[i] & 0xFF);
        }
        if (truncatedHash < 0) {
            truncatedHash = Math.abs(truncatedHash);
        }
        int otp = truncatedHash % 1000000;
        return String.format("%06d", otp);
    }

    interface OTPListener {
        void onOTPGenerated(String otp);
        void onError(String message);
    }
}
