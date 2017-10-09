package com.usupov.autopark.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.Gson;
import product.card.R;
import com.usupov.autopark.config.CarRestURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.service.ImageProcessService;
import com.usupov.autopark.service.MultipartRequest;
import com.usupov.autopark.squarecamera.CameraActivity;

import org.apache.http.HttpStatus;


public class CarFoundActivity extends AppCompatActivity {

    public static final int TAKE_PICTURE_REQUEST_B = 100;

    private ImageView mCameraImageView;
    private Bitmap mCameraBitmap;
    private static String imagePath;
    private int CAR_IMAGE_SIZE = 800;
    private RelativeLayout layoutCarImage;
    ProgressDialog progressDialogSend;


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
        layoutCarImage.setVisibility(View.GONE);
        imagePath = null;
    }
    private CarModel car;
    boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_found);
        Gson g = new Gson();
        progressDialogSend = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialogSend.setTitle(getString(R.string.please_wait));

        car = g.fromJson(getIntent().getExtras().getString("car"), CarModel.class);
        car.setFullName();
        setCarInforms();
        if (getIntent().hasExtra("update"))
            update = true;
        initToolbar(update);

        layoutCarImage = (RelativeLayout) findViewById(R.id.photoCarField);
        mCameraImageView = (ImageView) findViewById(R.id.ivPhotoCar);
        CAR_IMAGE_SIZE = Resources.getSystem().getDisplayMetrics().widthPixels  / 2;
        mCameraImageView.getLayoutParams().width = CAR_IMAGE_SIZE;
        if (update && car.getImageUrl() != null && car.getImageUrl().length() != 0)
            initImageCar();

        initSubmitButton();
        FloatingActionButton fabPhoto = (FloatingActionButton) findViewById(R.id.btnPhotoCar);
//        fabPhoto.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryLight));
        fabPhoto.setOnClickListener(mCaptureImageButtonClickListener);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }
    ActionProcessButton apbSaveCar;
    private void initSubmitButton() {
        apbSaveCar = (ActionProcessButton) findViewById(R.id.apbSaveCar);
        apbSaveCar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCarVolleyTask();
            }
        });
    }

    private void initImageCar() {
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                finish();
            }
        });
        progressDialog.show();

        Glide.with(this).load(Headers.getUrlWithHeaders(car.getImageUrl(), getApplicationContext())).
                skipMemoryCache(true).diskCacheStrategy( DiskCacheStrategy.NONE )
                .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        layoutCarImage.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        return false;
                    }
                })
                .into(mCameraImageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PICTURE_REQUEST_B) {
            if (resultCode == RESULT_OK) {

//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                if (mCameraBitmap != null) {
                    mCameraBitmap.recycle();
                    mCameraBitmap = null;
                }

                ArrayList<String> imageList = data.getStringArrayListExtra(CameraActivity.KEY_IMAGES);
                if (imageList != null && imageList.size() > 0) {
                    imagePath  = imageList.get(0);

                    layoutCarImage.setVisibility(View.VISIBLE);

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imagePath)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bitmap = ImageProcessService.getResizedBitmap(bitmap, CAR_IMAGE_SIZE, CAR_IMAGE_SIZE);
                    mCameraImageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void startImageCapture() {
        startActivityForResult(new Intent(CarFoundActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
    }

    private void setCarInforms() {
        ((TextView)findViewById(R.id.car_name)).setText(car.getFullName());
        ((TextView)findViewById(R.id.brandNameDesc)).setText(car.getBrandName());
        ((TextView)findViewById(R.id.modelNameDesc)).setText(car.getModelName());
        ((TextView)findViewById(R.id.yearNameDesc)).setText(car.getYearName());

        if (car.getVin() != null && car.getVin().length() > 0) {
            findViewById(R.id.vinLayout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.vinDesc)).setText(car.getVin());
        }
    }

    private void initToolbar(boolean update) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_found);
        if (update)
            toolbar.setTitle(getString(R.string.title_car_update));
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

    int resultCode;
    class TaskSendCar extends AsyncTask<Void, Void, Void> {

        HashMap<String, String> pairValues;
        String url;
        public TaskSendCar(String url, HashMap<String, String> pairValues) {
            this.url = url;
            this.pairValues = pairValues;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            apbSaveCar.setProgress(1);
            progressDialogSend.show();
            apbSaveCar.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler handler = new HttpHandler();
            resultCode = handler.postWithOneFile(url, pairValues, imagePath, getApplicationContext(), false).getStatusCode();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (resultCode==HttpStatus.SC_OK) {
                if (!update)
                    Toast.makeText(CarFoundActivity.this, getString(R.string.car_success_added),Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(CarFoundActivity.this, getString(R.string.car_success_edited),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CarFoundActivity.this, CarListActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent, bundle);
                finishAffinity();
            }
            else {
                Toast.makeText(CarFoundActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            }
//            apbSaveCar.setProgress(0);
            progressDialogSend.dismiss();
            apbSaveCar.setEnabled(true);
        }
    }

    private void sendCarVolleyTask() {
        String url = CarRestURIConstants.CREATE;
        if (update)
            url = String.format(CarRestURIConstants.UPDATE, car.getId());

        HashMap<String, String> params = new HashMap<>();
        if (!update) {
            if (car.getVin() != null && car.getVin().length() > 0)
                params.put("vin", car.getVin());
            params.put("brandId", car.getBrandId() + "");
            params.put("modelId", car.getModelId() + "");
            params.put("yearId", car.getYearId() + "");
        }
        TaskSendCar taskSendCar = new TaskSendCar(url, params);
        taskSendCar.execute();
//        apbSaveCar.setProgress(1);
//
//        MultipartRequest multipartRequest = new MultipartRequest(getApplicationContext(), url, params,
//                new File(imagePath), "name", "file",
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(CarFoundActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
//                    }
//                },
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(CarFoundActivity.this, getString(R.string.car_success_edited), Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(CarFoundActivity.this, CarListActivity.class);
//                        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
//                                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
//                        apbSaveCar.setProgress(0);
//                        startActivity(intent, bundle);
//                        finishAffinity();
//                    }
//                }
//        ) {
//
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(multipartRequest);


//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(CarFoundActivity.this, getString(R.string.car_success_edited), Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(CarFoundActivity.this, CarListActivity.class);
//                        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
//                                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
//                        apbSaveCar.setProgress(0);
//                        startActivity(intent, bundle);
//                        finishAffinity();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED /*|| error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR*/)) {
//                            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
//                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
//                            Intent intent = new Intent(CarFoundActivity.this, LoginActivity.class);
//                            intent.putExtra("unauthorized", true);
//                            startActivity(intent, bundle);
//                            finishAffinity();
//                        }
//                        else
//                            Toast.makeText(CarFoundActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return Headers.headerMap(getApplicationContext());
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//
//                if (!update) {
//                    if (car.getVin() != null && car.getVin().length() > 0)
//                        map.put("vin", car.getVin());
//                    map.put("brandId", car.getBrandId() + "");
//                    map.put("modelId", car.getModelId() + "");
//                    map.put("yearId", car.getYearId() + "");
//                }
//                if (imagePath != null) {
//                    System.out.println("SSSSSSSSSSSSSSSSEEEE");
//                    map.put("file", ImageProcessService.getSendingStringImage(imagePath, getBaseContext()));
//                }
//                return map;
//            }
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                return super.getBody();
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(stringRequest);
    }
}