package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class SingleBookingResponse {
    @SerializedName("booking")
    private Booking booking;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
