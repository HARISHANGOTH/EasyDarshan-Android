package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class PaymentVerificationRequest {

    @SerializedName("razorpayOrderId")
    private String razorpayOrderId;

    @SerializedName("razorpayPaymentId")
    private String razorpayPaymentId;

    @SerializedName("razorpaySignature")
    private String razorpaySignature;

    public PaymentVerificationRequest(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
    }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }
}
