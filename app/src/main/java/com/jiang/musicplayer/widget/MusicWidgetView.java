package com.jiang.musicplayer.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jiang.musicplayer.R;

public class MusicWidgetView extends LinearLayout {
    public static final int LOOP_ORDER = 0;
    public static final int LOOP_RANDOM = 1;
    public static final int TYPE_BAND_NONE = 1;
    public static final int TYPE_BAND_SERVICE = 2;
    private String album_name;
    private String author;
    private String cover;
    public boolean isNeedAutoDismiss = true;
    private TextView mArtistTv;
    private Context mContext;
    private int mCurPosition;
    public int mCurType = 0;
    private int mDuration;
    public int mFlag;
    private ProgressBar mLoadingPb;
    private TextView mMuiscInfoTv;
    private TextView mMusicNameTv;
    private ImageButton mNextIb;
    private final ThreadLocal<ImageButton> mPlayIb = new ThreadLocal<>();
    private ImageButton mPlayModeIb;
    private SeekBar mSeekbar;
    protected BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            String str1 = paramIntent.getAction();
            if (str1.equals("com.jiang.currentTime")) {
                MusicWidgetView.this.mSeekbar.setProgress(MusicWidgetView.this.mCurPosition);
                MusicWidgetView.this.mSeekbar.setSecondaryProgress(MusicWidgetView.this.mCurPosition);
                if (MusicWidgetView.this.mCurPosition != 0)
                    MusicWidgetView.this.mLoadingPb.setVisibility(View.GONE);
            } else if (str1.equals("com.jiang.duration")) {
                MusicWidgetView.this.mSeekbar.setMax(MusicWidgetView.this.mDuration);
                MusicWidgetView.this.mLoadingPb.setVisibility(View.GONE);
                return;
            } else if (str1.equals("com.jiang.play")) {
                MusicWidgetView.this.mFlag = 1;
                MusicWidgetView.this.mPlayIb.get().setImageResource(R.drawable.player_btn_player_pause);
                return;
            } else if (str1.equals("com.jiang.pause")) {
                MusicWidgetView.this.mFlag = 2;
                MusicWidgetView.this.mPlayIb.get().setImageResource(R.drawable.player_btn_player_play);
                return;
            } else if (str1.equals("com.jiang.stop")) {
                MusicWidgetView.this.mFlag = 3;
                MusicWidgetView.this.mPlayIb.get().setImageResource(R.drawable.player_btn_player_play);
                if (!MusicWidgetView.this.isNeedAutoDismiss)
                    MusicWidgetView.this.setVisibility(View.GONE);
                return;
            } else if (str1.equals("com.jiang.next")) {

            } else if (str1.equals("com.jiang.info")) {

            } else if (str1.equals("com.jiang.prepare")) {

            }

        }
    };
    private String src;
    private String title;

    public MusicWidgetView(Context paramContext) {
        super(paramContext);
        this.mContext = paramContext;
        initView();
    }

    public MusicWidgetView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.mContext = paramContext;
        initView();
    }

    public MusicWidgetView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.mContext = paramContext;
        initView();
    }

    private void showPlayBtn() {
        this.mPlayIb.get().setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                switch (MusicWidgetView.this.mFlag) {
                    case 1:
                        MusicWidgetView.this.pause();
                        return;
                    case 2:
                        MusicWidgetView.this.play();
                        return;
                    default:
                        return;
                }

            }
        });
        this.mNextIb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MusicWidgetView.this.next();
            }
        });
    }

    private void initView() {
        View localView = LayoutInflater.from(this.mContext).inflate(R.layout.player_music_mini, null, false);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -2);
        this.mPlayIb.set(((ImageButton) localView.findViewById(R.id.music_play_ib)));
        this.mNextIb = ((ImageButton) localView.findViewById(R.id.music_next_ib));
        this.mMuiscInfoTv = ((TextView) localView.findViewById(R.id.music_info_tv));
        this.mPlayModeIb = ((ImageButton) localView.findViewById(R.id.ibtn_player_control_mode));
        this.mSeekbar = ((SeekBar) localView.findViewById(R.id.sp_player_playprogress));
        this.mSeekbar.setEnabled(false);
        this.mLoadingPb = ((ProgressBar) localView.findViewById(R.id.loading));
        this.mMusicNameTv = ((TextView) localView.findViewById(R.id.song_title));
        this.mArtistTv = ((TextView) localView.findViewById(R.id.song_artist_tv));
        addView(localView, localLayoutParams);
        showPlayBtn();
        this.mLoadingPb.setVisibility(View.VISIBLE);
        this.mFlag = 2;
        init();
    }

    private void seekbar_change(int paramInt) {
        Intent localIntent = new Intent();
        localIntent.setAction("com.jiang.media.MUSIC_SERVICE");
        localIntent.putExtra("op", 4);
        localIntent.putExtra("progress", paramInt);
        this.mContext.startService(localIntent);
        this.mLoadingPb.setVisibility(View.VISIBLE);
    }

    public void getInfo() {
        Intent localIntent = new Intent();
        localIntent.setAction("com.jiang.media.MUSIC_SERVICE");
        localIntent.putExtra("op", 5);
        this.mContext.startService(localIntent);
    }

    public void hideDetailButton() {
        findViewById(R.id.music_detail_iv).setVisibility(View.GONE);
    }

    public void hideLoadingBar() {
        this.mLoadingPb.setVisibility(View.GONE);
    }

    public void hideNextOne() {
        this.mNextIb.setVisibility(View.GONE);
        this.mMuiscInfoTv.setVisibility(View.VISIBLE);
        this.mMusicNameTv.setVisibility(View.GONE);
        this.mArtistTv.setVisibility(View.GONE);
    }

    public void init() {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("com.jiang.currentTime");
        localIntentFilter.addAction("com.jiang.duration");
        localIntentFilter.addAction("com.jiang.next");
        localIntentFilter.addAction("com.jiang.play");
        localIntentFilter.addAction("com.jiang.pause");
        localIntentFilter.addAction("com.jiang.stop");
        localIntentFilter.addAction("com.jiang.info");
        localIntentFilter.addAction("com.jiang.prepare");
        this.mContext.registerReceiver(this.musicReceiver, localIntentFilter);
    }

    public void next() {
        this.mFlag = 1;
        this.mPlayIb.get().setImageResource(R.drawable.player_btn_player_pause);
        Intent localIntent = new Intent();
        localIntent.setAction("com.jiang.media.MUSIC_SERVICE");
        localIntent.putExtra("op", 6);
        this.mContext.startService(localIntent);
    }

    protected void onDetachedFromWindow() {
        unregisterMusicReceiver();
    }

    public void pause() {
        this.mFlag = 2;
        this.mPlayIb.get().setImageResource(R.drawable.player_btn_player_play);
        Intent localIntent = new Intent();
        localIntent.setAction("com.jiang.media.MUSIC_SERVICE");
        localIntent.setPackage(mContext.getPackageName());
        this.mContext.startService(localIntent);
    }

    public void play() {
        this.mFlag = 1;
        this.mPlayIb.get().setImageResource(R.drawable.player_btn_player_pause);
        Intent localIntent = new Intent();
        localIntent.setAction("com.jiang.media.MUSIC_SERVICE");
        localIntent.setPackage(mContext.getPackageName());
        this.mContext.startService(localIntent);
    }

    public void setAutoDismiss(boolean paramBoolean) {
        this.isNeedAutoDismiss = paramBoolean;
    }


    public void setPlayButtonOnClickListener(View.OnClickListener paramOnClickListener) {
        this.mPlayIb.get().setOnClickListener(paramOnClickListener);
    }

    public void showDetailButton() {
        findViewById(R.id.music_detail_iv).setVisibility(View.VISIBLE);
    }

