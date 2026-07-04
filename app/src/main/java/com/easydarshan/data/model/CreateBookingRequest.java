package com.easydarshan.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateBookingRequest {

    @SerializedName("templeId")
    private Long templeId;

    @SerializedName("darshanTypeId")
    private Long darshanTypeId;

    @SerializedName("slotId")
    private Long slotId;

    @SerializedName("darshanDate")
    private String darshanDate;

    @SerializedName("members")
    private List<MemberRequest> members;

    public CreateBookingRequest() {
    }

    public CreateBookingRequest(Long templeId, Long darshanTypeId, Long slotId,
                                String darshanDate, List<MemberRequest> members) {
        this.templeId = templeId;
        this.darshanTypeId = darshanTypeId;
        this.slotId = slotId;
        this.darshanDate = darshanDate;
        this.members = members;
    }

    public Long getTempleId() { return templeId; }
    public void setTempleId(Long templeId) { this.templeId = templeId; }

    public Long getDarshanTypeId() { return darshanTypeId; }
    public void setDarshanTypeId(Long darshanTypeId) { this.darshanTypeId = darshanTypeId; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getDarshanDate() { return darshanDate; }
    public void setDarshanDate(String darshanDate) { this.darshanDate = darshanDate; }

    public List<MemberRequest> getMembers() { return members; }
    public void setMembers(List<MemberRequest> members) { this.members = members; }

    public static class MemberRequest {
        @SerializedName("devoteeName")
        private String devoteeName;

        @SerializedName("age")
        private Integer age;

        @SerializedName("gender")
        private String gender;

        public MemberRequest(String devoteeName) {
            this.devoteeName = devoteeName;
        }

        public MemberRequest(String devoteeName, Integer age, String gender) {
            this.devoteeName = devoteeName;
            this.age = age;
            this.gender = gender;
        }

        public String getDevoteeName() { return devoteeName; }
        public void setDevoteeName(String devoteeName) { this.devoteeName = devoteeName; }

        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
    }
}
