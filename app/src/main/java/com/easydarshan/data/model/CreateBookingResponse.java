package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateBookingResponse {
    @SerializedName("message")
    private String message;
    
    @SerializedName("bookingId")
    private String bookingId;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("position")
    private Integer position;
    
    @SerializedName("paymentStatus")
    private String paymentStatus;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}





