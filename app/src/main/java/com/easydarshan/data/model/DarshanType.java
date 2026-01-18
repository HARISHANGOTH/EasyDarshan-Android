package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class DarshanType {
    @SerializedName("name")
    private String name;
    
    @SerializedName("price")
    private String price;
    
    @SerializedName("duration")
    private String duration;

    public DarshanType() {
    }

    public DarshanType(String name, String price, String duration) {
        this.name = name;
        this.price = price;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

