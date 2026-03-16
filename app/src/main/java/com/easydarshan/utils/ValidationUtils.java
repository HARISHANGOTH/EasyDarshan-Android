package com.easydarshan.utils;

import android.text.TextUtils;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern OTP_PATTERN = Pattern.compile("^\\d{4,6}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public static boolean isValidPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        // Remove any spaces or special characters
        String cleaned = phone.replaceAll("[^0-9]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }
    
    public static boolean isValidOtp(String otp) {
        if (TextUtils.isEmpty(otp)) {
            return false;
        }
        return OTP_PATTERN.matcher(otp).matches();
    }
    
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 50;
    }
    
    public static String cleanPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        return phone.replaceAll("[^0-9]", "");
    }
    
    public static String getPhoneValidationError(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "Phone number is required";
        }
        String cleaned = cleanPhoneNumber(phone);
        if (cleaned.length() != 10) {
            return "Phone number must be 10 digits";
        }
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            return "Please enter a valid phone number";
        }
        return null;
    }
    
    public static String getOtpValidationError(String otp) {
        if (TextUtils.isEmpty(otp)) {
            return "OTP is required";
        }
        if (!isValidOtp(otp)) {
            return "Please enter a valid 4-6 digit OTP";
        }
        return null;
    }
    
    public static String getNameValidationError(String name) {
        if (TextUtils.isEmpty(name)) {
            return "Name is required";
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2) {
            return "Name must be at least 2 characters";
        }
        if (trimmed.length() > 50) {
            return "Name must be less than 50 characters";
        }
        return null;
    }
}





