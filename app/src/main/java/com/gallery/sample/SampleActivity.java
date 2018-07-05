package com.gallery.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.customgallery.galleryapp.MainActivity;

public class SampleActivity extends AppCompatActivity {
    public final int REQ_CODE = 101;
    private ImageView ivSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ivSample = findViewById(R.id.iv_sample);
        MainActivity.openGallery(this, REQ_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            String imagePath = data.getStringExtra(MainActivity.IMAGE_KEY);
            ivSample.setImageURI(Uri.parse(imagePath));


        }
    }
}
