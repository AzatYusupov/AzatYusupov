package com.usupov.autopark.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.model.CarFoundModel;


public class CarFoundActivity extends AppCompatActivity {

    public static final int TAKE_PICTURE_REQUEST_B = 100;

    private ImageView mCameraImageView;
    private Bitmap mCameraBitmap;
    private Button mSaveImageButton;


    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startImageCapture();
        }
    };

    public void onClickClose(View v) {

        Toast toast = Toast.makeText(getApplicationContext(),
                "Удалено!", Toast.LENGTH_SHORT);
        toast.show();

        ImageView closeCarPhoto = (ImageView) findViewById(R.id.closeCarPhoto);
        closeCarPhoto.setVisibility(View.INVISIBLE);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.photoCarField);
        // layout.getLayoutParams().height = 0;
        layout.setVisibility(View.INVISIBLE);

        //ivPhotoCar.getLayoutParams().height = 0;
        mCameraImageView.setVisibility(View.INVISIBLE);

    }
    private OnClickListener mSaveImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            File saveFile = openFileForImage();
            if (saveFile != null) {
                saveImageToFile(saveFile);
            } else {
                Toast.makeText(CarFoundActivity.this, "Unable to open file for saving image.",
                        Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_found);

        setCarInforms(new CarFoundModel("Audi A5 Рестайлинг 2.0 МТ, 211 л.с 4WD", "1234567891011121", "2.0/211 л.с./ Бензин", 2015, "Купе", "Полный", "Механическая"));



        initToolbar();
        mCameraImageView = (ImageView) findViewById(R.id.ivPhotoCar);


//        Intent intent = getIntent();
//        if (intent.getByteArrayExtra(CameraActivity.EXTRA_CAMERA_DATA) != null) {
//            Toast.makeText(CarFoundActivity.this, "Ok", Toast.LENGTH_LONG)
//                    .show();
//            byte[] mCameraData = intent.getByteArrayExtra(CameraActivity.EXTRA_CAMERA_DATA);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
////            mCameraImageView.setImageBitmap(bitmap);
//        }



        findViewById(R.id.btnPhotoCar).setOnClickListener(mCaptureImageButtonClickListener);

//        mSaveImageButton = (Button) findViewById(R.id.save_image_button);
//
//        mSaveImageButton.setOnClickListener(mSaveImageButtonClickListener);
//        mSaveImageButton.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PICTURE_REQUEST_B) {
            if (resultCode == RESULT_OK) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                // Recycle the previous bitmap.
                if (mCameraBitmap != null) {
                    mCameraBitmap.recycle();
                    mCameraBitmap = null;
                }
                Bundle extras = data.getExtras();

                String imagePath = extras.getString(CameraActivity.EXTRA_CAMERA_DATA);
                if (imagePath != null && !imagePath.equals("")) {
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.photoCarField);
                    layout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;

                    layout.setVisibility(View.VISIBLE);

                    // Get the dimensions of the bitmap
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imagePath, bmOptions);

                    int targetW = 400;//mCameraImageView.getWidth();
                    int targetH = 1;

                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    if (photoH > photoW) {
                        targetW = 1;//mCameraImageView.getWidth();
                        targetH = 400;
                    }
                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
                    mCameraImageView.setImageBitmap(bitmap);

                    ImageView closeCarPhoto = (ImageView) findViewById(R.id.closeCarPhoto);
                    closeCarPhoto.setVisibility(View.VISIBLE);


                    mCameraImageView.setVisibility(View.VISIBLE);
                }
            } else {
                mCameraBitmap = null;
                mSaveImageButton.setEnabled(false);
            }
        }

    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void startImageCapture() {
        startActivityForResult(new Intent(CarFoundActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
    }

    private File openFileForImage() {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "com.o0reillyschool.android2.camera");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + "image_" +
                        dateFormat.format(new Date()) + ".png");
            }
        }
        return null;
    }

    private void saveImageToFile(File file) {
        if (mCameraBitmap != null) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                if (!mCameraBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                    Toast.makeText(CarFoundActivity.this, "Unable to save image to file.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CarFoundActivity.this, "Saved image to: " + file.getPath(),
                            Toast.LENGTH_LONG).show();
                }
                outStream.close();
            } catch (Exception e) {
                Toast.makeText(CarFoundActivity.this, "Unable to save image to file.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    private void setCarInforms(CarFoundModel car) {

        TextView carName = (TextView) findViewById(R.id.car_name);
        carName.setText(car.getCarName());

        TextView vinNumber = (TextView) findViewById(R.id.vin_number);
        vinNumber.setText(car.getVinNumber());

        TextView engine = (TextView) findViewById(R.id.engine);
        engine.setText(car.getEngine());

        TextView issueYear = (TextView) findViewById(R.id.issue_year);
        issueYear.setText(car.getIssue_year()+"");

        TextView carcase = (TextView) findViewById(R.id.carcase);
        carcase.setText(car.getCarcase());

        TextView driveUnit = (TextView) findViewById(R.id.drive_unit);
        driveUnit.setText(car.getDrive_unit());

        TextView KPP = (TextView) findViewById(R.id.kpp);
        KPP.setText(car.getKpp());

    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_found);
        setSupportActionBar(toolbar);
    }
}