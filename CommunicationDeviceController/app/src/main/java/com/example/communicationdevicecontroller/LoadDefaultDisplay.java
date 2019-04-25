package com.example.communicationdevicecontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class LoadDefaultDisplay {

    Context context;

    public LoadDefaultDisplay(Context context){
        this.context = context;
    }

    public void loadDefaultDisplay() {
        Display currentDisplay = Display.getInstance(context);
        SoundRecording soundRecording = new SoundRecording(context);
        String[] words = new String[]{"yes","no","more","stop"};
        String[] soundFiles = new String[words.length];
        Bitmap[] images = new Bitmap[words.length];
        for(int i = 0; i < words.length; i++){
            soundFiles[i] = getSoundFilePath(words[i] + "sound", soundRecording);
            images[i] = getBitMap(words[i] + "image");
        }
        currentDisplay.setDisplay(images, soundFiles, words);
//        DisplayPackager packager = new DisplayPackager(words.length, words, imageFiles, soundFiles, context);
        BluetoothActivity.sendDisplay(new DisplayPackager(words.length, currentDisplay.getLabels(),
                currentDisplay.getImageFiles(), currentDisplay.getSounds(),
                context));
//        BluetoothActivity.sendDisplay(packager);
    }

    public Bitmap getBitMap(String name) {
        InputStream bm = context.getResources().openRawResource(context.getResources().getIdentifier(name,
                "raw", context.getPackageName()));
        Bitmap bmp = BitmapFactory.decodeStream(bm);
        int nh = (int) (bmp.getHeight() * (512.0 / bmp.getWidth()));
        bmp = Bitmap.createScaledBitmap(bmp, 512, nh, true);
        return bmp;
    }

    public String getSoundFilePath(String name, SoundRecording soundRecording){
        InputStream bm = context.getResources().openRawResource(context.getResources().getIdentifier(name,
                "raw", context.getPackageName()));
        return soundRecording.writeSoundFile(bm, name);
    }
}
