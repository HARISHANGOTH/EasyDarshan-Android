package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

public class TempleDetailResponse {

    @SerializedName("data")
    private DataWrapper data;

    public DataWrapper getData() {
        return data;
    }

    public void setData(DataWrapper data) {
        this.data = data;
    }

    public static class DataWrapper {
        @SerializedName("temple")
        private Temple temple;

        public Temple getTemple() {
            return temple;
        }

        public void setTemple(Temple temple) {
            this.temple = temple;
        }
    }
}
