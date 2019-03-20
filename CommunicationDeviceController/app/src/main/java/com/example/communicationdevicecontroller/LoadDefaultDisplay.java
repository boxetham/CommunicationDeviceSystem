package com.example.communicationdevicecontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class LoadDefaultDisplay {

    Context context;

    public LoadDefaultDisplay(Context context){
        this.context = context;
    }

    public void loadDefaultDisplay() {
        Pictures pictures = new Pictures(context);
        SoundRecording soundRecording = new SoundRecording(context);
        String[] words = new String[]{"yes","no","more","stop"};
        String[] imageFiles = new String[words.length];
        String[] soundFiles = new String[words.length];
        for(int i = 0; i < imageFiles.length; i++){
            imageFiles[i] = pictures.writeImage(getBitMap(words[i] + "image"), imageFiles.length, i);
            soundFiles[i] = getSoundFilePath(words[i] + "sound", soundRecording);
        }
        DisplayPackager packager = new DisplayPackager(words.length, words, imageFiles, soundFiles, soundRecording, pictures);
        Bluetooth.sendDisplay(packager);
    }

    public Bitmap getBitMap(String name) {
        InputStream bm = context.getResources().openRawResource(context.getResources().getIdentifier(name,
                "raw", context.getPackageName()));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(bm);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
        int nh = (int) (bmp.getHeight() * (512.0 / bmp.getWidth()));
        bmp = Bitmap.createScaledBitmap(bmp, 512, nh, true);
        return bmp;
    }

    public String getSoundFilePath(String name, SoundRecording soundRecording){
        InputStream bm = context.getResources().openRawResource(context.getResources().getIdentifier(name,
                "raw", context.getPackageName()));
        soundRecording.writeSoundFile(bm, name);
        return "";
    }
}
