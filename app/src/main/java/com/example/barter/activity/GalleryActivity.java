package com.example.barter.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.adapter.GalleryAdapter;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int numberOfColumns = 3;

        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.recv_gallery);

        recyclerView.setHasFixedSize(true);


        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        mAdapter = new GalleryAdapter(this,getImagesPath(this));
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

        if(intent.getStringExtra("media").equals("video")){
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            projection = new String[]{ MediaStore.MediaColumns.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME };
        }else{
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

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
