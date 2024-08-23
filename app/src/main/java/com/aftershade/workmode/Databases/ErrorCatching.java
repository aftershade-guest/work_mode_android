package com.aftershade.workmode.Databases;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseError;

public class ErrorCatching {

    private final Context context;

    public ErrorCatching(Context context) {
        this.context = context;
    }

    public void onError(DatabaseError error) {

        if (error.getCode() == DatabaseError.NETWORK_ERROR) {
            Toast.makeText(context, "Network error occurred. \nPlease check your network and try again.", Toast.LENGTH_SHORT).show();
        } else if (error.getCode() == DatabaseError.EXPIRED_TOKEN || error.getCode() == DatabaseError.INVALID_TOKEN) {
            Toast.makeText(context, "It seems an unexpected error occurred. \nOur developers are working on it.", Toast.LENGTH_SHORT).show();
        } else if (error.getCode() == DatabaseError.UNKNOWN_ERROR) {
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onException(Exception e) {
        if (e instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(context, "A user with the same email or phone already exists.", Toast.LENGTH_SHORT).show();
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(context, "Wrong email address or password", Toast.LENGTH_SHORT).show();
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(context, "This account has already been disabled or deleted, please use or create another account or contact us to get help on this issue", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
