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

public class Settings extends AppCompatActivity {

    private int volume;
    private int brightness;
    private boolean music;
    private boolean vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        SeekBar brightnessBar = (SeekBar)findViewById(R.id.skbrBrightness);
        brightnessBar.setProgress(brightness);
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });
        SeekBar volumeBar = (SeekBar)findViewById(R.id.skbrVolume);
        volumeBar.setProgress(volume);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volume = progress;
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
        brightness = prefs.getInt("brightness",0);
        music = prefs.getBoolean("music",false);
        vibration = prefs.getBoolean("vibration", false);
    }

    private void saveValues() {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("volume", volume);
        editor.putInt("brightness", brightness);
        editor.putBoolean("music", music);
        editor.putBoolean("vibration", vibration);
        editor.commit();
    }

    private void saveGoToMain() {
        saveValues();
        goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
