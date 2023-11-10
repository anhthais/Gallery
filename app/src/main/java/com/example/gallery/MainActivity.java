package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.gallery.fragment.AlbumFragment;
import com.example.gallery.fragment.GalleryFragment;
import com.example.gallery.object.Album;
import com.example.gallery.object.Statistic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainCallBacks,MainCallBackObjectData {

    private static final int PERMISSION_REQUEST_READ_CODE = 1;

    FragmentTransaction ft;
    Menu menu;
    GalleryFragment gallery_fragment=null;
    AlbumFragment album_fragment=null;
    BottomNavigationView btnv;
    ActionBar action_bar;
    ArrayList<Statistic> statisticListImage;
    public BottomNavigationView getNavigationBar(){
        return this.btnv;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set widget view
        btnv=findViewById(R.id.navigationBar);
        btnv.setOnNavigationItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.btnGallery){
                menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
                if(this.gallery_fragment!=null){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.mainFragment, gallery_fragment); ft.commit();
                }else{
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    GalleryFragment galleryFragment = new GalleryFragment(MainActivity.this);
                    ft.replace(R.id.mainFragment, galleryFragment); ft.commit();
                }
            }
            else if (R.id.btnAlbum==id){
                menu.findItem(R.id.btnAddNewAlbum).setVisible(true);
                if(this.album_fragment!=null){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.mainFragment, album_fragment); ft.commit();
                }
                else {
                    ft = getSupportFragmentManager().beginTransaction();
                    ArrayList<Album> al = new ArrayList<Album>();
                    al.add(new Album("Album 1"));
                    al.add(new Album("Album 2"));
                    al.add(new Album("Album 3"));
                    al.add(new Album("Album 4"));
                    al.add(new Album("Album 5"));
                    AlbumFragment album = new AlbumFragment(this, al);
                    album_fragment = album;
                    ft.replace(R.id.mainFragment, album);
                    ft.commit();
                }

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
        this.gallery_fragment=galleryFragment;
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
        menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
        this.menu=menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.btnAddNewAlbum){
            album_fragment.addNewAlbum();
        }else if(id==R.id.btnAddImage){

        }
        // choose Statistic in ItemSelected
        else if(id == R.id.btnStatistic){

            // Build alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Statistic");
            // User titlebar_dialog layout to be title layout
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.titlebar_dialog, null);
            builder.setCustomTitle(view);

            // ArrayAdapter with custom_dialog_view layout
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_dialog_view);

            // Convert each element of statisticListImage to String and set it to arrayAdapter
            for(int i=0;i<statisticListImage.size();i++){
                String temp ="Date: "+ statisticListImage.get(i).getId()+" "+statisticListImage.get(i).getCount().toString()+ " ảnh "+ "Dung lượng : "+statisticListImage.get(i).getWeight().toString();
                arrayAdapter.add(temp);
            }

           builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {

               }
           });


            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {

    }

    // receive statisticListImage fragment GalleryFragment
    @Override
    public void onObjectPassed(ArrayList<Statistic> statisticList) {
        statisticListImage = statisticList;
    }
}