package com.example.project_upjao;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Objects;

public class UploadImageWorker extends Worker {
    StorageReference storageReference;
    String gName;
    static String docUrl;
    String gDate;
    Data outputData;
    Long num = new Long(0);

    public UploadImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        storageReference = FirebaseStorage.getInstance().getReference();
        String stringUri = getInputData().getString("image_uri");
        Log.v("uri", stringUri);
        Uri contentUri = Uri.parse(stringUri);
        String name = getInputData().getString("file_name");
        String cropType = getInputData().getString("cropType");
        String imageFileName = getInputData().getString("imageFileName");
        File succUploadDir = new File(Objects.requireNonNull(getInputData().getString("successful_uploaded_dir")));
        //String succUploadDir = getInputData().getString("successful_uploaded_dir");
        getNameDate(name);

        StorageReference image = storageReference.child("pictures/").child(gName+"/").child(gDate).child(cropType+
                "/"+name);
        Log.v("nameanddate", gName+" "+gDate);
        Log.v("imagename", name);
        Log.v("imageuri", stringUri);

        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.v("tag", "onSuccess : Uploaded Image URL is " + uri.toString());
                        //File imagefile = new File(contentUri.getPath());
                        File imagefile = new File(succUploadDir+"/"+"To Be Uploaded"+"/", imageFileName);
                        File createNewImage = new File(succUploadDir+"/"+"/Successfully Uploaded"+"/", imageFileName);
                        Log.v("direc", Boolean.toString(imagefile.exists()));
                        Log.v("direc", imageFileName);
                        /*File source = imagefile;

                        File destination = createNewImage;
                        try
                        {
                            FileUtils.copyFile(imagefile, createNewImage);
                        }
                        catch (IOException e)
                        {
                            Log.v("direc", e.getMessage());
                        }

                        if(imagefile.exists())
                        {
                            imagefile.delete();
                        }*/
                        //increasing the count of number of succuploaded files.
                        /*try {
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
                        }*/
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
        return Result.success();
    }


    public void getNameDate(String imageName)
    {
        StringBuilder name = new StringBuilder();
        StringBuilder date = new StringBuilder();
        int i = 0;
        for(i = 0; i < imageName.length(); i++)
        {
            if(imageName.charAt(i) == '$')
                break;
            if(imageName.charAt(i) != '$')
            {
                name.append(imageName.charAt(i));
            }
        }
        i++;
        for(; i < imageName.length(); i++)
        {
            if(imageName.charAt(i) == '$')
                break;
            if(imageName.charAt(i) != '$')
            {
                date.append(imageName.charAt(i));
            }
        }
        date.insert(4, '-');
        date.insert(7, '-');
        gName = name.toString();
        gDate = date.toString();
    }
}

