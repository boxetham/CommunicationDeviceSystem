package com.example.communicationdevicecontroller;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.View;

import java.net.URI;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class PictureSelection extends ChangeDisplay {

    private Dialog dialog;

    public PictureSelection(Dialog dialog){
        this.dialog = dialog;
    }

    public void showCameraPopup(){

    }

}
