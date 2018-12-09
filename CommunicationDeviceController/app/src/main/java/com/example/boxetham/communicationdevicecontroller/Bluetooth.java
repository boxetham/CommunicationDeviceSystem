package com.example.boxetham.communicationdevicecontroller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.UUID;

public class Bluetooth extends AppCompatActivity implements View.OnClickListener {
    public final String LOG_TAG = getClass().getName();
    public final String SERIAL_SERVICE = "00001101-0000-1000-8000-00805F9B34FB";
    public enum BT_STATE {UNKNOWN_STATE, CONNECTED_STATE, DISCOVERY_FINISH_STATE, DISCOVERY_START_STATE, FAILURE_STATE, NULL_ADAPTER};

    private ArrayList<BluetoothDevice> _deviceList = new ArrayList<BluetoothDevice>();
    private BluetoothDevice _selectedDevice = null;

    private BluetoothSocket _socket = null;

    private ListView _deviceListView;
    private BtArrayAdapter _btArrayAdapter;

    private BluetoothAdapter _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private TextView _tvBtAddress;
    private TextView _tvBtName;
    private TextView _tvBtState;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice temp = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (_deviceList.contains(temp)) {
                    Log.i(LOG_TAG, "skipping:" + temp.getName() + ":" + temp.getAddress());
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

        _deviceListView = (ListView) findViewById(R.id.lvBtDevice);

        _tvBtAddress = (TextView) findViewById(R.id.tvBtAddress);
        _tvBtName = (TextView) findViewById(R.id.tvBtName);
        _tvBtState = (TextView) findViewById(R.id.tvBtState);
    }

    @Override
    public void onClick(View view) {
        final WriteRead writeRead;
        AlertDialog.Builder builder;
        final EditText input;

        switch (view.getId()) {
            case R.id.buttonDiscoveryCancel:
                discoveryStop();
                break;
            case R.id.buttonDiscoveryStart:
                discoveryStart();
                break;
            case R.id.buttonToggleGreenLed:
                writeRead = new WriteRead(_socket, "green");//GREEN_LED);
//                writeRead = new WriteRead(_socket, songList[5]);
                new Thread(writeRead).start();
                break;
            case R.id.buttonToggleRedLed:
                writeRead = new WriteRead(_socket, "Yellow");//RED_LED);
                new Thread(writeRead).start();
                break;
            case R.id.buttonToggleYellowLed:
                writeRead = new WriteRead(_socket, "red");//YELLOW_LED);
                new Thread(writeRead).start();
                break;
            default:
                Log.i(LOG_TAG, "unknown click event");
        }
    }

    private void discoveryStart() {
        Log.d(LOG_TAG, "discoveryStart");

        if (_bluetoothAdapter == null) {
            updateState(BT_STATE.NULL_ADAPTER);
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        _bluetoothAdapter.startDiscovery();
    }

    private void discoveryStop() {
        Log.d(LOG_TAG, "discoveryStop");

        if (_bluetoothAdapter == null) {
            Log.i(LOG_TAG, "unable to run w/null BT adapter");
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
                _btArrayAdapter = new BtArrayAdapter(getBaseContext(), _deviceList);
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
        findViewById(R.id.buttonToggleGreenLed).setEnabled(flag);
        findViewById(R.id.buttonToggleRedLed).setEnabled(flag);
        findViewById(R.id.buttonToggleYellowLed).setEnabled(flag);
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
}

class WriteRead implements Runnable {
    public final String LOG_TAG = getClass().getName();

    //    private final MainActivity.BB_LED _led;
    private final BluetoothSocket _socket;
    String _song;

    private Reader _reader;
    private Writer _writer;

    private final StringBuilder _stringBuilder = new StringBuilder();

//    WriteRead(BluetoothSocket socket, MainActivity.BB_LED led) {
//        _socket = socket;
//        _led = led;
//    }

    WriteRead(BluetoothSocket socket, String msg) {
        _socket = socket;
        _song = msg;
    }

    public String getResponse() {
        return _stringBuilder.toString();
    }

    public void run() {
        try {
            _reader = new InputStreamReader(_socket.getInputStream(), "UTF-8");
            _writer = new OutputStreamWriter(_socket.getOutputStream(), "UTF-8");

//            switch (_song) {
//                case "Nocturne":
//                    Log.i(LOG_TAG, "write Nocturne");
//                    _writer.write(_song + "\n");
//                    _writer.flush();
//            }

            switch(_song) {//_led) {
                case "yellow":
                    Log.i(LOG_TAG, "write vol down");
                    _writer.write(_song + "\n");
                    _writer.flush();
                    break;
                case "green":
                    Log.i(LOG_TAG, "green");
                    _writer.write(_song + "\n");
                    _writer.flush();
                    break;
                case "red":
                    Log.i(LOG_TAG, "red");
                    _writer.write("stop\n");
                    _writer.flush();
                    break;
                default:
                    _writer.write("ahhhhh\n");
                    _writer.flush();
                    _writer.write(_song + "\n");
                    _writer.flush();
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