package com.example.gallery;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.gallery.fragment.ImageViewFragment;
import com.example.gallery.object.Image;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    private ArrayList<Image> images;
    private ArrayList<String> album_names;
    private int curPos;
    BottomNavigationView bottomNavigation;
    Intent intent_image;

    Toolbar tool_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: display cutouts [backlog]
        setContentView(R.layout.activity_image);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        intent_image=intent;
        images = intent.getParcelableArrayListExtra("images");
        curPos = intent.getIntExtra("curPos", 0);
        Gson gson=new Gson();
        String album_arr=intent.getStringExtra("ALBUM-LIST");
        album_names=gson.fromJson(album_arr, new TypeToken<ArrayList<String>>(){}.getType());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //getApplicationContext--> imageActivity.this
        ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images,album_names, curPos);
        ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();
        //bottomNavigation=findViewById(R.id.navigation_bar_picture);
        //handleBottomNavigation();
        //tool_bar=findViewById(R.id.topAppBar);
        //handleToolBar();
    }


}