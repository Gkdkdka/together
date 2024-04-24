import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class otp {

    private static final int TIME_STEP = 30; // 30초 단위 시간 윈도우
    private static final int CODE_DIGITS = 6; // OTP 코드 자릿수

    public static String generateOTP(String secretKey, long time) throws NoSuchAlgorithmException, InvalidKeyException {
        // 1. 현재 시간 윈도우 계산
        long timeWindow = time / (TIME_STEP * 1000);
        
        // 2. 시크릿 키 문자열 생성
        String secretKeyString = secretKey;

        // 3. 데이터 문자열 생성
        String dataString = Long.toString(timeWindow) + secretKeyString;
    
        // 4. 해시값 계산
        byte[] hash = hmacSha256(dataString.getBytes(), secretKey.getBytes());

        // 5. OTP 추출
        String otp = extractOTP(hash);

        return otp;
    }

    public static boolean validateOTP(String secretKey, String otp, long time) throws NoSuchAlgorithmException, InvalidKeyException {
        // 1. 현재 시간 윈도우 계산
        long timeWindow = time /(TIME_STEP * 1000);

        // 2. OTP 유효성 검증
        String generatedOTP = generateOTP(secretKey, timeWindow);
        return generatedOTP.equals(otp);
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

        
        String otpString = String.format("%06d", otp); // OTP 앞에 0 추가


        return otpString;
    }

    public static void main(String[] args) throws Exception {
        // 시크릿 키 생성 (예시)
        String secretKey = generateSecretKey();
     
        // 현재 시간 획득
        long extra_time = System.currentTimeMillis();
        extra_time = (extra_time % 30000);
        
        while (true) {
            // OTP 생성 및 출력
        	long time = System.currentTimeMillis();
            String otp = generateOTP(secretKey, time-extra_time);
            System.out.println("OTP: " + otp);

            // 10초 대기
            Thread.sleep(10000);
        }

        
    }

    private static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
}

//밥만두 바보 뭉총
