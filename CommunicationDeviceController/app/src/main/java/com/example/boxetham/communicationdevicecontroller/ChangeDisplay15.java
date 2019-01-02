package com.example.boxetham.communicationdevicecontroller;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ChangeDisplay15 extends AppCompatActivity {

    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_display_15);
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
        for (int i = 1; i <= 15; i++) {
            int id = getResources().getIdentifier("imageView"+i, "id", getPackageName());
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ShowCameraPopup(v);
                }
            });
        }
    }

    public void ShowCameraPopup(View v) {
        myDialog.setContentView(R.layout.change_display_change_tile);
        myDialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                ShowSoundPopup(v);
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void ShowSoundPopup(View v) {
        myDialog.setContentView(R.layout.change_display_change_tile_2);
        myDialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                ShowTextPopup(v);
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void ShowTextPopup(View v) {
        myDialog.setContentView(R.layout.change_display_change_tile_3);
        myDialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();

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
