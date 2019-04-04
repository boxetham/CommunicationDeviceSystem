package com.example.communicationdevicecontroller;

import android.graphics.Bitmap;
import static java.lang.Math.min;

public class Display {

    private static Display display_instance; //if you have multiple devices this could be a map
                                             //from device name to display to handle multiple devices
    private String labels[];
    private String soundFiles[];
    private Bitmap images[];
    private int currentTile;
    private String tempLabel;
    private String tempSoundFile;
    private Bitmap tempImage;

    public static Display getInstance()
    {
        if (display_instance == null) {
            display_instance = new Display();
        }

        return display_instance;
    }

    private Display(){
        //load default display
        currentTile = 0;
        labels = new String[4];
        soundFiles = new String[4];
        images = new Bitmap[4];
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
        //tiles number 1 to no. tiles, array index will be one less
        if(currentTile > 0 && currentTile < labels.length) {
            labels[currentTile - 1] = tempLabel;
            images[currentTile - 1] = tempImage;
            soundFiles[currentTile - 1] = tempSoundFile;
        }
    }

    public void setLabel(String newLabel, int tile){
        labels[tile] = newLabel;
    }

    public void setSoundFile(String newSound, int tile){
        soundFiles[tile] = newSound;
    }

    public void setImage(Bitmap newImage, int tile){
        images[tile] = newImage;
    }

    public void setCurrentTile(int tileNum) {
        currentTile = tileNum;
    }

    public String getSound(int i) {
        return soundFiles[i];
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

    public Bitmap[] getImages() {
        return images;
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
    }

    public int getNumTiles() {
        return images.length;
    }
}
