package com.example.boxetham.communicationdevicecontroller;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class ChangeDisplay4 extends AppCompatActivity {

    Dialog myDialog;
    private static final int CHOOSE_PICTURE = 2;
    private static final int TAKE_PICTURE = 1;
    private static final int numPics = 4;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_display_4);
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
        for (int i = 1; i <= numPics; i++) {
            int id = getResources().getIdentifier("imageView" + i, "id", getPackageName());
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ShowCameraPopup();
                }
            });
        }
    }

    private void ShowCameraPopup() {
        myDialog.setContentView(R.layout.change_display_change_tile);
        myDialog.findViewById(R.id.btCamera).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                if(checkPermissions() != 1) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, TAKE_PICTURE);
                    }
                }
            }
        });
        myDialog.findViewById(R.id.btGallery).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (choosePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(choosePictureIntent, CHOOSE_PICTURE);
                }
            }
        });
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

    private void ShowSoundPopup(View v) {
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

    private void ShowTextPopup(View v) {
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    TAKE_PICTURE);
            return 1;
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TAKE_PICTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    //
                } else {
                    cancelGoToMain();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
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
                    ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                    imageView.setImageBitmap(imageBitmap);
                }
        }
    }
}
