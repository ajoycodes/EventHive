package com.example.eventhive;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

public class EventHiveApplication extends Application {
    private static final String TAG = "EventHiveApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "EventHiveApplication onCreate() called");

        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this);

            // Enable App Check in DEBUG mode (bypasses reCAPTCHA for development)
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance());

            Log.d(TAG, "✅ Firebase initialized successfully");
            Log.d(TAG, "✅ App Check enabled in DEBUG mode");
            Log.d(TAG, "Firebase project: " + FirebaseApp.getInstance().getOptions().getProjectId());
        } catch (Exception e) {
            Log.e(TAG, "❌ Firebase initialization FAILED", e);
        }
    }
}
