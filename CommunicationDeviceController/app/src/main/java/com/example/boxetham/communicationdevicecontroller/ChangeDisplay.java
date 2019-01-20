package com.example.boxetham.communicationdevicecontroller;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class ChangeDisplay extends AppCompatActivity {

    Dialog myDialog;
    public static final int CROP_FROM_CAMERA = 4;
    private static final int RECORD_AUDIO = 3;
    private static final int CHOOSE_PICTURE = 2;
    private static final int TAKE_PICTURE = 1;
    private static int numPictures;
    private int imageViewID = 0;
    private int labelID = 0;
    private SoundRecording recorder = null;
    private Pictures pictureSettings = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_display);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setValues(savedInstanceState);
        configureButtons();
        setupImageViews();
    }

    private void setValues(Bundle savedInstanceState) {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        numPictures = prefs.getInt("numPictures", 4);
        if (savedInstanceState != null) {
            imageViewID = savedInstanceState.getInt("imageViewID");
            labelID = savedInstanceState.getInt("labelID");
        } else {
            imageViewID = getResources().getIdentifier("image" + numPictures + "View" + 1, "id", getPackageName());
            labelID = getResources().getIdentifier("lb" + numPictures + "Cpt" + 1, "id", getPackageName());
        }
        myDialog = new Dialog(this);
        recorder = new SoundRecording(this);
        pictureSettings = new Pictures(this, this);
    }

    private void configureButtons() {
        findViewById(R.id.btNum4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { setNumberOfPictures(4);
            }
        });
        findViewById(R.id.btNum8).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { setNumberOfPictures(8);
            }
        });
        findViewById(R.id.btNum15).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { setNumberOfPictures(15);
            }
        });
        findViewById(R.id.btNum24).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { setNumberOfPictures(24);
            }
        });
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { goToMain();
            }
        });
        findViewById(R.id.btUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoToMain();
            }
        });
    }

    private void setupImageViews(){
        setNumberOfPictures(numPictures);
        setupImageViewsHelper(24);
        setupImageViewsHelper(15);
        setupImageViewsHelper(8);
        setupImageViewsHelper(4);
    }

    private void setupImageViewsHelper(int numPics) {
        for (int i = 1; i <= numPics; i++) {
            int id = getResources().getIdentifier("image" + numPics + "View" + i, "id", getPackageName());
            final int finalI = i;
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ShowCameraPopup(finalI);
                }
            });
        }
    }

    private void ShowCameraPopup(int id) {
        myDialog.setContentView(R.layout.popup_choose_picture);
        imageViewID = getResources().getIdentifier("image" + numPictures + "View" + id, "id", getPackageName());
        labelID = getResources().getIdentifier("lb" + numPictures + "Cpt" + id, "id", getPackageName());
        myDialog.findViewById(R.id.btCamera).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                myDialog.dismiss();
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
        myDialog.findViewById(R.id.btGallery).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void ShowSoundPopup() {
        myDialog.setContentView(R.layout.popup_choose_sound);
        myDialog.findViewById(R.id.btMicrophone).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (checkPermissions(RECORD_AUDIO) != 1) {
                    configureSoundButtons();
                }
            }
        });
        myDialog.findViewById(R.id.btGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void configureSoundButtons() {
        SoundRecording.RecordButton mRecordButton = recorder.getRecordButton(myDialog.getContext());
        LinearLayout ll = myDialog.findViewById(R.id.layout);
        ll.addView(mRecordButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        SoundRecording.PlayButton mPlayButton = recorder.getPlayButton(myDialog.getContext());
        ll.addView(mPlayButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() { myDialog.findViewById(R.id.btNext).setVisibility(View.VISIBLE);}
        });
        myDialog.findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                ShowTextPopup();
            }
        });
        myDialog.findViewById(R.id.btMicrophone).setVisibility(View.GONE);
        myDialog.findViewById(R.id.btGallery).setVisibility(View.GONE);
    }

    private void ShowTextPopup() {
        myDialog.setContentView(R.layout.popup_choose_text);
        myDialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.findViewById(R.id.btNext).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText edit = (EditText)myDialog.findViewById(R.id.input);
                setLabelText(edit.getText());
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void setLabelText(Editable text) {
        TextView label = (TextView)findViewById(labelID);
        label.setText(text);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveGoToMain() {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("numPictures", numPictures);
        goToMain();
    }

    private void setNumberOfPictures(int numPics) {
        numPictures = numPics;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.images4).setVisibility(View.INVISIBLE);
            }
        });
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.images8).setVisibility(View.INVISIBLE);
            }
        });
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.images15).setVisibility(View.INVISIBLE);
            }
        });
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.images24).setVisibility(View.INVISIBLE);
            }
        });
        switch (numPics) {
            case 4:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.images4).setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 8:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.images8).setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 15:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.images15).setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 24:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.images24).setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int checkPermissions(int permission) {
        switch (permission) {
            case TAKE_PICTURE:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, TAKE_PICTURE);
                    return 1;
                }
                break;
            case RECORD_AUDIO:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
                    return 1;
                }
                break;
        }
        return 0;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("imageViewID", imageViewID);
        savedInstanceState.putInt("labelID", labelID);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay!
        } else {
            goToMain();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
//                    Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
//                    ImageView imageView = (ImageView) findViewById(imageViewID);
//                    imageView.setImageBitmap(imageBitmap);
//                    pictureSettings.sendBroadcast();
//                    pictureSettings.doCrop();
                    Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (choosePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(choosePictureIntent, CHOOSE_PICTURE);
                    }
                }
                break;
            case CHOOSE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    pictureSettings.setImageURI(imageUri);
                    pictureSettings.doCrop();
                }
                break;
            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    ImageView imageView = (ImageView) findViewById(imageViewID);
                    imageView.setImageBitmap(photo);
                    ShowSoundPopup();
                }
                break;
        }
    }
}
