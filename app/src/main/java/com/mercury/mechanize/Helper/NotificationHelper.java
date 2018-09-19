package com.mercury.mechanize.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.mercury.mechanize.R;

public class NotificationHelper extends ContextWrapper{


    private static final String GITAU_CHANNEL_ID ="com.mercury.mechanize.GITAU";
    private static final String GITAU_CHANNEL_NAME ="GITAU Mechanize";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel gitauChannels = new NotificationChannel(GITAU_CHANNEL_ID,
                GITAU_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        gitauChannels.enableLights(true);
        gitauChannels.enableVibration(true);
        gitauChannels.setLightColor(Color.GRAY);
        gitauChannels.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(gitauChannels);

    }

    public NotificationManager getManager() {
        if(manager == null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getmechanizeNotification(String title, String content, PendingIntent contentIntent,
                                                         Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),GITAU_CHANNEL_ID)
                .setContentText(content)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_stat_name);
    }
}
