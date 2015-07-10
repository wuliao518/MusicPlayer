package com.jiang.musicplayer.utils;

import android.util.Log;

import com.jiang.musicplayer.Constants;

/**
 * Created by Administrator on 2015/7/9 0009.
 */
public class LogUtils {
    private static final String TAG="jiang";
    public static void e(String tag,String message){
        if(Constants.DEBUG)
            Log.e(tag,message);
    }
    public static void e(String message){
        if(Constants.DEBUG)
            Log.e(TAG,message);

    }
    public static void i(String tag,String message){
        if(Constants.DEBUG)
            Log.i(tag,message);
    }
    public static void i(String message){
        if(Constants.DEBUG)
            Log.i(TAG,message);

    }
}
