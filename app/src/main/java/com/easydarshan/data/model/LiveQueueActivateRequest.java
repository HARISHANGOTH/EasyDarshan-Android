package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class LiveQueueActivateRequest {
    @SerializedName("queueDraftId")
    private String queueDraftId;

    public LiveQueueActivateRequest(String queueDraftId) {
        this.queueDraftId = queueDraftId;
    }

    public String getQueueDraftId() {
        return queueDraftId;
    }

    public void setQueueDraftId(String queueDraftId) {
        this.queueDraftId = queueDraftId;
    }
}





