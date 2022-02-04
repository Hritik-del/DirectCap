package com.example.project_upjao;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.core.ApiFuture;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class UploadImageWorker extends Worker {
    static UploadImageWorker activity;
    public UploadImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        activity = this;
    }

    @NonNull
    @Override
    public Result doWork() {
        File storageDir = new File(Objects.requireNonNull(getInputData().getString("successful_uploaded_dir")));
        String name = getInputData().getString("file_name");
        //createBucketWithStorageClassAndLocation("analyticsupjao", "hritikagrahari785");
        try {
            CloudStorage.uploadFile(activity,"app_images_full_dev", name, storageDir);
        } catch (Exception e) {
            Log.v("uploadcheckgcp", e.getMessage());
        }
        FirestoreOptions firestoreOptions =
                null;
        try {
            Context context = activity.getApplicationContext();
            AssetManager am = context.getAssets();
            InputStream pkc12Stream = am.open("jsonfile.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(pkc12Stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            /*GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("D:\\Android_Projects\\Project_Upjao\\app\\src\\main\\assets\\jsonfile.json"))
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));*/

            firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
                    .setProjectId("analyticsupjao")
                    .setCredentials(credentials)
                    .build();
            Log.v("uploadcheckgcp", "6");

        } catch (IOException e) {
            Log.v("uploadcheckgcp", e.getMessage());
        }
        Log.v("uploadcheckgcp", firestoreOptions.toString());
        try {
            Firestore db = firestoreOptions.getService();
            DocumentReference docRef = db.collection("users").document("alovelace");
            // Add document data  with id "alovelace" using a hashmap
            Map<String, Object> data = new HashMap<>();
            data.put("first", "Ada");
            data.put("last", "Lovelace");
            data.put("born", 1815);
            //asynchronously write data
            ApiFuture<WriteResult> result = docRef.set(data);
            // ...
            // result.get() blocks on response
            try {
                Log.v("uploadcheckgcp", "Update time : " + result.get().getUpdateTime());
            } catch (ExecutionException e) {
                Log.v("uploadcheckgcp1", e.getMessage());
            }

        }catch (Exception e){
            Log.v("uploadcheckgcp2", e.getMessage());
        }
        Log.v("uploadcheckgcp", "7");

        return Result.success();
    }

    public static void createBucketWithStorageClassAndLocation(String projectId, String bucketName) {

        try {
            Context context = activity.getApplicationContext();
            AssetManager am = context.getAssets();
            InputStream pkc12Stream = am.open("analyticsupjao-adcf1e4faf6c.p12");
            GoogleCredentials credentials = GoogleCredentials.fromStream(pkc12Stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();

            // See the StorageClass documentation for other valid storage classes:
            // https://googleapis.dev/java/google-cloud-clients/latest/com/google/cloud/storage/StorageClass.html
            StorageClass storageClass = StorageClass.COLDLINE;

            // See this documentation for other valid locations:
            // http://g.co/cloud/storage/docs/bucket-locations#location-mr
            String location = "ASIA";

            Bucket bucket =
                    storage.create(
                            BucketInfo.newBuilder(bucketName)
                                    .setStorageClass(storageClass)
                                    .setLocation(location)
                                    .build());

            Log.v("uploadcheckgcp",
                    "Created bucket "
                            + bucket.getName()
                            + " in "
                            + bucket.getLocation()
                            + " with storage class "
                            + bucket.getStorageClass());
        } catch (Exception e) {
            Log.v("uploadcheckgcp", e.getMessage());
        }
    }
}

