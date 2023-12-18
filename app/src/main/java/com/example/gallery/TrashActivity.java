package com.example.gallery;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gallery.adapter.ImageViewPagerAdapter;
import com.example.gallery.helper.FileManager;
import com.example.gallery.helper.StorageUtil;
import com.example.gallery.object.Image;
import com.example.gallery.object.TrashItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TrashActivity extends AppCompatActivity implements ToolbarCallbacks{
    private ArrayList<TrashItem> trashItems;
    private ArrayList<Image> trashItemFakeImages;
    private int curPos = 0;
    private ViewPager2 imageViewPager2;
    private ImageViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView btnv;
    private Toolbar topBar;
    private ArrayList<String> deletePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        getSupportActionBar().hide();
        imageViewPager2 = findViewById(R.id.view_pager_trash);
        btnv = findViewById(R.id.navigation_bar_picture_trash);
        topBar = findViewById(R.id.topAppBar_trash);

        Intent intent = getIntent();
        trashItems = intent.getParcelableArrayListExtra("trashItems");
        curPos = intent.getIntExtra("curPos", 0);
        trashItemFakeImages = new ArrayList<>();
        for (int i = 0; i < trashItems.size(); ++i) {
            trashItemFakeImages.add(new Image(trashItems.get(i).getPath()));
        }
        deletePath = new ArrayList<>();

        viewPagerAdapter = new ImageViewPagerAdapter(this, trashItemFakeImages);
        imageViewPager2.setAdapter(viewPagerAdapter);
        imageViewPager2.setCurrentItem(curPos, false);
        imageViewPager2.setPageTransformer(new MarginPageTransformer(Math.round(32 * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT))));

        viewPagerAdapter.setToolbarCallbacks(this);
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnv.setOnNavigationItemReselectedListener(item -> {
            int id = item.getItemId();
            Gson gson = new Gson();
            if (id == R.id.btnRestore) {
                curPos = imageViewPager2.getCurrentItem();
                TrashItem trashItem = trashItems.get(curPos);
                File from = new File(trashItem.getPath());
                File to = new File(trashItem.getPrevPath());
                if(!from.exists()) return;
                try{
                    StorageUtil.exportFile(from, to);
                    from.delete();
                    SharedPreferences myPref = getSharedPreferences("TRASH", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPref.edit();
                    editor.remove(trashItem.getPath());
                    editor.apply();
                    trashItems.remove(curPos);
                    trashItemFakeImages.remove(curPos);
                    viewPagerAdapter.notifyItemRemoved(curPos);
                    intent.putParcelableArrayListExtra("trashItems", trashItems);
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[] { trashItem.getPrevPath() }, null,null);
                    Toast.makeText(this, R.string.restore_success, Toast.LENGTH_SHORT).show();
                    if(trashItemFakeImages.size() == 0){
                        finish();
                    }
                } catch (Exception e){
                    Toast.makeText(this, R.string.cannot_restore, Toast.LENGTH_SHORT).show();
                }

            } else if (id == R.id.btnDeletePermanently) {
                curPos = imageViewPager2.getCurrentItem();
                TrashItem trashItem = trashItems.get(curPos);
                File f = new File(trashItem.getPath());
                if(f.delete()){
                    SharedPreferences myPref = getSharedPreferences("TRASH", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPref.edit();
                    editor.remove(trashItem.getPath());
                    editor.apply();
                    trashItems.remove(curPos);
                    trashItemFakeImages.remove(curPos);
                    viewPagerAdapter.notifyItemRemoved(curPos);
                    intent.putParcelableArrayListExtra("trashItems", trashItems);
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    Toast.makeText(this, R.string.permannently_delete, Toast.LENGTH_SHORT).show();
                    if(trashItemFakeImages.size() == 0){
                        finish();
                    }
                } else {
                    Toast.makeText(this, R.string.permanently_not_delete, Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void showOrHideToolbars(boolean show) {
        if (show) {
            topBar.setVisibility(View.VISIBLE);
            btnv.setVisibility(View.VISIBLE);
        } else {
            topBar.setVisibility(View.INVISIBLE);
            btnv.setVisibility(View.INVISIBLE);
        }
    }
}