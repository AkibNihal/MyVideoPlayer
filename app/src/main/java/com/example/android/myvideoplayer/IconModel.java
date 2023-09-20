package com.example.android.myvideoplayer;

import android.widget.ImageView;

public class IconModel {
    private int ImageView;
    private String iconTitle;

    public IconModel(int imageView, String iconTitle) {
        ImageView = imageView;
        this.iconTitle = iconTitle;
    }

    public void setImageView(int imageView) {
        ImageView = imageView;
    }

    public void setIconTitle(String iconTitle) {
        this.iconTitle = iconTitle;
    }

    public int getImageView() {
        return ImageView;
    }

    public String getIconTitle() {
        return iconTitle;
    }
}
