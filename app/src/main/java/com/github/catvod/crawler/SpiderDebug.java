package com.github.catvod.crawler;

import android.util.Log;

public class SpiderDebug {

    private static final String TAG = SpiderDebug.class.getSimpleName();

    public static void log(Throwable e) {
        Log.d(TAG, e.getMessage());
    }

    public static void log(String msg) {
        if(msg.length()>3000){
            for (int i = 0; i < msg.length(); i+=3000) {
                if(i + 3000 < msg.length()){
                    Log.d(TAG,"第"+ i +"数据:"+msg.substring(i,i+3000));
                }else{
                    Log.d(TAG,"第"+ i +"数据:"+msg.substring(i,msg.length()));
                }
            }
        }else
        Log.d(TAG, msg);
    }


}
