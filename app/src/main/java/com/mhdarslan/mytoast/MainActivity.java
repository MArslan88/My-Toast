package com.mhdarslan.mytoast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mhdarslan.tosty.Toaster;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toaster.showShortToast(this, "Hello world...!");
    }
}