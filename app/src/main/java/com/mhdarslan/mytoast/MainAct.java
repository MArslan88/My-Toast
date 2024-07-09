package com.mhdarslan.mytoast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;

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



    public void downloadApk(Context context, String url, String subPath) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Convert the response to a Uri
                        Uri uri = Uri.parse(url);

                        // Download the APK
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle("Downloading APK");
                        request.setDescription("Downloading the updated APK.");
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath +".apk");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        downloadManager.enqueue(request);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void installApk(Context context) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/L3-Craft-MenuBoard-V4.apk";
        File file = new File(filePath);
        if (file.exists()) {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
    }
}