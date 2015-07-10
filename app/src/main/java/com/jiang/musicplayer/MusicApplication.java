package com.jiang.musicplayer;

import android.app.Application;

/**
 * Created by Wuiliao on 2015/7/9 0009.
 */
public class MusicApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        CustomCrashHandler mCustomCrashHandler = CustomCrashHandler.getInstance();
        mCustomCrashHandler.setCustomCrashHanler(getApplicationContext());
    }
}
