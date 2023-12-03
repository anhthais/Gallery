package com.example.gallery;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.gallery.Database.DatabaseHelper;
import com.example.gallery.fragment.AlbumFragment;
import com.example.gallery.fragment.FavouriteImageFragment;
import com.example.gallery.fragment.GalleryFragment;
import com.example.gallery.fragment.ImageFragment;
import com.example.gallery.fragment.TrashFragment;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.helper.LocalStorageReader;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.example.gallery.object.ImageGroup;
import com.example.gallery.object.Statistic;
import com.example.gallery.object.TrashItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MainCallBacks,MainCallBackObjectData {
    private static final int PERMISSION_REQUEST_READ_CODE = 1;
    // TODO: init data used in all fragments in MainActivity
    public ArrayList<Image> allImages;
    public ArrayList<ImageGroup> imageGroupsByDate;
    public Map<Long, Image> allImagesInMap;
    public ArrayList<TrashItem> trashItems;
    public boolean isResetView = false;
    FragmentTransaction ft;
    Menu menu;
    GalleryFragment gallery_fragment = null;
    AlbumFragment album_fragment = null;
    TrashFragment trashFragment = null;
    ImageFragment imageFragment = null;
    FavouriteImageFragment favouriteImageFragment = null;

    BottomNavigationView btnv;
    ActionBar action_bar;
    ArrayList<Statistic> statisticListImage;
    public ArrayList<Album> album_list;
    String onChooseAlbum = "";
    public ArrayList<Album> getAlbum_list(){
        return album_list;
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
            Log.d("CheckImg","check");

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_READ_CODE);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(isResetView){
            allImages = LocalStorageReader.getImagesFromLocal(getApplicationContext());
            imageGroupsByDate = LocalStorageReader.getListImageGroupByDate(allImages);
            allImagesInMap = new HashMap<>();
            for(int i = 0; i < allImages.size(); ++i){
                allImagesInMap.put(allImages.get(i).getIdInMediaStore(), allImages.get(i));
            }
            loadAllAlbum();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        this.getContentResolver().unregisterContentObserver(observer);
    }

    private void initApp(){
        // TODO: init first data --> all fragments in this activity retrieve data directly from properties in the activity
        allImages = LocalStorageReader.getImagesFromLocal(getApplicationContext());
        imageGroupsByDate = LocalStorageReader.getListImageGroupByDate(allImages);
        allImagesInMap = new HashMap<>();
        for(int i = 0; i < allImages.size(); ++i){
            allImagesInMap.put(allImages.get(i).getIdInMediaStore(), allImages.get(i));
        }
        // TODO: Load SharedPref
        // 1. GALLERY:
        // 1.1 FAVORITE: idInMediaStore of favorite images
        // 1.2 ALBUM ...
        // 2. TRASH: <newPath, data>
        // - newPath: path of image after being moved to app specific external storage
        // - data: <oldPath, dateExpires> contains previous path of the image and time when the image will be deleted permanently (in long)
        loadDeleteImage(); // delete expired images when loading images
        loadFavouriteImage();
        loadAllAlbum();

        menu.findItem(R.id.btnRenameAlbum).setVisible(false);
        menu.findItem(R.id.btnDeleteAlbum).setVisible(false);
        menu.findItem(R.id.btnSlideShow).setVisible(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        this.favouriteImageFragment = FavouriteImageFragment.getInstance();
        this.gallery_fragment = GalleryFragment.getInstance();
        this.trashFragment = TrashFragment.getInstance();
        this.album_fragment = AlbumFragment.getInstance();
        ft.replace(R.id.mainFragment, gallery_fragment); ft.commit();
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
                        }else if (id == R.id.btnTrashbin)
                        {
                            menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
                            menu.findItem(R.id.btnChooseMulti).setVisible(true);
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.mainFragment,trashFragment);
                            ft.addToBackStack("FRAG");
                            ft.commit();
                        }else if (id == R.id.btnFavouriteImg)
                        {
                            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.mainFragment, favouriteImageFragment);
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
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if(frag instanceof GalleryFragment ||
                    frag instanceof FavouriteImageFragment ||
                    frag instanceof TrashFragment ){
                ((MultiSelectModeCallbacks) frag).changeOnMultiChooseMode();
            }
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
        else if(id==R.id.btnSlideShow){
            imageFragment.beginSlideShow();
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
            ArrayList<Image> images = new ArrayList<Image>();
            //
            menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
            menu.findItem(R.id.btnRenameAlbum).setVisible(true);
            menu.findItem(R.id.btnDeleteAlbum).setVisible(true);
            menu.findItem(R.id.btnSlideShow).setVisible(true);
            //2nd argument is album
            int index=0;
            for(int i=0;i<album_list.size();i++){
                if(album_list.get(i).getName().equals(strValue)){
                    index=i;
                    break;
                }
            }
            ImageFragment imageFragment = new ImageFragment(this, album_list.get(index));
            this.imageFragment = imageFragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, imageFragment);
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
                Gson gson = new Gson();

                // TODO: handle favorite images
                String addFavorite = data.getStringExtra("addFav");
                String removeFavorite = data.getStringExtra("removeFav");
                ArrayList<Long> addFavId = gson.fromJson(addFavorite, new TypeToken<ArrayList<Long>>() {
                }.getType());
                ArrayList<Long> removeFavId = gson.fromJson(removeFavorite, new TypeToken<ArrayList<Long>>() {
                }.getType());
                if (addFavId != null) {
                    for (int i = 0; i < addFavId.size(); ++i) {
                        Image image = allImagesInMap.get(addFavId.get(i));
                        image.setFavorite(true);
                    }
                }
                if (removeFavId != null) {
                    for (int i = 0; i < removeFavId.size(); ++i) {
                        Image image = allImagesInMap.get(removeFavId.get(i));
                        image.setFavorite(false);
                    }
                }
                saveFavoriteImages();

                // TODO: handle delete images
                String addDelete = data.getStringExtra("addDelete");
                String addDeleteTime = data.getStringExtra("addDeleteTime");
                String addDeleteNewPath = data.getStringExtra("addDeleteNewPath");
                ArrayList<Long> addDeletePos = gson.fromJson(addDelete, new TypeToken<ArrayList<Long>>(){}.getType());
                ArrayList<Long> addDeletePosTime = gson.fromJson(addDeleteTime, new TypeToken<ArrayList<Long>>(){}.getType());
                ArrayList<String> addDeletePosNewPath = gson.fromJson(addDeleteNewPath, new TypeToken<ArrayList<String>>(){}.getType());
                if(addDeletePos != null && addDeletePosTime != null && addDeletePosNewPath != null){
                    for(int i = 0; i < addDeletePos.size(); ++i){
                        Image image = allImagesInMap.get(addDeletePos.get(i));
                        trashItems.add(new TrashItem(addDeletePosNewPath.get(i), image.getPath(), DateConverter.plusMinutes(new Date(addDeletePosTime.get(i)), 10).getTime()));
                    }
                    isResetView = true;
                }

                // TODO: handle album
                for(int i=0; i<album_list.size(); i++){
                    //lấy danh sách ảnh được thêm vào album
                    String add_paths = data.getStringExtra(album_list.get(i).getName());
                    if(add_paths!=null && !add_paths.isEmpty()){
                        ArrayList<Long> imageIds = gson.fromJson(add_paths,new TypeToken<ArrayList<Long>>(){}.getType());
                        for(int j=0; j<imageIds.size(); j++){
                            //tìm ảnh cùng path và thêm vào album
                            Image image = allImagesInMap.get(imageIds.get(j));
                            if(image != null){
                                album_list.get(i).addImageToAlbum(image);
                            }
                        }
                    }
                    saveChangeToAlbum(album_list.get(i));
                }
            }
            catch (Exception e){
                Log.d("onActivityResult() MainActivity", e.getMessage());
            }
        }
        else if(requestCode == 2233 && resultCode==AppCompatActivity.RESULT_OK){
            Gson gson = new Gson();
            String deletePathJson = data.getStringExtra("deletePath");
            ArrayList<String> deletePath = gson.fromJson(deletePathJson, new TypeToken<ArrayList<String>>(){}.getType());
            if(deletePath != null){
                for(int i = 0; i < deletePath.size(); ++i){
                    for(int j = 0; j < trashItems.size(); ++j){
                        if(trashItems.get(j).getPath().equals(deletePath.get(i))){
                            trashItems.remove(j);
                        }
                    }
                }
                isResetView = true;
            }
        }
    }
    // receive statisticListImage fragment GalleryFragment
    @Override
    public void onObjectPassed(ArrayList<Statistic> statisticList) {
        statisticListImage = statisticList;
    }
    public void loadAllAlbum(){
        album_list = new ArrayList<Album>();
        Gson gson = new Gson();
        SharedPreferences albumPref = getSharedPreferences("GALLERY",Activity.MODE_PRIVATE);
        String album_name = albumPref.getString("ALBUM",null);
        if(album_name != null && !album_name.isEmpty()){
            ArrayList<String> albums = gson.fromJson(album_name,new TypeToken<ArrayList<String>>(){}.getType());
            for(int i=0;i<albums.size();i++){
                Album a = new Album(albums.get(i));
                String album_image = albumPref.getString(albums.get(i),null);
                ArrayList<Long> image_id = gson.fromJson(album_image,new TypeToken<ArrayList<Long>>(){}.getType());
                if(image_id != null){
                    for(int j = 0; j < image_id.size(); j++){
                        if(allImagesInMap.get(image_id.get(j)) != null){
                            a.addImageToAlbum(allImagesInMap.get(image_id.get(j)));
                        }
                    }
                }
                album_list.add(a);
            }
        }
    }

    public void saveChangeToAlbum(Album album){
        Gson gson=new Gson();
        SharedPreferences albumPref= getSharedPreferences("GALLERY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=albumPref.edit();
        ArrayList<Long> album_save=new ArrayList<>();
        for(int i=0;i<album.getAll_album_pictures().size();i++){
            album_save.add(album.getAll_album_pictures().get(i).getIdInMediaStore());
        }
        String albumjson=gson.toJson(album_save);
        editor.putString(album.getName(),albumjson);
        editor.commit();
    }

    public void loadDeleteImage(){
        trashItems = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences myPref = getSharedPreferences("TRASH", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        Map<String, ?> allEntries = myPref.getAll();
        if(allEntries != null){
            for (Map.Entry<String, ?> entry : allEntries.entrySet ()) {
                String key = entry.getKey();
                String value = (String) entry.getValue();
                ArrayList<String> dataFromJson = gson.fromJson(value, new TypeToken<ArrayList<String>>(){}.getType());

                if(DateConverter.stringToDate(dataFromJson.get(1)).getTime() <= (new Date()).getTime()){
                    editor.remove(key);
                    File f = new File(key);
                    if(f.delete()){
                        Log.d("delete expired image", "juan e nhe: " + key);
                    } else {
                        Log.d("delete expired image", "kho e a: " + key);
                    }
                }
                else {
                    TrashItem trashItem = new TrashItem(key, dataFromJson.get(0), DateConverter.stringToDate(dataFromJson.get(1)).getTime());
                    trashItems.add(trashItem);
                }
            }
        }
        editor.apply();
    }

    public void saveFavoriteImages(){
        Gson gson = new Gson();
        SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();

        ArrayList<Long> favList = new ArrayList<>();
        for(int i = 0; i < allImages.size(); ++i){
            if(allImages.get(i).isFavorite()){
                favList.add(Long.valueOf(allImages.get(i).getIdInMediaStore()));
            }
        }
        editor.putString("FAVORITE", gson.toJson(favList));
        editor.apply();
    }
    public void loadFavouriteImage() {
        Gson gson = new Gson();
        SharedPreferences myPref = getSharedPreferences("GALLERY",Activity.MODE_PRIVATE);
        String favImgIds = myPref.getString("FAVORITE", null);
        ArrayList<Long> id = gson.fromJson(favImgIds, new TypeToken<ArrayList<Long>>(){}.getType());

        if(id != null){
            for (int i = 0 ; i < id.size(); i++){
                if(allImagesInMap.get(id.get(i)) != null){
                    allImagesInMap.get(id.get(i)).setFavorite(true);
                }
            }
        }

    }

}
