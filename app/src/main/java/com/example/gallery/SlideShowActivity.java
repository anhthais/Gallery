package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.fragment.ImageViewFragment;
import com.example.gallery.fragment.SlideShowFragment;
import com.example.gallery.object.Image;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SlideShowActivity extends AppCompatActivity {
    private ArrayList<Image> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: display cutouts [backlog]
        setContentView(R.layout.activity_image);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        images = intent.getParcelableArrayListExtra("images");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideShowFragment imageViewFragment = new SlideShowFragment(SlideShowActivity.this, images);
        ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();

    }
}
