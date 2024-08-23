package com.aftershade.workmode.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.aftershade.workmode.MainActivity;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.BottomNav;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GeneralHelper {

    public static String getTodayDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        return sdf.format(date);
    }

    

    public static void updateNotification(Context context, Service service, int step) {

        final int NOTIFICATION_ID = 7873;
        Notification.Builder builder = new Notification.Builder(context, "Get_FIT_Step_Counter");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, BottomNav.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = builder
                .setContentTitle("Get_Fit Steps Counter")
                .setContentText(String.valueOf(step))
                .setTicker(String.valueOf(step))
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setStyle(new Notification.BigTextStyle().bigText("Step Counter"))
                .setStyle(new Notification.BigTextStyle().bigText(String.valueOf(step)))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setProgress(2000, step, true)
                .setPriority(Notification.PRIORITY_MIN)
                .build();

        service.startForeground(NOTIFICATION_ID, notification);
        // Set Service to run in the Foreground
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    public static String getCalories(int steps) {
        int cal = (int) (steps * 0.045);
        return cal + " calories ";
    }

    public static String getDistanceCovered(int steps) {
         int feet = (int) (steps * 2.5);
         double distance = feet/3.281;
         double finalDistance = Double.parseDouble(String.format("%.2f", distance));
         return String.valueOf(finalDistance);
    }

}
