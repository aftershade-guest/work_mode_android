package com.aftershade.workmode.Databases;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.TelephonyNetworkSpecifier;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.snackbar.Snackbar;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkCheck {

    //private static boolean isNetworkConnected = false;

    Context context;
    public static boolean isNetworkConnected;

    public NetworkCheck(Context context) {
        this.context = context;
        isNetworkConnected = false;
    }



    //Registers the network callback and modifies the isNetworkConnected variable
    public void regiserNetworkCallback() {

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

            /*NetworkRequest.Builder builder = new NetworkRequest.Builder();

            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

            NetworkRequest networkRequest = builder.build();
            networkRequest.*/

            /*if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                    || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                isNetworkConnected = true;

            }*/



            cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    isNetworkConnected = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isNetworkConnected = false;
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {

                    isNetworkConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

                }
            });


        } catch (Exception e) {
            isNetworkConnected = false;
        }
    }

}
