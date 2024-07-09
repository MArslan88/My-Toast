package com.mhdarslan.mytoast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainAct extends AppCompatActivity {
    Button update_btn;
    TextView textView;
    String versionName;
    int myVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        update_btn = findViewById(R.id.update_btn);
        textView = findViewById(R.id.textView);
        appVersionCode();

        textView.setText("Version: " + versionName);

        update_btn.setOnClickListener(view -> {
            updateApk();
        });




    }

    private void appVersionCode() {
        try{
            versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            myVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateApk() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://raw.githubusercontent.com/MArslan88/My-Toast/main/app/src/main/res/raw/my_update.json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            System.out.println("Response: " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            int versionCode = jsonObject.getInt("versionCode");
                            String versionName = jsonObject.getString("versionName");
                            String apkUrl = jsonObject.getString("apkUrl");

                            // Use the data from the JSON object
                            System.out.println("Version Code: " + versionCode);
                            System.out.println("Version Name: " + versionName);
                            System.out.println("APK URL: " + apkUrl);
                            if(myVersionCode < versionCode){
                                Toast.makeText(MainAct.this, "Update is Available", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MainAct.this, "Your app is already updated", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}