package com.ak.objectFinder;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            data = remoteMessage.getData();
            final String requestId = data.get("requestId");
            FirebaseAPI.getRequesterID(requestId, new FirebaseAPI.GetInfoCallback<String>() {
                @Override
                public void onSuccess(String info) {
                    if (!info.equals(FirebaseAPI.getCurrentUID())) {
                        createNotification(requestId);
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }


    }

    public void createNotification(String requestId) {
        Intent intent = new Intent(this, HelpSignActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("requestId", requestId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "eyespy")
                .setSmallIcon(R.drawable.baseline_live_help_black_48)
                .setContentTitle("Help Identify Text!")
                .setContentText("Tap here to help someone read some text")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }

}
