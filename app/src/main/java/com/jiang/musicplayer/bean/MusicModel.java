package com.jiang.musicplayer.bean;

import java.io.Serializable;

/**
 * Created by Wuliao on 2015/7/9 0009.
 */
public class MusicModel implements Serializable{
    private String mSongName;
    private String mSinger;
    private String mMusicSrc;
    private String imageSrc;

    public String getmSongName() {
        return mSongName;
    }

    public void setmSongName(String mSongName) {
        this.mSongName = mSongName;
    }

    public String getmSinger() {
        return mSinger;
    }

    public void setmSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    public String getmMusicSrc() {
        return mMusicSrc;
    }

    public void setmMusicSrc(String mMusicSrc) {
        this.mMusicSrc = mMusicSrc;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }
}
