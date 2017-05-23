package com.bkg.oakyildiz.suits;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NavUtils;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.net.Uri;
import android.media.MediaPlayer;

import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;

import android.widget.FrameLayout;
import android.widget.VideoView;
import android.widget.ImageView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class InboxActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private final Handler mHideHandler = new Handler();

    private ImageView mInboxView;
    private VideoView mVideoView;

    private int inbox_img; //name for resource files
    private int video;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            /* Note that some of these constants are new as of API 16 (Jelly Bean)
             and API 19 (KitKat). It is safe to use them, as they are inlined
             at compile-time and do nothing on earlier devices.*/
            mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };


    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mVideoView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inbox);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;


        setResources(readBundle());
        //setResources(1);
        setmInboxView();
        setmVideoView();

        playVideo();

        // Set up the user interaction to manually show or hide the system UI.


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

        private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void setmInboxView() {
        mInboxView = (ImageView) findViewById(R.id.inbox);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInboxView.setImageDrawable(getResources().getDrawable(this.inbox_img, getApplicationContext().getTheme()));
        } else {
            mInboxView.setImageDrawable(getResources().getDrawable(this.inbox_img));
        }
        mInboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideVideo();
            }
        });

    }
    private void setmVideoView(){
        mVideoView =(VideoView) findViewById(R.id.video);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                float log1=(float)(Math.log(50-25)/Math.log(50));
                mp.setVolume(1-log1,1-log1);
            }
        });
        //mVideoView.setMediaController(null);
        mVideoView.setOnCompletionListener(this);
        mInboxView.setOnTouchListener(mDelayHideTouchListener);
    }
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void playVideo(){
        String path = "android.resource://" + getPackageName() + "/" + this.video;
        this.mVideoView.setVideoURI(Uri.parse(path));
        this.mVideoView.start();
    }


    private void hideVideo() {
        mVideoView.stopPlayback();
        mVideoView.setVisibility(FrameLayout.GONE);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        hideVideo();
    }

    public void setResources(int sel){
        if (sel==0){
            this.inbox_img=R.drawable.harveyinbox;
            this.video=R.raw.harvey;
        }
        else if(sel==1){
            this.inbox_img=R.drawable.mikeinbox;
            this.video=R.raw.mike;

        }

    }
    public int readBundle(){
        Bundle b = getIntent().getExtras();
        if(b != null)
            return b.getInt("SEL");
        else
            return -1;
    }
}
