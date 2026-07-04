package com.easydarshan.data.model;

public class OtpVerifyRequest {
    private String phoneNumber;
    private String countryCode;
    private String otp;

    public OtpVerifyRequest() {}

    public OtpVerifyRequest(String phoneNumber, String countryCode, String otp) {
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.otp = otp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
