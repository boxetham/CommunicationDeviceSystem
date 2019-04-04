package com.example.communicationdevicecontroller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundSelection extends AppCompatActivity {

    private static Map<Integer, String[]> permissionMap = new HashMap<Integer, String[]>(){{
        put(RECORD_AUDIO, new String[]{Manifest.permission.RECORD_AUDIO});
        put(WRITE_EXTERNAL, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }};
    public static final int WRITE_EXTERNAL = 5;
    public static final int RECORD_AUDIO = 3;
    private SoundRecording recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_selection);
        recorder = new SoundRecording(this);
        findViewById(R.id.btBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        findViewById(R.id.btMicrophone).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (checkPermissions(RECORD_AUDIO) != 1) {
                    getFileName();
                }
            }
        });
        findViewById(R.id.btGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSoundFiles();
            }
        });
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void cancel() {
        Intent intent = new Intent(this, ChangeDisplay.class);
        startActivity(intent);
    }

    private void listSoundFiles() {
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        final Button selectButton = findViewById(R.id.btSelect);
        List<String> files = recorder.getSoundRecordings();
        ScrollView scroll = findViewById(R.id.scrollButtons);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFileFirst();
            }
        });
        scroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 370));
        LinearLayout ll = findViewById(R.id.recordingsLayout);
        for(final String file : files){
            final String filename = file.substring(0, file.lastIndexOf('.'));
            Button fileButton = new Button(this);
            fileButton.setText(filename);
            fileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recorder.startPlaying(filename);
                    setSelectedSound(filename);
                    selectButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            next(filename);
                        }
                    });
                }
            });
            ll.addView(fileButton, params);
        }
        changeVisibility(selectButton, View.VISIBLE);
        changeVisibility(findViewById(R.id.btBack), View.VISIBLE);
        changeVisibility(findViewById(R.id.lbSelectedSound), View.VISIBLE);
        changeVisibility(findViewById(R.id.btMicrophone), View.GONE);
        changeVisibility(findViewById(R.id.btGallery), View.GONE);
    }

    private void back() {
        Intent intent = new Intent(this, SoundSelection.class);
        startActivity(intent);
    }

    private void setSelectedSound(String filename) {
        TextView view = findViewById(R.id.lbSelectedSound);
        view.setText("Selected file: " + filename);
    }

    private void selectFileFirst() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a sound file first");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {                    }
        });
        builder.show();
    }

    private void getFileName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name for sound file with noimage spaces");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recorder.setName(input.getText().toString());
                showRecordingOptions();
            }
        });
        builder.show();
    }

    private void showRecordingOptions() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0);
        SoundRecording.RecordButton mRecordButton = recorder.getRecordButton(this);
        LinearLayout ll = findViewById(R.id.buttonLayout);
        ll.addView(mRecordButton, params);
        SoundRecording.PlayButton mPlayButton = recorder.getPlayButton(this);
        ll.addView(mPlayButton, params);
        changeVisibility(findViewById(R.id.btNext), View.VISIBLE);
        findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next(recorder.getName());
            }
        });
        changeVisibility(findViewById(R.id.btBack), View.VISIBLE);
        changeVisibility(findViewById(R.id.btMicrophone), View.GONE);
        changeVisibility(findViewById(R.id.btGallery), View.GONE);
    }

    private void next(String filename) {
        ChangeDisplay.currentDisplay.setTempSound(filename);
        Intent intent = new Intent(this, LabelSelection.class);
        startActivity(intent);
    }

    private void changeVisibility(final View view, final int visibilty){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(visibilty);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int checkPermissions(int permission) {
        if(ActivityCompat.checkSelfPermission(this, permissionMap.get(permission)[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissionMap.get(permission), permission);
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay!
        } else {
            cancel();
        }
    }
}
