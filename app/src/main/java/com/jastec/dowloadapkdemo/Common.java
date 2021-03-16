package com.jastec.dowloadapkdemo;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Common {


    public static final String URLDownloadHHT = "http://192.168.1.90:5100/api/Dowload";
    public static final String URLDownloadScanner = "http://192.168.1.90:5100/api/Dowload/Scanner";
    public static final String URLDownloadGenerate = "http://192.168.1.90:5100/api/Dowload/Generate";
    public static final String URLDownloadPDFPrint = "http://192.168.1.90:5100/api/Dowload/PDFPrint";

    public static final String apkMimeType = "application/vnd.android.package-archive";


    public static  String getAppPathDownload(Context context)
    {

        File dir = new File( context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator);
        if(!dir.exists())
            dir.mkdir();
        return dir.getPath()+ File.separator ;
    }

    public static void ChkFileExist(File file) {
        if (file.exists()) {
            file.delete();

        }
    }
}
