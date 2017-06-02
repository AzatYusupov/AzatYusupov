package com.usupov.autopark.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.usupov.autopark.R;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarFoundModel;
import com.usupov.autopark.model.CarModel;

import java.util.HashMap;

public class EditCarInfo extends AppCompatActivity {

    public static final int TAKE_PICTURE_REQUEST_B = 100;

    private ImageView mCameraImageView;
    private Bitmap mCameraBitmap;
    private static String imagePath;

    private CarModel car;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_found);

        initToolbar();
        Gson g = new Gson();
        car = g.fromJson(getIntent().getExtras().getString("car"), CarModel.class);
        car.setFullName();
        setCarInforms(new CarFoundModel(car.getFullName(), car.getVin(), "", car.getYearName(), "", "", ""));
        initbtnSave();
        mCameraImageView = (ImageView) findViewById(R.id.ivPhotoCar);
        Log.d("mylogs", "" + car.getImageUrl());
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.photoCarField);
        layout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.setVisibility(View.VISIBLE);
        mCameraImageView.setVisibility(View.VISIBLE);
        Glide.with(EditCarInfo.this).load(car.getImageUrl()).into(mCameraImageView);
        findViewById(R.id.closeCarPhoto).setVisibility(View.VISIBLE);
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_found);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("Izmenit");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnPhotoCar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(EditCarInfo.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
            }
        });
    }
    public void onClickClose(View v) {

        Toast toast = Toast.makeText(getApplicationContext(),
                "Удалено!", Toast.LENGTH_SHORT);
        toast.show();

        ImageView closeCarPhoto = (ImageView) findViewById(R.id.closeCarPhoto);
        closeCarPhoto.setVisibility(View.GONE);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.photoCarField);
        // layout.getLayoutParams().height = 0;
        layout.setVisibility(View.GONE);

        //ivPhotoCar.getLayoutParams().height = 0;
        mCameraImageView.setVisibility(View.GONE);
        imagePath = null;
    }
    private static TextView carNameView, vinNumberView ;

    private void setCarInforms(CarFoundModel car) {

        carNameView = (TextView) findViewById(R.id.car_name);
        carNameView.setText(car.getCarName());

        vinNumberView = (TextView) findViewById(R.id.vin_number);
        vinNumberView.setText(car.getVinNumber());
        if (car.getVinNumber() == null || car.getVinNumber().length() == 0)
            findViewById(R.id.vin_text).setVisibility(View.GONE);
/*
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
*/
    }
    private void initbtnSave() {
        Button btnSave = (Button) findViewById(R.id.btn_save_car);
        btnSave.setVisibility(View.GONE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = Config.getUrlCarCreat();
                HttpHandler handler = new HttpHandler();
                HashMap<String, String> pairs = new HashMap<>();
                if (car.getVin()!= null && car.getVin().length() > 0)
                    pairs.put("vin", car.getVin());
                pairs.put("brandId", car.getBrandId()+"");
                pairs.put("modelId", car.getModelId()+"");
                pairs.put("yearId", car.getYearId()+"");
                try {
                    boolean result = handler.postWithOneFile(url, pairs, imagePath);
                    imagePath = null;
                    if (result) {
                        Toast.makeText(EditCarInfo.this, getString(R.string.car_success_added), Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else
                        Toast.makeText(EditCarInfo.this, getString(R.string.car_not_added), Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

                imagePath = extras.getString(CameraActivity.EXTRA_CAMERA_DATA);
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
}
