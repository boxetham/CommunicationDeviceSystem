package com.example.boxetham.communicationdevicecontroller;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ChangeDisplay24 extends AppCompatActivity {

    Dialog myDialog;
    private static int[] popups = {R.layout.change_display_change_tile, R.layout.change_display_change_tile_2, R.layout.change_display_change_tile_3};
    private static int popNum = 3;
    private int popuptracker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_display_24);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        myDialog = new Dialog(this);
        findViewById(R.id.btNum4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToDisplay(4);
            }
        });
        findViewById(R.id.btNum8).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToDisplay(8);
            }
        });
        findViewById(R.id.btNum15).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToDisplay(15);
            }
        });
        findViewById(R.id.btNum24).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToDisplay(24);
            }
        });
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelGoToMain();
            }
        });
    }

    public void ShowPopup(View v, int layout) {
        popuptracker++;
        myDialog.setContentView(layout);
        myDialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                if(popuptracker != popNum){
                    ShowPopup(v, popups[popuptracker%popNum]);
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void cancelGoToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToDisplay(int numPics){
        Intent intent = null;
        switch (numPics){
            case 4:
                intent = new Intent(this, ChangeDisplay4.class);
                break;
            case 8:
                intent = new Intent(this, ChangeDisplay8.class);
                break;
            case 15:
                intent = new Intent(this, ChangeDisplay15.class);
                break;
            case 24:
                intent = new Intent(this, ChangeDisplay24.class);
                break;
        }
        startActivity(intent);
    }
}
