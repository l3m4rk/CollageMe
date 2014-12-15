package com.example.android.collageme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class PhotoPickerActivity extends Activity {

    private String[] photos;

    private static final String DEBUG_TAG = PhotoPickerActivity.class.getSimpleName();

    private ArrayList<PhotoItem> photoItems = new ArrayList<PhotoItem>();
    private List<PhotoItem> selected;
    private PhotoAdapter adapter;
    private ListView listView;

    private String nameOfPersonFolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        Bundle bundle = getIntent().getExtras();
        photos = bundle.getStringArray("photo");
        nameOfPersonFolder = bundle.getString("nickname");

        Log.d(DEBUG_TAG, "Photos count = " + photos.length);

        PhotoItem photoItem;

        for (String photo : photos) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(loadImage(photo));
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

    public void buildCollageClick(View view) {

        selected = adapter.getSelectedItems();
        int photoCount = selected.size();


        Bitmap collage = createCollageFromImages(selected);

        //save collage to the user folder

        //get path to collage
        String pathToCollage = saveToInternalStorage(collage);
        //send collage path to third activity
        Intent intent = new Intent(this, CollageSenderActivity.class);
        intent.putExtra("collage_path", pathToCollage);
        startActivity(intent);
    }

    private Bitmap createCollageFromImages(List<PhotoItem> selected) {
        Bitmap bitmap = null;

        int photoCount = selected.size();


        int width = ((BitmapDrawable) selected.get(0).view.getDrawable()).getBitmap().getWidth();
        int height = ((BitmapDrawable) selected.get(0).view.getDrawable()).getBitmap().getHeight();


        //TODO: at this moment it will work for even count of photos only
        final int COLUMNS = 2;
        final int ROWS = photoCount / COLUMNS;

        try {
            bitmap = Bitmap.createBitmap(width * COLUMNS, height * ROWS, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            int count = 0;
            Bitmap currentBitmap;
            for (int rows = 0; rows < ROWS; rows++) {
                for (int cols = 0; cols < COLUMNS; cols++) {
                    currentBitmap = ((BitmapDrawable) selected.get(count).view.getDrawable()).getBitmap();
                    canvas.drawBitmap(currentBitmap, width * cols, height * rows, null);
                    count++;
                }
            }
        } catch (Exception e) {
            Log.v(DEBUG_TAG, e.toString());
        }
        return bitmap;
    }

    //TODO: refactor this anyway
    private String saveToInternalStorage(Bitmap bitmap) {
        String nameOfFolder = "/CollageMe/images/" + nameOfPersonFolder + "/collage";
        String nameOfFile = "collage";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + nameOfFolder;
        File d = new File(filePath);

        if (!d.exists()) {
            d.mkdirs();
        }

        File file = new File(d, nameOfFile + ".jpg");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
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
}
