package com.example.musiktea;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotificationPlayReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Singleton.getInstance().getMediaPlayer() != null) {
            Singleton.getInstance().toggleNotificationSong();
        }
    }
}
