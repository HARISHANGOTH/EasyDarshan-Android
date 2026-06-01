package com.easydarshan.data.model;

public class OtpRequest {
    private String phoneNumber;
    private String countryCode;
    private DeviceInfo deviceInfo;
    private AppInfo appInfo;
    private UserContext userContext;
    private String referralCode;

    public OtpRequest() {}

    public OtpRequest(String phoneNumber, String countryCode, DeviceInfo deviceInfo, AppInfo appInfo, UserContext userContext, String referralCode) {
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.deviceInfo = deviceInfo;
        this.appInfo = appInfo;
        this.userContext = userContext;
        this.referralCode = referralCode;
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

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
}
