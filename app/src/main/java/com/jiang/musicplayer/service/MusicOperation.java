package com.jiang.musicplayer.service;

/**
 * Created by Wuliao on 2015/7/10 0010.
 */
public abstract interface MusicOperation
{
    public static final int MUSIC_INIT = 5;
    public static final int MUSIC_NEXT_ONE = 6;
    public static final int MUSIC_PAUSE = 2;
    public static final int MUSIC_PLAY = 1;
    public static final int MUSIC_STOP = 3;
    public static final int PROGRESS_CHANGE = 4;
}
