package com.example.communicationdevicecontroller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ChangeDisplayActivity extends AppCompatActivity {

    private static Map<Integer, String[]> permissionMap = new HashMap<Integer, String[]>(){{
        put(WRITE_EXTERNAL, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }};
    private static Map<Integer, Integer> numPicturesEncoding = new HashMap<Integer, Integer>(){{
        put(4,0);put(8,1);put(15,2);put(24,3);
    }};

    public static final int WRITE_EXTERNAL = 5;
    private static boolean testDisplay;
    public static Display currentDisplay;
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

    private void getIds(int numPics) {
        for(int i = 1; i <= numPics; i++){
            imageIds[numPicturesEncoding.get(numPics)][i-1] = getResources().getIdentifier("image" + numPics + "View" + i, "id", getPackageName());
            labelIds[numPicturesEncoding.get(numPics)][i-1] = getResources().getIdentifier("lb" + numPics + "Cpt" + i, "id", getPackageName());
        }
    }

    private void setValues(Bundle savedInstanceState) {
        imageIds = new int[4][24];
        labelIds = new int[4][24];
        currentDisplay = Display.getInstance(this);
        if (savedInstanceState != null) {
            for(Integer numPics : numPicturesEncoding.keySet()){
                for(int i = 0; i < numPics; i++){
                    imageIds[numPicturesEncoding.get(numPics)][i] = savedInstanceState.getInt("image" + numPics + "Id" + i);
                    labelIds[numPicturesEncoding.get(numPics)][i] = savedInstanceState.getInt("label" + numPics + "id" + i);
                }
            }
        } else {
            getIds();
        }
        updateDisplay();
    }

    public void updateDisplay(){
        int numTiles = currentDisplay.getNumTiles();
        for(int i = 0; i < numTiles; i++){
            int imageId = imageIds[numPicturesEncoding.get(numTiles)][i];
            ((ImageView) findViewById(imageId)).setImageBitmap(currentDisplay.getImage(i));
            int labelId = labelIds[numPicturesEncoding.get(numTiles)][i];
            ((TextView)findViewById(labelId)).setText(currentDisplay.getLabel(i));
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
            public void onClick(View v) {
                currentDisplay.discardChanges();
                goToMain();
            }
        });
        findViewById(R.id.btUpload).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                saveGoToMain();
            }
        });
        findViewById(R.id.btToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeMode(); }
        });
    }

    private void changeMode() {
        testDisplay = !testDisplay;
        if(testDisplay){
            ((Button)findViewById(R.id.btToggle)).setText("Change Display");
            ((TextView)findViewById(R.id.lbInstructions)).setText("Select tile to hear sound");

        }else{
            ((Button)findViewById(R.id.btToggle)).setText("Preview");
            ((TextView)findViewById(R.id.lbInstructions)).setText("Select tile to update");
        }
    }

    private void setupImageViews(){
        setNumberOfPictures(currentDisplay.getNumTiles());
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
                    if(testDisplay){
                        currentDisplay.playSound(finalI-1);
                    }else{
                        currentDisplay.setCurrentTile(finalI-1);
                        getImage();
                    }
                }
            });
        }
    }

    private void getImage() {
        Intent intent = new Intent(this, PictureSelectionActivity.class);
        startActivity(intent);
    }

    private void setNumberOfPictures(int numPics) {
        currentDisplay.copyData(numPics);
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
        updateDisplay();
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
        for(int x : numPicturesEncoding.keySet()){
            for(int i = 0; i < x; i++){
                savedInstanceState.putInt("image" + x + "Id" + i, imageIds[numPicturesEncoding.get(x)][i]);
                savedInstanceState.putInt("label" + x + "id" + i, labelIds[numPicturesEncoding.get(x)][i]);
            }
        }
        super.onSaveInstanceState(savedInstanceState);
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
        int numPictures = currentDisplay.getNumTiles();
        checkPermissions(WRITE_EXTERNAL);
        currentDisplay.updateSaved();
        BluetoothActivity.sendDisplay(new DisplayPackager(numPictures, currentDisplay.getLabels(),
                                                    currentDisplay.getImageFiles(), currentDisplay.getSounds(),
                                                    this));
    }
}