package com.aftershade.workmode.Databases;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SignUpSession {

    public static String SESSION_SIGNUP = "signUpSession";
    public static final String KEY_IS_SIGNUP = "isSignUp";
    public static final String KEY_FULLNAME_S = "fullName";
    public static final String KEY_EMAIL_S = "email";
    public static final String KEY_PHONENUMBER_S = "phoneNumber";
    public static final String KEY_PASSWORD_S = "password";
    public static final String KEY_DATE_S = "dateOfBirth";
    public static final String KEY_GENDER_S = "gender";

    private final String KEY_PROGRESS = "progress";

    SharedPreferences signUpSession;
    SharedPreferences.Editor editor;
    Context context;

    public SignUpSession(Context context) {
        this.context = context;
        signUpSession = context.getSharedPreferences(SESSION_SIGNUP, Context.MODE_PRIVATE);
        editor = signUpSession.edit();
    }

    public void addFirstPart(String fullName, String email, String password) {
        editor.putBoolean(KEY_IS_SIGNUP, true);
        editor.putString(KEY_PROGRESS, "fragment1");
        editor.putString(KEY_FULLNAME_S, fullName);
        editor.putString(KEY_EMAIL_S, email);
        editor.putString(KEY_PASSWORD_S, password);
        editor.commit();

    }

    public void addSecondPart(String gender, String date) {
        editor.putString(KEY_PROGRESS, "fragment2");
        editor.putString(KEY_GENDER_S, gender);
        editor.putString(KEY_DATE_S, date);
        editor.commit();
    }

    public void addThirdPart(String phoneNumber) {
        editor.putString(KEY_PROGRESS, "fragment3");
        editor.putString(KEY_PHONENUMBER_S, phoneNumber);
        editor.commit();
    }

    public HashMap<String, String> getInformation() {

        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_FULLNAME_S, signUpSession.getString(KEY_FULLNAME_S, ""));
        userData.put(KEY_EMAIL_S, signUpSession.getString(KEY_EMAIL_S, ""));
        userData.put(KEY_PASSWORD_S, signUpSession.getString(KEY_PASSWORD_S, ""));
        userData.put(KEY_GENDER_S, signUpSession.getString(KEY_GENDER_S, ""));
        userData.put(KEY_DATE_S, signUpSession.getString(KEY_DATE_S, ""));
        userData.put(KEY_PHONENUMBER_S, signUpSession.getString(KEY_PHONENUMBER_S, ""));

        return userData;

    }

    public HashMap<String, String> getFirstPart() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_FULLNAME_S, signUpSession.getString(KEY_FULLNAME_S, ""));
        userData.put(KEY_EMAIL_S, signUpSession.getString(KEY_EMAIL_S, ""));
        userData.put(KEY_PASSWORD_S, signUpSession.getString(KEY_PASSWORD_S, ""));

        return userData;
    }

    public HashMap<String, String> getSecondPart() {

        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_GENDER_S, signUpSession.getString(KEY_GENDER_S, ""));
        userData.put(KEY_DATE_S, signUpSession.getString(KEY_DATE_S, ""));

        return userData;
    }

    public String getThirdPart() {
        return signUpSession.getString(KEY_PHONENUMBER_S, "");
    }

    public void setProgress(String progress) {
        editor.putString(KEY_PROGRESS, progress);
        editor.commit();
    }

    public void clearProgress() {
        editor.clear();
        editor.commit();
    }

    public String checkProgress() {
        return signUpSession.getString(KEY_PROGRESS, "None");
    }

    public boolean checkSignUp() {
        return signUpSession.getBoolean(KEY_IS_SIGNUP, false);
    }
}
