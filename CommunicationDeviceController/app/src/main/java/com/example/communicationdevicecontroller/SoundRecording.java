package com.example.communicationdevicecontroller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundRecording {

    private static Context context;
    private static String mFileName = null;
    private static String testName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private boolean isRecording = false;

    public SoundRecording(Context context){
        this.context = context;
        mFileName = context.getExternalCacheDir().getAbsolutePath();
        testName = "audiorecordtest";
    }

    private void onRecord(boolean start) {
        if (start) {
            isRecording = true;
            startRecording();
        } else {
            stopRecording();
            isRecording = false;
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying(testName);
        } else {
            stopPlaying();
        }
    }

    public void startPlaying(String filename) {
        if(!isRecording) {
            mPlayer = new MediaPlayer();
            String file = mFileName + "/" + filename + ".3gp";
            InputStream istream = null;
            try {
                int c;
                istream = new FileInputStream(file);
                while ((c = istream.read()) != -1){

                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    istream.close();
                } catch (IOException e) {
                    System.out.println("File did not close");
                }
            }
            try {
                mPlayer.setDataSource(file);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e("auido fail", "prepare() failed");
            }
        }
    }

//    public void startPreRecordedPlaying(String fileName) {
//        mPlayer = new MediaPlayer();
//        try {
//            mPlayer.setDataSource(fileName);
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            Log.e("auido fail", "prepare() failed");
//        }
//    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        String file = mFileName + "/" + testName + ".3gp";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(file);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("audio fail", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public PlayButton getPlayButton(Context context) {
        return new PlayButton(context);
    }

    public RecordButton getRecordButton(Context context) {
        return new RecordButton(context);
    }

    public void setName(String filename) {
        testName = filename;
    }

    class RecordButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                if(isRecording){
                    cannotPlay();
                    return;
                }
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }

            private void cannotPlay() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cannot play when recording");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {                    }
                });
                builder.show();
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
}
