package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class PaymentVerificationRequest {
    @SerializedName("paymentOrderId")
    private String paymentOrderId;
    
    @SerializedName("transactionId")
    private String transactionId;
    
    @SerializedName("gatewayResponse")
    private String gatewayResponse;

    public PaymentVerificationRequest(String paymentOrderId, String transactionId, String gatewayResponse) {
        this.paymentOrderId = paymentOrderId;
        this.transactionId = transactionId;
        this.gatewayResponse = gatewayResponse;
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public void setPaymentOrderId(String paymentOrderId) {
        this.paymentOrderId = paymentOrderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
}





