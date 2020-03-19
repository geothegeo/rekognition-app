package edu.gmu.cs321.rekognition;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceHolder holder;
 /**
     * Constructor
     *
     * @param context
     * @param camera
     */
 public ShowCamera(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.holder = getHolder();
        holder.addCallback(this);
    }

/**
     * surfaceChanged method from SurfaceHolder.Callback interface
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * releases camera on surface change
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        //camera.release();
    }

    /**
     * Set camera parameters
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.Parameters param = camera.getParameters();

        List<Camera.Size> sizes = param.getSupportedPictureSizes();
        Camera.Size size = null;

        for(Camera.Size size1 : sizes){
                size = size1;
        }
        if(this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE){
            param.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            param.setRotation(90);
        }
        else{
            param.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            param.setRotation(0);
        }
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        param.setPictureSize(size.width, size.height);
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }
}
