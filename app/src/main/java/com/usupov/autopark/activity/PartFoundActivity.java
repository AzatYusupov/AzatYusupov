package com.usupov.autopark.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usupov.autopark.R;
import com.usupov.autopark.adapter.PartFoundAdapter;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.UserPartModel;
import com.usupov.autopark.service.ImageProcessService;
import com.usupov.autopark.squarecamera.CameraActivity;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartFoundActivity extends AppCompatActivity {

    public static final int TAKE_PICTURE_REQUEST_B = 200;
    private Bitmap mCameraBitmap;
    LinearLayout linLayoutPhotoParts;
    LayoutInflater inflater;
    private static ArrayList<String> photoList;
    private static List<UserPartModel> parts;
    private final int PART_PICTURE_SIZE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_found);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        Gson g = new Gson();
        parts = g.fromJson(getIntent().getExtras().getString("parts"), new TypeToken<List<UserPartModel>>(){}.getType());
        System.out.println(parts.size()+" 898989410");
        photoList = new ArrayList<>();
        initToolbar();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvPartFound);
        PartFoundAdapter adapter = new PartFoundAdapter(this, parts, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        findViewById(R.id.btn_photo_part_in).setOnClickListener(mCaptureImageButtonClickListener);
        linLayoutPhotoParts = (LinearLayout)findViewById(R.id.layout_parts_in);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initBtnSavePartIn();

    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_found);
        toolbar.setTitle("Деталь" + " " + getIntent().getStringExtra("car_full_name"));
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
    private void initBtnSavePartIn() {
        Button btnSave = (Button) findViewById(R.id.save_part_in);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EditText edtManufacturer = (EditText)findViewById(R.id.part_in_manufacturer);
//                String brand = edtManufacturer.getText().toString();
//                if (brand != null)
//                    brand = brand.trim();
//                if (brand==null || brand.length()==0) {
//                    Toast.makeText(PartFoundActivity.this, "Введите производител", Toast.LENGTH_LONG).show();
//                    return;
//                }

                EditText edtStatus = (EditText)findViewById(R.id.part_in_status);
                String status = edtStatus.getText().toString();
                if (status!= null)
                    status = status.trim();
                if (status==null || status.length()==0) {
                    Toast.makeText(PartFoundActivity.this, "Введите состоянию", Toast.LENGTH_LONG).show();
                    return;
                }
                String store = ((EditText)findViewById(R.id.part_in_store)).getText().toString();
                String comment = ((EditText)findViewById(R.id.part_in_comment)).getText().toString();
                UserPartModel first = parts.get(0);
                String url = String.format(PartRestURIConstants.CREATE, first.getCarId(), first.getCategoryId());

                HashMap<String, String> map = new HashMap<>();
                String partIdArray = "";
                for (UserPartModel part : parts) {
                    partIdArray += part.getId() + " ";
                }
                map.put("partId", partIdArray);
//                map.put("brand", brand);
                map.put("status", status);
                map.put("store", store);
                map.put("comment", comment);
                HttpHandler handler = new HttpHandler();
                int result = handler.postWithMultipleFiles(url, map, photoList, getApplicationContext(), false).getStatusCode();
                if (result== HttpStatus.SC_OK) {
                    Toast.makeText(PartFoundActivity.this, getString(R.string.part_added),Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    Toast.makeText(PartFoundActivity.this, result+"", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    private View.OnClickListener mCaptureImageButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder dialogSelectTypeImage = new AlertDialog.Builder(PartFoundActivity.this);
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
        startActivityForResult(new Intent(PartFoundActivity.this, SelectImageActivity.class), TAKE_PICTURE_REQUEST_B);
    }
    private void startImageCapture() {
        startActivityForResult(new Intent(PartFoundActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PICTURE_REQUEST_B) {
            if (resultCode == RESULT_OK) {

                // Recycle the previous bitmap.
                if (mCameraBitmap != null) {
                    mCameraBitmap.recycle();
                    mCameraBitmap = null;
                }

                ArrayList<String> imageList = data.getStringArrayListExtra(CameraActivity.KEY_IMAGES);
                Bitmap bitmap = null;
                if (imageList != null) {
                    for (final String imagePath : imageList) {
                        bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imagePath)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bitmap = ImageProcessService.getResizedBitmap(bitmap, PART_PICTURE_SIZE, PART_PICTURE_SIZE);

                        inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.item_part_in, null, false);
                        final ImageView image = (ImageView) view.findViewById(R.id.image_part_in);
                        final ImageView imageClose = (ImageView) view.findViewById(R.id.image_part_in_close);
                        image.setImageBitmap(bitmap);
                        imageClose.setImageResource(R.drawable.ic_action_close);

                        imageClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                image.setVisibility(View.GONE);
                                imageClose.setVisibility(View.GONE);
                                photoList.remove(photoList.indexOf(imagePath));
                            }
                        });
                        LinearLayout.LayoutParams rightMargin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        rightMargin.rightMargin = 70;

                        linLayoutPhotoParts.addView(view);
                        photoList.add(imagePath);
                    }
                }
            }
        }
    }
}
