package com.rosehulman.communicationdevicecontroller;

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
        SoundManager soundManager = new SoundManager(context);
        String[] words = new String[]{"yes","no","more","stop"};
        String[] soundFiles = new String[words.length];
        Bitmap[] images = new Bitmap[words.length];
        for(int i = 0; i < words.length; i++){
            soundFiles[i] = getSoundFilePath(words[i] + "sound", soundManager);
            images[i] = getBitMap(words[i] + "image");
        }
        currentDisplay.setDisplay(images, soundFiles, words);
        currentDisplay.updateSaved();
        BluetoothActivity.sendDisplay(new DisplayPackager(words.length, currentDisplay.getLabels(),
                currentDisplay.getImageFiles(), currentDisplay.getSounds(),
                context));
    }

    public Bitmap getBitMap(String name) {
        InputStream bm = context.getResources().openRawResource(context.getResources().getIdentifier(name,
                "raw", context.getPackageName()));
        Bitmap bmp = BitmapFactory.decodeStream(bm);
        int nh = (int) (bmp.getHeight() * (512.0 / bmp.getWidth()));
        bmp = Bitmap.createScaledBitmap(bmp, 512, nh, true);
        return bmp;
    }

    public String getSoundFilePath(String name, SoundManager soundManager){
        InputStream bm = context.getResources().openRawResource(context.getResources().getIdentifier(name,
                "raw", context.getPackageName()));
        return soundManager.writeSoundFile(bm, name);
    }
}
