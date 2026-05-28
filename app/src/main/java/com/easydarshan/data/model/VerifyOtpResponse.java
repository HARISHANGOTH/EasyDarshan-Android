package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpResponse {

    public boolean success;
    public String message;
    public boolean verified;
    public String accessToken;
    public String refreshToken;
    public String userId;
    public String flow;
    public boolean newUser;
}




