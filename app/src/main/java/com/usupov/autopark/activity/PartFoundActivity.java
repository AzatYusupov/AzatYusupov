package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import product.card.R;
import com.usupov.autopark.adapter.PartFoundAdapter;
import com.usupov.autopark.adapter.StatusListAdapter;
import com.usupov.autopark.config.ImageRestURIConstants;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.StatusModel;
import com.usupov.autopark.model.UserPartModel;
import com.usupov.autopark.service.ImageProcessService;
import com.usupov.autopark.squarecamera.CameraActivity;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PartFoundActivity extends AppCompatActivity {

    public static final int TAKE_PICTURE_REQUEST_B = 200;
    private Bitmap mCameraBitmap;
    LinearLayout linLayoutPhotoParts;
    LayoutInflater inflater;
    private static ArrayList<String> photoList;
    private static List<UserPartModel> parts;
    private final int PART_PICTURE_SIZE = 500;
    private TextView errorStatus, errorPrice;

    private int resultCode;
    ProgressDialog progressDialog;

    boolean isUpdating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_found);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        progressDialog = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.please_wait));

        Gson g = new Gson();
        parts = g.fromJson(getIntent().getExtras().getString("parts"), new TypeToken<List<UserPartModel>>(){}.getType());
        photoList = new ArrayList<>();

        if (getIntent().hasExtra("isUpdate")) {
            isUpdating = true;
            initialUpdateValues();
            loadPartImages();
        }

        initToolbar();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvPartFound);
        PartFoundAdapter adapter = new PartFoundAdapter(this, parts, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fabPhoto = (FloatingActionButton) findViewById(R.id.btn_photo_part_in);
        fabPhoto.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryLight));
        fabPhoto.setOnClickListener(mCaptureImageButtonClickListener);

        linLayoutPhotoParts = (LinearLayout)findViewById(R.id.layout_parts_in);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initBtnSavePartIn();

    }

    ArrayList<String> savedImageList = new ArrayList<>();
    private void loadPartImages() {
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
        final UserPartModel part = parts.get(0);
        if (part.getCntImages()==0)
            progressDialog.dismiss();

        for (int i = 0; i < part.getCntImages(); i++) {
            String url = String.format(ImageRestURIConstants.GET_PART, part.getId(), i);
            final int number = i;
            Glide.with(this)
                    .load(Headers.getUrlWithHeaders(url, getApplicationContext()))
                    .asBitmap()
                    .skipMemoryCache(true).diskCacheStrategy( DiskCacheStrategy.NONE )
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            String path = ImageProcessService.saveImage(getApplicationContext(), resource, "temp_image_"+number);
                            savedImageList.add(path);
                            if (savedImageList.size()==part.getCntImages()) {
                                progressDialog.dismiss();
                                Collections.sort(savedImageList);
                                addImages(savedImageList);
                            }
                        }
                    });
        }
    }


    private void initialUpdateValues() {
        ((EditText)findViewById(R.id.part_in_store)).setText(parts.get(0).getStore());
        ((EditText)findViewById(R.id.part_in_status)).setText(parts.get(0).getStatus());
        if (parts.get(0).getPrice() != 0)
            ((EditText)findViewById(R.id.part_in_price)).setText(parts.get(0).getPrice()+"");
        ((EditText)findViewById(R.id.part_in_comment)).setText(parts.get(0).getComment());

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_car_found);
        if (isUpdating)
            toolbar.setTitle("Редактирование" + " " + getIntent().getStringExtra("car_full_name"));
        else
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
    ActionProcessButton btnSave;
    private void initBtnSavePartIn() {
        btnSave = (ActionProcessButton) findViewById(R.id.apbSavePart);
        errorStatus = (TextView) findViewById(R.id.errorStatus);
        errorPrice = (TextView) findViewById(R.id.errorPrice);
        final EditText edtStatus = (EditText)findViewById(R.id.part_in_status);
        edtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorStatus.setVisibility(View.GONE);
                showSelectStatusDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = edtStatus.getText().toString().trim();

                if (status.isEmpty()) {
                    Toast.makeText(PartFoundActivity.this, getString(R.string.error_status_empty_bolun), Toast.LENGTH_LONG).show();
                    errorStatus.setVisibility(View.VISIBLE);
                    return;
                }
                errorStatus.setVisibility(View.GONE);
                Locale.setDefault(Locale.US);
                String price = ((EditText)findViewById(R.id.part_in_price)).getText().toString().trim();

                boolean wrongPrice = false;
                if (!price.isEmpty()) {
                    try {
                        if (Long.parseLong(price) <= 0)
                            wrongPrice = true;
                    } catch (NumberFormatException exc) {
                        wrongPrice = true;
                    }
                }
                else
                    price = "0";
                if (wrongPrice) {;
                    Toast.makeText(PartFoundActivity.this, getString(R.string.error_price_wrong_bolun), Toast.LENGTH_LONG).show();
                    errorPrice.setVisibility(View.VISIBLE);
                    errorPrice.setText(getString(R.string.error_price_wrong));
                    return;
                }
                btnSave.setEnabled(false);
//                btnSave.setProgress(1);
                progressDialog.show();
                errorPrice.setVisibility(View.GONE);

                String store = ((EditText)findViewById(R.id.part_in_store)).getText().toString();
                String comment = ((EditText)findViewById(R.id.part_in_comment)).getText().toString();
                UserPartModel first = parts.get(0);

                String url = String.format(PartRestURIConstants.CREATE, first.getCarId(), first.getCategoryId());
                if (isUpdating)
                    url = String.format(PartRestURIConstants.UPDATE, first.getId());


                HashMap<String, String> map = new HashMap<>();
                if (!isUpdating) {
                    String partIdArray = "";
                    String ycpIdArray = "";
                    for (UserPartModel part : parts) {
                        partIdArray += part.getId() + " ";
                        ycpIdArray += part.getYcpId() + " ";
                    }
                    map.put("partId", partIdArray);
                    map.put("ycpId", ycpIdArray);
                }

                map.put("status", status);
                map.put("store", store);
                map.put("comment", comment);
                map.put("price", price);

                TaskSendParts taskSendParts = new TaskSendParts(url, map);
                taskSendParts.execute();
            }
        });
    }

    private void showSelectStatusDialog() {

        final List<StatusModel> statusList = new ArrayList<>();
        statusList.add(new StatusModel("B", R.mipmap.ic_status_b, "Восстановленный", "Товар полностью работоспособен"));
        statusList.add(new StatusModel("100", R.mipmap.ic_status_100, "Отличное", "Товар полностью работоспособен"));
        statusList.add(new StatusModel("90", R.mipmap.ic_status_90, "Хорошее", "Товар полностью работоспособен"));
        statusList.add(new StatusModel("70", R.mipmap.ic_status_70, "Удовлетворительное", "Товар в основном работоспособен"));
        statusList.add(new StatusModel("50", R.mipmap.ic_status_50, "Под восстановление", "Без ремонта не работает"));
        statusList.add(new StatusModel("30", R.mipmap.ic_status_30, "Ремонтный набор", "Товар не работосопобен"));

        StatusListAdapter adapter = new StatusListAdapter(this, statusList);
        final AlertDialog dialog = new AlertDialog.Builder(this).setAdapter(adapter, null).create();
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((EditText)findViewById(R.id.part_in_status)).setText(statusList.get(position).getTitle());
                dialog.dismiss();
            }
        });
        dialog.show();

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

                if (mCameraBitmap != null) {
                    mCameraBitmap.recycle();
                    mCameraBitmap = null;
                }

                ArrayList<String> imageList = data.getStringArrayListExtra(CameraActivity.KEY_IMAGES);
                addImages(imageList);

            }
        }
    }

    private void addImages(ArrayList<String> imageList) {

        Bitmap bitmap = null;
        if (imageList != null) {
            for (final String imagePath : imageList) {
                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imagePath)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int size = ImageProcessService.dpToPx(200, this);
                bitmap = ImageProcessService.getResizedBitmap(bitmap, size, size);

                inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.item_part_found_image, null, false);
                final ImageView image = (ImageView) view.findViewById(R.id.image_part_in);
                final ImageView imageClose = (ImageView) view.findViewById(R.id.image_part_in_close);
                image.setImageBitmap(bitmap);

                imageClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image.setVisibility(View.GONE);
                        imageClose.setVisibility(View.GONE);
                        photoList.remove(photoList.indexOf(imagePath));
                        view.setVisibility(View.GONE);
                    }
                });

                linLayoutPhotoParts.addView(view);
                photoList.add(imagePath);
            }
        }
    }


    class TaskSendParts extends AsyncTask<Void, Void, Void> {

        HashMap<String, String> pairValues;
        String url;
        public TaskSendParts(String url, HashMap<String, String> pairValues) {
            this.url = url;
            this.pairValues = pairValues;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler handler = new HttpHandler();
            resultCode = handler.postWithMultipleFiles(url, pairValues, photoList, getApplicationContext(), false).getStatusCode();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (resultCode==HttpStatus.SC_OK) {
//                btnSave.setProgress(0);
                if (isUpdating) {
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                    Toast.makeText(PartFoundActivity.this, getString(R.string.part_updated), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(PartFoundActivity.this, PartListActivity.class), bundle);
                    finishAffinity();
                }
                else {
                    Toast.makeText(PartFoundActivity.this, getString(R.string.part_added), Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
            else {
                if (resultCode== HttpStatus.SC_UNAUTHORIZED || resultCode==HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                    Intent intent = new Intent(PartFoundActivity.this, LoginActivity.class);
                    intent.putExtra("unauthorized", true);
                    startActivity(intent, bundle);
                    finishAffinity();
                }
                else
                    Toast.makeText(PartFoundActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
//                btnSave.setProgress(0);
                btnSave.setEnabled(true);
            }
        }
    }
}
