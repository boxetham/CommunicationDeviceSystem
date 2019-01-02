package com.example.boxetham.communicationdevicecontroller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.view.Surface;

import java.util.*;

import java.util.List;

public class FromCamera implements PictureInput {

    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private CameraCaptureSession mSession;
    private CaptureRequest.Builder mBuilder;

    @Override
    public String getPicture() {
        //CameraDevice cam = getCameraInstance();
        return null;
    }

    /** A safe way to get an instance of the Camera object. */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getCameraInstance(Context context, Activity activity) {
        try {
            mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics("0");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA},
                        0);
                return 1;
            }
            mCameraManager.openCamera("0", new MyCameraDeviceStateCallback(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class MyCameraDeviceStateCallback extends CameraDevice.StateCallback {

        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            //get builder
            try {
                mBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_MANUAL);
                //flash on, default is on
                mBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
                mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                List<Surface> list = new ArrayList<Surface>();
                SurfaceTexture mSurfaceTexture = new SurfaceTexture(1);
                Size size = getSmallestSize(mCameraDevice.getId());
                mSurfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
                Surface mSurface = new Surface(mSurfaceTexture);
                list.add(mSurface);
                mBuilder.addTarget(mSurface);
                camera.createCaptureSession(list, new MyCameraCaptureSessionStateCallback(), null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class MyCameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigured(CameraCaptureSession session) {
            mSession = session;
            try {
                mSession.setRepeatingRequest(mBuilder.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Size getSmallestSize(String cameraId) throws CameraAccessException {
        Size[] outputSizes = mCameraManager.getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                .getOutputSizes(SurfaceTexture.class);
        if (outputSizes == null || outputSizes.length == 0) {
            throw new IllegalStateException(
                    "Camera " + cameraId + "doesn't support any outputSize.");
        }
        Size chosen = outputSizes[0];
        for (Size s : outputSizes) {
            if (chosen.getWidth() >= s.getWidth() && chosen.getHeight() >= s.getHeight()) {
                chosen = s;
            }
        }
        return chosen;
    }
}
