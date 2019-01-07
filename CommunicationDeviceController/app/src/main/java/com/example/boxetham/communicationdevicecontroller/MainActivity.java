package com.example.boxetham.communicationdevicecontroller;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);

        findViewById(R.id.btChangeDisplay).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToChangeDisplay();
            }
        });
        findViewById(R.id.btSettings).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToSettings();
            }
        });
        findViewById(R.id.btDefault).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowPopup();
            }
        });
        findViewById(R.id.btConnect).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToBluetooth();
            }
        });
    }

    public void ShowPopup() {
        myDialog.setContentView(R.layout.reset_to_default);
        myDialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.findViewById(R.id.btYes).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //reset to default view on device
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void goToBluetooth() {
        Intent intent = new Intent(this, Bluetooth.class);
        startActivity(intent);
    }

    private void goToChangeDisplay() {
        Intent intent = new Intent(this, ChangeDisplay.class);
        startActivity(intent);
    }

    private void goToSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
