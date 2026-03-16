package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class SlotLockRequest {
    @SerializedName("slotId")
    private String slotId;
    
    @SerializedName("templeId")
    private Long templeId;
    
    @SerializedName("bookingDate")
    private String bookingDate;
    
    @SerializedName("devotees")
    private Integer devotees;

    public SlotLockRequest(String slotId, Long templeId, String bookingDate, Integer devotees) {
        this.slotId = slotId;
        this.templeId = templeId;
        this.bookingDate = bookingDate;
        this.devotees = devotees;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public Long getTempleId() {
        return templeId;
    }

    public void setTempleId(Long templeId) {
        this.templeId = templeId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Integer getDevotees() {
        return devotees;
    }

    public void setDevotees(Integer devotees) {
        this.devotees = devotees;
    }
}





