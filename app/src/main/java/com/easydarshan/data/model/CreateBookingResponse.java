package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateBookingResponse {

    @SerializedName("statusCode")
    private String statusCode;

    @SerializedName("statusMessage")
    private String statusMessage;

    // Backend returns: { statusCode, statusMessage, booking: { bookingReference, ... } }
    @SerializedName("booking")
    private BookingData booking;

    public String getStatusCode() { return statusCode; }
    public String getStatusMessage() { return statusMessage; }
    public BookingData getBooking() { return booking; }

    // Convenience getters — delegate to inner booking object
    public String getBookingReference() {
        return booking != null ? booking.bookingReference : null;
    }

    public String getBookingStatus() {
        return booking != null ? booking.bookingStatus : null;
    }

    public String getDarshanDate() {
        return booking != null ? booking.darshanDate : null;
    }

    public Integer getTotalMembers() {
        return booking != null ? booking.totalMembers : null;
    }

    public String getTotalAmount() {
        return booking != null ? booking.totalAmount : null;
    }

    // Legacy aliases used by PreBookingViewModel
    public String getBookingId() { return getBookingReference(); }
    public void setBookingId(String bookingId) {
        if (booking == null) booking = new BookingData();
        booking.bookingReference = bookingId;
    }

    public String getStatus() { return getBookingStatus(); }
    public void setStatus(String status) {
        if (booking == null) booking = new BookingData();
        booking.bookingStatus = status;
    }

    public void setBookingReference(String bookingReference) {
        if (booking == null) booking = new BookingData();
        booking.bookingReference = bookingReference;
    }

    public boolean isSuccess() {
        return statusCode != null && statusCode.startsWith("SUCCESS")
                && booking != null && booking.bookingReference != null;
    }

    public static class BookingData {
        @SerializedName("bookingReference") String bookingReference;
        @SerializedName("bookingStatus")    String bookingStatus;
        @SerializedName("darshanDate")      String darshanDate;
        @SerializedName("totalMembers")     Integer totalMembers;
        @SerializedName("totalAmount")      String totalAmount;
    }
}
