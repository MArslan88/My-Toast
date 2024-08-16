package com.mhdarslan.mytoast.download;

import android.app.Dialog;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mhdarslan.mytoast.R;

public class DownloadDialog extends Dialog {
    private TextView videoDownload;
    private TextView totalVideos;
    private TextView currentVideos;
    private TextView fileSize;
    private TextView videoStuckWarnTxt;
    private ProgressBar progressBar;
    private int SCREEN_TYPE;
    private TextView internetSpeedTextView;
    private final Handler handler = new Handler();
    long previousRxBytes = 0;
    long previousTimeStamp = 0;

    public DownloadDialog(@NonNull Context context) {
        super(context);

//        if (SCREEN_TYPE == 1){
//            setContentView(R.layout.menu_screen_dialog);
//            Window window = getWindow();
//            if (window != null) {
//                WindowManager.LayoutParams params = window.getAttributes();
//                params.gravity = Gravity.TOP | Gravity.CENTER;
//                params.x = 0; // Adjust these values as needed
//                params.y = 0; // Adjust these values as needed
//                window.setAttributes(params);
//            }
//        }else if (SCREEN_TYPE == 2){
//
//        }
        setContentView(R.layout.config_download_dialog);

        videoDownload = findViewById(R.id.videoDownload);
        totalVideos = findViewById(R.id.textView6);
        currentVideos = findViewById(R.id.textView15);
        fileSize = findViewById(R.id.fileSizeTxt);
        internetSpeedTextView = findViewById(R.id.internetSpdTxt);
        videoStuckWarnTxt = findViewById(R.id.textView32);
        progressBar = findViewById(R.id.downloadProgress);

        // disable dialog outside touch
        setCanceledOnTouchOutside(false);
        startUpdatingInternetSpeed();
    }
    private void startUpdatingInternetSpeed() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String speed = getInternetSpeed();
                internetSpeedTextView.setText("Internet Speed: " + speed);
                handler.postDelayed(this, 1000); // Update every second
            }
        }, 1000);
    }
    public String getInternetSpeed() {

        long currentRxBytes = TrafficStats.getTotalRxBytes();
        long currentTimeStamp = System.currentTimeMillis();

        if (previousRxBytes == 0) {
            previousRxBytes = currentRxBytes;
            previousTimeStamp = currentTimeStamp;
            return "Calculating...";
        }

        long dataConsumed = currentRxBytes - previousRxBytes;
        long timeInterval = currentTimeStamp - previousTimeStamp;

        if (timeInterval == 0) {
            return "Calculating...";
        }

        long speed = (dataConsumed * 1000) / timeInterval; // Speed in bytes per second

        previousRxBytes = currentRxBytes;
        previousTimeStamp = currentTimeStamp;

        if (speed >= 1024 * 1024) {
            return String.format("%.2f MB/s", (float) speed / (1024 * 1024));
        } else if (speed >= 1024) {
            return String.format("%.2f KB/s", (float) speed / 1024);
        } else {
            return speed + " B/s";
        }
    }


    public void updateProgress(int progress) {
        progressBar.setProgress(progress,true);
    }
    public void updateVideoDownload(String text) {
        videoDownload.setText(text);
    }

    public void updateTotalVideos(String text) {
        totalVideos.setText(text);
    }

    public void updateCurrentVideos(String text) {
        currentVideos.setText(text);
    }

    public void videoPausedWarnTxt(String text) {videoStuckWarnTxt.setText(text);}
    public void showFileSize(String text) {fileSize.setText(text);}
}
