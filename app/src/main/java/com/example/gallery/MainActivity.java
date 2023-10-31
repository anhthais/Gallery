package com.example.gallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.gallery.Database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FragmentTransaction ft;

    BottomNavigationView btnv;
    DatabaseHelper galleryDB;
    ArrayList<String> image_id, image_name, image_address, album_name, image_status, image_timeRemaining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnv=findViewById(R.id.navigationBar);
        btnv.setOnNavigationItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.btnGallery){

            }
            else if (R.id.btnAlbum==id){
                ft=getSupportFragmentManager().beginTransaction();
                ArrayList<Album> al=new ArrayList<Album>();
                al.add(new Album("Album 1"));
                al.add(new Album("Album 2"));
                al.add(new Album("Album 3"));
                al.add(new Album("Album 4"));
                al.add(new Album("Album 5"));
                AlbumFragment album=new AlbumFragment(this,al);
                ft.replace(R.id.mainFragment,album);
                ft.commit();
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

        galleryDB = new DatabaseHelper(MainActivity.this);

        image_id = new ArrayList<>();
        image_name = new ArrayList<>();
        image_address = new ArrayList<>();
        album_name = new ArrayList<>();
        image_status = new ArrayList<>();
        image_timeRemaining = new ArrayList<>();
        storeDataInArrays();

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