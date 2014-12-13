package com.example.android.collageme;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by l3m4rk on 13.12.14.
 */
public class ImageViewList extends ArrayAdapter<ImageView> {

    private final Activity context;
    private final ImageView[] imageViews;



    public ImageViewList(Activity context, int resource, ImageView[] imageViews) {
        super(context, resource, imageViews);
        this.context = context;
        this.imageViews = imageViews;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_image);
        imageView.setImageDrawable(imageViews[position].getDrawable());
        return rowView;
    }
}
