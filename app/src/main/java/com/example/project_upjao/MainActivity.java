package com.example.project_upjao;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputLayout editText;
    TextInputLayout editTextCrop;
    Button button;
    private String filename = "MySampleFile.txt";
    private String filepath = "MyFileStorage";
    static File succUploaded;
    static File tobeUploaded;
    File myInternalFile;
    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
        List<String> permissionsToBeGranted = new ArrayList<>();

        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            if (!entry.getValue()) {

                permissionsToBeGranted.add(entry.getKey());
            }
        }
        if (permissionsToBeGranted.isEmpty()) {
           // gpsOn();
        } else {
            showSettingsDialog();
        }
        /*if (!permissionsToBeGranted.isEmpty()) {
            showSettingsDialog();
        }*/
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkRuntimePermission();
        editText = findViewById(R.id.name);
        editTextCrop = findViewById(R.id.cropType);
        button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = getExternalFilesDir("mdata").getPath()+ "/" + "Checksum";
                //File directory = new File(Environment.getExternalStorageDirectory(), "Image Capture/"+editText.getEditText().getText().toString());
                File directory = new File(filePath);
                if (!directory.exists()){
                    directory.mkdirs();
                    succUploaded = new File(directory, "Successfully uploaded.txt");
                    tobeUploaded = new File(directory, "To be uploaded.txt");
                    try {
                        FileOutputStream fos = new FileOutputStream(succUploaded);
                        fos.write("0".getBytes());
                        fos.close();
                        FileOutputStream fos1 = new FileOutputStream(tobeUploaded);
                        fos1.write("0".getBytes());
                        fos1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    File[] files = directory.listFiles();
                    Log.v("Files", "Size: "+ files.length);
                    for (int i = 0; i < files.length; i++)
                    {
                        if(i == 0){
                            tobeUploaded = files[i];
                        }
                        else if(i== 1){
                            succUploaded = files[i];
                        }
                        Log.d("Files", "FileName:" + files[i].getName());
                    }
                }

                if(!validateName())
                {
                    return;
                }
                //Toast.makeText(MainActivity.this, "Thank You!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, CameraOpen.class);
                i.putExtra("person_name", editText.getEditText().getText().toString());
                i.putExtra("cropType", editTextCrop.getEditText().getText().toString());
                startActivity(i);
            }
        });

    }

    private boolean validateName() {
        String name = editText.getEditText().getText().toString();
        if(name.isEmpty())
        {
            editText.setError("Field can't be empty");
            return false;
        }
        else
        {
            editText.setError(null);
            editText.setErrorEnabled(false);
            return true;
        }
    }

    public void checkRuntimePermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!hasPermissions(this, permissions))
            requestPermissionLauncher.launch(permissions);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showSettingsDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AlertDialogThemeMaterial);
        builder.setTitle("Need Permissions");
        builder.setCancelable(false);
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Exit App", (dialog, which) -> {
            dialog.cancel();
            finish();
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        checkRuntimePermission();
    }
}