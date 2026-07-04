package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TempleSearchResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("query")
    private String query;

    @SerializedName("page")
    private int page;

    @SerializedName("size")
    private int size;

    @SerializedName("totalResults")
    private long totalResults;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("results")
    private List<TempleSearchResult> results;

    public boolean isSuccess() { return success; }
    public String getQuery() { return query; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalResults() { return totalResults; }
    public int getTotalPages() { return totalPages; }
    public List<TempleSearchResult> getResults() { return results; }

    public static class TempleSearchResult {
        @SerializedName("id")
        private Long id;

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

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getLocation() { return location; }
        public String getDistance() { return distance; }
        public String getQueueStatus() { return queueStatus; }
        public String getQueueText() { return queueText; }
        public String getImage() { return image; }
        public String getOpeningTime() { return openingTime; }
        public String getClosingTime() { return closingTime; }
        public String getDescription() { return description; }
    }
}
