package com.mhdarslan.mytoast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainAct extends AppCompatActivity {
    Button update_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        update_btn = findViewById(R.id.update_btn);

        update_btn.setOnClickListener(view -> {
            
        });




    }
}