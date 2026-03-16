package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class DeviceRegistrationRequest {
    @SerializedName("deviceToken")
    private String deviceToken;
    
    @SerializedName("deviceType")
    private String deviceType; // ANDROID, IOS

    public DeviceRegistrationRequest(String deviceToken, String deviceType) {
        this.deviceToken = deviceToken;
        this.deviceType = deviceType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}





