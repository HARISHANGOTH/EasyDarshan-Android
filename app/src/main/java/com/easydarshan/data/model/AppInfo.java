package com.easydarshan.data.model;

public class AppInfo {
    private String appVersion;
    private String buildNumber;

    public AppInfo() {}

    public AppInfo(String appVersion, String buildNumber) {
        this.appVersion = appVersion;
        this.buildNumber = buildNumber;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }
}
