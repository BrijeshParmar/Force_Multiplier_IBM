package com.example.gb.forcemultiplier;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.provider.FirebaseInitProvider;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("fmultiplier", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public String sendCode(){
        String token1 = FirebaseInstanceId.getInstance().getToken();
        return token1;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        final RemoteMessage myRemoteMsg = remoteMessage;
        Log.d("fcm", "From: " + remoteMessage.getFrom());
        Log.d("fcm", "Title: " + remoteMessage.getNotification().getTitle());
        Log.d("fcm", "Notification Message Body: " + remoteMessage.getNotification().getBody());

    }
}

