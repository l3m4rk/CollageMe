package com.example.android.collageme;

import android.widget.ImageView;

/**
 * Created by l3m4rk on 14.12.14.
 */
public class PhotoItem {

    public ImageView photo;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
