package com.umonsoft.tabis.HelperClasses;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageController {
    private Context context;
    private ImageView imageupload;

    public ImageController(Context context, ImageView imageupload) {
        this.context = context;
        this.imageupload = imageupload;
    }

    public void setImgMain(Uri path) {
        if(imageupload!=null)
        {
            Glide
                    .with(context)
                    .load(path)
                    .into(imageupload);
        }

    }
}