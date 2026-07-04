package com.easydarshan.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(tableName = "bookings")
public class Booking {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;
    
    @SerializedName("temple")
    private String temple;
    
    @SerializedName("location")
    private String location;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("time")
    private String time;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("devotees")
    private Integer devotees;
    
    @SerializedName("position")
    private Integer position;
    
    @SerializedName("amount")
    private Double amount;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("paymentStatus")
    private String paymentStatus;
    
    @SerializedName("qrCode")
    private String qrCode;

    public Booking() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTemple() {
        return temple;
    }

    public void setTemple(String temple) {
        this.temple = temple;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDevotees() {
        return devotees;
    }

    public void setDevotees(Integer devotees) {
        this.devotees = devotees;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) &&
                Objects.equals(temple, booking.temple) &&
                Objects.equals(location, booking.location) &&
                Objects.equals(date, booking.date) &&
                Objects.equals(time, booking.time) &&
                Objects.equals(status, booking.status) &&
                Objects.equals(type, booking.type) &&
                Objects.equals(devotees, booking.devotees) &&
                Objects.equals(position, booking.position) &&
                Objects.equals(amount, booking.amount) &&
                Objects.equals(paymentMethod, booking.paymentMethod) &&
                Objects.equals(paymentStatus, booking.paymentStatus) &&
                Objects.equals(qrCode, booking.qrCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, temple, location, date, time, status, type, devotees, position, amount, paymentMethod, paymentStatus, qrCode);
    }
}
