package com.example.gallery;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.example.gallery.fragment.ImageViewFragment;
import com.example.gallery.object.Image;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    private ArrayList<Image> images;
    private int curPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: display cutouts [backlog]
        setContentView(R.layout.activity_image);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        images = intent.getParcelableArrayListExtra("images");
        curPos = intent.getIntExtra("curPos", 0);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ImageViewFragment imageViewFragment = new ImageViewFragment(getApplicationContext(), images, curPos);
        ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();

    }
}