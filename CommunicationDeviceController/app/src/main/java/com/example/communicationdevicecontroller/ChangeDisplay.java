package com.example.communicationdevicecontroller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class ChangeDisplay extends AppCompatActivity {

    private static Map<Integer, String[]> permissionMap = new HashMap<Integer, String[]>(){{
        put(TAKE_PICTURE, new String[]{Manifest.permission.CAMERA});
        put(RECORD_AUDIO, new String[]{Manifest.permission.RECORD_AUDIO});
        put(WRITE_EXTERNAL, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }};
    private static Map<Integer, Integer> numPicturesEncoding = new HashMap<Integer, Integer>(){{
        put(4,0);put(8,1);put(15,2);put(24,3);
    }};

    Dialog myDialog;
    public static final int WRITE_EXTERNAL = 5;
    public static final int CROP_FROM_CAMERA = 4;
    private static final int RECORD_AUDIO = 3;
    private static final int CHOOSE_PICTURE = 2;
    private static final int TAKE_PICTURE = 1;
    private static int numPictures;
    private Bitmap imageViewBitmap;
    private SoundRecording recorder;
    private Pictures pictureSettings;
    private String[] soundBites;
    private int currentTile = 1;
    private int[][] imageIds;
    private int[][] labelIds;

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

    private void getIds(){
        getIds(4);
        getIds(8);
        getIds(15);
        getIds(24);
    }

    private void getIds(int size) {
        for(int i = 1; i <= size; i++){
            imageIds[numPicturesEncoding.get(size)][i-1] = getResources().getIdentifier("image" + size + "View" + i, "id", getPackageName());
            labelIds[numPicturesEncoding.get(size)][i-1] = getResources().getIdentifier("lb" + numPictures + "Cpt" + i, "id", getPackageName());
        }
    }

    private void setValues(Bundle savedInstanceState) {
        imageIds = new int[4][24];
        labelIds = new int[4][24];
        myDialog = new Dialog(this);
        recorder = new SoundRecording(this);
        pictureSettings = new Pictures(this, this);
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        numPictures = prefs.getInt("numPictures", 4);
        getImageViews(prefs);
        if (savedInstanceState != null) {
            currentTile = savedInstanceState.getInt("currentTile");
            for(Integer numPics : numPicturesEncoding.keySet()){
                for(int i = 0; i < numPics; i++){
                    imageIds[numPicturesEncoding.get(numPics)][i] = savedInstanceState.getInt("image" + numPics + "Id" + i);
                    labelIds[numPicturesEncoding.get(numPics)][i] = savedInstanceState.getInt("label" + numPics + "id" + i);
                }
            }
        } else {
            currentTile = 1;
            getIds();
        }
    }

    private void getImageViews(SharedPreferences prefs) {
        for (int i = 1; i <= numPictures; i++) {
            String imageFile = prefs.getString("ImageView" + i, "");
            String label = prefs.getString("Label" + i, "label");
            int imageId = getResources().getIdentifier("image" + numPictures + "View" + i, "id", getPackageName());
            int labelId = getResources().getIdentifier("lb" + numPictures + "Cpt" + i, "id", getPackageName());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile, options);
            ((ImageView)findViewById(imageId)).setImageBitmap(bitmap);
            ((TextView)findViewById(labelId)).setText(label);
        }
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        currentTile = id;
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
                    getFileName();
                }
            }
        });
        myDialog.findViewById(R.id.btGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSoundFiles();
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

    private void listSoundFiles() {
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        final Button selectButton = myDialog.findViewById(R.id.btSelect);
        myDialog.findViewById(R.id.btBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                ShowSoundPopup();
            }
        });
        List<String> files = recorder.getSoundRecordings();
        ScrollView scroll = myDialog.findViewById(R.id.scrollButtons);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFileFirst();
            }
        });
        scroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 370));
        LinearLayout ll = myDialog.findViewById(R.id.recordingsLayout);
        for(String file : files){
            final String filename = file.substring(0, file.lastIndexOf('.'));
            Button fileButton = new Button(myDialog.getContext());
            fileButton.setText(filename);
            fileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recorder.startPlaying(filename);
                    setSelectedSound(filename);
                    selectButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            ShowTextPopup();
                        }
                    });
                }
            });
            ll.addView(fileButton, params);
        }
        changeVisibility(selectButton, View.VISIBLE);
        changeVisibility(myDialog.findViewById(R.id.btBack), View.VISIBLE);
        changeVisibility(myDialog.findViewById(R.id.lbSelectedSound), View.VISIBLE);
        myDialog.findViewById(R.id.btMicrophone).setVisibility(View.GONE);
        myDialog.findViewById(R.id.btGallery).setVisibility(View.GONE);
    }

    private void setSelectedSound(String filename) {
        TextView view = myDialog.findViewById(R.id.lbSelectedSound);
        view.setText("Selected file: " + filename);
    }

    private void selectFileFirst() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a sound file first");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {                    }
        });
        builder.show();
    }

    private void getFileName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name for sound file with no spaces");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recorder.setName(input.getText().toString());
                showRecordingOptions();
            }
        });
        builder.show();
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
                updateDisplay(edit.getText());
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void updateDisplay(Editable text) {
        TextView label = (TextView)findViewById(labelIds[numPicturesEncoding.get(numPictures)][currentTile-1]);
        label.setText(text);
        ImageView imageView = (ImageView) findViewById(imageIds[numPicturesEncoding.get(numPictures)][currentTile-1]);
        imageView.setImageBitmap(imageViewBitmap);
    }

    private void showRecordingOptions() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0);
        SoundRecording.RecordButton mRecordButton = recorder.getRecordButton(myDialog.getContext());
        LinearLayout ll = myDialog.findViewById(R.id.buttonLayout);
        ll.addView(mRecordButton, params);
        SoundRecording.PlayButton mPlayButton = recorder.getPlayButton(myDialog.getContext());
        ll.addView(mPlayButton, params);
        changeVisibility(myDialog.findViewById(R.id.btNext), View.VISIBLE);
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

    private void setNumberOfPictures(int numPics) {
        soundBites = new String[numPics];
        numPictures = numPics;
        changeVisibility(findViewById(R.id.images4), View.INVISIBLE);
        changeVisibility(findViewById(R.id.images8), View.INVISIBLE);
        changeVisibility(findViewById(R.id.images15), View.INVISIBLE);
        changeVisibility(findViewById(R.id.images24), View.INVISIBLE);
        switch (numPics) {
            case 4:
                changeVisibility(findViewById(R.id.images4), View.VISIBLE);
                break;
            case 8:
                changeVisibility(findViewById(R.id.images8), View.VISIBLE);
                break;
            case 15:
                changeVisibility(findViewById(R.id.images15), View.VISIBLE);
                break;
            case 24:
                changeVisibility(findViewById(R.id.images24), View.VISIBLE);
                break;
        }
    }

    private void changeVisibility(final View view, final int visibilty){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(visibilty);
            }
        });
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
            goToMain();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("currentTile", currentTile);
        for(int x : numPicturesEncoding.keySet()){
            for(int i = 0; i < x; i++){
                savedInstanceState.putInt("image" + x + "Id" + i, imageIds[numPicturesEncoding.get(x)][i]);
                savedInstanceState.putInt("label" + x + "id" + i, labelIds[numPicturesEncoding.get(x)][i]);
            }
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
//                    Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
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
                    imageViewBitmap = extras.getParcelable("data");
                    ShowSoundPopup();
                }
                break;
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saveGoToMain() {
        saveDisplayInfo();
        goToMain();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saveDisplayInfo() {
        String[] labels = new String[numPictures];
        Bitmap[] imageBitMaps = new Bitmap[numPictures];
        checkPermissions(WRITE_EXTERNAL);
        SharedPreferences.Editor editor = this.getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt("numPictures", numPictures);
        for (int i = 1; i <= numPictures; i++) {
            int imageId = imageIds[numPicturesEncoding.get(numPictures)][i-1];
            Bitmap imageBitMap = ((BitmapDrawable) ((ImageView) findViewById(imageId)).getDrawable()).getBitmap();
            int labelId = labelIds[numPicturesEncoding.get(numPictures)][i-1];
            String labelText =  String.valueOf(((TextView)findViewById(labelId)).getText());
            String picturefile = pictureSettings.writeImage(imageBitMap, numPictures, i);
            editor.putString("ImageView" + i, picturefile);
            editor.putString("Label" + i, labelText);
            labels[i-1] = labelText;
            imageBitMaps[i-1] = imageBitMap;
        }
        editor.commit();
        Bluetooth.sendDisplay(labels, imageBitMaps);
    }
}