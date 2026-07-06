package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class PaymentOrderRequest {

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("platformFee")
    private BigDecimal platformFee;

    @SerializedName("templeId")
    private Long templeId;

    @SerializedName("currency")
    private String currency = "INR";

    @SerializedName("gatewayProvider")
    private String gatewayProvider = "RAZORPAY";

    public PaymentOrderRequest(String bookingId, BigDecimal amount, BigDecimal platformFee, Long templeId) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.platformFee = platformFee;
        this.templeId = templeId;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getPlatformFee() { return platformFee; }
    public void setPlatformFee(BigDecimal platformFee) { this.platformFee = platformFee; }

    public Long getTempleId() { return templeId; }
    public void setTempleId(Long templeId) { this.templeId = templeId; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getGatewayProvider() { return gatewayProvider; }
    public void setGatewayProvider(String gatewayProvider) { this.gatewayProvider = gatewayProvider; }
}
