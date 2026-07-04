package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class PaymentOrderResponse {

    @SerializedName("statusCode")
    private String statusCode;

    @SerializedName("statusMessage")
    private String statusMessage;

    // Backend returns: { statusCode, statusMessage, paymentOrder: { ... } }
    @SerializedName("paymentOrder")
    private PaymentOrderData paymentOrder;

    public String getStatusCode() { return statusCode; }
    public String getStatusMessage() { return statusMessage; }
    public PaymentOrderData getPaymentOrder() { return paymentOrder; }

    // Convenience getters — delegate to inner paymentOrder object
    public Long getId() { return paymentOrder != null ? paymentOrder.id : null; }
    public String getOrderReference() { return paymentOrder != null ? paymentOrder.orderReference : null; }
    public String getGatewayOrderId() { return paymentOrder != null ? paymentOrder.gatewayOrderId : null; }
    public String getGatewayProvider() { return paymentOrder != null ? paymentOrder.gatewayProvider : null; }
    public BigDecimal getAmount() { return paymentOrder != null ? paymentOrder.amount : null; }
    public BigDecimal getPlatformFee() { return paymentOrder != null ? paymentOrder.platformFee : null; }
    public BigDecimal getTotalAmount() { return paymentOrder != null ? paymentOrder.totalAmount : null; }
    public String getCurrency() { return paymentOrder != null ? paymentOrder.currency : null; }
    public String getOrderStatus() { return paymentOrder != null ? paymentOrder.orderStatus : null; }
    public String getRazorpayKeyId() { return paymentOrder != null ? paymentOrder.razorpayKeyId : null; }

    // Legacy alias used by PreBookingViewModel
    public String getPaymentOrderId() { return getOrderReference(); }

    // Success = order was created and we have a reference
    public boolean isSuccess() {
        return statusCode != null && statusCode.startsWith("SUCCESS")
                && paymentOrder != null && paymentOrder.orderReference != null;
    }

    public static class PaymentOrderData {
        @SerializedName("id")           Long id;
        @SerializedName("orderReference") String orderReference;
        @SerializedName("gatewayOrderId") String gatewayOrderId;
        @SerializedName("gatewayProvider") String gatewayProvider;
        @SerializedName("amount")        BigDecimal amount;
        @SerializedName("platformFee")   BigDecimal platformFee;
        @SerializedName("totalAmount")   BigDecimal totalAmount;
        @SerializedName("currency")      String currency;
        @SerializedName("orderStatus")   String orderStatus;
        @SerializedName("razorpayKeyId") String razorpayKeyId;
    }
}
