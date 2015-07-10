package com.jiang.musicplayer.service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jiang.musicplayer.R;
import com.jiang.musicplayer.bean.MusicModel;
import com.jiang.musicplayer.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("NewApi")
public class MusicService extends Service implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener{
    private static final int MUSIC_NOTIFICATION_ID = 1550;
    private static final int MP_STATUS_ACTIVE = 0;
    private static final int MP_STATUS_INACTIVE = 1;
    private MediaPlayer mMediaPlayer;
    private Uri mUri;
    private boolean mIsPrepared;
    private MusicModel model;
    private static Notification mNotify;
    private RemoteViews mMusicRv;
    private boolean isShowNotification = false;

    private BroadcastReceiver mStopMusicReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            String str = paramIntent.getAction();
            if (TextUtils.isEmpty(str))
                return;
            if (str.equals("notification.stop.music")) {
                MusicService.this.stop(true);
                return;
            } else if (str.equals("notification.play.music")) {
                LogUtils.e("jiang", "receive play");
                MusicService.this.play();
                return;
            } else if (str.equals("notification.next.music")) {
                MusicService.this.nextOne();
                return;
            } else if (str.equals("notification.pause.music")) {
                LogUtils.e("jiang", "receive pause");
                MusicService.this.pause();
                return;
            }
        }
    };
    private int mFlag;
    private int mCurrentTime = 0;
    private int mDuration = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void play() {
        this.mFlag = MP_STATUS_ACTIVE;
        if ((mMediaPlayer != null) && (!mMediaPlayer.isPlaying())) {
            mMediaPlayer.start();
            showOrUpdateNotification(1);
        } else {

        }
    }

    private void sendDuration() {
        if ((mMediaPlayer != null) && (this.mIsPrepared)) {
            this.mDuration = mMediaPlayer.getDuration();
            Intent localIntent = new Intent();
            localIntent.setAction("com.app.duration");
            localIntent.putExtra("duration", this.mDuration);
            sendBroadcast(localIntent);
        }
        sendMusicInfo();
    }

    private void sendMusicInfo() {
        Intent localIntent = new Intent();
        localIntent.setAction("com.app.info");
        localIntent.putExtra("model", this.model);
        sendBroadcast(localIntent);
    }

    private void sendPrepare() {
        Intent localIntent = new Intent();
        localIntent.setAction("com.app.prepare");
        sendBroadcast(localIntent);
    }


    private void showOrUpdateNotification(int paramInt) {
        if (this.isShowNotification) {
            updateNotification(paramInt);
            return;
        }
        showNotification();
    }

    // 1 表示播放 2表示 暂停
    private void updateNotification(int paramInt) {
        if (paramInt != -1) {
            switch (paramInt) {
                case 1:
                    if (this.mMusicRv != null) {
                        Intent localIntent2 = new Intent("notification.pause.music");
                        PendingIntent localPendingIntent2 = PendingIntent
                                .getBroadcast(MusicService.this, 0, localIntent2, 0);
                        MusicService.this.mMusicRv.setImageViewResource(
                                R.id.play_music_iv, R.drawable.music_bar_pause_btn);
                        MusicService.this.mMusicRv.setOnClickPendingIntent(
                                R.id.play_music_iv, localPendingIntent2);
                        updateNotifyInfo();
                        MusicService.mNotify.contentView = MusicService.this.mMusicRv;
                        MusicService.this.startForeground(
                                MusicService.MUSIC_NOTIFICATION_ID,
                                MusicService.mNotify);
                    }
                    return;
                case 2:
                    if (this.mMusicRv != null) {
                        Intent localIntent2 = new Intent("notification.play.music");
                        PendingIntent localPendingIntent2 = PendingIntent
                                .getBroadcast(MusicService.this, 0, localIntent2, 0);
                        MusicService.this.mMusicRv.setImageViewResource(
                                R.id.play_music_iv, R.drawable.music_bar_play_btn);
                        MusicService.this.mMusicRv.setOnClickPendingIntent(
                                R.id.play_music_iv, localPendingIntent2);
                        updateNotifyInfo();
                        MusicService.mNotify.contentView = MusicService.this.mMusicRv;
                        MusicService.this.startForeground(
                                MusicService.MUSIC_NOTIFICATION_ID,
                                MusicService.mNotify);
                    }
                    return;

                default:
                    return;
            }
        }
    }

    protected boolean pause() {
        this.mFlag = MP_STATUS_INACTIVE;
        if ((mMediaPlayer != null) && (mMediaPlayer.isPlaying())) {
            mMediaPlayer.pause();
            showOrUpdateNotification(2);
            return true;
        } else {
            return false;
        }
    }

    protected void nextOne() {

    }

    protected void stop(boolean b) {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            removeNotification();
        }
    }

    private void removeNotification() {
        stopForeground(true);
        this.isShowNotification = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        IntentFilter notifyIntent = new IntentFilter();
        notifyIntent.addAction("notification.stop.music");
        notifyIntent.addAction("notification.next.music");
        notifyIntent.addAction("notification.play.music");
        notifyIntent.addAction("notification.pause.music");
        notifyIntent.addAction("notification.update.notify");
        registerReceiver(this.mStopMusicReceiver, notifyIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int op = intent.getIntExtra("op", -1);
            switch (op) {
                case MusicOperation.MUSIC_INIT:
                    sendDuration();
                    return super.onStartCommand(intent, Service.START_STICKY, startId);
                case MusicOperation.MUSIC_NEXT_ONE:
                    nextOne();
                    return super.onStartCommand(intent, Service.START_STICKY, startId);
                case MusicOperation.MUSIC_PAUSE:
                    pause();
                    return super.onStartCommand(intent, Service.START_STICKY, startId);
                case MusicOperation.MUSIC_PLAY:
                    this.model = (MusicModel) intent.getExtras().get("model");
                    //传送播放数据
                    if (this.model != null) {
                        this.mUri = Uri.parse(this.model.getmMusicSrc());

                    } else {
                        if (this.mUri != null) {
                            MusicService.this.play();
                        }
                    }
                    this.mIsPrepared = false;
                    mMediaPlayer.reset();
                    try {
                        mMediaPlayer.setDataSource(this, this.mUri);
                        if ((!mMediaPlayer.isPlaying()) && (this.mUri != null))
                            mMediaPlayer.prepareAsync();
                        mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mMediaPlayer.start();
                            }
                        });
                        showNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return super.onStartCommand(intent, Service.START_STICKY, startId);
                case MusicOperation.MUSIC_STOP:
                    stop(true);
                    return super.onStartCommand(intent, Service.START_STICKY, startId);
                case MusicOperation.PROGRESS_CHANGE:
                    this.mCurrentTime = intent.getExtras().getInt("progress");
                    mMediaPlayer.seekTo(this.mCurrentTime);
                    return super.onStartCommand(intent, Service.START_STICKY, startId);
                default:
                    return super.onStartCommand(intent, Service.START_STICKY, startId);
            }
        }

        if (intent == null || intent.getExtras().get("model") == null) {
            return super.onStartCommand(intent, Service.START_STICKY, startId);
        }

        return super.onStartCommand(intent, Service.START_STICKY, startId);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(0);
            mMediaPlayer.pause();
            showOrUpdateNotification(2);
        }
    }

    private void showNotification() {
        if (this.mUri == null)
            return;
        initNotify();
        startForeground(MUSIC_NOTIFICATION_ID, mNotify);
        this.isShowNotification = true;
    }

    private void initNotify() {
        mNotify = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher).getNotification();
        mNotify.icon = R.drawable.ic_launcher;
        mNotify.defaults = 4;
        Notification localNotification = mNotify;
        localNotification.flags = (0x2 | localNotification.flags);
        // 设定振动与声音
        // mNotify.defaults |= Notification.DEFAULT_SOUND;
        // mNotify.defaults |= Notification.DEFAULT_VIBRATE;
        this.mMusicRv = new RemoteViews(getPackageName(),
                R.layout.layout_notification_music);

        PendingIntent pauseIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("notification.pause.music"), 0);
        this.mMusicRv.setOnClickPendingIntent(R.id.play_music_iv, pauseIntent);

        PendingIntent stopIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("notification.stop.music"), 0);
        this.mMusicRv.setOnClickPendingIntent(R.id.stop_music_ll, stopIntent);

        updateNotifyInfo();
        mNotify.contentView = this.mMusicRv;
        // Intent localIntent;
        // PendingIntent localPendingIntent4 = PendingIntent.getActivity(
        // getApplicationContext(), 9527, localIntent, 134217728);
        // mNotify.contentIntent = localPendingIntent4;

    }

    private void updateNotifyInfo() {
        if (this.mMusicRv != null) {
            if (model != null) {
                this.mMusicRv.setTextViewText(R.id.song_name, model.getmSongName());
                this.mMusicRv.setTextViewText(R.id.singer, model.getmSinger());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeNotification();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
        if (this.mStopMusicReceiver != null)
            unregisterReceiver(this.mStopMusicReceiver);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
    //关于边缓存变播放
    private static final int READY_BUFF = 2000 * 1024;
    private static final int CACHE_BUFF = 500 * 1024;

    private void cache(){
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                FileOutputStream out = null;
//                InputStream is = null;
//
//                try {
//                    URL url = new URL(remoteUrl);
//                    HttpURLConnection httpConnection = (HttpURLConnection) url
//                            .openConnection();
//
//                    if (localUrl == null) {
//                        localUrl = Environment.getExternalStorageDirectory()
//                                .getAbsolutePath()
//                                + "/"+getPackageName()+"/"
//                                + System.currentTimeMillis() + ".mp4";
//                    }
//
//                    System.out.println("localUrl: " + localUrl);
//
//                    File cacheFile = new File(localUrl);
//
//                    if (!cacheFile.exists()) {
//                        cacheFile.getParentFile().mkdirs();
//                        cacheFile.createNewFile();
//                    }
//
//                    readSize = cacheFile.length();
//                    out = new FileOutputStream(cacheFile, true);
//
//                    httpConnection.setRequestProperty("User-Agent", "NetFox");
//                    httpConnection.setRequestProperty("RANGE", "bytes="
//                            + readSize + "-");
//
//                    is = httpConnection.getInputStream();
//
//                    mediaLength = httpConnection.getContentLength();
//                    if (mediaLength == -1) {
//                        return;
//                    }
//
//                    mediaLength += readSize;
//
//                    byte buf[] = new byte[4 * 1024];
//                    int size = 0;
//                    long lastReadSize = 0;
//
//                    mHandler.sendEmptyMessage(VIDEO_STATE_UPDATE);
//
//                    while ((size = is.read(buf)) != -1) {
//                        try {
//                            out.write(buf, 0, size);
//                            readSize += size;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        if (!isready) {
//                            if ((readSize - lastReadSize) > READY_BUFF) {
//                                lastReadSize = readSize;
//                                mHandler.sendEmptyMessage(CACHE_VIDEO_READY);
//                            }
//                        } else {
//                            if ((readSize - lastReadSize) > CACHE_BUFF
//                                    * (errorCnt + 1)) {
//                                lastReadSize = readSize;
//                                mHandler.sendEmptyMessage(CACHE_VIDEO_UPDATE);
//                            }
//                        }
//                    }
//
//                    mHandler.sendEmptyMessage(CACHE_VIDEO_END);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (out != null) {
//                        try {
//                            out.close();
//                        } catch (IOException e) {
//                            //
//                        }
//                    }
//
//                    if (is != null) {
//                        try {
//                            is.close();
//                        } catch (IOException e) {
//                            //
//                        }
//                    }
//                }
//
//            }
//        }).start();
    }
}
