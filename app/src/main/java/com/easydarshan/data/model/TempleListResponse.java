package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TempleListResponse {

    @SerializedName("temples")
    private List<Temple> temples;

    public List<Temple> getTemples() {
        return temples;
    }

    public void setTemples(List<Temple> temples) {
        this.temples = temples;
    }
}







