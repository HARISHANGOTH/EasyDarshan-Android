package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class UploadAvatarRequest {
    @SerializedName("avatarUrl")
    private String avatarUrl;

    public UploadAvatarRequest(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}





