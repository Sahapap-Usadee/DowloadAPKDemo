package com.jastec.dowloadapkdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import static com.jastec.dowloadapkdemo.Common.URLDownloadHHT;


public class MainActivity extends AppCompatActivity {
    private Button btn_download, btn_scanner, btn_gen,btn_pdfPrint;
    private long downloadID;

   String destination = "";


    public static final String FileNameHHT = "hhtdemo.apk";
    public static final String FileNameQrCode = "qrcodescanner.apk";
    public static final String FileNameGen = "genqrcode.apk";
    public static final String FileNamePdfPrint = "PDFPrintdemo.apk";

    DownloadManager downloadManager;

    // using broadcast method
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "inBroadcastReceiver", Toast.LENGTH_SHORT).show();
            //Fetching the download id received with the broadcast
            File fileInstall = new File(destination);
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {

                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL && !destination.equals("")) {

                        // download is successful
                        if (fileInstall.exists()) {
                            Intent install = new Intent(Intent.ACTION_VIEW);
                            install.setDataAndType(uriFromFile(getApplicationContext(), fileInstall), Common.apkMimeType);
                            //  install.setDataAndType(contentUri, "application/vnd.android.package-archive");
                            //  install.putExtra(Intent.EXTRA_STREAM, contentUri);
                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                getApplicationContext().startActivity(install);
                            } catch (Throwable e) {

                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();

                                Log.e("TAG", "Error in opening the file!");

                            }finally {
                                EnableBody(true);
                            }
                        }
                    } else if (status == DownloadManager.STATUS_FAILED) {

                        Toast.makeText(context, "STATUS Download fail", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                    }
                }

                EnableBody(true);
          //      downloadManager.remove(downloadID);


//                if (DownloadStatus.contains("Completed") && !destination.equals("")) {
//                            Toast.makeText(context, "inBroadcastReceiver_install", Toast.LENGTH_SHORT).show();
//                            // Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(destination));
//                            if (fileInstall.exists()) {
//                                Intent install = new Intent(Intent.ACTION_VIEW);
//                                install.setDataAndType(uriFromFile(getApplicationContext(), new File(destination)), "application/vnd.android.package-archive");
//                                //  install.setDataAndType(contentUri, "application/vnd.android.package-archive");
//                                //  install.putExtra(Intent.EXTRA_STREAM, contentUri);
//                                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        try {
//                            getApplicationContext().startActivity(install);
//                        } catch (Throwable  e) {
//                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
//                            e.printStackTrace();
//
//                            Log.e("TAG", "Error in opening the file!");
//                        }
//                    }
//
//                }
            }
        }
    };

    Uri uriFromFile(Context context, File file) {
        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
//        } else {
//            return Uri.fromFile(file);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
            } else {
            }
        }
//
//        //Storage Permission
//
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        btn_download = findViewById(R.id.buttonDow);
        btn_scanner = findViewById(R.id.buttonDow_scanner);
        btn_gen = findViewById(R.id.buttonDow_gen);
        btn_pdfPrint = findViewById(R.id.buttonDow_pdfPrint);
        // using broadcast method


//        fileName = "hhtdemo.apk";
//        destination += fileName;
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  fileName = "hhtdemo.apk";
             //   destination = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator;
             //   destination += fileName;
                //  if (isConnectedToServer(urlDownload, 100)) {
//                ProgressDialog progress = new ProgressDialog(MainActivity.this);
//                progress.setTitle("Loading");
//                progress.setMessage("Wait while loading...");
//                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//                progress.show();
// To dismiss the dialog


                beginDownload(GetAppPathDownload(FileNameHHT),Common.URLDownloadHHT,FileNameHHT);

                //  progress.dismiss();
