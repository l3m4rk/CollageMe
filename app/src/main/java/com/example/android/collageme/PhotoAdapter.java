package com.example.android.collageme;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by l3m4rk on 13.12.14.
 */
public class PhotoAdapter extends ArrayAdapter<PhotoItem> {

    private final Activity context;
    private final PhotoItem[] photoItems;
    private List<PhotoItem> selectedPhotos;

    public PhotoAdapter(Activity context, int resource, PhotoItem[] photoItems) {
        super(context, resource, photoItems);
        this.context = context;
        this.photoItems = photoItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item, null);
            final PhotoItem photoItem = new PhotoItem();
            photoItem.view = (ImageView) view.findViewById(R.id.item_image);
            photoItem.checked = false;
            view.setTag(photoItem);

        } else {
            view = convertView;
        }
        PhotoItem photoItem = (PhotoItem) view.getTag();
        photoItem.view.setImageDrawable(photoItems[position].view.getDrawable());
        photoItem.checked = false;
        return view;
    }

    public List<PhotoItem> getSelectedItems() {
        selectedPhotos = new ArrayList<>();
        for (PhotoItem photoItem : photoItems) {
            if (photoItem.checked)
                selectedPhotos.add(photoItem);
        }
        return selectedPhotos;
    }

    public void toggleSelection(int position) {
        PhotoItem item = photoItems[position];
        if (item.checked) {
            item.checked = false;
            Log.d(this.getClass().getSimpleName(), "Item " + position + " deselect!");
        } else {
            item.checked = true;
            Log.d(this.getClass().getSimpleName(), "Item " + position + " select!");
        }
    }


}
