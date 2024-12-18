package com.bedjaoui.backend.Util;

import java.security.SecureRandom;

public class OtpUtil {
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Génère un chiffre entre 0 et 9
        }

        return otp.toString();
    }
}
