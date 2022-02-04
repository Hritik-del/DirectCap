package com.example.project_upjao;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MySecondaryProject {
    private static FirebaseApp INSTANCE;

    public static FirebaseApp getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = getSecondProject(context);
        }
        return INSTANCE;
    }

    private static FirebaseApp getSecondProject(Context context) {
        FirebaseOptions options1 = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyA7u0bBFuTXlJOmX_nNhyJUlAmmYrRAJRw")
                .setApplicationId("1:883889477600:android:6ec7cc2cf07fdff901fd85")
                .setProjectId("testapp-f18ba")
                // setDatabaseURL(...)      // in case you need firebase Realtime database
                .setStorageBucket("testapp-f18ba.appspot.com")    // in case you need firebase storage MySecondaryProject
                .build();

        FirebaseApp.initializeApp(context, options1, "admin");
        return FirebaseApp.getInstance("admin");
    }
}
