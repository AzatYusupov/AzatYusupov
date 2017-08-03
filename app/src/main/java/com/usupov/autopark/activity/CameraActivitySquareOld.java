package com.usupov.autopark.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.usupov.autopark.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraActivitySquareOld extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback, Camera.AutoFocusCallback
{
    public static final String EXTRA_CAMERA_DATA = "camera_data";
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private ImageView shotBtn, saveBtn, cancelBtn;
    private static final int optimalImageSize = 400;
    private Bitmap bitmapImage = null;
    private ImageView resultView;
    private FrameLayout frameImage;
    private Point imageSize;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // если хотим, чтобы приложение постоянно имело портретную ориентацию
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // если хотим, чтобы приложение было полноэкранным
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // и без заголовка
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_camera_square_old);
//        initToolbar();
        // наше SurfaceView имеет имя SurfaceView01
        surfaceView = (SurfaceView) findViewById(R.id.image_preview);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // кнопка имеет имя Button01
        shotBtn = (ImageView) findViewById(R.id.image_shot_btn);
        shotBtn.setOnClickListener(this);

        cancelBtn = (ImageView) findViewById(R.id.image_cancel_btn);
        cancelBtn.setOnClickListener(this);

        saveBtn = (ImageView) findViewById(R.id.image_save_btn);
        saveBtn.setOnClickListener(this);
        saveBtn.setVisibility(View.INVISIBLE);

        resultView = (ImageView) findViewById(R.id.image_result_view);
        resultView.setVisibility(View.GONE);
        frameImage = (FrameLayout) findViewById(R.id.frameImage);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

//    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_camera);
//        toolbar.setTitle("Фотографировать");
//        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }

    @Override
    protected void onResume()
    {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (camera != null)
        {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for (Size size: camera.getParameters().getSupportedPictureSizes()) {
            System.out.println(size.width+" "+size.height);
        }

//        Size previewSize = camera.getParameters().getPreviewSize();
        Size previewSize = getOptimalSize();
//        float aspect = (float) previewSize.width / previewSize.height;
//        System.out.println(previewSize.width+" "+previewSize.height+" ------");
//        System.out.println("Aspect="+aspect);

        int previewSurfaceWidth = previewSize.width;
        int previewSurfaceHeight = previewSize.height;

        LayoutParams lp = surfaceView.getLayoutParams();

        // здесь корректируем размер отображаемого preview, чтобы не было искажений

//        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT)
//        {
//            // портретный вид
//            camera.setDisplayOrientation(90);
//            lp.height = previewSurfaceWidth;
//            lp.width = previewSurfaceHeight;
//        }
//        else
//        {
//            // ландшафтный
//            camera.setDisplayOrientation(0);
//            lp.height = previewSurfaceWidth;
//            lp.width = previewSurfaceHeight;
//        }

//        surfaceView.setLayoutParams(lp);


        imageSize = new Point();
        imageSize.x = getResources().getDisplayMetrics().widthPixels;
        imageSize.y = imageSize.x * previewSurfaceWidth / previewSurfaceHeight;

        frameImage.setLayoutParams(new LinearLayout.LayoutParams(imageSize.y, imageSize.x));
        surfaceView.setLayoutParams(new FrameLayout.LayoutParams(imageSize.y, imageSize.x));
        camera.setDisplayOrientation(90);

        Camera.Parameters parameters = camera.getParameters();
//        parameters.setPreviewSize(previewSurfaceHeight, previewSurfaceWidth);
        camera.setParameters(parameters);
        /*
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(90);
            lp.height = imageSize.y;
            lp.width = imageSize.x;
        } else {
            camera.setDisplayOrientation(0);
            lp.width = imageSize.x;
            lp.height = imageSize.y;
        }
        */



//        frameImage.getLayoutParams().height = previewSurfaceWidth;
//        frameImage.getLayoutParams().width = previewSurfaceHeight;
//        surfaceView.getLayoutParams().height = previewSurfaceWidth;
//        surfaceView.getLayoutParams().width = previewSurfaceHeight;
//
//        resultView.getLayoutParams().height = previewSurfaceWidth;
//        resultView.getLayoutParams().width = previewSurfaceHeight;
//        resultView.setLayoutParams(lp);

        camera.startPreview();
    }

    private Size getOptimalSize() {
        Size previewSize = camera.getParameters().getPreviewSize();
        return previewSize;
//        int minHeight = (int)1e9;
//        for (Size size : camera.getParameters().getSupportedPreviewSizes()) {
//            System.out.println(size.width+" "+size.height+" 4564564564564564564");
//            if (size.width >= optimalImageSize && size.width < minHeight) {
//                minHeight = size.width;
//                previewSize = size;
//            }
//        }
//        System.out.println("OOOOOOOOOOOOOOOOOOOOO");
//        System.out.println(previewSize.width+" "+previewSize.height);
//        return previewSize;
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.image_shot_btn :
                if (saveBtn.getVisibility()==View.VISIBLE) {
                    shotBtn.setImageResource(R.drawable.ic_action_do_photo);
                    saveBtn.setVisibility(View.INVISIBLE);

                    resultView.setVisibility(View.GONE);
                    surfaceView.setVisibility(View.VISIBLE);

                    camera.startPreview();
                }
                else {
                    // либо делаем снимок непосредственно здесь
                    // 	либо включаем обработчик автофокуса
                    camera.takePicture(null, null, null, this);
                    // camera.autoFocus(this);
                }
                break;
            case R.id.image_cancel_btn :
                Toast.makeText(CameraActivitySquareOld.this, "Отменено", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.image_save_btn :
                String addressSavedImage = saveImage();
                if (addressSavedImage ==null)
                    Toast.makeText(CameraActivitySquareOld.this, "Не удалось сохранить фото", Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_CAMERA_DATA, addressSavedImage);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // сохраняем полученные jpg в папке /sdcard/CameraExample/
        // имя файла - System.currentTimeMillis()

        Bitmap  bitmap = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length);

//        bitmapImage = getResizedBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
        bitmapImage = bitmap;
        saveBtn.setVisibility(View.VISIBLE);
        shotBtn.setImageResource(R.drawable.ic_action_repeat);

        surfaceView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);
        System.out.println("77777777777777777");
        System.out.println(imageSize.x+" "+imageSize.y);
        resultView.setLayoutParams(new FrameLayout.LayoutParams(imageSize.x, imageSize.y));

        resultView.setImageBitmap(bitmap);

//        bitmap.recycle();
//        paramCamera.startPreview();
    }

    private String saveImage() {
        try
        {
            String root = Environment.getExternalStorageDirectory().toString();
            File saveDir = new File(root + "/productcard_images/");


            if (!saveDir.exists())
            {
                saveDir.mkdirs();
            }
            String fileName = String.format(root+"/productcard_images/%d.jpg", System.currentTimeMillis());
            FileOutputStream os = new FileOutputStream(fileName);

            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return fileName;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // после того, как снимок сделан, показ превью отключается. необходимо включить его
       return null;
    }

    @Override
    public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
    {
        if (paramBoolean)
        {
            // если удалось сфокусироваться, делаем снимок
            paramCamera.takePicture(null, null, null, this);
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

    @Override
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // здесь можно обрабатывать изображение, показываемое в preview

    }
}