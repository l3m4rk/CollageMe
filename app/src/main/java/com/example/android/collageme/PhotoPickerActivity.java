package com.example.android.collageme;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class PhotoPickerActivity extends Activity {

    private String[] photos;

    private static final String DEBUG_TAG = PhotoPickerActivity.class.getSimpleName();

    private ArrayList<ImageView> imageItems = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        Bundle bundle = getIntent().getExtras();
        photos = bundle.getStringArray("photo");

        Log.d(DEBUG_TAG, "Photos count = " + photos.length);

        for (int i = 0; i < photos.length; i++) {

//            Log.d(DEBUG_TAG, photos[i]);

            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(loadImage(photos[i]));
//            File file = new File(photos[i]);
//            Picasso.with(this).load(file).into(imageView);

//            imageItems.add(new ImageItem(false, imageView, photos[i]));
            imageItems.add(imageView);
        }

        ImageViewList adapter = new ImageViewList(this, R.id.listView, imageItems.toArray(new ImageView[imageItems.size()]));

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }


    public void buildCollage(View view) {
        Toast.makeText(this, "А здесь мы будем делать коллаж! Только вот ХУЙ ЗНАЕТ КАК!", Toast.LENGTH_SHORT).show();

//        showSelected();
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

    private void removeAllFilesFromDirectory(File d) {
        String[] children = d.list();
        if (children.length != 0) {
            for (int i = 0; i < children.length; ++i)
                new File(d, children[i]).delete();
        }
        Log.d(DEBUG_TAG, Arrays.toString(children));
    }


}
