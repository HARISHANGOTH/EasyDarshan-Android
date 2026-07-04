package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class DarshanType {
    // Backend field: darshanTypeId
    @SerializedName("darshanTypeId")
    private Long id;
    
    @SerializedName("name")
    private String name;
    
    // Backend field: amount (BigDecimal serialized as number)
    @SerializedName("amount")
    private String price;
    
    @SerializedName("duration")
    private String duration;
    
    // Backend field: active (boolean, not isActive)
    @SerializedName("active")
    private Boolean isActive;

    public DarshanType() {
    }

    public DarshanType(Long id, String name, String price, String duration) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

