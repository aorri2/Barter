package com.example.barter.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barter.adapter.GalleryAdapter;
import com.example.barter.R;

import java.util.ArrayList;

import static com.example.barter.Util.GALLERY_IMAGE;
import static com.example.barter.Util.GALLERY_VIDEO;
import static com.example.barter.Util.INTENT_MEDIA;
import static com.example.barter.Util.showToast;

public class GalleryActivity extends BasicActivity {
   


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbarTitle("갤러리");
        setContentView(R.layout.activity_gallery);
        if(ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(GalleryActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,1);

            if(ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else {
               showToast(GalleryActivity.this, getResources().getString(R.string.grant_permis));


            }
        }else {
            recyclerInit();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    recyclerInit();
                }  else {
                    finish();
                    Toast.makeText(GalleryActivity.this,"권한을 허용해 주세",Toast.LENGTH_SHORT).show();

                }

        }
    }

    private void recyclerInit(){
        final int numberOfColumns = 3;
        RecyclerView recyclerView = findViewById(R.id.recv_gallery);


        recyclerView.setHasFixedSize(true);


        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

       RecyclerView.Adapter  mAdapter = new GalleryAdapter(this,getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }

    public  ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        Intent intent = getIntent();
        String[] projection;

        final int media = intent.getIntExtra(INTENT_MEDIA, GALLERY_IMAGE);
        if(media == GALLERY_VIDEO){
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            projection = new String[]{ MediaStore.MediaColumns.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME };
        }else{
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

           projection = new String[]{ MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        }


        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }
}
