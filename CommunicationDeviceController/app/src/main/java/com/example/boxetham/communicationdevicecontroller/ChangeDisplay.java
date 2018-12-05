package com.example.boxetham.communicationdevicecontroller;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

public class ChangeDisplay extends AppCompatActivity {

    Dialog myDialog;
    private static int[] popups = {R.layout.change_display_change_tile, R.layout.change_display_change_tile_2, R.layout.change_display_change_tile_3};
    private static int popNum = 3;
    private int popuptracker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_display);
        myDialog = new Dialog(this);
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelGoToMain();
            }
        });
        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popuptracker = 0;
                ShowPopup(v, popups[popuptracker]);
            }
        });
        findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popuptracker = 0;
                ShowPopup(v, popups[popuptracker]);
            }
        });
        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popuptracker = 0;
                ShowPopup(v, popups[popuptracker]);
            }
        });
        findViewById(R.id.imageView4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popuptracker = 0;
                ShowPopup(v, popups[popuptracker]);
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
}
