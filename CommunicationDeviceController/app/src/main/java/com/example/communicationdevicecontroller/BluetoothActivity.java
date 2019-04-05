package com.example.communicationdevicecontroller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {
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

        startCancelButton(true);

        findViewById(R.id.buttonDiscoveryCancel).setOnClickListener(this);
        findViewById(R.id.buttonDiscoveryStart).setOnClickListener(this);
        findViewById(R.id.btMain).setOnClickListener(this);

        _deviceListView = (ListView)findViewById(R.id.lvBtDevice);

        _tvBtAddress = (TextView) findViewById(R.id.tvBtAddress);
        _tvBtName = (TextView)findViewById(R.id.tvBtName);
        _tvBtState = (TextView)findViewById(R.id.tvBtState);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonDiscoveryCancel:
                discoveryStop();
                break;
            case R.id.buttonDiscoveryStart:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
                discoveryStart();
                break;
            case R.id.btMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                //do nothing
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
                startCancelButton(true);
                _tvBtState.setText(getString(R.string.label_bt_state_failure));
                break;
            case NULL_ADAPTER:
                startCancelButton(true);
                _tvBtState.setText(getString(R.string.label_null_adapter));
                break;
            case UNKNOWN_STATE:
                startCancelButton(true);
                _tvBtState.setText(getString(R.string.label_bt_state_unknown));
                break;
        }
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

    public static void sendSettings(SettingsPackager settingInfo) {
        ArrayList<Byte> settings = new ArrayList<>();
        for (String setting : settingInfo.getSettings()){
            settings.addAll(settingInfo.getSetting(setting));
        }
        settings.add((byte)127);
        WriteRead writeRead = new WriteRead(_socket, getbyteArray(settings));
        new Thread(writeRead).start();
    }

    public static void sendDisplay(DisplayPackager displayInfo) {
        ArrayList<Byte> display = new ArrayList<>();
        display.addAll(displayInfo.getDisplayConfig());
        for(int i = 0; i < displayInfo.getNumTiles(); i++){
            display.addAll(displayInfo.getTile(i));
        }
        WriteRead writeRead = new WriteRead(_socket, getbyteArray(display));
        new Thread(writeRead).start();
    }

    private static byte[] getbyteArray(ArrayList<Byte> settings) {
        byte[] arr = new byte[settings.size()];
        for(int i = 0; i < arr.length; i++){
            arr[i] = settings.get(i);
        }
        return arr;
    }

}

class WriteRead implements Runnable {
    private final BluetoothSocket _socket;
    byte[] _message;

    private Reader _reader;
    private OutputStream output;

    private final StringBuilder _stringBuilder = new StringBuilder();

    WriteRead(BluetoothSocket socket, byte[] msg) {
        _socket = socket;
        _message = msg;
    }

    public String getResponse() {
        return _stringBuilder.toString();
    }

    public void run() {
        try {
            _reader = new InputStreamReader(_socket.getInputStream(), "UTF-8");
            output = _socket.getOutputStream();
            output.write(_message);
            output.flush();

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