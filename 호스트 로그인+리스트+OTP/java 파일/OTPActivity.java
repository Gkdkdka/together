package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OTPActivity extends AppCompatActivity {

    private static final int TIME_STEP = 30; // 30초 단위 시간 윈도우
    private static final int CODE_DIGITS = 6; // OTP 코드 자릿수
    private static String secretKey;

    private TextView otpTextView;
    private EditText attendanceNumberEditText;
    private Button confirmButton;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // UI 요소 초기화
        otpTextView = findViewById(R.id.textViewTogether);
        attendanceNumberEditText = findViewById(R.id.editTextAttendanceNumber);
        confirmButton = findViewById(R.id.buttonConfirm);

        // 시크릿 키 생성 (예시)
        secretKey = generateSecretKey();

        // 주기적으로 OTP를 생성하는 Runnable 생성
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    long time = System.currentTimeMillis();
                    String otp = generateOTP(secretKey, time);
                    otpTextView.setText(otp);
                } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                    e.printStackTrace();
                }
                // 10초 대기 후 다시 실행
                handler.postDelayed(this, 10000);
            }
        };

        // OTP 생성 시작
        handler.post(runnable);

        // 확인 버튼 클릭 이벤트 처리
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    long time = System.currentTimeMillis();
                    String enteredNumber = attendanceNumberEditText.getText().toString();
                    if (enteredNumber != null && !enteredNumber.isEmpty()) {
                        String otp = generateOTP(secretKey, time);
                        if (validateOTP(secretKey, enteredNumber, time)) {
                            otpTextView.setText("OTP가 확인되었습니다.");
                        } else {
                            otpTextView.setText("유효하지 않은 OTP입니다.");
                        }
                    } else {
                        otpTextView.setText("번호를 입력하세요.");
                    }
                } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 Runnable 중지
        handler.removeCallbacks(runnable);
    }

    private static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return android.util.Base64.encodeToString(bytes, android.util.Base64.URL_SAFE);
    }


    // generateOTP 메서드 수정
    private static String generateOTP(String secretKey, long time) throws NoSuchAlgorithmException, InvalidKeyException {
        // 현재 시간 윈도우 계산
        long timeWindow = time / (TIME_STEP * 1000);

        // 데이터 문자열 생성
        String dataString = Long.toString(timeWindow) + secretKey;

        // 해시값 계산
        byte[] hash = hmacSha256(dataString.getBytes(), secretKey.getBytes());

        // OTP 추출
        return extractOTP(hash);
    }

    // validateOTP 메서드 수정
    private static boolean validateOTP(String secretKey, String otp, long time) throws NoSuchAlgorithmException, InvalidKeyException {
        // 현재 시간 윈도우 계산
        long timeWindow = time / (TIME_STEP * 1000);

        // 사용자가 입력한 OTP
        int userOTP = Integer.parseInt(otp);

        // 현재 시간 윈도우에서 이전 윈도우까지 검증
        for (int i = 0; i <= 1; i++) {
            // 이전 시간 윈도우부터 현재 시간 윈도우까지의 OTP를 검증
            String generatedOTP = generateOTP(secretKey, time - (i * TIME_STEP * 1000));
            int generatedOTPInt = Integer.parseInt(generatedOTP);

            // 입력된 OTP와 생성된 OTP가 일치하면 true 반환
            if (userOTP == generatedOTPInt) {
                return true;
            }
        }

        // 모든 윈도우에서 일치하는 OTP를 찾지 못한 경우 false 반환
        return false;
    }




    private static byte[] hmacSha256(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

    private static String extractOTP(byte[] hash) {
        int offset = hash.length - CODE_DIGITS; // 6자리 OTP를 위한 오프셋 계산
        int truncatedHash = 0; // 초기화된 해시값

        // 해시 배열에서 마지막 CODE_DIGITS 바이트 추출
        for (int i = offset; i < hash.length; i++) {
            truncatedHash = (truncatedHash << 8) | (hash[i] & 0xFF);
        }
        if (truncatedHash < 0) {
            truncatedHash = Math.abs(truncatedHash); // 절대값으로 변환
        }

        int otp = truncatedHash % 1000000; // OTP 계산 (나머지 연산 사용)

        // OTP 앞에 0 추가하여 6자리로 만듦
        return String.format("%06d", otp);
    }
}

