package com.aftershade.workmode.HelperClasses.Models;

public class Instructor {

    private String uid, fullName, trainerType, bio, dateOfBirth, email, gender, phoneNumber;

    public Instructor() {
    }

    public Instructor(String uid, String fullName, String trainerType, String bio,
                      String dateOfBirth, String email, String gender, String phoneNumber) {
        this.uid = uid;
        this.fullName = fullName;
        this.trainerType = trainerType;
        this.bio = bio;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTrainerType() {
        return trainerType;
    }

    public String getBio() {
        return bio;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
