package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class OtpVerifyRequest {
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("otp")
    private String otp;
    
    @SerializedName("purpose")
    private String purpose;

    public OtpVerifyRequest(String phone, String otp) {
        this.phone = phone;
        this.otp = otp;
        this.purpose = "LOGIN"; // Default to LOGIN, can be changed to REGISTRATION or PASSWORD_RESET
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    // For backward compatibility
    public String getMobile() {
        return phone;
    }
    
    public void setMobile(String mobile) {
        this.phone = mobile;
    }
}

