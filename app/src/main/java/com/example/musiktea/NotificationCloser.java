package com.example.musiktea;

import static com.example.musiktea.Singleton.notificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotificationCloser extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Singleton.getInstance().getMediaPlayer() != null && Singleton.getInstance().getMediaPlayer().isPlaying())
            Singleton.getInstance().toggleNotificationSong();
        notificationManager.cancel(1);
        Singleton.getInstance().setNotification(null);
    }
}
