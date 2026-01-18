package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class OtpVerifyRequest {
    @SerializedName("mobile")
    private String mobile;
    
    @SerializedName("otp")
    private String otp;

    public OtpVerifyRequest(String mobile, String otp) {
        this.mobile = mobile;
        this.otp = otp;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

