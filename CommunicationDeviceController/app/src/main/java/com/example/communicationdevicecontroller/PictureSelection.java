package com.example.communicationdevicecontroller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class PictureSelection extends AppCompatActivity {

    private static Map<Integer, String[]> permissionMap = new HashMap<Integer, String[]>(){{
        put(TAKE_PICTURE, new String[]{Manifest.permission.CAMERA});
        put(WRITE_EXTERNAL, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }};

    public static final int WRITE_EXTERNAL = 5;
    public static final int CROP = 4;
    public static final int CHOOSE_PICTURE = 2;
    public static final int TAKE_PICTURE = 1;
    private Pictures pictureSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selection);
        pictureSettings = new Pictures(this);
        findViewById(R.id.btCamera).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                if (checkPermissions(TAKE_PICTURE) != 1) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    URI uri = pictureSettings.createImageFile().toURI();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, TAKE_PICTURE);
                    }
                }
            }
        });
        findViewById(R.id.btGallery).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (choosePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(choosePictureIntent, CHOOSE_PICTURE);
                }
            }
        });
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void cancel() {
        Intent intent = new Intent(this, ChangeDisplay.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int checkPermissions(int permission) {
        if(ActivityCompat.checkSelfPermission(this, permissionMap.get(permission)[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissionMap.get(permission), permission);
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay!
        } else {
            cancel();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    pictureSettings.doCrop(imageBitmap);
                }
                break;
            case CHOOSE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pictureSettings.doCrop(bitmap);
                }
                break;
            case CROP:
                Bundle extras = data.getExtras();
                if (extras != null) {
                    String file = data.getStringExtra("imagefile");
                    Bitmap imageViewBitmap = pictureSettings.getImageBitmap(file);
                    pictureSettings.deleteFile(file);
                    if(imageViewBitmap!=null){
                        ChangeDisplay.currentDisplay.setTempImage(imageViewBitmap);
                        Intent intent = new Intent(this, SoundSelection.class);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

}