//    public void showLoading(boolean paramBoolean)
//    {
//        ProgressBar localProgressBar = this.mLoadingPb;
//        if (paramBoolean);
//        for (int i = 0; ; i = 8)
//        {
//            localProgressBar.setVisibility(i);
//            return;
//        }
//    }

    public void showNextOne() {
        this.mNextIb.setVisibility(View.VISIBLE);
        this.mMuiscInfoTv.setVisibility(View.GONE);
        this.mMusicNameTv.setVisibility(View.VISIBLE);
        this.mArtistTv.setVisibility(View.VISIBLE);
    }

    public void unbandMusicService() {
        this.mCurType = 1;
        unregisterMusicReceiver();
    }

    public void unregisterMusicReceiver() {
        this.mContext.unregisterReceiver(this.musicReceiver);
    }

    public void updateMusicProgress(int paramInt) {
        this.mSeekbar.setProgress(paramInt);
        this.mSeekbar.setSecondaryProgress(paramInt);
    }

    public void updateMusicWidget(boolean paramBoolean, String paramString, int paramInt) {
        if (paramBoolean) {
            this.mFlag = 1;
            this.mPlayIb.get().setImageResource(R.drawable.player_btn_player_pause);
            this.mLoadingPb.setVisibility(View.GONE);
        } else {
            this.mMuiscInfoTv.setText(paramString);
            this.mSeekbar.setMax(paramInt);
            this.mSeekbar.setProgress(0);
            this.mSeekbar.setSecondaryProgress(0);
            this.mFlag = 2;
            this.mPlayIb.get().setImageResource(R.drawable.player_btn_player_play);
        }
    }

//    public void updatePlayButton(boolean paramBoolean)
//    {
//        int i;
//        ImageButton localImageButton;
//        if (paramBoolean)
//        {
//            i = 1;
//            this.mFlag = i;
//            localImageButton.set(this.mPlayIb);
//            if (!paramBoolean)
//                break label36;
//        }
//        label36: for (int j = 2130838857; ; j = 2130838858)
//        {
//            localImageButton.setImageResource(j);
//            return;
//            i = 2;
//            break;
//        }
//    }
}