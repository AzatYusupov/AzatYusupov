package com.usupov.autopark.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.usupov.autopark.R;
import com.usupov.autopark.config.CarRestURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.service.ImageProcessService;
import com.usupov.autopark.squarecamera.CameraActivity;

import org.apache.http.HttpStatus;


public class CarFoundActivity extends AppCompatActivity {

    public static final int TAKE_PICTURE_REQUEST_B = 100;

    private ImageView mCameraImageView;
    private Bitmap mCameraBitmap;
    private static String imagePath;
    private final int CAR_IMAGE_SIZE = 800;

    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final AlertDialog.Builder dialogSelectTypeImage = new AlertDialog.Builder(CarFoundActivity.this);
            dialogSelectTypeImage.setTitle("Фотография");

            dialogSelectTypeImage.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            String[] variants = {"Сделать снимок", "Загрузить из галереи"};
            dialogSelectTypeImage.setSingleChoiceItems(variants, 4, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0)
                        startImageCapture();
                    else
                        startLoadFromGallery();
                    dialog.dismiss();
                }
            });
            dialogSelectTypeImage.show();
        }
    };

    private void startLoadFromGallery() {
        startActivityForResult(new Intent(CarFoundActivity.this, SelectImageActivity.class), TAKE_PICTURE_REQUEST_B);
    }
    public void onClickClose(View v) {

        Toast toast = Toast.makeText(getApplicationContext(),
                "Удалено!", Toast.LENGTH_SHORT);
        toast.show();

        ImageView closeCarPhoto = (ImageView) findViewById(R.id.closeCarPhoto);
        closeCarPhoto.setVisibility(View.GONE);

        FrameLayout layout = (FrameLayout) findViewById(R.id.photoCarField);
        // layout.getLayoutParams().height = 0;
        layout.setVisibility(View.GONE);

        //ivPhotoCar.getLayoutParams().height = 0;
        mCameraImageView.setVisibility(View.GONE);
        imagePath = null;
    }
    private CarModel car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_found);
        Gson g = new Gson();
        car = g.fromJson(getIntent().getExtras().getString("car"), CarModel.class);
        car.setFullName();
        setCarInforms();
        initToolbar();

        mCameraImageView = (ImageView) findViewById(R.id.ivPhotoCar);
        findViewById(R.id.btnPhotoCar).setOnClickListener(mCaptureImageButtonClickListener);

        initbtnSave();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
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

                ArrayList<String> imageList = data.getStringArrayListExtra(CameraActivity.KEY_IMAGES);
                if (imageList != null && imageList.size() > 0) {
                    imagePath  = imageList.get(0);
                    FrameLayout layout = (FrameLayout) findViewById(R.id.photoCarField);
                    layout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;

                    layout.setVisibility(View.VISIBLE);

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imagePath)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("454545454545545544");
                    System.out.println(bitmap.getWidth()+" "+bitmap.getHeight());
                    System.out.println(ImageProcessService.dpToPx(bitmap.getWidth(), this)+" "+ImageProcessService.dpToPx(bitmap.getHeight(), this));

                    bitmap = ImageProcessService.getResizedBitmap(bitmap, CAR_IMAGE_SIZE, CAR_IMAGE_SIZE);
                    System.out.println(bitmap.getWidth()+" "+bitmap.getHeight());
                    mCameraImageView.setImageBitmap(bitmap);


                    ImageView closeCarPhoto = (ImageView) findViewById(R.id.closeCarPhoto);
                    closeCarPhoto.setVisibility(View.VISIBLE);

                    mCameraImageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void startImageCapture() {
        startActivityForResult(new Intent(CarFoundActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
    }

    private void setCarInforms() {

        ((TextView)findViewById(R.id.brandNameDesc)).setText(car.getBrandName());
        ((TextView)findViewById(R.id.modelNameDesc)).setText(car.getModelName());
        ((TextView)findViewById(R.id.yearNameDesc)).setText(car.getYearName());

        if (car.getVin() != null && car.getVin().length() > 0) {
            findViewById(R.id.vinLayout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.vinDesc)).setText(car.getVin());
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_found);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private void initbtnSave() {
        Button btnSave = (Button) findViewById(R.id.btn_save_car);
        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = CarRestURIConstants.CREATE;
                HttpHandler handler = new HttpHandler();
                HashMap<String, String> pairs = new HashMap<>();
                if (car.getVin()!= null && car.getVin().length() > 0)
                    pairs.put("vin", car.getVin());
                pairs.put("brandId", car.getBrandId()+"");
                pairs.put("modelId", car.getModelId()+"");
                pairs.put("yearId", car.getYearId()+"");

                try {
                    int resultCode = handler.postWithOneFile(url, pairs, imagePath, getApplicationContext(), false).getStatusCode();
                    imagePath = null;
                    if (resultCode== HttpStatus.SC_OK) {
                        Toast.makeText(CarFoundActivity.this, getString(R.string.car_success_added), Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else
                        Toast.makeText(CarFoundActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}