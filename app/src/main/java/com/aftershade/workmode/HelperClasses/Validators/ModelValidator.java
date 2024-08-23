package com.aftershade.workmode.HelperClasses.Validators;

import java.util.Calendar;

public final class ModelValidator {

    public static final String ERR_EMPTY = "0";
    public static final String NOT_MATCH = "1";

    public static String validateFullName(String fullname) {

        fullname = fullname.trim();

        if (fullname.isEmpty()){
            return ERR_EMPTY;
        } else {
            return fullname;
        }

    }

    public static String validateEmail(String email) {
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        email = email.trim();

        if (email.isEmpty()) {
            return ERR_EMPTY;
        } else if (!email.matches(checkEmail)) {
            return NOT_MATCH;
        } else {
            return email;
        }

    }

    public static String validatePassword(String password) {
        String checkPassword = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=!])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        password = password.trim();

        if (password.isEmpty()) {
            return ERR_EMPTY;
        } else if (!password.matches(checkPassword)){
            return NOT_MATCH;
        } else {
            return password;
        }
    }

    public static String validateConfirmPassword(String password, String confirmPass) {
        password = password.trim();
        confirmPass = confirmPass.trim();

        String check = ModelValidator.validatePassword(confirmPass);

        if (check.equals(ERR_EMPTY)) {
            return ERR_EMPTY;
        } else if (check.equals(NOT_MATCH)) {
            return NOT_MATCH;
        } else if (!confirmPass.equals(password)) {
            return NOT_MATCH;
        } else {
            return confirmPass;
        }
    }

    public static String validateAge(int year) {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int isAgeValid = currentYear - year;

        if (isAgeValid < 16) {
            return NOT_MATCH;
        } else {
            return "Valid";
        }
    }

    public static String validatePhone(String phone) {
        String pattern = "(?=.*[()@#$%^&=;/.,])";

        phone = phone.trim();

        if (phone.isEmpty()) {
            return ERR_EMPTY;
        } else if (phone.startsWith("0")) {
            phone = phone.substring(1);
            return phone;
        } else if (phone.startsWith("+")) {
            phone = phone.substring(3);
            return phone;
        } else if (phone.matches(pattern)) {
            return NOT_MATCH;
        } else {
            return phone;
        }
    }

}
