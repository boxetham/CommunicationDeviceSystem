package com.example.communicationdevicecontroller;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SettingsPackager {

    private Map<String, Byte> settingsEncoding = new HashMap<String, Byte>(){{
        put("volume", new Byte((byte)0)); put("vibration", new Byte((byte)1)); put("music", new Byte((byte)2));
    }};
    private int volume;
    private boolean vibration;
    private boolean music;

    public SettingsPackager(int vol, boolean vib, boolean music){
        this.volume = vol;
        this.vibration = vib;
        this.music = music;
    }

    public ArrayList<Byte> getSetting(String setting) {
        ArrayList<Byte> settings = new ArrayList<>();
        settings.add(getSettingEncoding(setting));
        settings.addAll(getData(setting));
        return settings;
    }

    private ArrayList<Byte> getData(String key) {
        ArrayList<Byte> arr = new ArrayList<>();
        switch (key){
            case "volume":
                ByteBuffer b = ByteBuffer.allocate(4);
                b.putInt(volume);
                byte[] num = b.array();
                ArrayList<Byte> vol = new ArrayList<>();
                for(int i = 0; i < num.length; i++){
                    vol.add(num[i]);
                }
                arr.addAll(vol);
                break;
            case "vibration":
                arr.add((byte)(vibration?1:0));
                break;
            case "music":
                arr.add((byte)(music?1:0));
                break;
        }
        return arr;
    }

    public Set<String> getSettings() {
        return settingsEncoding.keySet();
    }

    private Byte getSettingEncoding(String setting) {
        return settingsEncoding.get(setting);
    }
}