package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.gallery.Database.DatabaseHelper;
import com.example.gallery.fragment.GalleryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_CODE = 1;
    BottomNavigationView btnv;
    DatabaseHelper galleryDB;
    ArrayList<String> image_id, image_name, image_address, album_name, image_status, image_timeRemaining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set widget view
        btnv=findViewById(R.id.navigationBar);
        btnv.setOnNavigationItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.btnGallery){

            }
            else if (R.id.btnAlbum==id){

            }
            else if (R.id.btnSettings==id){
                View v=findViewById(R.id.btnSettings);
                PopupMenu pm = new PopupMenu(this, v);
                pm.getMenuInflater().inflate(R.menu.settings_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                }); pm.show();
            }
            return true;
        });

        // request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_READ_CODE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_READ_CODE);
        }


        // init DB
//        galleryDB = new DatabaseHelper(MainActivity.this);
//
//        image_id = new ArrayList<>();
//        image_name = new ArrayList<>();
//        image_address = new ArrayList<>();
//        album_name = new ArrayList<>();
//        image_status = new ArrayList<>();
//        image_timeRemaining = new ArrayList<>();
//        storeDataInArrays();

    }

    private void initApp(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        GalleryFragment galleryFragment = new GalleryFragment(MainActivity.this);
        ft.replace(R.id.mainFragment, galleryFragment); ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the contacts-related task you need to do.
                // => transacting fragments here.
                Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
                initApp();
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    void storeDataInArrays(){
        Cursor cursor = galleryDB.readAllData();

        if(cursor.getCount() ==0 ){
            Toast.makeText(this, "No data",Toast.LENGTH_SHORT).show();
        }
        else{
            while(cursor.moveToNext()){
                image_id.add(cursor.getString(0));
                image_name.add(cursor.getString(1));
                image_address.add(cursor.getString(2));
                album_name.add(cursor.getString(3));
                image_status.add(cursor.getString(4));
                image_timeRemaining.add(cursor.getString(5));

            }
        }
    }


}