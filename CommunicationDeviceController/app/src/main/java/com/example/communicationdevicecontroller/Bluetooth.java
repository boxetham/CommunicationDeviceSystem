package com.example.communicationdevicecontroller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Bluetooth extends AppCompatActivity implements View.OnClickListener {
    private static Map<Boolean, Byte> booleanEncoding = new HashMap<Boolean, Byte>(){{
        put(false, new Byte((byte)0)); put(true, new Byte((byte)1));
    }};
    public final int REQUEST_ENABLE_BT = 1;
    public final String SERIAL_SERVICE = "00001101-0000-1000-8000-00805F9B34FB";

    public enum BT_STATE {UNKNOWN_STATE, CONNECTED_STATE, DISCOVERY_FINISH_STATE, DISCOVERY_START_STATE, FAILURE_STATE, NULL_ADAPTER};

    private ArrayList<BluetoothDevice> _deviceList = new ArrayList<BluetoothDevice>();
    private BluetoothDevice _selectedDevice = null;

    private static BluetoothSocket _socket = null;

    private ListView _deviceListView;
    private BtArrayAdapter _btArrayAdapter;

    private BluetoothAdapter _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private TextView _tvBtAddress;
    private TextView _tvBtName;
    private TextView _tvBtState;

    private SoundRecording recording;
    private static ArrayList<Byte> displayInfo = new ArrayList<>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice temp = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (_deviceList.contains(temp)) {
                    //skip
                } else {
                    _deviceList.add(temp);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                updateState(BT_STATE.DISCOVERY_START_STATE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                updateState(BT_STATE.DISCOVERY_FINISH_STATE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        toggleButton(false);
        startCancelButton(true);

        findViewById(R.id.buttonDiscoveryCancel).setOnClickListener(this);
        findViewById(R.id.buttonDiscoveryStart).setOnClickListener(this);
        findViewById(R.id.buttonToggleGreenLed).setOnClickListener(this);
        findViewById(R.id.buttonToggleRedLed).setOnClickListener(this);
        findViewById(R.id.buttonToggleYellowLed).setOnClickListener(this);

        _deviceListView = (ListView)findViewById(R.id.lvBtDevice);

        _tvBtAddress = (TextView) findViewById(R.id.tvBtAddress);
        _tvBtName = (TextView)findViewById(R.id.tvBtName);
        _tvBtState = (TextView)findViewById(R.id.tvBtState);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View view) {
        final WriteRead writeRead;

        switch (view.getId()) {
            case R.id.buttonDiscoveryCancel:
                discoveryStop();
                break;
            case R.id.buttonDiscoveryStart:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
                discoveryStart();
                break;
            case R.id.buttonToggleGreenLed:
                recording = new SoundRecording(this);
                String soundFile = recording.getFile("wavtest");
                ArrayList<Byte> sound = new ArrayList<>();
                try {
                    InputStream inputStream = new FileInputStream(soundFile);
                    byte[] bytedata = new byte[1024];
                    int    bytesRead = inputStream.read(bytedata);
                    while(bytesRead != -1) {
                        for(int i = 0; i < bytesRead; i++){
                            sound.add(bytedata[i]);
                        }
                        bytesRead = inputStream.read(bytedata);
                    }
                }catch(Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Please try again");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {                    }
                    });
                    builder.show();
                }
                writeRead = new WriteRead(_socket, getbyteArray(sendFile("wavtest", "sound", recording)), "display");//RED_LED);
                new Thread(writeRead).start();
                break;
            case R.id.buttonToggleRedLed:
                Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (choosePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(choosePictureIntent, 1);
                }
                break;
            case R.id.buttonToggleYellowLed:
                writeRead = new WriteRead(_socket, "yellow", "label");//YELLOW_LED);
                new Thread(writeRead).start();
                break;
            default:
                //do nothing
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    ArrayList<Byte> picture = new ArrayList<Byte>();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        byte[] bytedata = new byte[1024];
                        int    bytesRead = inputStream.read(bytedata);
                        while(bytesRead != -1) {
                            for(int i = 0; i < bytesRead; i++){
                                picture.add(bytedata[i]);
                            }
                            bytesRead = inputStream.read(bytedata);
                        }
                    }catch(Exception e){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Please try again");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {                    }
                        });
                        builder.show();
                    }
                    WriteRead writeRead = new WriteRead(_socket, getbyteArray(picture), "picture");//RED_LED);
                    new Thread(writeRead).start();
                }
                break;
        }
    }

    private void discoveryStart() {
        if (_bluetoothAdapter == null) {
            updateState(BT_STATE.NULL_ADAPTER);
            return;
        }

        if (!_bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        _bluetoothAdapter.startDiscovery();
    }

    private void discoveryStop() {
        if (_bluetoothAdapter == null) {
            return;
        }

        _bluetoothAdapter.cancelDiscovery();
    }

    private void bluetoothClose() {
        if (_socket != null) {
            try {
                _socket.close();
            } catch (Exception exception) {
                // empty
            }
        }

        _socket = null;
    }

    private void bluetoothConnect(BluetoothDevice target) {
        _selectedDevice = target;
        _tvBtAddress.setText(target.getAddress());
        _tvBtName.setText(target.getName());

        try {
            _socket = target.createRfcommSocketToServiceRecord(UUID.fromString(SERIAL_SERVICE));
            _socket.connect();

            toggleButton(true);
            updateState(BT_STATE.CONNECTED_STATE);
        } catch(Exception exception) {
            bluetoothClose();
            exception.printStackTrace();
            updateState(BT_STATE.FAILURE_STATE);
        }
    }

    private void updateState(BT_STATE target) {
        switch(target) {
            case CONNECTED_STATE:
                toggleButton(true);
                _tvBtState.setText(getString(R.string.label_bt_state_connected));
                break;
            case DISCOVERY_FINISH_STATE:
                _btArrayAdapter = new BtArrayAdapter(this, _deviceList);
                _deviceListView.setAdapter(_btArrayAdapter);

                _deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                        bluetoothConnect((BluetoothDevice) adapter.getItemAtPosition(position));
                    }
                });

                _tvBtState.setText(getString(R.string.label_bt_state_discovery_finish));
                startCancelButton(true);
                break;
            case DISCOVERY_START_STATE:
                _deviceList.clear();
                _tvBtState.setText(getString(R.string.label_bt_state_discovery_start));
                startCancelButton(false);
                break;
            case FAILURE_STATE:
                bluetoothClose();
                toggleButton(false);
                startCancelButton(true);
                _tvBtState.setText(getString(R.string.label_bt_state_failure));
                break;
            case NULL_ADAPTER:
                toggleButton(false);
                startCancelButton(true);
                _tvBtState.setText(getString(R.string.label_null_adapter));
                break;
            case UNKNOWN_STATE:
                toggleButton(false);
                startCancelButton(true);
                _tvBtState.setText(getString(R.string.label_bt_state_unknown));
                break;
        }
    }

    private void toggleButton(boolean flag) {
        findViewById(R.id.buttonToggleGreenLed).setEnabled(true);
        findViewById(R.id.buttonToggleRedLed).setEnabled(true);
        findViewById(R.id.buttonToggleYellowLed).setEnabled(true);
    }

    private void startCancelButton(boolean flag) {
        if (flag) {
            findViewById(R.id.buttonDiscoveryCancel).setEnabled(false);
            findViewById(R.id.buttonDiscoveryStart).setEnabled(true);
        } else {
            findViewById(R.id.buttonDiscoveryCancel).setEnabled(true);
            findViewById(R.id.buttonDiscoveryStart).setEnabled(false);
        }
    }

    // 0 - volume
    // 1 - vibration
    // 2 - music
    public static void sendSettings(boolean vibration, boolean music, int volume) {
        ArrayList<Byte> settings = new ArrayList<>();
        settings.add((byte)0);
        settings.addAll(getByteArrayList(volume));
        settings.add((byte)1);
        settings.add(booleanEncoding.get(vibration));
        settings.add((byte)2);
        settings.add(booleanEncoding.get(music));
        settings.add((byte)127);
        WriteRead writeRead = new WriteRead(_socket, getbyteArray(settings), "settings");
        new Thread(writeRead).start();
    }

    private static byte[] getbyteArray(ArrayList<Byte> settings) {
        byte[] arr = new byte[settings.size()];
        for(int i = 0; i < arr.length; i++){
            arr[i] = settings.get(i);
        }
        return arr;
    }

    private static ArrayList<Byte> getByteArrayList(int volume) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(volume);
        byte[] arr = b.array();
        ArrayList<Byte> vol = new ArrayList<>();
        for(int i = 0; i < arr.length; i++){
            vol.add(arr[i]);
        }
        return vol;
    }

    public static void sendDisplay(String[] labels, String[] pictureFiles, String[] soundFiles, SoundRecording rec) {
        displayInfo.clear();
        displayInfo.addAll(sendDisplayConfig(labels.length));
        for(int i = 0; i < labels.length; i++){
            displayInfo.addAll(sendFile(pictureFiles[i], "picture", rec));
            displayInfo.addAll(sendFile(soundFiles[i], "sound", rec));
            displayInfo.addAll(sendLabel(labels[i]));
        }
        WriteRead writeRead = new WriteRead(_socket, getbyteArray(displayInfo), "display");
        new Thread(writeRead).start();
    }

    private static ArrayList<Byte> sendDisplayConfig(int numPictures) {
        int row = 0;
        int col = 0;
        switch (numPictures){
            case 4:
                row = 1;
                col = 4;
                break;
            case 8:
                row = 2;
                col = 4;
                break;
            case 15:
                row = 3;
                col = 5;
                break;
            case 24:
                row = 4;
                col = 6;
                break;
        }
        ArrayList<Byte> settings = new ArrayList<>();
        settings.add((byte)127);
        settings.addAll(getByteArrayList(row));
        settings.addAll(getByteArrayList(col));
        return  settings;
    }

    private static ArrayList<Byte> sendLabel(String label) {
        ArrayList<Byte> msg = new ArrayList<>();
        msg.addAll(getByteArrayList(label.length()));
        msg.addAll(getBytesFromString(label));
        return msg;
    }

    private static ArrayList<Byte> getBytesFromString(String str){
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes("UTF-8");
        }catch (Exception e){}
        ArrayList<Byte> arr = new ArrayList<>();
        for(int i = 0; i < bytes.length; i++){
            arr.add(bytes[i]);
        }
        return arr;
    }

    private static ArrayList<Byte> sendFile(String file, String type, SoundRecording recording) {
        ArrayList<Byte> msg = new ArrayList<>();
        ArrayList<Byte> arr = readFile(file);
        if(type.equals("sound")){
            String soundFile = recording.getFile("wavtest");
            arr = readFile(soundFile);
            msg.addAll(getByteArrayList(arr.size()));
        }
        msg.addAll(arr);
        return msg;
    }

    private static ArrayList<Byte> readFile(String pictureFile) {
        ArrayList<Byte> sound = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(pictureFile);
            byte[] bytedata = new byte[1024];
            int    bytesRead = inputStream.read(bytedata);
            while(bytesRead != -1) {
                for(int i = 0; i < bytesRead; i++){
                    sound.add(bytedata[i]);
                }
                bytesRead = inputStream.read(bytedata);
            }
        }catch(Exception e){
        }
        return sound;
    }
}

