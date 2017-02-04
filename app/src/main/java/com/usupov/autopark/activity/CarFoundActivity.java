package com.usupov.autopark.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.model.CarFoundModel;

import java.io.File;
import java.util.Date;

/**
 * Created by Azat on 31.01.2017.
 */

public class CarFoundActivity extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_found);

        setCarInforms(new CarFoundModel("Audi A5 Рестайлинг 2.0 МТ, 211 л.с 4WD", "1234567891011121", "2.0/211 л.с./ Бензин", 2015, "Купе", "Полный", "Механическая"));

        initToolbar();

    }

    public void onClickClose(View v) {

        Toast toast = Toast.makeText(getApplicationContext(),
                "Удалено!", Toast.LENGTH_SHORT);
        toast.show();

        ImageView closeCarPhoto = (ImageView) findViewById(R.id.closeCarPhoto);
        closeCarPhoto.setVisibility(View.INVISIBLE);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.photoCarField);
       // layout.getLayoutParams().height = 0;
        layout.setVisibility(View.INVISIBLE);
        ivPhotoCar = (ImageView) findViewById(R.id.ivPhotoCar);
        //ivPhotoCar.getLayoutParams().height = 0;
        ivPhotoCar.setVisibility(View.INVISIBLE);

    }
  //  File directory;
    final int TYPE_PHOTO = 1;
    final int TYPE_VIDEO = 2;

    final int REQUEST_CODE_PHOTO = 1;
    final int REQUEST_CODE_VIDEO = 2;
    final String TAG = "myLogs";

    ImageView ivPhotoCar;

    public void onClickPhoto(View view) {
      //  createDirectory();
        ivPhotoCar = (ImageView) findViewById(R.id.ivPhotoCar);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
        startActivityForResult(intent, REQUEST_CODE_PHOTO);

    }

    public void onClickVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      //  intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_VIDEO));
        startActivityForResult(intent, REQUEST_CODE_VIDEO);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_CODE_PHOTO) {

            if (resultCode == RESULT_OK) {

                if (intent == null) {

                    Log.d(TAG, "Intent is null");

                } else {

                    Log.d(TAG, "Photo uri: " + intent.getData());
                    Bundle bndl = intent.getExtras();
                    if (bndl != null) {
                        Object obj = intent.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                            //bitmap = getResizedBitmap(bitmap, 300, 200);

                            ivPhotoCar.setImageBitmap(bitmap);
                            ImageView closeCarPhoto = (ImageView) findViewById(R.id.closeCarPhoto);
                            closeCarPhoto.setVisibility(View.VISIBLE);

                            RelativeLayout layout = (RelativeLayout) findViewById(R.id.photoCarField);
                            layout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;

                            layout.setVisibility(View.VISIBLE);
                            ivPhotoCar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {

                Log.d(TAG, "Canceled");

            }

        }

        if (requestCode == REQUEST_CODE_VIDEO) {

            if (resultCode == RESULT_OK) {

                if (intent == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Video uri: " + intent.getData());
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }
    }
/*
    private Uri generateFileUri(int type) {
        File file = null;
        switch (type) {
            case TYPE_PHOTO:
                file = new File(directory.getPath() + "/" + "photo_"
                        + (new Date().getTime()) + ".jpg");
                break;
            case TYPE_VIDEO:
                file = new File(directory.getPath() + "/" + "video_"
                        + (new Date().getTime()) + ".mp4");
                break;
        }
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }
/*
    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
    }
*/
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
