package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class LiveQueueDraftResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("queueDraftId")
    private String queueDraftId;
    
    @SerializedName("status")
    private String status;

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

    public String getQueueDraftId() {
        return queueDraftId;
    }

    public void setQueueDraftId(String queueDraftId) {
        this.queueDraftId = queueDraftId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}





