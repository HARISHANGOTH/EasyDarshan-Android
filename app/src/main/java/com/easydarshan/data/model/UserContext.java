package com.easydarshan.data.model;

public class UserContext {
    private String timezone;
    private String language;

    public UserContext() {}

    public UserContext(String timezone, String language) {
        this.timezone = timezone;
        this.language = language;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