class WriteRead implements Runnable {
    private final BluetoothSocket _socket;
    byte[] _message;

    private Reader _reader;
    private Writer _writer;
    private OutputStream output;
    private String type;
    private String _label;

    private final StringBuilder _stringBuilder = new StringBuilder();

    WriteRead(BluetoothSocket socket, String msg, String type) {
        this.type = type;
        _socket = socket;
        _label = msg;
    }

    WriteRead(BluetoothSocket socket, byte[] msg, String type) {
        this.type = type;
        _socket = socket;
        _message = msg;
    }

    public String getResponse() {
        return _stringBuilder.toString();
    }

    public void run() {
        try {
            _reader = new InputStreamReader(_socket.getInputStream(), "UTF-8");
            _writer = new OutputStreamWriter(_socket.getOutputStream(), "UTF-8");
            output = _socket.getOutputStream();

            switch(type) {

                case "label":
                    _writer.write(_label.length());
                    _writer.write(_label); // write the message
                    _writer.flush();
                    break;
                case "sound":
                    output.write(_message);
                    output.flush();
                    break;
                case "picture":
                    output.write(_message);
                    output.flush();
                    break;
                case "settings":
                    output.write(_message);
                    output.flush();
                    break;
                case "config":
                    output.write(_message);
                    output.flush();
                    break;
                case "display":
                    output.write(_message);
                    output.flush();
                    break;
                default:
                    break;
            }

            final char[] buffer = new char[8];
            while (true) {
                int size = _reader.read(buffer);
                if (size < 0) {
                    break;
                } else {
                    _stringBuilder.append(buffer, 0, size);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}