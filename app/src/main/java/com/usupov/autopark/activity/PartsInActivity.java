package com.usupov.autopark.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.usupov.autopark.R;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.PartModel;

import java.util.ArrayList;
import java.util.HashMap;

public class PartsInActivity extends AppCompatActivity {

    public static final int TAKE_PICTURE_REQUEST_B = 200;
    private Bitmap mCameraBitmap;
    LinearLayout linLayoutPhotoParts;
    LayoutInflater inflater;
    private static int carId;
    private static int categoryId;
    private static ArrayList<String> photoList;
    private static PartModel part;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_in);
        Gson g = new Gson();
        part = g.fromJson(getIntent().getExtras().getString("part"), PartModel.class);
        photoList = new ArrayList<>();
        initTollbar();
        TextView tViewPartInname = (TextView) findViewById(R.id.part_in_name);
        tViewPartInname.setText(part.getTitle());

        findViewById(R.id.btn_photo_part_in).setOnClickListener(mCaptureImageButtonClickListener);
        linLayoutPhotoParts = (LinearLayout)findViewById(R.id.layout_parts_in);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        initBtnSavePartIn();

    }
    private void initTollbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_found);
        toolbar.setTitle(getString(R.string.charactersitic) + " " + getIntent().getStringExtra("car_full_name"));
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initBtnSavePartIn() {
        Button btnSave = (Button) findViewById(R.id.save_part_in);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtManufacturer = (EditText)findViewById(R.id.part_in_manufacturer);
                String brand = edtManufacturer.getText().toString();
                if (brand != null)
                    brand = brand.trim();
                if (brand==null || brand.length()==0) {
                    Toast.makeText(PartsInActivity.this, "Введите производител", Toast.LENGTH_LONG).show();
                    return;
                }

                EditText edtStatus = (EditText)findViewById(R.id.part_in_status);
                String status = edtStatus.getText().toString();
                if (status!= null)
                    status = status.trim();
                if (status==null || status.length()==0) {
                    Toast.makeText(PartsInActivity.this, "Введите состоянию", Toast.LENGTH_LONG).show();
                    return;
                }
                String store = ((EditText)findViewById(R.id.part_in_store)).getText().toString();
                String comment = ((EditText)findViewById(R.id.part_in_comment)).getText().toString();
                String url = Config.getUrlCar()+part.getCarId()+"/"+Config.getpathCategory()+"/"+part.getCategoryId()+"/create";
                HashMap<String, String> map = new HashMap<>();
                map.put("partId", part.getId()+"");
                map.put("brand", brand);
                map.put("status", status);
                map.put("store", store);
                map.put("comment", comment);
                HttpHandler handler = new HttpHandler();
                boolean result;
                result = handler.postWithMultipleFiles(url, map, photoList, getBaseContext());
                if (result) {
                    Toast.makeText(PartsInActivity.this, getString(R.string.part_added),Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else
                    Toast.makeText(PartsInActivity.this, getString(R.string.part_not_added),Toast.LENGTH_LONG).show();

            }
        });
    }
    private View.OnClickListener mCaptureImageButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startImageCapture();
        }
    };
    private void startImageCapture() {
        startActivityForResult(new Intent(PartsInActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
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

                final String imagePath = extras.getString(CameraActivityOld.EXTRA_CAMERA_DATA);
                if (imagePath != null && !imagePath.equals("")) {
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.photoCarField);
//                    layout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;

//                    layout.setVisibility(View.VISIBLE);

                    // Get the dimensions of the bitmap
//                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                    bmOptions.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFile(imagePath, bmOptions);

                    int targetH = dpToPx(150);//mCameraImageView.getWidth();
                    int targetW = dpToPx(1);



//                    int photoW = bmOptions.outWidth;
//                    int photoH = bmOptions.outHeight;

//                    if (photoH > photoW) {
//                        targetW = 1;//mCameraImageView.getWidth();
//                        targetH = 300;
//                    }
//                    Toast.makeText(PartsInActivity.this, photoH+" "+photoW, Toast.LENGTH_LONG).show();
                    // Determine how much to scale down the image
//                    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                    // Decode the image file into a Bitmap sized to fill the View
//                    bmOptions.inJustDecodeBounds = false;
//                    bmOptions.inSampleSize = scaleFactor;
//                    bmOptions.inPurgeable = true;

//                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
                    Bitmap bitmap = resizeBitmap(imagePath, targetW, targetH);

//                    ImageView mCameraImageView = (ImageView)findViewById(R.id.image_part_in);

//                    mCameraImageView.setImageBitmap(bitmap);

//                    ImageView closeCarPhoto = (ImageView) findViewById(R.id.closeCarPhoto);
//                    closeCarPhoto.setVisibility(View.VISIBLE);

//                    mCameraImageView.setVisibility(View.VISIBLE);

                    inflater = getLayoutInflater();
                    View viev = inflater.inflate(R.layout.item_part_in, null, false);
                    final ImageView image = (ImageView) viev.findViewById(R.id.image_part_in);
                    final ImageView imageClose = (ImageView)viev.findViewById(R.id.image_part_in_close);
                    image.setImageBitmap(bitmap);
                    imageClose.setImageResource(R.drawable.ysk_ic_close);

                    imageClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            image.setVisibility(View.GONE);
                            imageClose.setVisibility(View.GONE);
                            photoList.remove(photoList.indexOf(imagePath));
                        }
                    });
                    linLayoutPhotoParts.addView(viev);
                    photoList.add(imagePath);
//                    Toast.makeText(PartsInActivity.this, bitmap.getHeight()+" "+bitmap.getWidth(), Toast.LENGTH_LONG).show();
                }
            } else {
                mCameraBitmap = null;
            }
        }
    }
    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
    public Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }
}
