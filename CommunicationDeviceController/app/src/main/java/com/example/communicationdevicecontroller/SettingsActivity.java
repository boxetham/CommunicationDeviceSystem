package com.example.communicationdevicecontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private int volume;
    private boolean music;
    private boolean vibration;
    private TextView currVol;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        currVol = findViewById(R.id.lbCurrentVolume);
        getValues();
        configureSeekbars();
        configureSwitches();
        configureButtons();
    }

    private void configureButtons() {
        findViewById(R.id.btSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoToMain();
            }
        });
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { goToMain();
            }
        });
    }

    private void configureSwitches() {
        Switch musicSwith = (Switch)findViewById(R.id.swMusic);
        musicSwith.setChecked(music);
        musicSwith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { music = isChecked;
            }
        });
        Switch vibrationSwitch = (Switch)findViewById(R.id.swVibration);
        vibrationSwitch.setChecked(vibration);
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { vibration = isChecked; }
        });
    }

    private void configureSeekbars() {
        SeekBar volumeBar = (SeekBar)findViewById(R.id.skbrVolume);
        volumeBar.setProgress(volume);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volume = progress;
                currVol.setText("" + volume);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });
    }

    private void getValues() {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        volume = prefs.getInt("volume", 0);
        currVol.setText("" + volume);
        music = prefs.getBoolean("music",false);
        vibration = prefs.getBoolean("vibration", false);
    }

    private void saveValues() {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("volume", volume);
        editor.putBoolean("music", music);
        editor.putBoolean("vibration", vibration);
        editor.commit();
    }

    private void sendValues() {
        BluetoothActivity.sendSettings(new SettingsPackager(volume, vibration, music));
    }

    private void saveGoToMain() {
        saveValues();
        sendValues();
        goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}