package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class PaymentVerificationRequest {

    @SerializedName("paymentOrderId")
    private String paymentOrderId;      // Razorpay order_id

    @SerializedName("transactionId")
    private String transactionId;       // Razorpay payment_id (or UPI txnId)

    @SerializedName("razorpaySignature")
    private String razorpaySignature;   // HMAC-SHA256 from Razorpay SDK callback

    @SerializedName("gatewayResponse")
    private String gatewayResponse;     // Raw response string (optional)

    public PaymentVerificationRequest(String paymentOrderId, String transactionId,
                                       String razorpaySignature, String gatewayResponse) {
        this.paymentOrderId = paymentOrderId;
        this.transactionId = transactionId;
        this.razorpaySignature = razorpaySignature;
        this.gatewayResponse = gatewayResponse;
    }

    public String getPaymentOrderId() { return paymentOrderId; }
    public String getTransactionId() { return transactionId; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public String getGatewayResponse() { return gatewayResponse; }
}
