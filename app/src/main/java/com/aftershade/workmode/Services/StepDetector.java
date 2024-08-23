package com.aftershade.workmode.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import com.aftershade.workmode.HelperClasses.Listeners.StepsCallback;
import com.aftershade.workmode.HelperClasses.PrefsHelper;
import com.aftershade.workmode.Notifications.GeneralHelper;

public class StepDetector extends Service implements SensorEventListener {

    static StepsCallback stepsCallback;

    public StepDetector() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
            GeneralHelper.updateNotification(this, this, PrefsHelper.getInt("FSteps"));
            stepsCallback.subscribeSteps(PrefsHelper.getInt("FSteps"));

        } else {
            Toast.makeText(this, "Sensor not detected", Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (PrefsHelper.getString("TodayDate").equals(GeneralHelper.getTodayDate())) {
            PrefsHelper.putInt("Steps", (int) event.values[0]);
            PrefsHelper.putString("TodayDate", GeneralHelper.getTodayDate());
        } else {
            int storeSteps = PrefsHelper.getInt("Steps");
            int sensorSteps = (int) event.values[0];
            int finalSteps = sensorSteps - storeSteps;
            if (finalSteps > 0) {
                PrefsHelper.putInt("FSpeps", finalSteps);
                GeneralHelper.updateNotification(this, this, finalSteps);
                stepsCallback.subscribeSteps(finalSteps);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void register(StepsCallback callback) {
        stepsCallback = callback;
    }
}