package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class SlotsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("slots")
    private List<SlotInfo> slots;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SlotInfo> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotInfo> slots) {
        this.slots = slots;
    }

    public static class SlotInfo {
        @SerializedName("slotId")
        private String slotId;
        
        @SerializedName("time")
        private String time;
        
        @SerializedName("status")
        private String status;
        
        @SerializedName("price")
        private BigDecimal price;
        
        @SerializedName("availableCapacity")
        private int availableCapacity;
        
        @SerializedName("maxCapacity")
        private int maxCapacity;

        public String getSlotId() {
            return slotId;
        }

        public void setSlotId(String slotId) {
            this.slotId = slotId;
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

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public int getAvailableCapacity() {
            return availableCapacity;
        }

        public void setAvailableCapacity(int availableCapacity) {
            this.availableCapacity = availableCapacity;
        }

        public int getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }
    }
}





