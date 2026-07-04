package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentVerificationResponse {

    @SerializedName("statusCode")
    private String statusCode;

    @SerializedName("statusMessage")
    private String statusMessage;

    // Backend returns: { statusCode, statusMessage, payment: { ... } }
    @SerializedName("payment")
    private PaymentData payment;

    public String getStatusCode() { return statusCode; }
    public String getStatusMessage() { return statusMessage; }
    public PaymentData getPayment() { return payment; }

    // Convenience getters
    public Long getId() { return payment != null ? payment.id : null; }
    public String getTransactionReference() { return payment != null ? payment.transactionReference : null; }
    public String getGatewayPaymentId() { return payment != null ? payment.gatewayPaymentId : null; }
    public String getPaymentStatus() { return payment != null ? payment.paymentStatus : null; }
    public BigDecimal getAmount() { return payment != null ? payment.amount : null; }
    public String getBookingId() { return payment != null ? payment.bookingId : null; }

    // Success = backend returned SUCCESS_200 and paymentStatus is SUCCESS
    public boolean isSuccess() {
        return statusCode != null && statusCode.startsWith("SUCCESS")
                && payment != null && "SUCCESS".equals(payment.paymentStatus);
    }

    public static class PaymentData {
        @SerializedName("id")                   Long id;
        @SerializedName("transactionReference") String transactionReference;
        @SerializedName("gatewayPaymentId")     String gatewayPaymentId;
        @SerializedName("gatewayProvider")      String gatewayProvider;
        @SerializedName("amount")               BigDecimal amount;
        @SerializedName("paymentMethod")        String paymentMethod;
        @SerializedName("paymentStatus")        String paymentStatus;
        @SerializedName("paidAt")               String paidAt;
        @SerializedName("bookingId")            String bookingId;
    }
}
