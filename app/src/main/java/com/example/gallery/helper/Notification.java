package com.example.gallery.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gallery.MainActivity;
import com.example.gallery.R;

public class Notification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title=intent.getStringExtra("title");
        String message=intent.getStringExtra("mess");
        String CHANNEL_ID=intent.getStringExtra("channel");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_album_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
// notificationId is a unique int for each notification that you must define.
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify((int)Long.parseLong(CHANNEL_ID), builder.build());
    }
}
