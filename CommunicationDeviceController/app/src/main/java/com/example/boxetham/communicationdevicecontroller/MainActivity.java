package com.example.boxetham.communicationdevicecontroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btChangeDisplay).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToChangeDisplay();
            }
        });
        findViewById(R.id.btSettings).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gotToSettings();
            }
        });
    }

    private void goToChangeDisplay() {
        Intent intent = new Intent(this, ChangeDisplay.class);
        startActivity(intent);
    }

    private void gotToSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
