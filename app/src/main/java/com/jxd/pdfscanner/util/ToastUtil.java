package com.jxd.pdfscanner.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private Toast t = null;
    private Context context = null;
    private static ToastUtil toastUtil;
    public static ToastUtil getInstance(){
        if(toastUtil == null){
            toastUtil = new ToastUtil();
        }
        return toastUtil;
    }

    public void initContext(Context context){
        this.context = context;
    }

    public void showToast(String text){
        if(t == null){
            t = Toast.makeText(context,text,Toast.LENGTH_SHORT);
        }else {
            t.setText(text);
        }
        t.show();
    }
}
