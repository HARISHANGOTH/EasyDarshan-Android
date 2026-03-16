package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LiveQueueDraftRequest {
    @SerializedName("templeId")
    private Long templeId;
    
    @SerializedName("devotees")
    private List<DevoteeInfo> devotees;

    public LiveQueueDraftRequest(Long templeId, List<DevoteeInfo> devotees) {
        this.templeId = templeId;
        this.devotees = devotees;
    }

    public Long getTempleId() {
        return templeId;
    }

    public void setTempleId(Long templeId) {
        this.templeId = templeId;
    }

    public List<DevoteeInfo> getDevotees() {
        return devotees;
    }

    public void setDevotees(List<DevoteeInfo> devotees) {
        this.devotees = devotees;
    }

    public static class DevoteeInfo {
        @SerializedName("name")
        private String name;
        
        @SerializedName("age")
        private Integer age;
        
        @SerializedName("gender")
        private String gender;

        public DevoteeInfo(String name, Integer age, String gender) {
            this.name = name;
            this.age = age;
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }
}





