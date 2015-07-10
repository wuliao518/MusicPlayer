package com.jiang.musicplayer.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiang.musicplayer.R;
import com.jiang.musicplayer.adapter.MyBaseAdapter;
import com.jiang.musicplayer.bean.MusicModel;
import com.jiang.musicplayer.service.MusicService;
import com.jiang.musicplayer.utils.LogUtils;
import com.jiang.musicplayer.utils.ViewHolder;
import com.jiang.musicplayer.widget.MusicWidgetView;
import com.jiang.musicplayer.widget.refresh.SwipeRefreshBase;
import com.jiang.musicplayer.widget.refresh.SwipeRefreshListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    SwipeRefreshListView mRefreshableView;
    private List<MusicModel> models = new ArrayList<>();
    //记录哪一首个正在播放
    private int position = -1;
    private MusicWidgetView musicWidgetView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicWidgetView= (MusicWidgetView) findViewById(R.id.musicWidget);
        MusicModel musicModel = new MusicModel();
        musicModel.setmMusicSrc("http://7d9orq.com1.z0.glb.clouddn.com/1.mp3");
        musicModel.setmSinger("陈楚生");
        musicModel.setmSongName("某某");
        models.add(musicModel);
        mRefreshableView = (SwipeRefreshListView) findViewById(R.id.refreshList);
        mRefreshableView.setRefreshing(true);
        loadData();
        mRefreshableView.setOnRefreshListener(new SwipeRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

    }

    private void loadData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mRefreshableView.setRefreshing(false);
                mRefreshableView.getRefreshableView().setAdapter(new MyBaseAdapter<MusicModel>(MainActivity.this, models) {
                    @Override
                    public View getItemView(final int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = mInflate.inflate(R.layout.listview_item_music, null);
                        }
                        final ImageView songPhoto = ViewHolder.findViewById(convertView, R.id.song_photo);
                        final ImageView playBtn = ViewHolder.findViewById(convertView, R.id.play_btn);
                        TextView author = ViewHolder.findViewById(convertView, R.id.author);
                        TextView song = ViewHolder.findViewById(convertView, R.id.song);
                        songPhoto.setImageResource(R.drawable.music);
                        author.setText(list.get(position).getmSinger());
                        song.setText(list.get(position).getmSongName());
                        playBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (MainActivity.this.position == position) {
                                    MainActivity.this.position = -1;
                                    playBtn.setBackgroundResource(R.drawable.channel_play);
                                    ObjectAnimator xAnimate=ObjectAnimator.ofFloat(playBtn, "translationX", 0);
                                    ObjectAnimator yAnimate=ObjectAnimator.ofFloat(playBtn, "translationY", 0);
                                    AnimatorSet animationSet=new AnimatorSet();
                                    animationSet.setDuration(500);
                                    animationSet.playTogether(xAnimate, yAnimate);
                                    animationSet.start();
                                    Intent intent=new Intent();
                                    intent.setAction("notification.pause.music");
                                    sendBroadcast(intent);
                                    musicWidgetView.pause();
                                } else {
                                    MainActivity.this.position = position;
                                    playBtn.setBackgroundResource(R.drawable.channel_pause);
                                    ObjectAnimator xAnimate=ObjectAnimator.ofFloat(playBtn, "translationX", songPhoto.getMeasuredWidth() - playBtn.getMeasuredWidth() - playBtn.getLeft());
                                    ObjectAnimator yAnimate=ObjectAnimator.ofFloat(playBtn, "translationY", songPhoto.getMeasuredHeight() - playBtn.getMeasuredHeight() - playBtn.getTop());
                                    AnimatorSet animationSet=new AnimatorSet();
                                    animationSet.setDuration(500);
                                    animationSet.playTogether(xAnimate, yAnimate);
                                    animationSet.start();
                                    Intent intent=new Intent(MainActivity.this, MusicService.class);
                                    intent.putExtra("model",list.get(position));
                                    startService(intent);
                                    showMusicWidget();
                                    musicWidgetView.play();
                                }
                            }
                        });
                        return convertView;
                    }
                });
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showMusicWidget(){
        musicWidgetView.setVisibility(View.VISIBLE);
    }
    private void hideMusicWidget(){
        musicWidgetView.setVisibility(View.GONE);
    }
}
