package com.example.communicationdevicecontroller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;

import com.fenchtose.nocropper.CropperView;

public class CropActivity extends AppCompatActivity {

    private CropperView cropImageView;
    private static Bitmap image;
    private static PictureManager pictureSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cropImageView = (CropperView) findViewById(R.id.CropImageView);
        findViewById(R.id.cropbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneCrop();
            }
        });
        ((SeekBar)findViewById(R.id.skbarRotate)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rotate(progress*45);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });
        cropImageView.setImageBitmap(image);
    }

    private void rotate(int degrees){
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        cropImageView.setImageBitmap(rotatedBitmap);
    }

    private void doneCrop(){
        Bitmap croppedImage = cropImageView.getCroppedBitmap().getBitmap();
        String file = pictureSettings.writeImage(croppedImage, 0, 0);
        Intent intent = new Intent();
        intent.putExtra("imagefile", file);
        setResult(RESULT_OK, intent);
        finish();
        return;
    }

    public static void setBitmap(Bitmap bitmap){
        image = bitmap;
    }

    public static void setPictureSettings(PictureManager pictureManager) { pictureSettings = pictureManager; }
}