//                }
//                else
//                {
//                    Toast.makeText(MainActivity.this    ,"Connot connect to Service " + urlDownload, Toast.LENGTH_LONG).show();
//                }
                Toast.makeText(MainActivity.this, "Finish loop dowload", Toast.LENGTH_LONG).show();
            }
        });
        btn_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                beginDownload(GetAppPathDownload(FileNameQrCode),Common.URLDownloadScanner,FileNameQrCode);

                Toast.makeText(MainActivity.this, "Finish loop dowload", Toast.LENGTH_LONG).show();
            }
        });

        btn_gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  if (isConnectedToServer(urlDownload, 100)) {
//                ProgressDialog progress = new ProgressDialog(MainActivity.this);
//                progress.setTitle("Loading");
//                progress.setMessage("Wait while loading...");
//                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//                progress.show();
// To dismiss the dialog


                beginDownload(GetAppPathDownload(FileNameGen),Common.URLDownloadGenerate,FileNameGen);

                //  progress.dismiss();
//                }
//                else
//                {
//                    Toast.makeText(MainActivity.this    ,"Connot connect to Service " + urlDownload, Toast.LENGTH_LONG).show();
//                }
                Toast.makeText(MainActivity.this, "Finish loop dowload", Toast.LENGTH_LONG).show();
            }
        });
        btn_pdfPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                beginDownload(GetAppPathDownload(FileNamePdfPrint),Common.URLDownloadPDFPrint,FileNamePdfPrint);

                Toast.makeText(MainActivity.this, "Finish loop dowload", Toast.LENGTH_LONG).show();
            }
        });
    }


    public boolean isConnectedToServer(String url, int timeout) {
        try {
//            URL myUrl = new URL(url);
//            URLConnection connection = myUrl.openConnection();
//
//          //  connection.setConnectTimeout(timeout);
//            connection.connect();
            InetAddress.getByName(url).isReachable(2000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // using broadcast method
        unregisterReceiver(onDownloadComplete);
    }

    private void beginDownload(String urlFile,String urlDownload,String fileName) {
        EnableBody(false);

        try {

            File file = new File(urlFile);
            Common.ChkFileExist(file);

//            File file = new File(destination);
//            if (file.exists()) {
//                file.delete();
//
//            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlDownload))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setMimeType(Common.apkMimeType)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                    .setTitle(fileName)// Title of the Download Notification
                    .setDescription("Downloading")// Description of the Download Notification
                    .setRequiresCharging(false)// Set if charging is required to begin the download
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true);// Set if download is allowed on roaming network
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.

            // using query method
//            boolean finishDownload = false;
//            int progress;
//            long t= System.currentTimeMillis();
//            long end = t+10000;
//            while (!finishDownload) {
//                if (System.currentTimeMillis() > end){
//                    downloadManager.remove(downloadID);
//                    Toast.makeText(MainActivity.this, "Download Service time out ,from " + urlDownload, Toast.LENGTH_SHORT).show();
//                    finishDownload = true;
//                    return;
//                }
//                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
//                if (cursor.moveToFirst()) {
//                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//                    switch (status) {
//                        case DownloadManager.STATUS_FAILED: {
//                            DownloadStatus = "Download Failed";
//                            finishDownload = true;
//                            break;
//                        }
//                        case DownloadManager.STATUS_PAUSED:
//                            break;
//                        case DownloadManager.STATUS_PENDING:
//                            break;
//                        case DownloadManager.STATUS_RUNNING: {
//                            final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//                            if (total >= 0) {
//                                final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                                progress = (int) ((downloaded * 100L) / total);
//                                // if you use downloadmanger in async task, here you can use like this to display progress.
//                                // Don't forget to do the division in long to get more digits rather than double.
//                                //  publishProgress((int) ((downloaded * 100L) / total));
//                            }
//                            break;
//                        }
//                        case DownloadManager.STATUS_SUCCESSFUL: {
//                            progress = 100;
//                            // if  use aysnc task
//                            // publishProgress(100);
//                            finishDownload = true;
//                            DownloadStatus = "Download Completed";
//                            //Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
//                            break;
//                        }
//                    }
//                }
//            }

        } catch (Throwable e) {
            downloadManager.remove(downloadID);
            EnableBody(true);
            e.printStackTrace();
        }
    }



    public void EnableBody(boolean bool) {

        btn_download.setEnabled(bool);
        btn_scanner.setEnabled(bool);
        btn_gen.setEnabled(bool);
        btn_pdfPrint.setEnabled(bool);
    }


    private String GetAppPathDownload(String appName){
        destination = Common.getAppPathDownload(MainActivity.this ) + appName;
        return destination;
    }
}
