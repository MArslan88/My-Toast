package com.mhdarslan.mytoast.download;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;


import androidx.core.content.FileProvider;

import com.mhdarslan.mytoast.connectivity.HttpWeb;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class VideoDownloadManager extends AsyncTask<Void, Void, Void> {
    private Activity activity;

    boolean onlyOneVideoDownload = false;
    String videoType = "";
    onVideoListener onVideoListener;
    private DownloadDialog downloadDialog;
    int dl_progress;
    double downloadedMB;
    String apkUrl,subPath;

    public void setVideoDownloadListener(onVideoListener onVideoListener) {
        this.onVideoListener = onVideoListener;
    }

    public VideoDownloadManager() {
    }

    public VideoDownloadManager(Activity activity,
                                String apkUrl,String subPath) {
        this.activity = activity;
        this.apkUrl = apkUrl;
        this.subPath = subPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // SCREEN_TYPE = 1 is for menu screen dialog
        // SCREEN_TYPE = 2 is for config screen dialog
        downloadDialog = new DownloadDialog(activity);
        // to get round bg of dialog
        downloadDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

//        if (SCREEN_TYPE == 1) {
//            //to remove the dark bg of dialog
//            downloadDialog.getWindow().setDimAmount(0);
//        }
        downloadDialog.show();
    }


    @Override
    protected Void doInBackground(Void... voids) {

        downloadVideo(apkUrl,subPath);
//        if (CommonUtils.createCustomFolder(AppConstants.videoAddsFolder)) {
//            updateVideosIdentifiers();
//            currentVideoIndex = 0;
//            totalVideos = videoAdsList.size();
//
//        } else {
//            UIHelper.showErrorDialog(activity, "Folder creation failed", "Check for storage permissions", 1);
//        }
        return null;
    }

    private void downloadVideo(String videoUrl,String subPath) {

        String randomId = UUID.randomUUID().toString();


        activity.runOnUiThread(() -> {
//            downloadDialog.updateTotalVideos((vIndex + 1) + "/" + totalVideos);
//            downloadDialog.updateCurrentVideos("Add Video " + (vIndex + 1));
            downloadDialog.updateVideoDownload("0%");
            downloadDialog.updateProgress(0);
        });

        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(videoUrl));
        request1.setTitle("Ad Video " + 1);
        request1.setDescription("Downloading Video.. Please Wait");
        String cookie1 = CookieManager.getInstance().getCookie(videoUrl);
        request1.addRequestHeader("cookie1", cookie1);
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //***************************************new work for android 10 & Above*******************************************************
//        String fileName = randomId + " " + videoType + "^" + id + ".mp4";
//        Log.d("downloading file ", "" + fileName);
//        File file = new File(AppConstants.videoAddsFolder, fileName);
//        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/"+ randomId + subPath +".apk";
//        File file = new File(filePath);
        //request1.setDestinationUri(Uri.fromFile(file));
        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, randomId + "_"+subPath + ".apk");
        Log.d("MyApp","subPath"+subPath);
        //***************************************new work for android 10 & Above*******************************************************
        DownloadManager downloadManager1 = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        final long downloadId = downloadManager1.enqueue(request1);
        new Thread(new Runnable() {
            @SuppressLint("Range")
            @Override
            public void run() {
                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId); //filter by id which you have receieved when reqesting download from download manager
                    Cursor cursor = downloadManager1.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }

                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_RUNNING){
                        long fileSizeBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        long downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

                        if (fileSizeBytes > 0) {
//                            // Convert bytes to megabytes
                            double fileSizeMB = (double) fileSizeBytes / (1024 * 1024);
                            downloadedMB = (double) downloadedBytes / (1024 * 1024);
                            String fileSizeText = String.format("%.2f MB / %.2f MB ", downloadedMB, fileSizeMB);


                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Update the UI with the downloaded size and total size in MB
                                    downloadDialog.showFileSize(fileSizeText);

                                }
                            });
                        }
                    }

                    //when video paused dismiss the dialog
                    if (status == DownloadManager.STATUS_PAUSED){
                        downloadDialog.videoPausedWarnTxt("Downloading Failed");
                        downloading = false;
                        onVideoListener.onVideoDownloadFail(videoType, onlyOneVideoDownload);
                        downloadDialog.dismiss();
                        Log.d("MyApp","status downloading paused ");
                        break;
                    }

                    // when video failed dismiss the dialog
                    if (status == DownloadManager.STATUS_FAILED){
                        downloadDialog.videoPausedWarnTxt("Downloading Failed");
                        downloading = false;
                        downloadDialog.dismiss();
                        onVideoListener.onVideoDownloadFail(videoType, onlyOneVideoDownload);
                        Log.d("MyApp","status downloading failed ");
                        break;
                    }


                    if (!HttpWeb.isConnectingToInternet(activity)) {
//                        Log.d("corrupted file name :", "" + LocalDataManager.getInstance().getString("randomVideoId"));
//                        LocalDataManager.getInstance().putString("corruptedVideoId", randomId);
                        System.out.println("Internet not available");
                        downloadDialog.dismiss();
                        downloading = false;
                        //Log.d("Corrupted file: ", "deleted from db: " + randomId);
                        onVideoListener.onVideoDownloadFail(videoType, onlyOneVideoDownload);
                        break;
                    }

                    if (bytes_total != 0) {
                        dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadDialog.updateProgress(dl_progress);
                            downloadDialog.updateVideoDownload(dl_progress + "%");
                            if (dl_progress == 100) {
                                downloadedMB = 0.00;
                                onlyOneVideoDownload = true;
                                downloadDialog.dismiss();
                                onVideoListener.onVideoDownloadSuccess(subPath,randomId);

                            }
                        }
                    });
                    cursor.close();
                }
            }
        }).start();
    }


    public interface onVideoListener {
        void onVideoDownloadSuccess(String subPath,String randomId);

        void onVideoDownloadFail(String videoType, boolean onlyOneVideoDownload);
    }

}
