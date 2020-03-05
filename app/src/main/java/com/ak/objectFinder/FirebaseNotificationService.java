package com.ak.objectFinder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data;
        Log.e("scott", "got");
        // Check if message contains a data payload.
        String requestId, callId;
        if (remoteMessage.getData().size() > 0) {
            data = remoteMessage.getData();
            requestId = data.get("requestId");
            callId = data.get("callId");
            if (requestId != null) {
                FirebaseAPI.getRequesterID(requestId, new FirebaseAPI.GetInfoCallback<String>() {
                    @Override
                    public void onSuccess(String info) {
                        Log.e("scott", "info" + info);
                        if (info.equals(FirebaseAPI.getCurrentUID())) {
                            createTextNotification(requestId);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
            if (callId != null) {
                FirebaseAPI.getCallerID(callId, new FirebaseAPI.GetInfoCallback<String>() {
                    @Override
                    public void onSuccess(String info) {
                        if (!info.equals(FirebaseAPI.getCurrentUID())) {
                            createCallNotification(callId);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }


    }

    public void createCallNotification(String callId) {
        Intent intent = new Intent(this, VideoAgorio.class);
        intent.putExtra("callId", callId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "eyespy")
                .setSmallIcon(R.drawable.baseline_live_help_black_48)
                .setContentTitle("Answer a Call For Help")
                .setContentText("Tap here to video chat with someone who needs your help")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int hash = callId.hashCode();
        notificationManager.notify(hash, builder.build());
        listenAndCancelCall(callId, hash);

    }

    public void listenAndCancelCall(String callId, int hash) {
        FirebaseAPI.listenForHelpResponseCall(callId, new FirebaseAPI.GetInfoCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean info) {
                if (info) {
                    String ns = Context.NOTIFICATION_SERVICE;
                    NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                    nMgr.cancel(hash);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public void createTextNotification(String requestId) {
        Intent intent = new Intent(this, HelpSignActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("requestId", requestId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "eyespy")
                .setSmallIcon(R.drawable.baseline_live_help_black_48)
                .setContentTitle("Help Identify Text")
                .setContentText("Tap here to help someone read some text")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int hash = requestId.hashCode();
        notificationManager.notify(hash, builder.build());
        Log.e("scott", "notify");
        listenAndCancelText(requestId, hash);
    }

    public void listenAndCancelText(String requestId, int hash) {
        FirebaseAPI.listenForHelpResponseText(requestId, new FirebaseAPI.GetInfoCallback<String>() {
            @Override
            public void onSuccess(String info) {
                if (!info.equals("Waiting for reply...")) {
                    Log.e("scott", "cancel");
                    String ns = Context.NOTIFICATION_SERVICE;
                    NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                    nMgr.cancel(hash);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

}
