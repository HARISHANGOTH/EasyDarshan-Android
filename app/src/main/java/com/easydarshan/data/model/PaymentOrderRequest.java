package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class PaymentOrderRequest {
    @SerializedName("bookingId")
    private String bookingId;
    
    @SerializedName("paymentMethod")
    private String paymentMethod; // UPI, CARD, NET_BANKING

    public PaymentOrderRequest(String bookingId, String paymentMethod) {
        this.bookingId = bookingId;
        this.paymentMethod = paymentMethod;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}





