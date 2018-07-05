package com.customgallery.galleryapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ivImage = findViewById(R.id.iv_image);

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
