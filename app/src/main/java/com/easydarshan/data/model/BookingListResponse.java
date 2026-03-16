package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingListResponse {
    @SerializedName("bookings")
    private List<Booking> bookings;

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
