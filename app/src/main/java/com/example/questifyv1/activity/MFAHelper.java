package com.example.questifyv1.activity;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;

public class MFAHelper {

    public static String generateSecretKey() {
        DefaultSecretGenerator generator = new DefaultSecretGenerator();
        return generator.generate();
    }

    public static String generateOTP(String secretKey) {
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        SystemTimeProvider timeProvider = new SystemTimeProvider();
        try {
            return codeGenerator.generate(secretKey, timeProvider.getTime());
        } catch (CodeGenerationException e) {
            // Handle the exception, e.g., log it or return an error message
            e.printStackTrace();
            return null;  // Or handle it differently based on your application's requirements
        }
    }

    public static boolean verifyOTP(String secretKey, String userCode) {
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        SystemTimeProvider timeProvider = new SystemTimeProvider();
        try {
            String generatedCode = codeGenerator.generate(secretKey, timeProvider.getTime());
            return generatedCode.equals(userCode);
        } catch (CodeGenerationException e) {
            // Handle the exception
            e.printStackTrace();
            return false;  // If there's an exception, return false or handle differently
        }
    }
}
