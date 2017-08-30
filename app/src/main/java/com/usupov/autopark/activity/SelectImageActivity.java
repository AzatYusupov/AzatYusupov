package com.usupov.autopark.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import productcard.ru.R;
import com.usupov.autopark.adapter.GalleryAdapter;
import com.usupov.autopark.model.AlbumModel;
import com.usupov.autopark.squarecamera.CameraActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectImageActivity extends AppCompatActivity {

    private List<AlbumModel> imageList;
    private GalleryAdapter adapter;
    private TextView readySelectedImagesBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_image);

        imageList = new ArrayList<>();
        readySelectedImagesBtn = (TextView) findViewById(R.id.readySelectedImagesBtn);
        readySelectedImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedImagesList = new ArrayList<>();
                for (AlbumModel item : imageList) {
                    if (item.isSelected())
                        selectedImagesList.add(item.getImageUri());
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra(CameraActivity.KEY_IMAGES, selectedImagesList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);


//        adapter = new AlbumsAdapter(this, imageList);
//        RecyclerView.LayoutManager layoutManager = null;
//        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
//            layoutManager = new GridLayoutManager(this, 2);
//        else
//            layoutManager = new GridLayoutManager(this, 4);

//        recyclerView.setLayoutManager(layoutManager);

//        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
//        layoutManager2.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
//        recyclerView.setLayoutManager(layoutManager2);

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);

        adapter = new GalleryAdapter(SelectImageActivity.this, imageList);
        GridView gridView = (GridView)findViewById(R.id.gridviewGallery);
        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
            gridView.setNumColumns(2);
        else
            gridView.setNumColumns(3);
        gridView.setAdapter(adapter);
        prepareAlbums();


    }

    private void prepareAlbums() {
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
            final String orderBy = MediaStore.Images.Media._ID;
//Stores all the images from the gallery in Cursor
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                    null, orderBy);
//Total number of images
            int count = cursor.getCount();

//Create an array to store path to all the images

            for (int i = count-1; i >= 0; i--) {
                cursor.moveToPosition(i);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                //Store the path of the image
                imageList.add(new AlbumModel(cursor.getString(dataColumnIndex)));
                adapter.notifyDataSetChanged();
                if (imageList.size()==200)
                    break;
//                myAdapter.addImage(cursor.getString(dataColumnIndex));
            }
// The cursor should be freed up after use with close()
            cursor.close();

        }
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */

}
