package com.example.project_upjao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    TextInputLayout editText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.name);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateName())
                {
                    return;
                }
                Toast.makeText(MainActivity.this, "Thank You!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, CameraOpen.class);
                i.putExtra("person_name", editText.getEditText().getText().toString());
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