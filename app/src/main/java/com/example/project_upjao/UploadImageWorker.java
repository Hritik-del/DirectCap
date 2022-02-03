package com.example.project_upjao;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;
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
import java.util.List;
import java.util.Objects;

public class UploadImageWorker extends Worker {
    StorageReference storageReference;
    String gName;
    static String docUrl;
    String gDate;
    Data outputData;
    Long num = new Long(0);

    static UploadImageWorker activity;
    public UploadImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        activity = this;
    }

    @NonNull
    @Override
    public Result doWork() {

        String stringUri = getInputData().getString("image_uri");
        Uri contentUri = Uri.parse(stringUri);
        File file = new File(contentUri.getPath());
        String name = getInputData().getString("file_name");
        try {
            CloudStorage.uploadFile(activity,"app_images_full_dev", name, contentUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
                /*storageReference = FirebaseStorage.getInstance().getReference();
        String stringUri = getInputData().getString("image_uri");
        Log.v("uri", stringUri);
        Uri contentUri = Uri.parse(stringUri);
        String name = getInputData().getString("file_name");
        String cropType = getInputData().getString("cropType");
        File succUploadDir = new File(Objects.requireNonNull(getInputData().getString("successful_uploaded_dir")));
        //String succUploadDir = getInputData().getString("successful_uploaded_dir");
        getNameDate(name);

        StorageReference image = storageReference.child("pictures/").child(gName+"/").child(gDate).child(cropType+
                "/"+name);
        Log.v("imagename", name);
        Log.v("imageuri", stringUri);

        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.v("tag", "onSuccess : Uploaded Image URL is " + uri.toString());
                        File imagefile = new File(contentUri.getPath());
                        File createNewImage = new File(succUploadDir, String.valueOf(contentUri).substring(String.valueOf(contentUri).lastIndexOf('/')));
                        File source = imagefile;

                        File destination = createNewImage;
                        try
                        {
                            FileUtils.copyFile(source, destination);
                        }
                        catch (IOException e)
                        {
                            Log.v("direc", e.getMessage());
                        }
                        Log.v("direc", Boolean.toString(imagefile.exists()));
                        Log.v("direc", Boolean.toString(destination.exists()));
                        if(imagefile.exists())
                        {
                            imagefile.delete();
                        }
                        //increasing the count of number of succuploaded files.
                        try {
                            FileReader fis = new FileReader(MainActivity.succUploaded);
                            //DataInputStream in = new DataInputStream(fis);
                            BufferedReader br =
                                    new BufferedReader(fis);
                            String strLine;
                            while ((strLine = br.readLine()) != null) {
                                Log.v("myapp", strLine);
                                num = Long.parseLong(strLine);
                            }
                            Log.v("myapp", Long.toString(num));
                            num++;
                            br.close();
                        } catch (IOException e) {
                            Log.v("filepdf", e.getMessage());
                            e.printStackTrace();
                        }
                        try {
                            FileWriter fos = new FileWriter(MainActivity.succUploaded);
                            fos.write(Long.toString(num));
                            fos.close();
                        } catch (IOException e) {
                            Log.v("filepdf", e.getMessage());
                        }
                        Toast.makeText(getApplicationContext(), "Upload Successful :)", Toast.LENGTH_SHORT).show();

                        docUrl = uri.toString();

                        //result[0] = Result.success(outputData);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("tag", "onComplete: image NOT uploaded - RETRYING");
                //result[0] = Result.retry();
            }
        });
        Data.Builder outputBuilder = new Data.Builder();
        outputBuilder.putString("image_url", docUrl.toString());
        outputData = outputBuilder.build();
        //Log.v("hritik", result[0].toString());
        Log.v("hritik", docUrl.toString());
        return Result.success(outputData);*/
        return Result.success();
    }

}

