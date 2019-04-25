package com.example.communicationdevicecontroller;

import android.content.Context;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayPackager {

    private static Map<Integer, int[]> sizeEncoding = new HashMap<Integer, int[]>(){{
        put(4, new int[]{1,4}); put(8, new int[]{2,4}); put(15, new int[]{3,5}); put(24, new int[]{4,6});
    }};

    private int numTiles;
    private String[] labels;
    private String[] pictureFiles;
    private String[] soundFiles;
    private SoundRecording recording;
    private PictureManager pictureManager;

    public DisplayPackager(int numTiles, String[] labels, String[] pictureFiles, String[] soundFiles, Context context){
        this.numTiles = numTiles;
        this.labels = labels;
        this.pictureFiles = pictureFiles;
        this.soundFiles = soundFiles;
        this.recording = new SoundRecording(context);
        this.pictureManager = new PictureManager(context);
    }

    public ArrayList<Byte> getDisplayConfig() {
        int[] dimensions = sizeEncoding.get(numTiles);
        int row = dimensions[0];
        int col = dimensions[1];
        ArrayList<Byte> configInfo = new ArrayList<>();
        configInfo.add((byte)127);
        configInfo.addAll(getByteArrayList(row));
        configInfo.addAll(getByteArrayList(col));
        return  configInfo;
    }

    public ArrayList<Byte> getTile(int i) {
        ArrayList<Byte> tile = new ArrayList<>();
        tile.addAll(getPicture(i));
        tile.addAll(getSound(i));
        tile.addAll(getLabel(i));
        return tile;
    }

    private ArrayList<Byte> getPicture(int i) {
        return pictureManager.readImageFile(pictureFiles[i]);
    }

    private ArrayList<Byte> getSound(int i) {
        ArrayList<Byte> sound = recording.readSoundFile(soundFiles[i]);
        sound.addAll(0, getByteArrayList(sound.size()));
        return sound;
    }

    private ArrayList<Byte> getLabel(int i) {
        if(labels[i] == null){
            return new ArrayList<>();
        }
        ArrayList<Byte> msg = new ArrayList<>();
        msg.addAll(getByteArrayList(labels[i].length()));
        msg.addAll(getBytesFromString(labels[i]));
        return msg;
    }

    public int getNumTiles() {
        return numTiles;
    }

    private static ArrayList<Byte> getByteArrayList(int num) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(num);
        byte[] arr = b.array();
        ArrayList<Byte> finalVal = new ArrayList<>();
        for(int i = 0; i < arr.length; i++){
            finalVal.add(arr[i]);
        }
        return finalVal;
    }

    private static ArrayList<Byte> getBytesFromString(String str){
        byte[] bytes = new byte[0];
        try { bytes = str.getBytes("UTF-8"); }catch (Exception e){}
        ArrayList<Byte> arr = new ArrayList<>();
        for(int i = 0; i < bytes.length; i++){
            arr.add(bytes[i]);
        }
        return arr;
    }
}
