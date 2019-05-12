package com.rosehulman.communicationdevicecontroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public class LabelSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_selection);

        setContentView(R.layout.activity_label_selection);
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancel();
            }
        });
        findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText edit = (EditText)findViewById(R.id.input);
                updateDisplay(edit.getText());
            }
        });
    }

    private void updateDisplay(Editable text) {
        ChangeDisplayActivity.currentDisplay.setTempLabel(text.toString());
        ChangeDisplayActivity.currentDisplay.uploadTemp();
        Intent intent = new Intent(this, ChangeDisplayActivity.class);
        startActivity(intent);
    }

    private void cancel() {
        Intent intent = new Intent(this, ChangeDisplayActivity.class);
        startActivity(intent);
    }
}
