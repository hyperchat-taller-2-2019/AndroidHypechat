package com.example.hypechat.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.example.hypechat.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import androidx.core.app.NotificationCompat;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    /*@Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("INFO", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("INFO", "Message data payload: " + remoteMessage.getData());


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("INFO", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        super.onMessageReceived(remoteMessage);
    }*/

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // There are two types of messages data messages and notification  messages. Data messages are handled here in onMessageReceived whether the app is in the foreground or background. Data messages are the type traditionally used with FCM. Notification messages are only received here in onMessageReceived when the app is in the foreground. When the app is in the background an automatically generated notification is displayed.When the user taps on the notification they are returned to the app. Messages containing both notification and data payloads are treated as notification messages.
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("INFO", "Message data payload: " + remoteMessage.getData());


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null)
        {
            Log.d("INFO", "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //Display notification in notification bar
        sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }


    /**Create and show a simple notification containing the received  push Notification.
     *@param messageBody FCM message body received.
     */
    private void sendNotification(String messageTitle, String messageBody) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.hypechat.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("EDMT Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);


        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.slack_icon))
                .setSmallIcon(R.drawable.slack_icon,40)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());



    }
}


