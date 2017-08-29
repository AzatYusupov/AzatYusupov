package com.usupov.autopark.activity;




import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.productcard.R;

public class CameraActivityOld extends AppCompatActivity implements PictureCallback, SurfaceHolder.Callback {

    public static final String EXTRA_CAMERA_DATA = "camera_data";
    public static int orientation;

    private static final String KEY_IS_CAPTURING = "is_capturing";

    private Camera mCamera;
    private ImageView mCameraImage;
    public SurfaceView mCameraPreview;
    private ImageView mCaptureImageButton;
    private byte[] mCameraData;
    private boolean mIsCapturing;
    private boolean orientationPortatiat;
    private FrameLayout cameraFrame;

    OrientationEventListener myOrientationEventListener;

    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage();
        }
    };

    private OnClickListener mRecaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setupImageCapture();
        }
    };
    private OnClickListener mCancelButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    Bitmap bitmapImage;
    private OnClickListener mDoneButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (mCameraData != null) {
                    String path = SaveImage(bitmapImage);
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_CAMERA_DATA, path);
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
            catch(Exception ex) {
                Toast.makeText(CameraActivityOld.this, ex.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        }
    };

    int angle;
    ImageView saveButton, cancelButton;
    private static int heightCameraPreview, widthCameraPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_old);

        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);

        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCaptureImageButton = (ImageView) findViewById(R.id.capture_image_button);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

        saveButton = (ImageView) findViewById(R.id.save_image_button);
        saveButton.setOnClickListener(mDoneButtonClickListener);
        saveButton.setVisibility(View.INVISIBLE);

        cancelButton = (ImageView) findViewById(R.id.cancel_image_button);
        cancelButton.setOnClickListener(mCancelButtonListener);

        mIsCapturing = true;

        cameraFrame = (FrameLayout)findViewById(R.id.cameraFrame);

        myOrientationEventListener
                = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL){
            @Override
            public void onOrientationChanged(int arg0) {
                // TODO Auto-generated method stub
                angle = arg0;
            }};

        if (myOrientationEventListener.canDetectOrientation()){
            myOrientationEventListener.enable();
        }
        else{
            Toast.makeText(this, "Can't DetectOrientation", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            List<String> permissions = new ArrayList<>();
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);

                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 111);
            }
        }
    }
    private String SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/autopark_images");
        if (!myDir.exists())
            myDir.mkdir();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Image-"+ timeStamp +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 111: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);


                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        };
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            try {
                mCamera = Camera.open(0);
                mCamera.setDisplayOrientation(90);
                Camera.Parameters params= mCamera.getParameters();
                mCameraPreview.getLayoutParams().width = params.getPreviewSize().height;
                mCameraPreview.getLayoutParams().height = params.getPreviewSize().width;
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());

                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size pictureSize = null;
                int cnt = 0;
                for (int i = sizes.size()-1; i >= 0; i--) {
                    cnt++;
                    pictureSize = sizes.get(i);
                    if (cnt==4)
                        break;
                }
                params.setPictureSize(pictureSize.width, pictureSize.height);
                mCamera.setParameters(params);

                for (Camera.Size size : sizes) {
                    System.out.println(size.width+" "+size.height);
                }
                // setCameraDisplayOrientation(0);
                if (mIsCapturing) {
                    mCamera.startPreview();
                }

            } catch (Exception e) {
                Toast.makeText(CameraActivityOld.this, e.getMessage()+"", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;


        int diff0 = Math.min(360-angle, angle);
        int diff90 = Math.abs(angle-90);
        int diff180 = Math.abs(angle-180);
        int diff270 = Math.abs(angle-270);

        int minDiff = Math.min(Math.min(diff0, diff90), Math.min(diff180, diff270));

        if (minDiff==diff0)
            orientation = 0;
        else if (minDiff==diff90)
            orientation = 90;
        else if (minDiff==diff180)
            orientation = 180;
        else
            orientation = 270;
        setupImageDisplay();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);

                orientationPortatiat = true;
            }
            else {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "landscape");
                // For Android 2.2 and above
                mCamera.setDisplayOrientation(0);
                // Uncomment for Android 2.0 and above
                parameters.setRotation(0);
                orientationPortatiat = false;
            }

            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(surfaceHolder);

            mCamera.startPreview();

        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if(display.getRotation() == Surface.ROTATION_0) {
            mCamera.setDisplayOrientation(90);
        } else if(display.getRotation() == Surface.ROTATION_270) {
            mCamera.setDisplayOrientation(180);
        }

        mCamera.startPreview();
    }

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }

    private void setupImageCapture() {
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        if (mCamera==null)
            mCamera = Camera.open(0);
        mCamera.startPreview();
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
    }

    private void setupImageDisplay() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
        mCameraImage.setVisibility(View.VISIBLE);

        Matrix matrix = new Matrix();
        int rotate = 0;
        if (orientation==0)
            rotate = 90;
        else if (orientation==90)
            rotate = 180;
        else if (orientation==180)
            rotate = 270;
        matrix.postRotate(rotate);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmapImage = rotatedBitmap;
        mCameraImage.setImageBitmap(bitmapImage);
        mCamera.stopPreview();
      //  mCameraPreview.setVisibility(View.INVISIBLE);

        saveButton.setVisibility(View.VISIBLE);

        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);
    }
}
