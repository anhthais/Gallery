package com.example.gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.gallery.fragment.ImageViewFragment;
import com.example.gallery.object.Image;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity implements MainCallBacks{
    private ArrayList<Image> images;
    private ArrayList<String> album_names;
    private int curPos;
    BottomNavigationView bottomNavigation;
    Intent intent_image;
    Uri uri;
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
        //bottomNavigation=findViewByIzd(R.id.navigation_bar_picture);
        //handleBottomNavigation();
        //tool_bar=findViewById(R.id.topAppBar);
        //handleToolBar();
    }


    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
             if(sender == "EDIT-PHOTO"){
                 Toast.makeText(this,"Edit Feature Pos: "+strValue,Toast.LENGTH_SHORT).show();

                 try {
                     uri = Uri.parse(images.get(Integer.valueOf(strValue)).getPath().toString());


                     if (uri!=null) {
                         Intent intent = new Intent(getApplicationContext(), EditPhotoActivity.class);
                         intent.putExtra("uri", uri.toString());
                         startActivity(intent);
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
    }
}