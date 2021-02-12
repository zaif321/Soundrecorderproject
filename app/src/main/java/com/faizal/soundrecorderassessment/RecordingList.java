package com.faizal.soundrecorderassessment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class RecordingList extends AppCompatActivity implements RecordingListAdapter.OnItemListClick {

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private File[] allFiles;
    private FloatingActionButton playBtn, pauseButton, forwardBtn, backBtn;
    private RecyclerView audioList;
    private TextView playerFilename;
    private RecordingListAdapter recordingListAdapter;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay = null;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    private String color = "FFFFFF";


    public RecordingList() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_list);

        playerSheet = (ConstraintLayout) findViewById(R.id.player_sheet_lay);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioList = (RecyclerView) findViewById(R.id.audio_list_view);
        playBtn = (FloatingActionButton) findViewById(R.id.player_play_btn_sheet);
        pauseButton = (FloatingActionButton) findViewById(R.id.player_pause_btn_sheet);
        playerFilename = (TextView) findViewById(R.id.player_filename_sheet);
        forwardBtn = (FloatingActionButton) findViewById(R.id.player_forward_sheet);
        backBtn = (FloatingActionButton) findViewById(R.id.player_back_sheet);
        playerSeekbar = (SeekBar) findViewById(R.id.player_seekbar_sheet);
        String path = getApplicationContext().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recordingListAdapter = new RecordingListAdapter(allFiles, this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(linearLayoutManager);
        audioList.setAdapter(recordingListAdapter);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //We cant do anything here for this app
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = true;
                pauseAudio();
                pauseButton.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.INVISIBLE);


            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying) {
                    pauseAudio();
                    isPlaying = false;
                    pauseButton.setVisibility(View.INVISIBLE);
                    playBtn.setVisibility(View.VISIBLE);
                } else {
                    resumeAudio();
                    isPlaying = true;
                    pauseButton.setVisibility(View.VISIBLE);
                    playBtn.setVisibility(View.INVISIBLE);
                }


            }
        });
        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                isPlaying = false;
                resumeAudio();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    playAudio(fileToPlay);
                }
            }
        });
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    playAudio(fileToPlay);
                }
            }
        });


    }


    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        playAudio(fileToPlay);

    }

    private void pauseAudio() {
        mediaPlayer.pause();
        playBtn.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        playBtn.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void stopAudio() {
        //Stop The Audio
        pauseButton.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        mediaPlayer.stop();
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }


    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        playerFilename.setText(fileToPlay.getName());
        //Play the audio
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };

    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();
        }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            NavUtils.navigateUpTo(this, new Intent(this, Recorder.class));
            // interstatialAd.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}