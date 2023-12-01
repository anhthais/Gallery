package com.example.gallery;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.gallery.adapter.ImageViewPagerAdapter;
import com.example.gallery.object.Image;
import com.example.gallery.object.TrashItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class TrashActivity extends AppCompatActivity {
    private ArrayList<TrashItem> trashItems;
    private ArrayList<Image> trashItemFakeImages;
    private int curPos = 0;
    private ViewPager2 imageViewPager2;
    private ImageViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView btnv;
    private Toolbar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        imageViewPager2 = findViewById(R.id.view_pager_trash);
        btnv = findViewById(R.id.navigation_bar_picture_trash);
        topBar = findViewById(R.id.topAppBar_trash);

        Intent intent = getIntent();
        trashItems = intent.getParcelableArrayListExtra("trashItems");
        curPos = intent.getIntExtra("curPos", 0);
        trashItemFakeImages = new ArrayList<>();
        for(int i = 0; i < trashItems.size(); ++i){
            trashItemFakeImages.add(new Image(trashItems.get(i).getPath()));
        }

        viewPagerAdapter = new ImageViewPagerAdapter(getBaseContext(), trashItemFakeImages);
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnv.setOnNavigationItemReselectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.btnRestore){

            }
            else if(id == R.id.btnDeletePermanently){

            }
        });

        imageViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                String[] fileDirs = trashItemFakeImages.get(position).getPath().split("/");
                String name = fileDirs[fileDirs.length - 1];
                topBar.setTitle(name);
            }
        });

    }
}