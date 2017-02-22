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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_found);

        setCarInforms(new CarFoundModel("Audi A5 Рестайлинг 2.0 МТ, 211 л.с 4WD", "1234567891011121", "2.0/211 л.с./ Бензин", 2015, "Купе", "Полный", "Механическая"));



        initToolbar();
        mCameraImageView = (ImageView) findViewById(R.id.ivPhotoCar);

        findViewById(R.id.btnPhotoCar).setOnClickListener(mCaptureImageButtonClickListener);

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
            }
        }

    }


    private void startImageCapture() {
        startActivityForResult(new Intent(CarFoundActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
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