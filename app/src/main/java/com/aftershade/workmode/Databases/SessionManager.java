package com.aftershade.workmode.Databases;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SessionManager {

    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DATE = "dateOfBirth";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_SESSIONEMAIL = "email";
    public static final String KEY_SESSIONPASSWORD = "password";
    public static final String KEY_SESSIONBIO = "bio";


    //User session variables
    private static final String IS_LOGIN = "IsLoggedIn";
    //Remember Me Variables
    private static final String IS_REMEMBERME = "IsRememberMe";
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;


    public SessionManager(Context context, String sessionName) {
        this.context = context;
        userSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = userSession.edit();
    }

    public void createLoginSession(String fullname, String email, String phoneNumber,
                                   String password, String age, String gender){

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONENUMBER, phoneNumber);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_DATE, age);
        editor.putString(KEY_GENDER, gender);

        editor.commit();
    }

    public void updateLoginSession(String fullname, String email, String phoneNumber,
                                   String age, String gender) {

        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONENUMBER, phoneNumber);
        editor.putString(KEY_DATE, age);
        editor.putString(KEY_GENDER, gender);

        editor.commit();

    }

    public void createRememberMeSession(String email, String password){
        editor.putBoolean(IS_REMEMBERME, true);

        editor.putString(KEY_SESSIONEMAIL, email);
        editor.putString(KEY_SESSIONPASSWORD, password);
        editor.commit();

    }

    //Checks the user login session
    public boolean checkLogin() {
        return userSession.getBoolean(IS_LOGIN, false);
    }

    //Checks the remember session
    public boolean checkRememberMe() {
        return userSession.getBoolean(IS_REMEMBERME, false);
    }

    //logs user out from a session
    public void logooutUserFromSession() {
        editor.clear();
        editor.commit();
    }

    //gets remember me details from the session
    public HashMap<String, String> getRememberMeDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_SESSIONEMAIL, userSession.getString(KEY_SESSIONEMAIL, null));
        userData.put(KEY_SESSIONPASSWORD, userSession.getString(KEY_SESSIONPASSWORD, null));

        return userData;
    }

    public HashMap<String, String> getLoginDetails() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_SESSIONEMAIL, userSession.getString(KEY_SESSIONEMAIL, null));
        userData.put(KEY_FULLNAME, userSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_PHONENUMBER, userSession.getString(KEY_PHONENUMBER, null));
        userData.put(KEY_DATE, userSession.getString(KEY_DATE, null));
        userData.put(KEY_GENDER, userSession.getString(KEY_GENDER, null));
        userData.put(KEY_SESSIONBIO, userSession.getString(KEY_SESSIONBIO, null));

        return userData;
    }


    //Returns fullname of user from a session
    public String getFullname() {
        return userSession.getString(KEY_FULLNAME, "");
    }

    //Returns the email from the session
    public String getEmail() {
        return userSession.getString(KEY_EMAIL, "");
    }

    //Returns the password from a session
    public String getPassword() {
        return userSession.getString(KEY_PASSWORD, "");
    }

}
