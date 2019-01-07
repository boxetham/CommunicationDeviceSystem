package com.example.boxetham.communicationdevicecontroller;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ChangeDisplay extends AppCompatActivity {

    Dialog myDialog;
    private static final int RECORD_AUDIO = 3;
    private static final int CHOOSE_PICTURE = 2;
    private static final int TAKE_PICTURE = 1;
    private static int numPictures = 4;
    private int imageViewID = 0;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private RecordButton mRecordButton = null;
    private PlayButton   mPlayButton = null;
    private static String mFileName = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            imageViewID = savedInstanceState.getInt("imageViewID");
        }else{
            imageViewID = getResources().getIdentifier("image" + numPictures + "View" + 1, "id", getPackageName());
        }
        setContentView(R.layout.change_display);
        goToDisplay(numPictures);
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
        for (int i = 1; i <= numPictures; i++) {
            int id = getResources().getIdentifier("image" + numPictures + "View" + i, "id", getPackageName());
            final int finalI = i;
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ShowCameraPopup(finalI);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("imageViewID", imageViewID);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void ShowCameraPopup(int id) {
        myDialog.setContentView(R.layout.change_display_change_tile);
        imageViewID = getResources().getIdentifier("image" + numPictures + "View" + id, "id", getPackageName());
        myDialog.findViewById(R.id.btCamera).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                if(checkPermissions(TAKE_PICTURE) != 1) {
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
                ShowSoundPopup();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void ShowSoundPopup() {
        myDialog.setContentView(R.layout.change_display_change_tile_2);
        myDialog.findViewById(R.id.btMicrophone).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(checkPermissions(RECORD_AUDIO) != 1) {
                    mFileName = getExternalCacheDir().getAbsolutePath();
                    mFileName += "/audiorecordtest.3gp";
                    mRecordButton = new RecordButton(myDialog.getContext());
                    LinearLayout ll = myDialog.findViewById(R.id.layout);
                    ll.addView(mRecordButton, new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    0));
                    mPlayButton = new PlayButton(myDialog.getContext());
                    ll.addView(mPlayButton, new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    0));
                    myDialog.findViewById(R.id.btMicrophone).setVisibility(View.GONE);
                    myDialog.findViewById(R.id.btGallery).setVisibility(View.GONE);
                    myDialog.findViewById(R.id.btInternet).setVisibility(View.GONE);
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
                ShowTextPopup(v);
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("auido fail", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("audio fail", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
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
        numPictures = numPics;
        this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                findViewById(R.id.images4).setVisibility(View.INVISIBLE);
            } });
        this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                findViewById(R.id.images8).setVisibility(View.INVISIBLE);
            } });
        this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                findViewById(R.id.images15).setVisibility(View.INVISIBLE);
            } });
        this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                findViewById(R.id.images24).setVisibility(View.INVISIBLE);
            } });
        switch (numPics){
            case 4:
                this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        findViewById(R.id.images4).setVisibility(View.VISIBLE);
                    } });
                break;
            case 8:
                this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        findViewById(R.id.images8).setVisibility(View.VISIBLE);
                    } });
                break;
            case 15:
                this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        findViewById(R.id.images15).setVisibility(View.VISIBLE);
                    } });
                break;
            case 24:
                this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        findViewById(R.id.images24).setVisibility(View.VISIBLE);
                    } });
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int checkPermissions(int permission) {
        switch (permission){
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TAKE_PICTURE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    cancelGoToMain();
                }
                return;
            case RECORD_AUDIO:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    cancelGoToMain();
                }
                return;
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
                    ImageView imageView = (ImageView) findViewById(imageViewID);
                    imageView.setImageBitmap(imageBitmap);
                    myDialog.dismiss();
                    ShowSoundPopup();

//                    MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "fun" , "fun in the sun");


//                    String filename = "image.png";
//                    File sd = getPublicAlbumStorageDir("CommicationDevice");
//                    File dest = new File(sd, filename);
//                    try {
//                        FileOutputStream out = new FileOutputStream(dest);
//                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//                        out.flush();
//                        out.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

//                    ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
//                    File file = wrapper.getDir("Images",MODE_PRIVATE);
//                    file = new File(file, "UniqueFileName"+".jpg");
//
//                    try{
//                        OutputStream stream = new FileOutputStream(file);
//                        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
//                        stream.flush();
//                        stream.close();
//
//                    }catch (IOException e) // Catch the exception
//                    {
//                        e.printStackTrace();
//                    }
                }
                break;
            case CHOOSE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri imageUri = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                        ImageView imageView = (ImageView) findViewById(imageViewID);
                        imageView.setImageBitmap(imageBitmap);
                        myDialog.dismiss();
                        ShowSoundPopup();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case RECORD_AUDIO:

        }
    }

    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            //sad
        }
        return file;
    }

    class RecordButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
}
