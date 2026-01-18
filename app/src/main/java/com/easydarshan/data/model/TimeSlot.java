package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class TimeSlot {
    @SerializedName("time")
    private String time;
    
    @SerializedName("capacity")
    private String capacity;
    
    @SerializedName("price")
    private int price;
    
    @SerializedName("available")
    private boolean available;

    public TimeSlot() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

