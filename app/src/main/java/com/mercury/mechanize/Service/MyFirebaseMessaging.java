package com.mercury.mechanize.Service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mercury.mechanize.Helper.NotificationHelper;
import com.mercury.mechanize.R;
import com.mercury.mechanize.RatingActivity;

import java.util.Map;


public class MyFirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        if(remoteMessage.getData() != null) {
            Map<String,String> data = remoteMessage.getData();
            String title = data.get("title");
            final String message = data.get("message");

            if (title.equals("Cancel")) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyFirebaseMessaging.this, message, Toast.LENGTH_SHORT).show();

                    }
                });
            } else if (title.equals("Arrived")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    showArrivedNotificationAPI26(message);
                else
                    showArrivedNotification(message);


            } else if (title.equals("Job Complete")) {
                openRateActivity(message);
            }
        }
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showArrivedNotificationAPI26(String body) {
        PendingIntent contentintent = PendingIntent.getActivity(getBaseContext(),
                0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getmechanizeNotification("Call A'Mechanic",body,contentintent,defaultSound);

        notificationHelper.getManager().notify(1,builder.build());

    }

    private void openRateActivity(String body) {
        Intent intent =new Intent(this, RatingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    private void showArrivedNotification(String body) {
        PendingIntent contentintent = PendingIntent.getActivity(getBaseContext(),
        0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Call A'Mechanic")
                .setContentText(body)
                .setContentIntent(contentintent);
        NotificationManager manager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());
    }


}

