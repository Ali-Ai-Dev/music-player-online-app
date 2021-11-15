package com.app.ali_bozorgzad.music_player;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.IOException;

public class ActivityPlayingMusic extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener,
                                          SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener{

    TextView txtMusicNamePlaying, txtArtistNamePlaying, txtCurrentTime, txtTotalTime;
    ImageView imgPlay, imgForward, imgBackward, imgPlayingMusicCover, imgBlur, imgBackgroundPicture;
    SeekBar seekbar;
    MediaPlayer mediaPlayer;
    Context context;
    Bundle bundle;

    public Handler handler = new Handler();
    Settings settings;
    long totalTime, currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_music);

        context = ActivityPlayingMusic.this;
        settings = new Settings();

        txtCurrentTime = (TextView) findViewById(R.id.txtCurrentTime);
        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);
        txtMusicNamePlaying = (TextView) findViewById(R.id.txtMusicNamePlaying);
        txtArtistNamePlaying = (TextView) findViewById(R.id.txtArtistNamePlaying);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgForward = (ImageView) findViewById(R.id.imgForward);
        imgBackward = (ImageView) findViewById(R.id.imgBackward);
        imgPlayingMusicCover = (ImageView) findViewById(R.id.imgPlayingMusicCover);
        imgBlur = (ImageView) findViewById(R.id.imgBlur);
        imgBackgroundPicture = (ImageView) findViewById(R.id.imgBackgroundPicture);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);

        bundle = getIntent().getExtras();
        txtMusicNamePlaying.setText(bundle.getString("musicName"));
        txtArtistNamePlaying.setText(bundle.getString("artistName"));
        Picasso.with(context).load(bundle.getString("musicPicture")).into(imgPlayingMusicCover);
        ActivityPlayingMusic.this.setTitle(bundle.getString("artistName")+" - "+ bundle.getString("musicName"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        imgPlay.setEnabled(false);
        imgForward.setEnabled(false);
        imgBackward.setEnabled(false);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    imgPlay.setImageResource(R.mipmap.play);
                }else{
                    mediaPlayer.start();
                    imgPlay.setImageResource(R.mipmap.pause);
                    updateSeekbarTimer();
                }
            }
        });

        imgForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newTimePosition = mediaPlayer.getCurrentPosition() + 4000;
                if (newTimePosition <= mediaPlayer.getDuration()){
                    mediaPlayer.seekTo(newTimePosition);
                }else{
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            }
        });

        imgBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newTimePosition = mediaPlayer.getCurrentPosition() - 4000;
                if (newTimePosition >= 0){
                    mediaPlayer.seekTo(newTimePosition);
                }else{
                    mediaPlayer.seekTo(0);
                }
            }
        });

        if (!mediaPlayer.isPlaying()){
            new Player().execute();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        seekbar.setSecondaryProgress(i);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(null);
        int total = mediaPlayer.getDuration();
        int currentPosition = settings.progressToTimer(seekBar.getProgress(), total);
        mediaPlayer.seekTo(currentPosition);
        updateSeekbarTimer();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        handler.removeCallbacks(null);
        imgPlay.setImageResource(R.mipmap.play);
        seekbar.setProgress(0);
        txtTotalTime.setText("00:00");
        txtCurrentTime.setText("00:00");
        imgPlay.setImageResource(R.mipmap.play);
    }

    private class Player extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try{
                mediaPlayer.setDataSource(bundle.getString("link"));
                mediaPlayer.prepare();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);

            mediaPlayer.start();
            imgPlay.setEnabled(true);
            imgForward.setEnabled(true);
            imgBackward.setEnabled(true);
            imgPlay.setImageResource(R.mipmap.pause);
            imgPlayingMusicCover.buildDrawingCache();

            Bitmap bitmap= imgPlayingMusicCover.getDrawingCache();
            imgBlur.setImageBitmap(BlurImage.blur(context, bitmap));
            imgBackgroundPicture.setImageResource(R.mipmap.background_player);
            updateSeekbarTimer();
        }
    }

    private void updateSeekbarTimer() {
         try {
             if(mediaPlayer.isPlaying()) {
                 totalTime = mediaPlayer.getDuration();
                 currentTime = mediaPlayer.getCurrentPosition();
                 txtTotalTime.setText("" + settings.milliSecondsToTimer(totalTime));
                 txtCurrentTime.setText("" + settings.milliSecondsToTimer(currentTime));
                 int progress = settings.getProgressPercentage(currentTime, totalTime);
                 seekbar.setProgress(progress);

                 Runnable runnable = new Runnable() {
                     @Override
                     public void run() {
                         updateSeekbarTimer();
                     }
                 };
                 handler.postDelayed(runnable, 1000);
             }
         }catch (Exception e){
             e.printStackTrace();
         }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }
}