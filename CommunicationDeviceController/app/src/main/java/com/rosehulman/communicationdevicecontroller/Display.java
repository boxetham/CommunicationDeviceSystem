package com.rosehulman.communicationdevicecontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import java.util.HashMap;

import static java.lang.Math.min;

public class Display {

    private static Display display_instance; //if you have multiple devices this could be a map
                                             //from device name to display to handle multiple devices
                                             // then you would have getInstance(Context, name)
    private static PictureManager pictureManager;
    private static SoundManager soundManager;
    private static Context context;
    private String labels[];
    private String soundFiles[];
    private Bitmap images[];
    private String imageFiles[];
    private int currentTile;
    private String tempLabel;
    private String tempSoundFile;
    private Bitmap tempImage;
    private HashMap<Integer, Display> oldDisplays;

    public static Display getInstance(Context c)
    {
        pictureManager = new PictureManager(c);
        soundManager = new SoundManager(c);
        context = c;
        if (display_instance == null) {
            display_instance = new Display();
        }

        return display_instance;
    }

    private Display(){
        //load default display
        oldDisplays = new HashMap<>();
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

    private Display(String labels[], String soundFiles[], Bitmap images[], String imageFiles[]){
        this.labels = labels;
        this.soundFiles = soundFiles;
        this.imageFiles = imageFiles;
        this.images = images;
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
        soundManager.startPlaying(soundFiles[i]);
    }

    public Bitmap getImage(int i) {
        return images[i];
    }

    public String getLabel(int i) {
        return labels[i];
    }

    public String[] getSounds() { return soundFiles; }

    public String[] getLabels() { return labels; }

    public Bitmap[] getImages() { return images; }

    public void copyData(int newNumPics) {
        int oldNumPics = getNumTiles();
        //update current display
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
        imageFiles = new String[newNumPics];
        checkForOldDisplayInfo(oldNumPics);
    }

    private void checkForOldDisplayInfo(int oldNumTiles) {
        Display oldDisplay = oldDisplays.get(getNumTiles());
        if(oldDisplay !=null){
            String[] oldSoundFiles = oldDisplay.getSounds();
            String[] oldLabels = oldDisplay.getLabels();
            Bitmap[] oldImages = oldDisplay.getImages();
            for(int i = oldNumTiles; i < oldDisplay.getNumTiles(); i++){
                soundFiles[i] = oldSoundFiles[i];
                labels[i] = oldLabels[i];
                images[i] = oldImages[i];
            }
        }
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
        int numTiles = getNumTiles();
        Display oldDisplay = new Display(labels, soundFiles, images, imageFiles);
        oldDisplays.put(numTiles, oldDisplay);
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        imageFiles = getImageFiles();
        myEditor.putInt("numberTiles",images.length);
        for(int i = 0; i < images.length; i++){
            myEditor.putString("label"+i, labels[i]);
            myEditor.putString("sound"+i, soundFiles[i]);
            myEditor.putString("image"+i, imageFiles[i]);
        }
        myEditor.commit();
    }

    public void discardChanges() {
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

    public String[] getImageFiles() {
        int numPictures = getNumTiles();
        for (int i = 0; i < numPictures; i++) {
            String picturefile = pictureManager.writeImage(getImage(i), numPictures, i);
            imageFiles[i] = picturefile;
        }
        return imageFiles;
    }
}
