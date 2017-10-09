package com.usupov.autopark.squarecamera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import product.card.R;

import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {

    public static final String TAG = CameraActivity.class.getSimpleName();

    public static final String KEY_IMAGES = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.squarecamera__CameraFullScreenTheme);
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.squarecamera__activity_camera);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, CameraFragment.newInstance(), CameraFragment.TAG)
                    .commit();
        }
    }

    public void returnPhotoUri(Uri uri) {
        Intent data = new Intent();
        ArrayList<String> imageList = new ArrayList<>();
        imageList.add(uri.getPath());
        data.putStringArrayListExtra(KEY_IMAGES, imageList);
        if (getParent() == null) {
            setResult(RESULT_OK, data);
        } else {

            getParent().setResult(RESULT_OK, data);
        }

        finish();
    }


    public void onCancel(View view) {
        getSupportFragmentManager().popBackStack();
    }
}
