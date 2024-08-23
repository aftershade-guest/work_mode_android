package com.aftershade.workmode.HelperClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class HelperMethods {

    public static void updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> statusMap = new HashMap<>();
        statusMap.put("time", saveCurrentTime);
        statusMap.put("date", saveCurrentDate);
        statusMap.put("state", state);
        statusMap.put("showOfflineStatus", state);

        rootRef.child("userState")
                .updateChildren(statusMap);

    }

}
