package com.ak.objectFinder;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data;
        String requestId = null;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            data = remoteMessage.getData();
            requestId = data.get("requestId");

        }

        if (requestId != null) {
            Intent intent = new Intent(this, HelpSignActivity.class);
            intent.putExtra("requestId", requestId);
            startActivity(intent);
        }


    }
}
