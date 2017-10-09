package com.usupov.autopark.service;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import product.card.R;

public class ImageProcessService {

    final static int SENDING_IMAGE_MAX_SIZE = 600;
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {

        double x = Math.min(newWidth, 1.0 * newHeight * bm.getWidth() / bm.getHeight());
        double y = x * bm.getHeight() / bm.getWidth();

        return Bitmap.createScaledBitmap(bm, (int)x, (int)y, true);
//        int width = bm.getWidth();
//        int height = bm.getHeight();
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        // CREATE A MATRIX FOR THE MANIPULATION
//        Matrix matrix = new Matrix();
//        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight);
//
//        // "RECREATE" THE NEW BITMAP
//        Bitmap resizedBitmap = Bitmap.createBitmap(
//                bm, 0, 0, width, height, matrix, false);
//        bm.recycle();
//        return resizedBitmap;
    }

    public static String getSendingStringImage(String imagePath, Context context) {

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        bitmap = ImageProcessService.getResizedBitmap(bitmap, SENDING_IMAGE_MAX_SIZE, SENDING_IMAGE_MAX_SIZE);

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getString(R.string.app_name)
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String savePath =  mediaStorageDir.getPath() + File.separator + "temp.jpg";
        File saveImage = new File(savePath);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(saveImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            bitmap = BitmapFactory.decodeFile(savePath);
        }catch (Exception e) {

        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        if (saveImage != null)
            saveImage.delete();

        return encodedImage;
    }

    public static int dpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static String saveImage(Context context, Bitmap bitmap, String preferedName) {

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getString(R.string.app_name)
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String filePath = mediaStorageDir.getPath() + File.separator + "IMG_" + preferedName + ".jpg";
        File mediaFile = new File(filePath);

        // Saving the bitmap
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            FileOutputStream stream = new FileOutputStream(mediaFile);
            stream.write(out.toByteArray());
            stream.close();
            return filePath;

        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }

        // Mediascanner need to scan for the image saved
//        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri fileContentUri = Uri.fromFile(mediaFile);
//        mediaScannerIntent.setData(fileContentUri);
//        context.sendBroadcast(mediaScannerIntent);

//        return fileContentUri;


    }
}
