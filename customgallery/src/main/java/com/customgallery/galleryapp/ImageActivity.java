package com.customgallery.galleryapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {
    @BindView(R.id.iv_image)
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            Uri image = (Uri) getIntent().getExtras().get("image");
            if (image != null)
                ivImage.setImageURI(image);
        }

        if (getIntent().getExtras() != null) {
            Uri image = (Uri) getIntent().getExtras().get("photo");
            if (image != null)
                ivImage.setImageURI(image);
        }
    }
}
