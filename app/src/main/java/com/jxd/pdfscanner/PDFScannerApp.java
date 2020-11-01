package com.jxd.pdfscanner;

import android.app.Application;

import com.jxd.pdfscanner.util.AppUtil;
import com.jxd.pdfscanner.util.JXDLog;
import com.jxd.pdfscanner.util.ToastUtil;

public class PDFScannerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.initIsDebug(this);//初始化版本是否debug
        ToastUtil.getInstance().initContext(this);
        JXDLog.d("PDFScannerApp onCreate "+getPackageName());
    }

}
