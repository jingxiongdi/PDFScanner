package com.jxd.pdfscanner.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class AppUtil {
    private static Boolean isDebug = null;

    public static boolean isDebug(){
        return isDebug == null?false:isDebug.booleanValue();
    }

    /**
     * 需要在应用初始化的时候去调用
     * @param context
     */
    public static void initIsDebug(Context context){
        if(isDebug==null){
            isDebug = context.getApplicationInfo()!=null&&
                    (context.getApplicationInfo().flags& ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        }
    }
}
