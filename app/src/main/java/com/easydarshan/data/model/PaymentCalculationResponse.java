package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class PaymentCalculationResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("darshanFee")
    private BigDecimal darshanFee;
    
    @SerializedName("serviceFee")
    private BigDecimal serviceFee;
    
    @SerializedName("totalAmount")
    private BigDecimal totalAmount;
    
    @SerializedName("bookingId")
    private String bookingId;

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

    public BigDecimal getDarshanFee() {
        return darshanFee;
    }

    public void setDarshanFee(BigDecimal darshanFee) {
        this.darshanFee = darshanFee;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}





