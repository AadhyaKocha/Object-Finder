package com.ak.objectFinder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String from = remoteMessage.getFrom();
        Map<String, String> data;
        String body;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            data = remoteMessage.getData();

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            body = remoteMessage.getNotification().getBody();
        }

        //TODO: Navigage to helper intent

    }
}
