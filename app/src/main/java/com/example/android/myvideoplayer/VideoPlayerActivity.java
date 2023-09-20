package com.example.android.myvideoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<MediaFiles> mVideoFiles = new ArrayList<>();
    PlayerView playerView;
    SimpleExoPlayer player;
    int position;
    String videoTitle;
    TextView title;
    ConcatenatingMediaSource concatenatingMediaSource;
    ImageView nextButton, prevButton, backButton, lock, unlock, scaling;
    RelativeLayout root;
    private ControlsMode controlsMode;

    /* for horizontal recyclerView */
    private ArrayList<IconModel> iconModelArrayList = new ArrayList<>();
    PlaybackIconsAdapter playbackIconsAdapter;
    RecyclerView recyclerViewIcons;
    Boolean expand = false;
    View nightMode;
    boolean isDark = false;
    boolean isMute = false;

    public enum ControlsMode {
        LOCK, FULLSCREEN;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_video_player);
        getSupportActionBar().hide();
        playerView = findViewById(R.id.exoplayer_id);
        position = getIntent().getIntExtra("position", 1);
        videoTitle = getIntent().getStringExtra("video_title");
        mVideoFiles = getIntent().getParcelableArrayListExtra("videoListArray");

        nextButton = findViewById(R.id.exo_next);
        prevButton = findViewById(R.id.exo_prev);
        backButton = findViewById(R.id.video_back);
        lock = findViewById(R.id.lock);
        unlock = findViewById(R.id.unlock);
        root = findViewById(R.id.root_layout);
        scaling = findViewById(R.id.scaling);
        recyclerViewIcons = findViewById(R.id.recyclerview_icon);
        nightMode = findViewById(R.id.night_mode);

        title = findViewById(R.id.video_title);
        title.setText(videoTitle);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        lock.setOnClickListener(this);
        unlock.setOnClickListener(this);
        root.setOnClickListener(this);
        scaling.setOnClickListener(customListener);

        iconModelArrayList.add(new IconModel(R.drawable.ic_right, ""));
        iconModelArrayList.add(new IconModel(R.drawable.ic_night_mode, "Night"));
        iconModelArrayList.add(new IconModel(R.drawable.ic_volume_off, "Mute"));
        iconModelArrayList.add(new IconModel(R.drawable.ic_rotate, "Rotate"));

        playbackIconsAdapter = new PlaybackIconsAdapter(iconModelArrayList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true);
        recyclerViewIcons.setLayoutManager(layoutManager);
        recyclerViewIcons.setAdapter(playbackIconsAdapter);
        playbackIconsAdapter.notifyDataSetChanged();
        playbackIconsAdapter.setOnItemClickListener(new PlaybackIconsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    if (expand) {
                        iconModelArrayList.clear();
                        iconModelArrayList.add(new IconModel(R.drawable.ic_right, ""));
                        iconModelArrayList.add(new IconModel(R.drawable.ic_night_mode, "Night"));
                        iconModelArrayList.add(new IconModel(R.drawable.ic_volume_off, "Mute"));
                        iconModelArrayList.add(new IconModel(R.drawable.ic_rotate, "Rotate"));
                        playbackIconsAdapter.notifyDataSetChanged();
                        expand = false;
                    } else {
                        if (iconModelArrayList.size() == 4) {
                            iconModelArrayList.add(new IconModel(R.drawable.ic_volume, "Volume"));
                            iconModelArrayList.add(new IconModel(R.drawable.ic_brightness, "Brightness"));
                            iconModelArrayList.add(new IconModel(R.drawable.ic_equalizer, "Equalizer"));
                            iconModelArrayList.add(new IconModel(R.drawable.ic_speed, "Speed"));
                            iconModelArrayList.add(new IconModel(R.drawable.ic_subtitles, "Subtitle"));
                        }
                        iconModelArrayList.set(position, new IconModel(R.drawable.ic_left, ""));
                        playbackIconsAdapter.notifyDataSetChanged();
                        expand = true;
                    }
                } else if (position == 1) {
                    if (isDark) {
                        nightMode.setVisibility(View.GONE);
                        iconModelArrayList.set(position, new IconModel(R.drawable.ic_night_mode, "Night"));
                        playbackIconsAdapter.notifyDataSetChanged();
                        isDark = false;
                    } else {
                        nightMode.setVisibility(View.VISIBLE);
                        iconModelArrayList.set(position, new IconModel(R.drawable.ic_night_mode, "Day"));
                        playbackIconsAdapter.notifyDataSetChanged();
                        isDark = true;
                    }
                } else if (position == 2) {
                    if (isMute) {

                        player.setVolume(100);
                        iconModelArrayList.set(position, new IconModel(R.drawable.ic_volume_off, "Mute"));
                        playbackIconsAdapter.notifyDataSetChanged();
                        isMute = false;
                    } else {
                        player.setVolume(0);
                        iconModelArrayList.set(position, new IconModel(R.drawable.ic_volume, "unMute"));
                        playbackIconsAdapter.notifyDataSetChanged();
                        isMute = true;
                    }
                } else if (position == 3) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        playbackIconsAdapter.notifyDataSetChanged();
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        playbackIconsAdapter.notifyDataSetChanged();

                    }
                } else if (position == 4) {
                    VolumeDialog volumeDialog = new VolumeDialog();
                    volumeDialog.show(getSupportFragmentManager(), "dialog");
                    playbackIconsAdapter.notifyDataSetChanged();
                }
                else if(position == 5){
                    BrightnessDialog brightnessDialog = new BrightnessDialog();
                    brightnessDialog.show(getSupportFragmentManager(), "dialog");
                    playbackIconsAdapter.notifyDataSetChanged();
                }
                else if(position == 6){
                    Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent, 1);
                    }
                    else {
                        Toast.makeText(VideoPlayerActivity.this, "No equaliser found", Toast.LENGTH_SHORT).show();
                    }
                    playbackIconsAdapter.notifyDataSetChanged();
                }
            }
        });

        playVideo();
    }

    private void playVideo() {
        String path = mVideoFiles.get(position).getPath();
        Uri uri = Uri.parse(path);
        player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "any_text"));
        concatenatingMediaSource = new ConcatenatingMediaSource();  // to play the videos sequentially
        for (int i = 0; i < mVideoFiles.size(); i++) {
            new File(String.valueOf(mVideoFiles.get(i)));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exo_next:
                try {
                    player.stop();
                    position++;
                    playVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "There is no next video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.exo_prev:
                try {
                    player.stop();
                    position--;
                    playVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "There is no previous video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.video_back:
                if (player != null) {
                    player.release();
                }
                finish();
                break;
            case R.id.lock:
                controlsMode = ControlsMode.FULLSCREEN;
                root.setVisibility(View.VISIBLE);
                lock.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "unlocked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unlock:
                controlsMode = ControlsMode.LOCK;
                root.setVisibility(View.INVISIBLE);
                lock.setVisibility(View.VISIBLE);
                Toast.makeText(this, "locked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scaling.setImageResource(R.drawable.fullscreen);

            Toast.makeText(VideoPlayerActivity.this, "Full Screen", Toast.LENGTH_SHORT).show();
            scaling.setOnClickListener(customListener2);

        }
    };

    View.OnClickListener customListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scaling.setImageResource(R.drawable.zoom);

            Toast.makeText(VideoPlayerActivity.this, "Zoom", Toast.LENGTH_SHORT).show();
            scaling.setOnClickListener(customListener3);
        }
    };

    View.OnClickListener customListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scaling.setImageResource(R.drawable.fit);

            Toast.makeText(VideoPlayerActivity.this, "Fit", Toast.LENGTH_SHORT).show();
            scaling.setOnClickListener(customListener);
        }
    };


}