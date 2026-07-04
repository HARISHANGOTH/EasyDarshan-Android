package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TempleListResponse {

    // Backend wraps list in: { "data": { "temples": [...], "total": N } }
    @SerializedName("data")
    private DataWrapper data;

    public List<Temple> getTemples() {
        return data != null ? data.temples : null;
    }

    public static class DataWrapper {
        @SerializedName("temples")
        private List<Temple> temples;

        @SerializedName("total")
        private Integer total;
    }
}







