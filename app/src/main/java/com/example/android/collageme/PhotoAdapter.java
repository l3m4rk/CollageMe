package com.example.android.collageme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

    private View currentViewSelected;

    private final static String DEBUG_TAG = PhotoAdapter.class.getSimpleName();

    private final Activity context;
    private final PhotoItem[] photoItems;
    private int currentSelected;

    public PhotoAdapter(Activity context, int resource, PhotoItem[] photoItems) {
        super(context, resource, photoItems);
        this.context = context;
        this.photoItems = photoItems;
    }

    private int selectedPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.item, parent, false);

        }

        if (photoItems[position].isSelected()) {
            Log.d(DEBUG_TAG, "Item " + position + " selected!");
            view.setBackgroundColor(Color.RED);
        } else {
            Log.d(DEBUG_TAG, "Item " + position + " not selected!");
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        imageView.setImageDrawable(photoItems[position].photo.getDrawable());
        return view;
    }

    public List<PhotoItem> getSelectedItems() {
        ArrayList<PhotoItem> selectedPhotos = new ArrayList<>();
        for (PhotoItem photoItem : photoItems) {
            if (photoItem.isSelected())
                selectedPhotos.add(photoItem);
        }
        return selectedPhotos;
    }

    public void toggleSelection(int position) {
        photoItems[position].setSelected(!photoItems[position].isSelected());

        Log.d(this.getClass().getSimpleName(), "Item " + position + (!photoItems[position].isSelected() ? " deselect!" : " select!"));
    }

}
