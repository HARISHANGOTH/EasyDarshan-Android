package com.easydarshan.data.model;

public class VerifyOtpResponse {
    private boolean success;
    private String message;
    private boolean verified;
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String flow;
    private boolean newUser;

    public VerifyOtpResponse() {}

    public VerifyOtpResponse(boolean success, String message, boolean verified, String accessToken, String refreshToken, String userId, String flow, boolean newUser) {
        this.success = success;
        this.message = message;
        this.verified = verified;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.flow = flow;
        this.newUser = newUser;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
}
