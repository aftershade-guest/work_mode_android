package com.aftershade.workmode.HelperClasses.Models;

public class User {

    private String uid, fullName, email, dateOfBirth, phoneNumber, gender, bio;

    public User() {
    }

    public User(String uid, String fullName, String email, String dateOfBirth, String phoneNumber, String gender, String bio) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.bio = bio;
    }

    public String getUid() {
        return uid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }
}
