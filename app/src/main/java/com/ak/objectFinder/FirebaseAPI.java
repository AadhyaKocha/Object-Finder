package com.ak.objectFinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseAPI {
    public static void setNotificationStatus(Boolean shouldBeNotified) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("users").child(userId).child("notify").setValue(shouldBeNotified);
    }

    public static void createUser() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("users").child(userId).child("notify").setValue(false);
    }
}
