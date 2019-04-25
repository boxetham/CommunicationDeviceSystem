package com.example.communicationdevicecontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import static java.lang.Math.min;

public class Display {

    private static Display display_instance; //if you have multiple devices this could be a map
                                             //from device name to display to handle multiple devices
                                             // then you would have getInstance(Context, name)
    private static PictureManager pictureManager;
    private static SoundRecording soundRecording;
    private static Context context;
    private String labels[];
    private String soundFiles[];
    private Bitmap images[];
    private String imageFiles[];
    private int currentTile;
    private String tempLabel;
    private String tempSoundFile;
    private Bitmap tempImage;

    public static Display getInstance(Context c)
    {
        pictureManager = new PictureManager(c);
        soundRecording = new SoundRecording(c);
        context = c;
        if (display_instance == null) {
            display_instance = new Display();
        }

        return display_instance;
    }

    private Display(){
        //load default display
        currentTile = 0;
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int numTiles = myPreferences.getInt("numberTiles",4);
        labels = new String[numTiles];
        soundFiles = new String[numTiles];
        images = new Bitmap[numTiles];
        imageFiles = new String[numTiles];
        for(int i = 0; i < numTiles; i++){
            labels[i] = myPreferences.getString("label"+i, "");
            soundFiles[i] = myPreferences.getString("sound"+i, "");
            String imageFile = myPreferences.getString("image"+i, "");
            images[i] = pictureManager.getImageBitmap(imageFile);
        }
    }

    public void setTempImage(Bitmap image){
        tempImage = image;
    }

    public void setTempSound(String soundFile){
        tempSoundFile = soundFile;
    }

    public void setTempLabel(String label){
        tempLabel = label;
    }

    public void uploadTemp(){
        if(currentTile >= 0 && currentTile < labels.length) {
            labels[currentTile] = tempLabel;
            images[currentTile ] = tempImage;
            soundFiles[currentTile] = tempSoundFile;
        }
    }

    public void setCurrentTile(int tileNum) {
        currentTile = tileNum;
    }

    public void playSound(int i) {
        soundRecording.startPlaying(soundFiles[i]);
    }

    public Bitmap getImage(int i) {
        return images[i];
    }

    public String getLabel(int i) {
        return labels[i];
    }

    public String[] getSounds() {
        return soundFiles;
    }

    public String[] getLabels() {
        return labels;
    }

    public void copyData(int newNumPics) {
        int oldNumPics = getNumTiles();
        String[] newSoundBites = new String[newNumPics];
        String[] newLabels = new String[newNumPics];
        Bitmap[] newImages = new Bitmap[newNumPics];
        for(int i = 0; i < min(oldNumPics, newNumPics); i++){
            newSoundBites[i] = soundFiles[i];
            newLabels[i] = labels[i];
            newImages[i] = images[i];
        }
        soundFiles = newSoundBites;
        labels = newLabels;
        images = newImages;
    }

    public void setDisplay(Bitmap[] newImages, String[] newSoundFiles, String[] newLabels) {
        soundFiles = newSoundFiles;
        images = newImages;
        labels = newLabels;
        imageFiles = new String[images.length];
    }

    public int getNumTiles() {
        return images.length;
    }

    public void updateSaved(){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putInt("numberTiles",images.length);
        for(int i = 0; i < images.length; i++){
            myEditor.putString("label"+i, labels[i]);
            myEditor.putString("sound"+i, soundFiles[i]);
            myEditor.putString("image"+i, imageFiles[i]);
        }
        myEditor.commit();
    }

    public String[] getImageFiles() {
        int numPictures = getNumTiles();
        for (int i = 0; i < numPictures; i++) {
            String picturefile = pictureManager.writeImage(getImage(i), numPictures, i);
            imageFiles[i] = picturefile;
        }
        return imageFiles;
    }
}
