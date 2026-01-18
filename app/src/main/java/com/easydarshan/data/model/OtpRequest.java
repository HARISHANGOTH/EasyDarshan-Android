package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class OtpRequest {
    @SerializedName("mobile")
    private String mobile;

    public OtpRequest(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}

