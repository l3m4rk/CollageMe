package com.example.android.collageme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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

    private ArrayList<PhotoItem> photoItems = new ArrayList<>();
    private List<PhotoItem> selected;

    private PhotoAdapter adapter;
    private ListView photosListView;

    private String nameOfPersonFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        Bundle bundle = getIntent().getExtras();
        photos = bundle.getStringArray("photo");
        nameOfPersonFolder = bundle.getString("nickname");

        PhotoItem photoItem;

        for (String photo : photos) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(loadImage(photo));
            photoItem = new PhotoItem();
            photoItem.photo = imageView;
            photoItem.setSelected(false);
            photoItems.add(photoItem);
        }

        photosListView = (ListView) findViewById(R.id.photos_list);
        selected = new ArrayList<>();

        adapter = new PhotoAdapter(this, R.layout.item, photoItems.toArray(new PhotoItem[photoItems.size()]));
        photosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        photosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.toggleSelection(position);
                view.setBackgroundColor(adapter.getItem(position).isSelected() ? Color.RED : Color.TRANSPARENT);
            }
        });
        photosListView.setAdapter(adapter);
    }

    public void buildCollageClick(View view) {

        selected = adapter.getSelectedItems();
        if (selected.size() == 0) {
            Toast.makeText(this, "Выберите хотя бы одну фотографию!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(DEBUG_TAG, "Всего выбрано " + selected.size());

        Bitmap collage = createCollageFromImages(selected);
        String pathToCollage = saveToInternalStorage(collage);
        Intent intent = new Intent(this, CollageSenderActivity.class);
        intent.putExtra("collage_path", pathToCollage);
        startActivity(intent);
    }

    private Bitmap createCollageFromImages(List<PhotoItem> selected) {
        Bitmap bitmap = null;

        int photoCount = selected.size();

        int width = ((BitmapDrawable) selected.get(0).photo.getDrawable()).getBitmap().getWidth();
        int height = ((BitmapDrawable) selected.get(0).photo.getDrawable()).getBitmap().getHeight();


        //TODO: at this moment it will work for even count of photos only
        if (photoCount % 2 == 0) {

            final int COLUMNS = 2;
            final int ROWS = photoCount / COLUMNS;

            try {
                bitmap = Bitmap.createBitmap(width * COLUMNS, height * ROWS, Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);
                int count = 0;
                Bitmap currentBitmap;
                for (int rows = 0; rows < ROWS; rows++) {
                    for (int cols = 0; cols < COLUMNS; cols++) {
                        currentBitmap = ((BitmapDrawable) selected.get(count).photo.getDrawable()).getBitmap();
                        canvas.drawBitmap(currentBitmap, width * cols, height * rows, null);
                        count++;
                    }
                }
            } catch (Exception e) {
                Log.v(DEBUG_TAG, e.toString());
            }
        } else {
            Log.d(DEBUG_TAG, "Количество фотографий нечётное, нам ПИЗДА");

            photoCount--;
            int scaler = 2;
            final int ROWS = 2;
            final int COLUMNS = photoCount / ROWS;

            try {
                Bitmap rightBitmap = Bitmap.createBitmap(width * COLUMNS, height * ROWS, Bitmap.Config.ARGB_8888);

                //save first bmp for after adding
                Bitmap firstBitmap = ((BitmapDrawable) selected.get(0).photo.getDrawable()).getBitmap();
                Canvas canvas = new Canvas(rightBitmap);
                int count = 1;
                Bitmap currentBitmap;
                for (int rows = 0; rows < ROWS; rows++) {
                    for (int cols = 0; cols < COLUMNS; cols++) {
                        currentBitmap = ((BitmapDrawable) selected.get(count).photo.getDrawable()).getBitmap();
                        canvas.drawBitmap(currentBitmap, width * cols, height * rows, null);
                        count++;
                    }
                }

                Bitmap leftBitmap = Bitmap.createScaledBitmap(firstBitmap, width * scaler, height * scaler, true);
                bitmap = Bitmap.createBitmap(leftBitmap.getWidth() + rightBitmap.getWidth(),
                        leftBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);

                c.drawBitmap(leftBitmap, 0, 0, null);
                c.drawBitmap(rightBitmap, leftBitmap.getWidth(), 0, null);

            } catch (Exception e) {
                Log.v(DEBUG_TAG, e.toString());
            }


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
