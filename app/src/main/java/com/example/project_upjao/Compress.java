package com.example.project_upjao;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class Compress extends Worker {

    public Compress(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        String stringUri = getInputData().getString("image_uri");
        Log.v("StringUri", stringUri);
        File f = new File(stringUri);
        Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        try  {
            FileOutputStream out = new FileOutputStream(f);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            out.close();
        } catch (IOException e) {
            Log.v("bitmaperror", e.getMessage());
        }

        return Result.success();
    }



}


