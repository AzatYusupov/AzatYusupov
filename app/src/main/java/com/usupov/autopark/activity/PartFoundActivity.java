package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.android.productcard.R;
import com.usupov.autopark.adapter.PartFoundAdapter;
import com.usupov.autopark.adapter.StatusListAdapter;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.StatusModel;
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
    private TextView errorStatus;
    private ProgressDialog progressDialog;

    private int resultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_found);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        Gson g = new Gson();
        parts = g.fromJson(getIntent().getExtras().getString("parts"), new TypeToken<List<UserPartModel>>(){}.getType());
        photoList = new ArrayList<>();
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
        errorStatus = (TextView) findViewById(R.id.errorStatus);

        progressDialog = new ProgressDialog(this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.please_wait));

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

                String status = edtStatus.getText().toString();
                if (status!= null)
                    status = status.trim();
                if (status==null || status.length()==0) {
                    Toast.makeText(PartFoundActivity.this, getString(R.string.error_status_empty_bolun), Toast.LENGTH_LONG).show();
                    errorStatus.setVisibility(View.VISIBLE);
                    return;
                }
                errorStatus.setVisibility(View.GONE);
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
                map.put("status", status);
                map.put("store", store);
                map.put("comment", comment);

                TaskSendParts task = new TaskSendParts(url, map);
                task.execute();
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
            progressDialog.show();
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
            if (resultCode==HttpStatus.SC_OK) {
                Toast.makeText(PartFoundActivity.this, getString(R.string.part_added),Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
            else {
                Toast.makeText(PartFoundActivity.this, resultCode+"", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }
    }
}
