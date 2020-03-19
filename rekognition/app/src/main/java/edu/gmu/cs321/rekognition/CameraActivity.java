package edu.gmu.cs321.rekognition;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.hardware.Camera;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public class CameraActivity extends AppCompatActivity {

    protected Camera camera;
    protected FrameLayout frameLayout;
    private ShowCamera showCamera;
    private byte[] pictureData;
    private int CAMERA_PERMISSION_CODE = 1;
    private RekognitionClient rekognitionClient; {
        try {
            rekognitionClient = RekognitionClient.getInstance();
        } catch (IOException e) {
            if (e instanceof ProtocolException) {
                // TODO: print to screen "shit be fucked"
            } else if (e instanceof MalformedURLException) {
                // TODO: print to screen "more shit be fucked"
            } else {
                // TODO: print to screen "extreme amounts of shit be fucked"
            }
        }
    }


    /**
     * Creates activity. Requests camera permission and initalizes camera with permission given
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            initUI();
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {


        /**
         * Writes picture file to byte array
         *
         * @param data
         * @param camera
         */
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            rekognitionClient.processImg(data);
        }
    };


    /**
     * Captures image from current view
     *
     * @param v
     */
    public void captureImage(View v) {
        if (camera != null) {
            camera.takePicture(null, null, mPictureCallback);
            try {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            moveToProduct();
        }
    }

    /**
     * Intializes Camera UI. Creates a ShowCamera instance and sets Button
     * to captureImage
     */
    private void initUI() {
        setContentView(R.layout.activity_camera);
        frameLayout = (FrameLayout) findViewById(R.id.camera_layout);
        camera = Camera.open();

        showCamera = new ShowCamera(this, camera);

        frameLayout.addView(showCamera);

        Button button = (Button) findViewById(R.id.capture);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                captureImage(v);
            }
        });
    }

    /**
     * Requests camera permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission GRANTED", Toast.LENGTH_SHORT).show();
                initUI();
            } else {
                Toast.makeText(this, "Camera Permission DENIED", Toast.LENGTH_SHORT).show();
                moveToMain();
            }
        }
    }

    /**
     * Moves to Main Activity if permission is not given
     */
    public void moveToMain() {
        Intent intent = new Intent(CameraActivity.this, MainActivity.class);
        startActivity(intent);
    }
    private void moveToProduct() {
        Intent intent = new Intent(CameraActivity.this, ProductActivity.class);
        startActivity(intent);
    }
}
