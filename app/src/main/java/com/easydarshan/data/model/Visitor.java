package com.easydarshan.data.model;

public class Visitor {
    private String fullName;
    private String age;
    private String gender;
    private String idProof;
    private String idNumber;
    private boolean isAdult;

    public Visitor(boolean isAdult) {
        this.isAdult = isAdult;
        this.gender = "Male"; // Default
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getIdProof() { return idProof; }
    public void setIdProof(String idProof) { this.idProof = idProof; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public boolean isAdult() { return isAdult; }
    public void setAdult(boolean adult) { isAdult = adult; }
}