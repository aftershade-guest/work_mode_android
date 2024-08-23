package com.aftershade.workmode.HelperClasses;

public class UserHelperClass {

    private String fullname, email, phoneNo, password, date, gender;

    public UserHelperClass() {
    }

    public UserHelperClass(String fullname, String email, String phoneNo, String password, String date, String gender) {
        this.fullname = fullname;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.date = date;
        this.gender = gender;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public String getDate() {
        return date;
    }

    public String getGender() {
        return gender;
    }
}
