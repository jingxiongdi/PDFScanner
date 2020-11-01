package com.jxd.pdfscanner.util;

import android.util.Log;

public class JXDLog {
    private static final String TAG = "PDFScanner_tag";

    public static void d(String log){
        if(AppUtil.isDebug()){
            Log.d(TAG,log);
        }
    }

    public static void d(String tag,String log){
        if(AppUtil.isDebug()){
            Log.d(tag,log);
        }
    }

    public static void e(String log){
        if(AppUtil.isDebug()){
            Log.e(TAG,log);
        }
    }

    public static void e(String tag,String log){
        if(AppUtil.isDebug()){
            Log.e(tag,log);
        }
    }
}
