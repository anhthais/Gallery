package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.gallery.fragment.AlbumFragment;
import com.example.gallery.fragment.FavouriteImageFragment;
import com.example.gallery.fragment.GalleryFragment;
import com.example.gallery.fragment.ImageFragment;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.example.gallery.object.Statistic;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainCallBacks,MainCallBackObjectData {

    private static final int PERMISSION_REQUEST_READ_CODE = 1;

    FragmentTransaction ft;
    Menu menu;
    Album Trash;
    Album Favorite;
    GalleryFragment gallery_fragment=null;
    AlbumFragment album_fragment=null;
    ImageFragment imageFragment=null;
    BottomNavigationView btnv;
    ActionBar action_bar;
    ArrayList<Statistic> statisticListImage;
    ArrayList<Album> album_list;
    ArrayList<Image> favourite_img_list;
    FavouriteImageFragment favouriteImageFragment;
    String onChooseAlbum = "";
    public ArrayList<Album> getAlbum_list(){
        return album_list;
    }
    public BottomNavigationView getNavigationBar(){
        return this.btnv;
    }

    public ArrayList<Image> getFavourite_img_list() {
        return favourite_img_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set widget view
        action_bar=getSupportActionBar();
        action_bar.setDisplayShowHomeEnabled(true);
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
        menu.findItem(R.id.btnRenameAlbum).setVisible(false);
        menu.findItem(R.id.btnDeleteAlbum).setVisible(false);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        GalleryFragment galleryFragment = GalleryFragment.getInstance();
        this.gallery_fragment=galleryFragment;
        ft.replace(R.id.mainFragment, galleryFragment); ft.commit();

        loadAllAlbum();
        AlbumFragment album = AlbumFragment.getInstance();
        album_fragment = album;
        this.favourite_img_list = new ArrayList<Image>();

        FavouriteImageFragment favourite_image_fragment= FavouriteImageFragment.getInstance();
        this.favouriteImageFragment = favourite_image_fragment;
        loadFavouriteImage();
        btnv=findViewById(R.id.navigationBar);
        btnv.setOnNavigationItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.btnGallery){
                menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
                menu.findItem(R.id.btnChooseMulti).setVisible(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,this.gallery_fragment).commit();
            }
            else if (R.id.btnAlbum==id){
                menu.findItem(R.id.btnAddNewAlbum).setVisible(true);
                menu.findItem(R.id.btnChooseMulti).setVisible(false);

                getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,this.album_fragment).commit();
            }
            else if (R.id.btnSettings==id){
                View v=findViewById(R.id.btnSettings);
                PopupMenu pm = new PopupMenu(this, v);
                pm.getMenuInflater().inflate(R.menu.settings_menu, pm.getMenu());

                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id=item.getItemId();
                        if(id==R.id.btnThemeDark){
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }else if (id==R.id.btnThemeLight){
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }else if (id == R.id.btnFavouriteImg)
                        {

                            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.mainFragment,favourite_image_fragment);
                            ft.addToBackStack("FRAG");
                            ft.commit();
                        }
                        return true;
                    }
                }); pm.show();
            }
            return true;
        });
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
    public Menu getMenu(){
        return menu;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if (id ==R.id.btnChooseMulti)
        {
            gallery_fragment.changeOnMultiChooseMode();
        }
        else if(id==R.id.btnAddNewAlbum){
            album_fragment.addNewAlbum();
        }else if(id==R.id.btnAddImage){

        }
        // choose Statistic in ItemSelected
        else if(id == R.id.btnStatistic) {

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
            for (int i = 0; i < statisticListImage.size(); i++) {
                String temp = "Date: " + statisticListImage.get(i).getId() + " " + statisticListImage.get(i).getCount().toString() + " ảnh " + "Dung lượng : " + statisticListImage.get(i).getWeight().toString();
                arrayAdapter.add(temp);
            }

            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog alert=builder.create();
            alert.show();
        }
        else if(id==R.id.btnRenameAlbum){
            imageFragment.RenameAlbum();
        }
        else if (id == R.id.btnDeleteAlbum)
        {
            boolean checkDeleteAlbum = album_fragment.deleteAlbum(onChooseAlbum);
        }
        //special case: back-arrow on action bar
        else{
            getSupportFragmentManager().popBackStackImmediate();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if(sender.equals("ALBUM")){
            //strValue is Album's name

            //get all Images in Album
            ArrayList<Image> images=new ArrayList<Image>();
            //
            menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
            menu.findItem(R.id.btnRenameAlbum).setVisible(true);
            menu.findItem(R.id.btnDeleteAlbum).setVisible(true);
            //2nd argument is album
            int index=0;
            for(int i=0;i<album_list.size();i++){
                if(album_list.get(i).getName().equals(strValue)){
                    index=i;
                    break;
                }
            }
            ImageFragment imageFragment=new ImageFragment(this,album_list.get(index));
            this.imageFragment=imageFragment;
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment,imageFragment);
            ft.addToBackStack("ALBUM-FRAG");
            ft.commit();
            this.onChooseAlbum= strValue;
        }
        else if (sender.equals("DELETE-ALBUM"))
        {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1122 && resultCode==AppCompatActivity.RESULT_OK){
            try{
                //lấy danh sách ảnh bị xoá từ intent data
                String delete=data.getStringExtra("Trash");
                Gson gson=new Gson();
                //kiểm tra có danh sách ảnh bị xoá hay không
                if(delete!=null&&!delete.isEmpty()){
                    ArrayList<String> delete_paths=gson.fromJson(delete,new TypeToken<ArrayList<String>>(){}.getType());
                    //xoá các ảnh cần xoá
                    for(int i=0;i<delete_paths.size();i++){
                        gallery_fragment.deleteImage(delete_paths.get(i));
                        //xoá trong các album
                        for(int j=0;j<album_list.size();j++){
                            album_list.get(j).removeImageFromAlbum(delete_paths.get(i));
                            saveChangeToAlbum(album_list.get(i));
                        }
                    }
                }
                for(int i=0;i<album_list.size();i++){
                    //lấy danh sách ảnh được thêm vào album
                    String add_paths=data.getStringExtra(album_list.get(i).getName());
                    if(add_paths!=null&&!add_paths.isEmpty()){
                        ArrayList<String> paths=gson.fromJson(add_paths,new TypeToken<ArrayList<String>>(){}.getType());
                        for(int j=0;j<paths.size();j++){
                            //tìm ảnh cùng path và thêm vào album
                            Image image=gallery_fragment.findImageByPath(paths.get(i));
                            if(image!=null){
                                album_list.get(i).addImageToAlbum(image);
                                saveChangeToAlbum(album_list.get(i));
                            }
                        }
                    }
                }
                String addFav = data.getStringExtra("FAV-ADD");

                if(addFav!=null&&!addFav.isEmpty()) {

                    ArrayList<String> paths=gson.fromJson(addFav,new TypeToken<ArrayList<String>>(){}.getType());


                    for(int j=0;j<paths.size();j++){

                        Image image=gallery_fragment.findImageByPath(paths.get(j));
                        if(image!=null){
                            boolean check = false;
                            for (int i =0 ; i < favourite_img_list.size();i++)
                            {
                                if (image.getPath().equals(favourite_img_list.get(i).getPath())== true)
                                    check = true;
                            }
                            if (check == false)
                            {
                                favourite_img_list.add(image);
                            }


                        }
                    }
                    saveFavouriteImage();
                }
                String delFav = data.getStringExtra("FAV-DEL");

                if(delFav!=null&&!delFav.isEmpty()) {

                    ArrayList<String> paths=gson.fromJson(delFav,new TypeToken<ArrayList<String>>(){}.getType());
                    for(int j=0;j<paths.size();j++){

                        Image image=gallery_fragment.findImageByPath(paths.get(j));
                        if(image!=null){
                            Log.d("CheckImg3",paths.get(j));
                            for (int i = 0 ; i < favourite_img_list.size(); i++)
                            {
                                if (image.getPath().equals(favourite_img_list.get(i).getPath())==true)
                                {
                                    favourite_img_list.remove(i);
                                }
                            }
                            favouriteImageFragment.removeFavImage();
                        }
                    }
                    saveFavouriteImage();
                }
            }
            catch (Exception e){

            }
        }
    }
    // receive statisticListImage fragment GalleryFragment
    @Override
    public void onObjectPassed(ArrayList<Statistic> statisticList) {
        statisticListImage = statisticList;
    }
    public void loadAllAlbum(){
        album_list=new ArrayList<Album>();
        Gson gson=new Gson();
        SharedPreferences albumPref= getSharedPreferences("GALLERY",Activity.MODE_PRIVATE);
        String album_name=albumPref.getString("ALBUM",null);
        if(album_name==null || album_name.isEmpty()){
            return;
        }
        ArrayList<String> albums=gson.fromJson(album_name,new TypeToken<ArrayList<String>>(){}.getType());
        for(int i=0;i<albums.size();i++){
            Album a=new Album(albums.get(i));
            String album_image=albumPref.getString(albums.get(i),null);
            ArrayList<String> all_album_imagepath=gson.fromJson(album_image,new TypeToken<ArrayList<String>>(){}.getType());
            if(all_album_imagepath!=null){
                for(int j=0;j<all_album_imagepath.size();j++){
                    a.addImageToAlbum(new Image(all_album_imagepath.get(j)));
                }
            }
            album_list.add(a);
        }

    }
    public void saveChangeToAlbum(Album album){
        Gson gson=new Gson();
        SharedPreferences albumPref= getSharedPreferences("GALLERY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=albumPref.edit();
        ArrayList<String> album_save=new ArrayList<>();
        for(int i=0;i<album.getAll_album_pictures().size();i++){
            album_save.add(album.getAll_album_pictures().get(i).getPath());
        }
        String albumjson=gson.toJson(album_save);
        editor.putString(album.getName(),albumjson);
        editor.commit();

    }
    public void loadFavouriteImage()
    {
        //album_list=new ArrayList<Album>();
        favourite_img_list = new ArrayList<>();
        Gson gson=new Gson();
        SharedPreferences pref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
        String favourite_image = pref.getString("FavouriteImage", null);
        if(favourite_image==null || favourite_image.isEmpty()){
            return;
        }

        ArrayList<String> fav_img = gson.fromJson(favourite_image, new TypeToken<ArrayList<String>>(){}.getType());
        for (int i =0 ; i < fav_img.size(); i++)
        {
            favourite_img_list.add(new Image(fav_img.get(i)));
        }
    }
    public void saveFavouriteImage()
    {
        Gson gson=new Gson();
        SharedPreferences pref= getSharedPreferences("GALLERY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        ArrayList<String> list = new ArrayList<>();
        for (int i =0 ; i < favourite_img_list.size(); i++)
        {
            list.add(favourite_img_list.get(i).getPath());
        }
        String favJson = gson.toJson(list);
        editor.putString("FavouriteImage",favJson);
        editor.commit();
    }
}