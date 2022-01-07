package com.example.project_upjao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextInputLayout editText;
    TextInputLayout editTextCrop;
    Button button;
    private String filename = "MySampleFile.txt";
    private String filepath = "MyFileStorage";
    static File succUploaded;
    static File tobeUploaded;
    File myInternalFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    private boolean validateName()
    {
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
}