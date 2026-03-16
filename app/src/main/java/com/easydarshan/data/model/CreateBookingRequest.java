package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateBookingRequest {
    @SerializedName("templeId")
    private Long templeId;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("timeSlot")
    private String timeSlot;
    
    @SerializedName("darshanType")
    private String darshanType;
    
    @SerializedName("devotees")
    private Integer devotees;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("amount")
    private Double amount;

    public CreateBookingRequest() {
    }

    public CreateBookingRequest(Long templeId, String date, String timeSlot, String darshanType, 
                                Integer devotees, String paymentMethod, Double amount) {
        this.templeId = templeId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.darshanType = darshanType;
        this.devotees = devotees;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    public Long getTempleId() {
        return templeId;
    }

    public void setTempleId(Long templeId) {
        this.templeId = templeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getDarshanType() {
        return darshanType;
    }

    public void setDarshanType(String darshanType) {
        this.darshanType = darshanType;
    }

    public Integer getDevotees() {
        return devotees;
    }

    public void setDevotees(Integer devotees) {
        this.devotees = devotees;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}





