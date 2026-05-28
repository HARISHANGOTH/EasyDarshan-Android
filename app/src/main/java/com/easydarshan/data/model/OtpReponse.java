package com.easydarshan.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpReponse {
    public String message;
    public String userId;
    public boolean requiresOtpVerification;
    public String flow;
}
