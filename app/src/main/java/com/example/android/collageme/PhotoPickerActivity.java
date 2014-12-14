package com.example.android.collageme;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class PhotoPickerActivity extends Activity {

    private String[] photos;

    private static final String DEBUG_TAG = PhotoPickerActivity.class.getSimpleName();

    private ArrayList<PhotoItem> photoItems = new ArrayList<PhotoItem>();
    private ArrayList<ImageView> selected = new ArrayList<>();
    PhotoAdapter adapter;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        Bundle bundle = getIntent().getExtras();
        photos = bundle.getStringArray("photo");

        Log.d(DEBUG_TAG, "Photos count = " + photos.length);

        PhotoItem photoItem;

        for (int i = 0; i < photos.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(loadImage(photos[i]));
//            File file = new File(photos[i]);
//            Picasso.with(this).load(file).into(imageView);
            photoItem = new PhotoItem();
            photoItem.view = imageView;
            photoItem.checked = false;
            photoItems.add(photoItem);
        }

        adapter = new PhotoAdapter(this, R.id.listView, photoItems.toArray(new PhotoItem[photoItems.size()]));

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.toggleSelection(position);
            }
        });
    }

    public void buildCollage(View view) {
        int photoCount = adapter.getSelectedItems().size();
        Toast.makeText(PhotoPickerActivity.this, "Ты выбрал всего " + photoCount + "  объектов", Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(PhotoPickerActivity.this, CollageSenderActivity.class);
//        startActivity(intent);
    }

    private Bitmap loadImage(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            InputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap loadImageWithPicasso(String path) {
        return null;
    }




}
