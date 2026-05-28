package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {
    public String phoneNumber;
    public String countryCode;
    public DeviceInfo deviceInfo;
    public AppInfo appInfo;
    public UserContext userContext;
    public String referralCode;
}

