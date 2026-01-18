package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class OtpRequest {
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("purpose")
    private String purpose;

    public OtpRequest(String phone) {
        this.phone = phone;
        this.purpose = "LOGIN"; // Default to LOGIN, can be changed to REGISTRATION or PASSWORD_RESET
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

