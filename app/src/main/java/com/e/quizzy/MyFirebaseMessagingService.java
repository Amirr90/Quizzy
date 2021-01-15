package com.e.quizzy;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;

import static com.e.quizzy.NotificationClass.NOTIFICATION_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    NotificationManagerCompat managerCompat;
    private static final int PENDING_INTENT_REQ_CODE = 101;//any random number
    private static final CharSequence SUMMERY = "summary";
    private static final CharSequence BIG_CONTENT_TITLE = "big_content_title";


    private String TAG = "com.e.quizzy.MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            managerCompat = NotificationManagerCompat.from(this);

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();
            String notification_id = remoteMessage.getData().get(NOTIFICATION_ID);
            String big_content_title = remoteMessage.getData().get(BIG_CONTENT_TITLE);
            String summary = remoteMessage.getData().get(SUMMERY);
            String icon = remoteMessage.getNotification().getIcon();
            String description = remoteMessage.getNotification().getTag();

            if (big_content_title != null && !big_content_title.equals("")) {
                showNotification(title, body, click_action, notification_id, icon, description, big_content_title, summary);
            } else {
                showNotification(title, body, click_action, notification_id, icon, description);
            }

        }

    }

    private void showNotification(String title, String body, String click_action, String notification_id, String icon, String description) {
        Intent intent = new Intent(click_action);
        intent.putExtra("notification_id", notification_id);
        intent.putExtra("body", body);
        intent.putExtra("title", title);
        intent.putExtra("description", description);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                PENDING_INTENT_REQ_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap largeImage;
        try {
            URL url = new URL(icon);
            largeImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            largeImage = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        }


        Notification notification = new NotificationCompat.Builder(MyFirebaseMessagingService.this, NOTIFICATION_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setColor(Color.GREEN)
                .setLargeIcon(largeImage)
                .build();
        managerCompat.notify((int) System.currentTimeMillis(), notification);
    }


    private void showNotification(String title, String body, String click_action, String notification_id, String icon, String description, String big_content_title, String summary) {
        Intent intent = new Intent(click_action);
        intent.putExtra("notification_id", notification_id);
        intent.putExtra("body", body);
        intent.putExtra("title", title);
        intent.putExtra("description", description);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                PENDING_INTENT_REQ_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap largeImage;
        try {
            URL url = new URL(icon);
            largeImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            largeImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
        }


        Notification notification = new NotificationCompat.Builder(MyFirebaseMessagingService.this, NOTIFICATION_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setColor(Color.GREEN)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setSummaryText(summary)
                        .setBigContentTitle(big_content_title)
                        .bigText(description)).setAutoCancel(true)
                .setLargeIcon(largeImage)
                .build();
        managerCompat.notify((int) System.currentTimeMillis(), notification);
    }


}
