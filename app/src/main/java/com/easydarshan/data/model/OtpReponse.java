package com.easydarshan.data.model;

public class OtpReponse {
    private String message;
    private String userId;
    private boolean requiresOtpVerification;
    private String flow;

    public OtpReponse() {}

    public OtpReponse(String message, String userId, boolean requiresOtpVerification, String flow) {
        this.message = message;
        this.userId = userId;
        this.requiresOtpVerification = requiresOtpVerification;
        this.flow = flow;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRequiresOtpVerification() {
        return requiresOtpVerification;
    }

    public void setRequiresOtpVerification(boolean requiresOtpVerification) {
        this.requiresOtpVerification = requiresOtpVerification;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }
}
