package com.faizal.soundrecorderassessment;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Recorder extends AppCompatActivity {

    private Chronometer timer;
    private String recordFile;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private static final int RequestPermissionCode = 111;
    FloatingActionButton start_button,recording_list_button,stop_recording_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        start_button =(FloatingActionButton) findViewById(R.id.start_button);
        recording_list_button = (FloatingActionButton) findViewById(R.id.recording_list_button);
        stop_recording_button = (FloatingActionButton) findViewById(R.id.stop_recording_button);
        timer = (Chronometer)findViewById(R.id.timer_recording);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startRecording();
                    start_button.setVisibility(View.INVISIBLE);
                    stop_recording_button.setVisibility(View.VISIBLE);
                    recording_list_button.setVisibility(View.INVISIBLE);


            }
        });

        recording_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RecordingList.class);
                startActivity(i);
            }
        });


        stop_recording_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    stopRecording();
                    start_button.setVisibility(View.VISIBLE);
                    stop_recording_button.setVisibility(View.INVISIBLE);
                    recording_list_button.setVisibility(View.VISIBLE);


            }

        });



    }
    private void startRecording() {
        //Start timer from 0
        if(checkPermission()) {
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();

            //Get app external directory path
            String recordPath = getApplicationContext().getExternalFilesDir("/").getAbsolutePath();

            //Get current date and time
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ENGLISH);
            Date now = new Date();

            //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
            recordFile = "Recording_" + formatter.format(now) + ".3gp";
            //Section Recorder for recording
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Start Recording
            mediaRecorder.start();
        } else{
            requestPermission();
        }
    }
    private void stopRecording() {
        //Stop Timer, very obvious
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast.makeText(getApplicationContext(),"Recording Saved Successfully",Toast.LENGTH_LONG).show();

    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onStop() {
        super.onStop();
        if(isRecording){
            stopRecording();
        }
        }

    @Override
    public void onBackPressed() {
        stopRecording();
        super.onBackPressed();
    }

}