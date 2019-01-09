package com.example.boxetham.communicationdevicecontroller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ChangeDisplay extends AppCompatActivity {

    Dialog myDialog;
    private static final int CROP_FROM_CAMERA = 4;
    private static final int RECORD_AUDIO = 3;
    private static final int CHOOSE_PICTURE = 2;
    private static final int TAKE_PICTURE = 1;
    private static int numPictures = 4;
    private int imageViewID = 0;
    private SoundRecording recorder = null;
    private Uri mImageCaptureUri = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_display);
        setNumberOfPictures(numPictures);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        myDialog = new Dialog(this);
        recorder = new SoundRecording(this);
        if (savedInstanceState != null) {
            imageViewID = savedInstanceState.getInt("imageViewID");
        } else {
            imageViewID = getResources().getIdentifier("image" + numPictures + "View" + 1, "id", getPackageName());
        }
        findViewById(R.id.btNum4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setNumberOfPictures(4);
            }
        });
        findViewById(R.id.btNum8).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setNumberOfPictures(8);
            }
        });
        findViewById(R.id.btNum15).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setNumberOfPictures(15);
            }
        });
        findViewById(R.id.btNum24).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setNumberOfPictures(24);
            }
        });
        findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelGoToMain();
            }
        });
        setupImageViews(24);
        setupImageViews(15);
        setupImageViews(8);
        setupImageViews(4);
    }

    public void setupImageViews(int numPics) {
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
                myDialog.dismiss();
                if (checkPermissions(TAKE_PICTURE) != 1) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getPhotoFileUri("image.jpg");
                    URI uri = file.toURI();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
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
                if (checkPermissions(RECORD_AUDIO) != 1) {
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
                    MediaScannerConnection.scanFile(this, new String[]{getPhotoFileUri("image.jpg").getAbsolutePath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) { Log.i("", "Scanned " + path);
                                }
                            });
//                    doCrop();
                }
                break;
            case CHOOSE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri imageUri = data.getData();
                        mImageCaptureUri =  imageUri;
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                        ImageView imageView = (ImageView) findViewById(imageViewID);
                        imageView.setImageBitmap(imageBitmap);
                        doCrop();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) f.delete();
                break;
        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            //sad
            return;
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CommunicationDevice");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("", "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }
}
