package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Temple {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("location")
    private String location;
    
    @SerializedName("distance")
    private String distance;
    
    @SerializedName("queueStatus")
    private String queueStatus;
    
    @SerializedName("queueText")
    private String queueText;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("openingTime")
    private String openingTime;
    
    @SerializedName("closingTime")
    private String closingTime;
    
    @SerializedName("description")
    private String description;

    public Temple() {
    }

    public Temple(int id, String name, String location, String distance, String queueStatus, String queueText, String image) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.distance = distance;
        this.queueStatus = queueStatus;
        this.queueText = queueText;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }

    public String getQueueText() {
        return queueText;
    }

    public void setQueueText(String queueText) {
        this.queueText = queueText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Temple temple = (Temple) o;
        return id == temple.id &&
                Objects.equals(name, temple.name) &&
                Objects.equals(location, temple.location) &&
                Objects.equals(distance, temple.distance) &&
                Objects.equals(queueStatus, temple.queueStatus) &&
                Objects.equals(queueText, temple.queueText) &&
                Objects.equals(image, temple.image) &&
                Objects.equals(openingTime, temple.openingTime) &&
                Objects.equals(closingTime, temple.closingTime) &&
                Objects.equals(description, temple.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, distance, queueStatus, queueText, image, openingTime, closingTime, description);
    }
}
