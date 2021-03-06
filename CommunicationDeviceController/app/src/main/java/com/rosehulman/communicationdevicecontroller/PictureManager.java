package com.rosehulman.communicationdevicecontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PictureManager {

    private Context context;
    private static final long DESIRED_FILE_SIZE = 10000;

    public PictureManager(Context context){
        this.context = context;
    }

    public void doCrop(Bitmap bitmap) {
        CropActivity.setBitmap(bitmap);
        CropActivity.setPictureSettings(this);
        Intent intent = new Intent(context, CropActivity.class);
        ((Activity)context).startActivityForResult(intent, PictureSelectionActivity.CROP);
    }

    public File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // Save a file: path for use with ACTION_VIEW intents
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPicturesDir() {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    public String writeImage(Bitmap imageBitMap, int numPictures, int i) {
        if(imageBitMap == null){
            return null;
        }
        OutputStream fOut = null;
        File file = new File(getPicturesDir(), "Image" + numPictures + "View"+i+".jpg");
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            fOut = new FileOutputStream(file);
            long KB = imageBitMap.getByteCount()/1024;
            int compression = (int)Math.min(100, (DESIRED_FILE_SIZE*100)/KB);
            imageBitMap.compress(Bitmap.CompressFormat.JPEG, compression, fOut);
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public Bitmap getImageBitmap(String filename){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(filename, options);
    }

    public ArrayList<Byte> readImageFile(String pictureFile) {
        if(pictureFile == null){
            return new ArrayList<>();
        }
        ArrayList<Byte> picture = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(pictureFile);
            byte[] bytedata = new byte[1024];
            int    bytesRead = inputStream.read(bytedata);
            while(bytesRead != -1) {
                for(int i = 0; i < bytesRead; i++){
                    picture.add(bytedata[i]);
                }
                bytesRead = inputStream.read(bytedata);
            }
        }catch(Exception e){ }
        return picture;
    }

    public void deleteFile(String filepath) {
        File file = new File(filepath);
        file.delete();
        if(file.exists()){
            if(file.exists()){
                context.deleteFile(file.getName());
            }
        }
    }
}
