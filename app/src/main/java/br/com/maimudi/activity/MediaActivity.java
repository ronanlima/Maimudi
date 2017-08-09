package br.com.maimudi.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.adapter.SquareViewAdapter;
import br.com.maimudi.asyncTask.ImageLoader;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 17/03/16.
 */
@RuntimePermissions
public class MediaActivity extends BaseActivity  {

    private List<String> imgData;
    private GridView gridView;
    private List<Bitmap> listBitmaps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_mudi_media);

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",listBitmaps.get(position));
                setResult(RESULT_OK,returnIntent);
                finish();

            }
        });
        imgData = new ArrayList<>();

        if(listBitmaps == null){
            listBitmaps = new ArrayList<>();
            MediaActivityPermissionsDispatcher.getAllGalleryWithCheck(this);
        }else{
            gridView.setAdapter(new SquareViewAdapter(this,listBitmaps));
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MediaActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void getAllGallery(){

        String[] projection = new String[]{
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA
        };

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = getContentResolver().query(images,
                projection, // Which columns to return
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?",       // Which rows to return (all rows)
                new String[]{"Camera"},       // Selection arguments (none)
                null        // Ordering
        );

        Log.i("ListingImages"," query count="+cur.getCount());

        if (cur.moveToFirst()) {

            String bucket;
            String data;
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dataColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATA);

            do {
                bucket = cur.getString(bucketColumn);
                data = cur.getString(dataColumn);

                Log.i("ListingImages", " bucket=" + bucket
                        + " data= " + data);

                imgData.add(data);


            } while (cur.moveToNext());

        }

        new ImageLoader(this).execute(imgData);

    }

    public void result(List<Bitmap> bitmaps){

        listBitmaps = bitmaps;

        gridView.setAdapter(new SquareViewAdapter(this,listBitmaps));
    }

}
