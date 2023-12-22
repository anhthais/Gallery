package com.example.gallery;

import static android.app.PendingIntent.getActivity;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.gallery.fragment.AddImageToAlbumFragment;
import com.example.gallery.fragment.AlbumFragment;
import com.example.gallery.fragment.FavouriteImageFragment;
import com.example.gallery.fragment.GalleryFragment;
import com.example.gallery.fragment.HideFragment;
import com.example.gallery.fragment.ImageFragment;
import com.example.gallery.fragment.SearchingFragment;
import com.example.gallery.fragment.SettingFragment;
import com.example.gallery.fragment.TrashFragment;
import com.example.gallery.helper.AddImageFromCamera;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.helper.FileManager;
import com.example.gallery.helper.ImageLoader;
import com.example.gallery.helper.LocalStorageReader;
import com.example.gallery.helper.Notification;
import com.example.gallery.helper.SortUtil;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.example.gallery.object.ImageGroup;
import com.example.gallery.object.Statistic;
import com.example.gallery.object.TrashItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.os.Handler;
import android.app.ProgressDialog;
import android.widget.ProgressBar;
import com.example.gallery.adapter.TrashAdapter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.SearchView;
import com.example.gallery.fragment.SearchingFragment;
import java.util.List;
import java.io.Serializable;
public class MainActivity extends AppCompatActivity implements MainCallBacks,MainCallBackObjectData, LoaderManager.LoaderCallbacks<ArrayList<Image>> {
    private static final int IMAGE_LOADER_ID = 1;
    private static final int PERMISSION_REQUEST_READ_CODE = 1;
    // TODO: init data used in all fragments in MainActivity
    public ArrayList<Image> allImages;
    public ArrayList<ImageGroup> imageGroupsByDate;
    public Map<Long, Image> allImagesInMap;
    public ArrayList<TrashItem> trashItems;
    Menu menu;
    Fragment currentFragment = null;

    GalleryFragment gallery_fragment = null;
    AlbumFragment album_fragment = null;
    TrashFragment trashFragment = null;
    ImageFragment imageFragment = null;
    SettingFragment settingFragment = null;
    FavouriteImageFragment favouriteImageFragment = null;
    HideFragment hideFragment = null;
    BottomNavigationView btnv;
    ActionBar action_bar;
    ArrayList<Statistic> statisticListImage;
    public ArrayList<Album> album_list;
    public int curIdxAlbum;
    private Handler handler;
    private ProgressDialog progressDialog;
    String onChooseAlbum = "";
    public boolean updateViewManually = false;
    public int sortOrder = SortUtil.TypeDESC;
    public ArrayList<Image> search_result_list = new ArrayList<>();
    SearchingFragment searchFragment = null;
    SearchView searchView = null ;
    public boolean isSearchBarEmpty = true;
    public ArrayList<Album> getAlbum_list(){
        return album_list;
    }
    public HideFragment getHideFragment() {
        return this.hideFragment;
    }
    public FavouriteImageFragment getFavouriteImageFragment() {
        return this.favouriteImageFragment;
    }
    public ArrayList<TrashItem> getTrash_list(){
        return this.trashItems;
    }
    public TrashFragment getTrashFragment() {
        return this.trashFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref=getSharedPreferences("GALLERY",MODE_PRIVATE) ;
        String theme=pref.getString("THEME","LIGHT");
        if(theme.equals("LIGHT")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else if(theme.equals("DARK")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        String locale=pref.getString("LANGUAGE","en");
        Locale myLocale = new Locale(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        // set widget view
        action_bar=getSupportActionBar();
        action_bar.setDisplayShowHomeEnabled(true);
        // fragments
        this.favouriteImageFragment = FavouriteImageFragment.getInstance();
        this.gallery_fragment = GalleryFragment.getInstance();
        this.trashFragment = TrashFragment.getInstance();
        this.album_fragment = AlbumFragment.getInstance();
        this.settingFragment = SettingFragment.getInstance();
        // request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_READ_CODE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_READ_CODE);
        }
    }

    @Override
    public Loader<ArrayList<Image>> onCreateLoader(int id, Bundle args) {
        return new ImageLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Image>> loader, ArrayList<Image> data) {
        if(data != allImages){
            new ImageLoaderTask(this).execute(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Image>> loader) {
    }

    private void initApp(){
        LoaderManager.getInstance(this).initLoader(IMAGE_LOADER_ID, null, this);
        loadDeleteImage();

        menu.findItem(R.id.btnDeleteAlbum).setVisible(false);
        menu.findItem(R.id.btnSlideShow).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, gallery_fragment); ft.commit(); // TODO: change to the latest fragment
        btnv=findViewById(R.id.navigationBar);
        btnv.setOnNavigationItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.btnGallery){
                menu.findItem(R.id.btnFind).setVisible(true);
                menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
                menu.findItem(R.id.btnChooseMulti).setVisible(true);
                menu.findItem(R.id.btnAI_Image).setVisible(true);
                menu.findItem(R.id.btnDeleteAlbum).setVisible(false);
                menu.findItem(R.id.btnSlideShow).setVisible(false);
                menu.findItem(R.id.btnEvent).setVisible(true);
                menu.findItem(R.id.btnSort).setVisible(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,this.gallery_fragment).commit();
            }
            else if (R.id.btnAlbum==id){
                menu.findItem(R.id.btnAddNewAlbum).setVisible(true);
                menu.findItem(R.id.btnChooseMulti).setVisible(false);
                menu.findItem(R.id.btnFind).setVisible(false);
                menu.findItem(R.id.btnAI_Image).setVisible(false);
                menu.findItem(R.id.btnDeleteAlbum).setVisible(false);
                menu.findItem(R.id.btnSlideShow).setVisible(false);
                menu.findItem(R.id.btnSort).setVisible(false);
                menu.findItem(R.id.btnEvent).setVisible(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,this.album_fragment).commit();
            }
            else if (R.id.btnSettings==id) {
                menu.findItem(R.id.btnFind).setVisible(false);
                menu.findItem(R.id.btnChooseMulti).setVisible(false);
                menu.findItem(R.id.btnSlideShow).setVisible(false);
                menu.findItem(R.id.btnAI_Image).setVisible(false);
                menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
                menu.findItem(R.id.btnDeleteAlbum).setVisible(false);
                menu.findItem(R.id.btnEvent).setVisible(false);
                menu.findItem(R.id.btnSort).setVisible(false);
                View v = findViewById(R.id.btnSettings);
        //        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,this.settingFragment).commit();
//                PopupMenu pm = new PopupMenu(this, v);
//                pm.getMenuInflater().inflate(R.menu.settings_menu, pm.getMenu());
//
//                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        int id=item.getItemId();
//                        if(id==R.id.btnEnglish){
//                            if(!getResources().getConfiguration().getLocales().get(0).toString().equals("en")){
//                                SharedPreferences pref=getSharedPreferences("GALLERY",MODE_PRIVATE);
//                                SharedPreferences.Editor editor=pref.edit();
//                                editor.putString("LANGUAGE","en");
//                                editor.commit();
//                                setLocale("en");
//                            }
//                        }
//                        else if(id==R.id.btnVietnamese){
//                            if(!getResources().getConfiguration().getLocales().get(0).toString().equals("vi")){
//                                SharedPreferences pref=getSharedPreferences("GALLERY",MODE_PRIVATE);
//                                SharedPreferences.Editor editor=pref.edit();
//                                editor.putString("LANGUAGE","vi");
//                                editor.commit();
//                                setLocale("vi");
//                            }
//                        }
//                        else if(id==R.id.btnThemeDark){
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                            SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = myPref.edit();
//                            editor.putString("THEME","DARK").apply();
//                        }else if (id==R.id.btnThemeLight){
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                            SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = myPref.edit();
//                            editor.putString("THEME","LIGHT").apply();
//                        }else if (id == R.id.btnTrashbin)
//                        {
//                            menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
//                            menu.findItem(R.id.btnChooseMulti).setVisible(true);
//                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                            ft.replace(R.id.mainFragment,trashFragment);
//                            ft.addToBackStack("FRAG");
//                            ft.commit();
//                        }else if (id == R.id.btnFavouriteImg)
//                        {
//                            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
//                            ft.replace(R.id.mainFragment, favouriteImageFragment);
//                            ft.addToBackStack("FRAG");
//                            ft.commit();
//                        }else if(id==R.id.btnShowHideFrag){
//                            SharedPreferences hidePref=getSharedPreferences("GALLERY",MODE_PRIVATE);
//                            String password=hidePref.getString("PASSWORD",null);
//                            if(password==null){
//                                //show add new album frag
//                                int opendate=hidePref.getInt("OPEN-DATE",0);
//                                int openyear=hidePref.getInt("OPEN-YEAR",0);
//                                int openmoth=hidePref.getInt("OPEN-MONTH",0);
//                                Calendar calendar=Calendar.getInstance();
//                                int curr_date=calendar.get(Calendar.DATE);
//                                int curr_month=calendar.get(Calendar.MONTH);
//                                int curr_year=calendar.get(Calendar.YEAR);
//                                boolean firstVisit=true;
//                                if(curr_year<openyear){
//                                    firstVisit=false;
//                                }else if(curr_year==openyear){
//                                    if(curr_month<openmoth){
//                                        firstVisit=false;
//                                    }else if(curr_month==openmoth){
//                                        if(curr_date<opendate){
//                                            firstVisit=false;
//                                        }
//                                    }
//                                }
//                                if(firstVisit){
//                                    createPasswordHideFragmentDialog();
//                                }else{
//                                    Toast.makeText(MainActivity.this, R.string.comebacklater, Toast.LENGTH_SHORT).show();
//                                }
//                            }else{
//                                showHideFragmentDialog();
//                            }
//                        }
//                        return true;
//                    }
//                }); pm.show();
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
                Toast.makeText(MainActivity.this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                initApp();
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu,menu);
        menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public Menu getMenu(){
        return menu;
    }
    public ArrayList<Image> getAllImages() {
        return allImages;
    }
    public String getName(Image img ) {
        File file = new File(img.getPath());
        return file.getName();
    }
    private void performSearch(String query) {
        this.search_result_list = filterArray(allImages, query);

        SearchingFragment searchingFragment = new SearchingFragment(this, search_result_list);
        this.searchFragment = searchingFragment;
        getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, searchFragment);
        ft.addToBackStack(null);
        ft.commit();
        if (search_result_list.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Results");
            builder.setMessage("Sorry, no results were found.");

            Button okButton = new Button(this);
            okButton.setText("OK");
            okButton.setTextColor(Color.GREEN);

            builder.setPositiveButton(null, null); // Null to remove the default button
            builder.setView(okButton);

            AlertDialog dialog = builder.create();
            dialog.show();
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });}
        Toast.makeText(this, "number of results " + search_result_list.size() , Toast.LENGTH_SHORT).show();

    }
    private ArrayList<Image> filterArray(List<Image> allImages, String query) {
        if (TextUtils.isEmpty(query)) {
            this.isSearchBarEmpty = true;
            return new ArrayList<>();
        }
        this.isSearchBarEmpty = false;
        ArrayList<Image> filteredList = new ArrayList<>();

        for (Image item : allImages) {
            if (getName(item).toLowerCase().contains(query.toLowerCase()) || (item.getTags() != null && item.getTags().toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    private void performSearchOnTextChange(String query) {
        this.search_result_list = filterArray(allImages, query);
        this.searchView.requestFocus();
        //  SearchingFragment searchingFragment = new SearchingFragment(this, search_result_list);
        // this.searchFragment = searchingFragment;

        this.searchFragment.updateData();
//        getSupportFragmentManager().popBackStackImmediate();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.mainFragment, searchFragment);
//        ft.addToBackStack(null);
//        ft.commit();
        this.searchView.requestFocus();
    }

    private void runRemoveDuplicateImages() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Removing Duplicate Images...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//       progressDialog.setMax(100);
//       progressDialog.setProgress(0);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setCancelable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                removeDuplicateImages();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();

        progressDialog.show();
    }

    private void removeDuplicateImages() {
        Map<String, Image> imageMap = new HashMap<>();
        Iterator<Image> iterator = allImages.iterator();

        int totalImages = allImages.size();
        int processedImages = 0;

        while (iterator.hasNext()) {
            Image image = iterator.next();
            try {
                String imageHash = calculateHash(new File(image.getPath()));

                // Check if the hash is already in the map
                if (imageMap.containsKey(imageHash)) {
                    iterator.remove();
                    // for moving file from source to trash
                    DeLeteAImage(image);
                } else {
                    // Add the hash to the map along with the image
                    imageMap.put(imageHash, image);
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            // Update progress
            processedImages++;
            final int progress = (int) ((processedImages / (float) totalImages) * 100);

            // Send progress update to the UI thread
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setProgress(progress);
                    progressDialog.setMessage("Removing Duplicate Images... " + progress + "%");
                }
            });
        }
    }
    private static String calculateHash(File file) throws IOException, NoSuchAlgorithmException {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return getBitmapHash(bitmap);
    }

    private static String getBitmapHash(Bitmap bitmap) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        // Convert the bitmap to a byte array
        byte[] byteArray;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error converting bitmap to byte array", e);
        }

        // Update the digest with the byte array
        md.update(byteArray);

        // Convert the byte array to a hexadecimal string
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if  (id ==R.id.btnFind)
        {
            MenuItem searchItem = menu.findItem(R.id.Search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setMaxWidth(Integer.MAX_VALUE);
            this.searchView = searchView;
        //    menu.findItem(R.id.btnAddImageAcTion).setVisible(false);
            menu.findItem(R.id.btnDeleteAlbum).setVisible(false);
       //     menu.findItem(R.id.btnStatistic).setVisible(false);
            menu.findItem(R.id.btnFind).setVisible(false);
            menu.findItem(R.id.Search).setVisible(true);
            searchView.onActionViewExpanded();

            searchView.setQueryHint("Search here");
            SearchingFragment searchingFragment = new SearchingFragment(this,search_result_list);

            this.searchFragment = searchingFragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, searchFragment);
            ft.addToBackStack(null);
            ft.commit();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //performSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    performSearchOnTextChange(newText);
                    searchView.requestFocus();
                    return true;

                }
            });
            searchView.setQuery("", false);
            return true;

        }

        else if (id ==R.id.btnDeleteDupicateImages)
        {
            runRemoveDuplicateImages();
            loadDeleteImage();
        }
        else
        if(id==R.id.btnEvent){
            SharedPreferences pref=getSharedPreferences("GALLERY",MODE_PRIVATE);
            String eventJSON=pref.getString(album_list.get(curIdxAlbum).getPath(),null);
            Gson gson=new Gson();
            ArrayList<String> data=null;
            if(eventJSON!=null&&!eventJSON.isEmpty()){
                data=gson.fromJson(eventJSON,new TypeToken<ArrayList<String>>(){}.getType());
            }
            String name="", descript="",date="";
            final String channel;
            if(data!=null){
                name=data.get(0);
                descript=data.get(1);
                date=data.get(2);
                channel=data.get(3);
            }else{
                channel="";
            }
            //show dialog
            Dialog addDialog=new Dialog(MainActivity.this);
            addDialog.setContentView(R.layout.create_event_dialog);
            EditText editText=addDialog.findViewById(R.id.eventNameEditText);
            EditText editText2=addDialog.findViewById(R.id.eventDescriptionEditText);
            EditText editText3=addDialog.findViewById(R.id.eventDateNotice);
            Button clear=addDialog.findViewById(R.id.btnEventClear);

            editText.setText(name);
            editText2.setText(descript);
            editText3.setText(date);
            AppCompatButton calen=addDialog.findViewById(R.id.btnChooseCalendar);
            int[] dateOfmoth={0,0,0};
            if(date!=null && !date.isEmpty()){
                String sub=date.substring(date.lastIndexOf("/")+1);
                dateOfmoth[2]=Integer.parseInt(sub);
                sub=date.substring(0,date.lastIndexOf("/"));

                dateOfmoth[1]=Integer.parseInt(sub.substring(sub.lastIndexOf("/")+1))-1;
                sub=sub.substring(0,sub.lastIndexOf("/"));

                dateOfmoth[0]=Integer.parseInt(sub);
            }
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setText("");
                    editText2.setText("");
                    editText3.setText("");
                }
            });
            calen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog date_picker=new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    month+=1;
                                    String date=dayOfMonth+"/"+String.format("%02d",month)+"/"+year;
                                    editText3.setText(date);
                                    dateOfmoth[0]=dayOfMonth;
                                    dateOfmoth[1]=month-1;
                                    dateOfmoth[2]=year;
                                }
                            },      Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    date_picker.show();
                }
            });
            Button ok=addDialog.findViewById(R.id.btnSaveEvent);
            Button cancel=addDialog.findViewById(R.id.btnEventCancel);
            addDialog.create();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            addDialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
            addDialog.show();

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor=pref.edit();
                    String name_save=editText.getText().toString();
                    String des_save=editText2.getText().toString();
                    String date_save=editText3.getText().toString();
                    ArrayList<String> saved=new ArrayList<>();
                    saved.add(name_save);
                    saved.add(des_save);
                    saved.add(date_save);
                    String channel_save=null;
                    if(channel==null||channel.isEmpty()){
                        channel_save=String.valueOf(System.currentTimeMillis());
                    }else{
                        channel_save=channel;
                    }
                    saved.add(channel_save);
                    editor.putString(album_list.get(curIdxAlbum).getPath(),gson.toJson(saved)).apply();
                    Toast.makeText(MainActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
                    if(editText3.getText().toString()!=null && !editText3.getText().toString().isEmpty()){
                        try{
                            NotificationManagerCompat manager=NotificationManagerCompat.from(MainActivity.this);
                            manager.cancel((int)Long.parseLong(channel_save));
                            manager.deleteNotificationChannel(channel_save);
                        }catch (Exception e){}
                        createNotificationChannel(channel_save);
                        Calendar calendar=Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH,dateOfmoth[0]);
                        calendar.set(Calendar.MONTH,dateOfmoth[1]);
                        calendar.set(Calendar.YEAR,dateOfmoth[2]);
                        calendar.set(Calendar.HOUR,0);
                        calendar.set(Calendar.MINUTE,0);
                        //for testing
                        //calendar=Calendar.getInstance();
                        //calendar.add(Calendar.SECOND,10);
                        if(calendar.compareTo(Calendar.getInstance())<=0){
                            Toast.makeText(MainActivity.this, R.string.invalid_time, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String title = getString(R.string.app_name) + " " + getString(R.string.event) + " " + name_save;
                            String mess = getString(R.string.visit_our_app) + ": " + des_save;
                            Toast.makeText(MainActivity.this, R.string.schedule, Toast.LENGTH_SHORT).show();
                            scheduleNotification(calendar, title, mess, channel_save);
                        }
                    }
                    else{
                        try{
                            NotificationManagerCompat manager=NotificationManagerCompat.from(MainActivity.this);
                            manager.cancel((int)Long.parseLong(channel_save));
                            manager.deleteNotificationChannel(channel_save);
                        }catch (Exception e){}
                    }
                    //addDialog.cancel();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDialog.cancel();
                }
            });

        }
        else if (id ==R.id.btnChooseMulti)
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
        }
        // choose Statistic in ItemSelected
        else if(id == R.id.btnAI_Image) {
            // change to TextToImageActivity
            Intent intent = new Intent(MainActivity.this, TextToImageActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.btnDeleteAlbum)
        {
            if(album_fragment.deleteAlbum(onChooseAlbum)){
                Toast.makeText(this, "Delete album successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cannot delete album", Toast.LENGTH_SHORT).show();
            }
        }
        else if(id==R.id.btnSlideShow){
            imageFragment.beginSlideShow();
        }
//        else if(id == R.id.btnSort){
//            View v = findViewById(R.id.btnSort);
//            PopupMenu pm = new PopupMenu(MainActivity.this, v);
//            pm.getMenuInflater().inflate(R.menu.multi_select_menu_gallery, pm.getMenu());
//            pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    int id1 = item.getItemId();
//                    if(id1 == R.id.btnSortByDateIncrease){
//                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
//                        if(frag instanceof GalleryFragment && frag.isVisible()){
//                            updateAllListsInSort(SortUtil.CriterionDateAdded, SortUtil.TypeASC);
//                            ((GalleryFragment) frag).updateView();
//                        }
//                        else if(frag instanceof ImageFragment && frag.isVisible()){
//                            updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionDateAdded, SortUtil.TypeASC);
//                            ((ImageFragment) frag).updateView();
//                        }
//                        Toast.makeText(getApplicationContext(), "sort date asc", Toast.LENGTH_SHORT).show();
//                    }
//                    else if(id1 == R.id.btnSortByDateDecrease){
//                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
//                        if(frag instanceof GalleryFragment && frag.isVisible()){
//                            updateAllListsInSort(SortUtil.CriterionDateAdded, SortUtil.TypeDESC);
//                            ((GalleryFragment) frag).updateView();
//                        }
//                        else if(frag instanceof ImageFragment && frag.isVisible()){
//                            updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionDateAdded, SortUtil.TypeDESC);
//                            ((ImageFragment) frag).updateView();
//                        }
//                        Toast.makeText(getApplicationContext(), "sort date desc", Toast.LENGTH_SHORT).show();
//                    }
//                    else if(id1 == R.id.btnSortByNameIncrease){
//                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
//                        if(frag instanceof GalleryFragment && frag.isVisible()){
//                            updateListByEachGroupInSort(SortUtil.CriterionName, SortUtil.TypeASC);
//                            ((GalleryFragment) frag).updateView();
//                        }
//                        else if(frag instanceof ImageFragment && frag.isVisible()){
//                            updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionName, SortUtil.TypeASC);
//                            ((ImageFragment) frag).updateView();
//                        }
//                        Toast.makeText(getApplicationContext(), "sort name asc", Toast.LENGTH_SHORT).show();
//                    }
//                    else if(id1 == R.id.btnSortByNameDecrease){
//                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
//                        if(frag instanceof GalleryFragment && frag.isVisible()){
//                            updateListByEachGroupInSort(SortUtil.CriterionName, SortUtil.TypeDESC);
//                            ((GalleryFragment) frag).updateView();
//                        }
//                        else if(frag instanceof ImageFragment && frag.isVisible()){
//                            updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionName, SortUtil.TypeDESC);
//                            ((ImageFragment) frag).updateView();
//                        }
//                        Toast.makeText(getApplicationContext(), "sort name desc", Toast.LENGTH_SHORT).show();
//                    }
//                    else if(id1 == R.id.btnSortByFileSizeIncrease){
//                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
//                        if(frag instanceof GalleryFragment && frag.isVisible()){
//                            updateListByEachGroupInSort(SortUtil.CriterionFileSize, SortUtil.TypeASC);
//                            ((GalleryFragment) frag).updateView();
//                        }
//                        else if(frag instanceof ImageFragment && frag.isVisible()){
//                            updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionFileSize, SortUtil.TypeASC);
//                            ((ImageFragment) frag).updateView();
//                        }
//                        Toast.makeText(getApplicationContext(), "sort file asc", Toast.LENGTH_SHORT).show();
//                    }
//                    else if(id1 == R.id.btnSortByFileSizeDecrease){
//                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
//                        if(frag instanceof GalleryFragment && frag.isVisible()){
//                            updateListByEachGroupInSort(SortUtil.CriterionFileSize, SortUtil.TypeDESC);
//                            ((GalleryFragment) frag).updateView();
//                        }
//                        else if(frag instanceof ImageFragment && frag.isVisible()){
//                            updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionFileSize, SortUtil.TypeDESC);
//                            ((ImageFragment) frag).updateView();
//                        }
//                        Toast.makeText(getApplicationContext(), "sort file desc", Toast.LENGTH_SHORT).show();
//                    }
//                    pm.show();
//                    return true;
//                }
//            });
//        }
        //special case: back-arrow on action bar
        else if(id == R.id.btnSortByDateIncrease){
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if(frag instanceof GalleryFragment && frag.isVisible()){
                updateAllListsInSort(SortUtil.CriterionDateAdded, SortUtil.TypeASC);
                ((GalleryFragment) frag).forceUpdateView();
            }
            else if(frag instanceof ImageFragment && frag.isVisible()){
                updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionDateAdded, SortUtil.TypeASC);
                ((ImageFragment) frag).updateView();
            }
            Toast.makeText(getApplicationContext(), "sort date asc", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.btnSortByDateDecrease){
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if(frag instanceof GalleryFragment && frag.isVisible()){
                updateAllListsInSort(SortUtil.CriterionDateAdded, SortUtil.TypeDESC);
                ((GalleryFragment) frag).forceUpdateView();
            }
            else if(frag instanceof ImageFragment && frag.isVisible()){
                updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionDateAdded, SortUtil.TypeDESC);
                ((ImageFragment) frag).updateView();
            }
            Toast.makeText(getApplicationContext(), "sort date desc", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.btnSortByNameIncrease){
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if(frag instanceof GalleryFragment && frag.isVisible()){
                updateListByEachGroupInSort(SortUtil.CriterionName, SortUtil.TypeASC);
                ((GalleryFragment) frag).forceUpdateView();
            }
            else if(frag instanceof ImageFragment && frag.isVisible()){
                updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionName, SortUtil.TypeASC);
                ((ImageFragment) frag).updateView();
            }
            Toast.makeText(getApplicationContext(), "sort name asc", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.btnSortByNameDecrease){
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if(frag instanceof GalleryFragment && frag.isVisible()){
                updateListByEachGroupInSort(SortUtil.CriterionName, SortUtil.TypeDESC);
                ((GalleryFragment) frag).forceUpdateView();
            }
            else if(frag instanceof ImageFragment && frag.isVisible()){
                updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionName, SortUtil.TypeDESC);
                ((ImageFragment) frag).updateView();
            }
            Toast.makeText(getApplicationContext(), "sort name desc", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.btnSortByFileSizeIncrease){
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if(frag instanceof GalleryFragment && frag.isVisible()){
                updateListByEachGroupInSort(SortUtil.CriterionFileSize, SortUtil.TypeASC);
                ((GalleryFragment) frag).forceUpdateView();
            }
            else if(frag instanceof ImageFragment && frag.isVisible()){
                updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionFileSize, SortUtil.TypeASC);
                ((ImageFragment) frag).updateView();
            }
            Toast.makeText(getApplicationContext(), "sort file asc", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.btnSortByFileSizeDecrease){
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if(frag instanceof GalleryFragment && frag.isVisible()){
                updateListByEachGroupInSort(SortUtil.CriterionFileSize, SortUtil.TypeDESC);
                ((GalleryFragment) frag).forceUpdateView();
            }
            else if(frag instanceof ImageFragment && frag.isVisible()){
                updateCurAlbumInSort(album_list.get(curIdxAlbum), SortUtil.CriterionFileSize, SortUtil.TypeDESC);
                ((ImageFragment) frag).updateView();
            }
            Toast.makeText(getApplicationContext(), "sort file desc", Toast.LENGTH_SHORT).show();
        }
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
            menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
            menu.findItem(R.id.btnSort).setVisible(true);
            menu.findItem(R.id.btnDeleteAlbum).setVisible(true);
            menu.findItem(R.id.btnSlideShow).setVisible(true);
            menu.findItem(R.id.btnSort).setVisible(false);
            //2nd argument is album
            curIdxAlbum = 0;
            for(int i=0;i<album_list.size();i++){
                if(album_list.get(i).getPath().equals(strValue)){
                    curIdxAlbum=i;
                    break;
                }
            }
            SharedPreferences myPref = getSharedPreferences("ALBUM", Activity.MODE_PRIVATE);
            String dataStr = myPref.getString(album_list.get(curIdxAlbum).getName(), null);
            if(dataStr != null){
                Gson gson = new Gson();
                ArrayList<Integer> data = gson.fromJson(dataStr, new TypeToken<ArrayList<Integer>>() {}.getType());
                if(data.get(0) != SortUtil.CriterionDateAdded && data.get(1) != SortUtil.TypeDESC){
                    updateCurAlbumInSort(album_list.get(curIdxAlbum), data.get(0), data.get(1));
                }
            }
            ImageFragment imageFragment = new ImageFragment(this, album_list.get(curIdxAlbum));
            this.imageFragment = imageFragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, imageFragment);
            ft.addToBackStack("ALBUM-FRAG");
            ft.commit();
            this.onChooseAlbum= strValue;
        }
        else if (sender.equals("DELETE-ALBUM"))
        {
            getSupportFragmentManager().popBackStack();
        }
        else if (sender.equals("ADD-TO-ALBUM")){
            Log.d("ADD-TO-AlBUM", "AddToAlbum");
            Gson gson = new Gson();
            ArrayList<String> paths = gson.fromJson(strValue, new TypeToken<ArrayList<String>>() {}.getType());
            AddImageToAlbumFragment addImageToAlbumFragment = new AddImageToAlbumFragment(this, album_list, paths);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, addImageToAlbumFragment);
            ft.addToBackStack("ALBUM-FRAG");
            ft.commit();
        }
        else if(sender.equals("SETTING")){
            if(strValue.equals("BACK-UP")){
                // change to TextToImageActivity
                Intent intent = new Intent(MainActivity.this, ShowBackupActivity.class);
                startActivity(intent);
                finish();
            }
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
                ArrayList<Long> addFavId = gson.fromJson(addFavorite, new TypeToken<ArrayList<Long>>() {}.getType());
                ArrayList<Long> removeFavId = gson.fromJson(removeFavorite, new TypeToken<ArrayList<Long>>() {}.getType());
                if (addFavId != null) {
                    for (int i = 0; i < addFavId.size(); ++i) {
                        Image image = allImagesInMap.get(addFavId.get(i));
                        if(image != null) image.setFavorite(true);
                    }
                }
                if (removeFavId != null) {
                    for (int i = 0; i < removeFavId.size(); ++i) {
                        Image image = allImagesInMap.get(removeFavId.get(i));
                        if(image != null) image.setFavorite(false);
                    }
                }

                //TODO: handle images location
                String addLocation = data.getStringExtra("addLocation");
                String removeLocation = data.getStringExtra("removeLocation");
                ArrayList<Long> addLocationId = gson.fromJson(addLocation, new TypeToken<ArrayList<Long>>() {
                }.getType());
                ArrayList<Long> removeLocationId = gson.fromJson(removeLocation, new TypeToken<ArrayList<Long>>() {
                }.getType());
                if (addLocationId != null) {
                    for (int i = 0; i < addLocationId.size(); ++i) {
                        Double latitude = data.getDoubleExtra(addLocationId.get(i).toString()+ "-LATITUDE",-1.0);
                        Double longitude = data.getDoubleExtra(addLocationId.get(i).toString()+ "-LONGITUDE",-1.0);
                        String stringLocation = data.getStringExtra(addLocationId.get(i).toString()+ "-STRINGLOCATION");
                        Image image = allImagesInMap.get(addLocationId.get(i));
                        LatLng latLng = new LatLng(latitude,longitude);
                        image.setLocation(latLng,stringLocation);
                        Log.d("CheckImg", String.valueOf(image.getLocation()));
                    }
                }
                if (removeLocationId != null) {
                    for (int i = 0; i < removeLocationId.size(); ++i) {
                        Image image = allImagesInMap.get(removeLocationId.get(i));
                        image.setLocation(null,null);
                    }
                }
                saveImagesLocation();
            }
            catch (Exception e){
                Log.d("onActivityResult() MainActivity", e.getMessage());
            }
        }else if(requestCode == 2233 && resultCode==AppCompatActivity.RESULT_OK){
            trashItems = data.getParcelableArrayListExtra("trashItems");
        } else if(requestCode==1123 && resultCode==AppCompatActivity.RESULT_OK) {
            String delete = data.getStringExtra("Trash");
            Gson gson = new Gson();
            //kiểm tra có danh sách ảnh bị xoá hay không
            if (delete != null && !delete.isEmpty()) {
                ArrayList<String> delete_paths = gson.fromJson(delete, new TypeToken<ArrayList<String>>() {
                }.getType());
                hideFragment.removeImage(delete_paths);
                //xoá các ảnh cần xoá
                for (int i = 0; i < delete_paths.size(); i++) {
//                    gallery_fragment.deleteImage(delete_paths.get(i));

                    //xoá trong các album
                    for (int j = 0; j < album_list.size(); j++) {
                        if (album_list.get(j).removeImageFromAlbum(delete_paths.get(i))) {
                            break;
                        }
                    }
                }
            }
            String added = data.getStringExtra("Unhide");
            //kiểm tra có danh sách ảnh bị xoá hay không
            if (added != null && !added.isEmpty()) {
                ArrayList<String> add_paths = gson.fromJson(added, new TypeToken<ArrayList<String>>() {
                }.getType());
                //xoá các ảnh cần xoá
//                gallery_fragment.addImage(add_paths);
                hideFragment.removeImage(add_paths);
                for (int i = 0; i < add_paths.size(); i++) {
                    //xoá trong các album
                    for (int j = 0; j < album_list.size(); j++) {
                        String albumpath = album_list.get(i).getPath();
                        if (add_paths.get(i).contains(albumpath) && add_paths.get(i).lastIndexOf("/") == albumpath.length()) {
                            album_list.get(j).addImageToAlbum(new Image(add_paths.get(i)));
                        }

                    }
                }
            }
        } else if(resultCode == AppCompatActivity.RESULT_OK && requestCode == 1){
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                Gson gson = new Gson();
                String urisStr = data.getStringExtra("delete-uris");
                ArrayList<Uri> urisArr = gson.fromJson(urisStr, new TypeToken<ArrayList<Uri>>() {}.getType());
                for(int i = 0; i < urisArr.size(); ++i){
                    getContentResolver().delete(urisArr.get(i), null, null);
                }
            }
            Toast.makeText(this, R.string.delete_photo_success, Toast.LENGTH_SHORT).show();
        }else if (requestCode==1125 && resultCode ==RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Toast.makeText(this, R.string.load_camera_img, Toast.LENGTH_SHORT).show();
            try{
                new AddImageFromCamera(getBaseContext()).execute(photo);
            }catch (Exception e){
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
            String r=   getString(R.string.download);
            album_list.add(new Album(r,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()));

            Gson gson=new Gson();
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File sdFile = new File(picturePath);
            final boolean[] rootExist = {false};
            File[] foldersSd = sdFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    //check if exist image outside all folder in SD
                    if(s.contains(".")){
                        rootExist[0] =true;
                    }
                    return !s.contains(".");
                }
            });
            if(rootExist[0]){
                String album_name=picturePath.substring(picturePath.lastIndexOf("/")+1);
                Album a=new Album(album_name,picturePath);
                album_list.add(a);
            }
            for(File file:foldersSd){
                String album_name=file.getPath().substring(file.getPath().lastIndexOf("/")+1);
                Album a=new Album(album_name,file.getPath());
                album_list.add(a);
            }
            rootExist[0]=false;
            String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            File dcimFile = new File(dcimPath);
            File[] foldersDCIM = dcimFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    //check if exist image outside all folder in DCIM
                    if(s.contains(".")){
                        rootExist[0]=true;
                    }
                    return !s.contains(".");
                }
            });
            for(File file:foldersDCIM){
                Album a=new Album(file.getPath().substring(file.getPath().lastIndexOf("/")+1),file.getPath());
                album_list.add(a);
            }
            if(rootExist[0]){
                String album_name=dcimPath.substring(dcimPath.lastIndexOf("/")+1);
                Album a=new Album(album_name,dcimPath);
                album_list.add(a);
            }

        }
    public void loadAllAlbumData(ArrayList<Image> images){
        for(int i=0;i<images.size();i++){
            for(int j=0;j<album_list.size();j++){
                Image image=images.get(i);
                Album album=album_list.get(j);
                String imagepath=image.getPath();
                String albumpath=album.getPath();
                if(imagepath.contains(albumpath) &&
                        (imagepath.lastIndexOf("/"))==albumpath.length()){
                    album_list.get(j).addImageToAlbum(images.get(i));
                }

            }
        }
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
                    try{
                    File f = new File(key);
                    if(f.delete()){
                        Log.d("delete expired image", "juan e nhe: " + key);
                    } else {
                        Log.d("delete expired image", "kho e a: " + key);
                    }}
                    catch (Exception e){

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
    public void DeLeteAImage(Image image)
    {
        File imageFile = new File(image.getPath());
        Gson gson = new Gson();
        String sourceFolderPath = imageFile.getParent();
        String destinationFolderPath = "/storage/emulated/0/Android/data/com.example.gallery/files/Trash";   // path of trash
        File file = new File(image.getPath());
        String imageName = file.getName();
        String sourceFilePath = sourceFolderPath + File.separator + imageName;
        String destinationFilePath = destinationFolderPath + File.separator + imageName;
        FileManager.moveFile(this,sourceFilePath,destinationFilePath,this.getApplicationContext());
        SharedPreferences myPref = getSharedPreferences("TRASH", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        ArrayList<String> data = new ArrayList<>(2);
        data.add(sourceFolderPath);
        Date dateExpires = DateConverter.plusTime(new Date(), 30, Calendar.DATE);
        data.add(DateConverter.longToString(dateExpires.getTime()));
        editor.putString(destinationFilePath,gson.toJson(data));
        editor.apply();
        trashItems.add(new TrashItem(destinationFilePath, image.getPath(),DateConverter.plusTime(new Date(), 30, Calendar.DATE).getTime()));
        return;
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
    //change language of application
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }

    public void showHideFragment(){
        hideFragment= HideFragment.getInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,hideFragment).commit();
    }
    public void showHideFragmentDialog(){
        Dialog addDialog=new Dialog(this);
        addDialog.setContentView(R.layout.access_hidefragment_dialog);
        EditText editText=addDialog.findViewById(R.id.confirmPasswordEditText1);
        Button ok=addDialog.findViewById(R.id.btnOKConfirmPassword);
        Button cancel=addDialog.findViewById(R.id.btnCancelConfirmPassword);
        Button reset=addDialog.findViewById(R.id.btnResetPassword);
        addDialog.create();
        addDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences hidePref=getSharedPreferences("GALLERY",MODE_PRIVATE);
                String pass=hidePref.getString("PASSWORD",null);
                if(pass.equals(editText.getText().toString())){
                    //show hide frag
                    showHideFragment();
                    addDialog.cancel();
                }else{
                    Toast.makeText(MainActivity.this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.cancel();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences hidePref=getSharedPreferences("GALLERY",MODE_PRIVATE);
                SharedPreferences.Editor editor=hidePref.edit();
                editor.putString("PASSWORD",null);
                Calendar calendar=Calendar.getInstance();
                int curr_date=calendar.get(Calendar.DATE);
                int curr_month=calendar.get(Calendar.MONTH);
                int curr_year=calendar.get(Calendar.YEAR);
                editor.putInt("OPEN-DATE",curr_date);
                editor.putInt("OPEN-MONTH",curr_month);
                editor.putInt("OPEN-YEAR",curr_year);
                editor.commit();
                addDialog.cancel();
            }
        });
    }
    public void createPasswordHideFragmentDialog(){
        Dialog addDialog=new Dialog(this);
        addDialog.setContentView(R.layout.create_password_hidefrag_dialog);
        EditText password=addDialog.findViewById(R.id.createPasswordEditText1);
        EditText confirmPass=addDialog.findViewById(R.id.createPasswordEditText2);
        Button ok=addDialog.findViewById(R.id.btnOKCreatePassword);
        Button cancel=addDialog.findViewById(R.id.btnCancelCreatePassword);
        addDialog.create();
        addDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=password.getText().toString();
                String confirm=confirmPass.getText().toString();
                if(!pass.equals(confirm)){
                    Toast.makeText(MainActivity.this, R.string.retype_password_not_match, Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences hidePref=getSharedPreferences("GALLERY",MODE_PRIVATE);
                    SharedPreferences.Editor editor=hidePref.edit();
                    editor.putString("PASSWORD",pass);
                    editor.commit();
                    addDialog.cancel();
                    //show hide fragment
                    showHideFragment();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.cancel();
            }
        });
    }
    public void scheduleNotification(Calendar calendar,String title, String message,String channel) {
        Intent intent = new Intent(getApplicationContext(), Notification.class);
        intent.putExtra("title", title);
        intent.putExtra("mess", message);
        intent.putExtra("channel",channel);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
    private void createNotificationChannel(String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void saveImagesLocation(){
        Gson gson = new Gson();
        SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        ArrayList<Long> imagesLocationList = new ArrayList<>();
        for(int i = 0; i < allImagesInMap.size(); ++i){
            if(allImages.get(i).getLocation()!= null){
                imagesLocationList.add(Long.valueOf(allImages.get(i).getIdInMediaStore()));
                editor.putFloat(allImages.get(i).getIdInMediaStore() + "-LATITUDE", (float) allImages.get(i).getLocation().latitude);
                editor.putFloat(allImages.get(i).getIdInMediaStore() + "-LONGITUDE", (float) allImages.get(i).getLocation().longitude);
                editor.putString(allImages.get(i).getIdInMediaStore() + "-STRINGLOCATION",(String) allImages.get(i).getStringLocation());
            }
        }
        editor.putString("IMAGES-ID-LOCATION", gson.toJson(imagesLocationList));
        editor.apply();
    }

    public void loadImagesLocation(){
        Gson gson = new Gson();
        SharedPreferences myPref = getSharedPreferences("GALLERY",Activity.MODE_PRIVATE);
        String imgIds = myPref.getString("IMAGES-ID-LOCATION", null);
        ArrayList<Long> id = gson.fromJson(imgIds, new TypeToken<ArrayList<Long>>(){}.getType());
        if(id != null){
            for (int i = 0 ; i < id.size(); i++){
                if(allImagesInMap.get(id.get(i)) != null){
                    Float latitude = (float) myPref.getFloat(id.get(i)+"-LATITUDE",-1);
                    Float longitude = (float) myPref.getFloat(id.get(i)+"-LONGITUDE",-1);
                    String stringLocation = (String) myPref.getString(id.get(i)+"-STRINGLOCATION","");
                    Log.d("CheckImg",latitude+ "_"+stringLocation);
                    LatLng latLng = new LatLng(latitude,longitude);
                    allImagesInMap.get(id.get(i)).setLocation(latLng,stringLocation);

                }
            }
        }
    }

    public void updateListByEachGroupInSort(int criterion, int type){
        SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        allImages = new ArrayList<>();
        for(int i = 0; i < imageGroupsByDate.size(); ++i){
            ArrayList<Image> images = SortUtil.sort(imageGroupsByDate.get(i).getList(), criterion, type);
            imageGroupsByDate.get(i).setList(images);
            allImages.addAll(images);
        }
        editor.putInt("SORT-GROUP-CRITERION", criterion);
        editor.putInt("SORT-GROUP-TYPE", type);
        editor.apply();
    }

    public void updateAllListsInSort(int criterion, int type){
        SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        SortUtil.sort(allImages, criterion, type);
        imageGroupsByDate = LocalStorageReader.getListImageGroupByDate(allImages);
        editor.putInt("SORT-CRITERION", criterion);
        editor.putInt("SORT-TYPE", type);
        editor.apply();
    }

    public void updateCurAlbumInSort(Album album, int criterion, int type){
        SharedPreferences myPref = getSharedPreferences("ALBUM", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        SortUtil.sort(album.getAll_album_pictures(), criterion, type);
        Gson gson = new Gson();
        ArrayList<Integer> data = new ArrayList<>();
        data.add(criterion);
        data.add(type);
        editor.putString(album.getName(), gson.toJson(data));
        editor.apply();
    }

    public class ImageLoaderTask extends AsyncTask<ArrayList<Image>, Void, Void> {
        private Context context;

        public ImageLoaderTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(ArrayList<Image>... params) {
            allImages = params[0];

            SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
            int sortCriterion = myPref.getInt("SORT-CRITERION", -1);
            int sortType = myPref.getInt("SORT-TYPE", -1);
            int sortGroupCriterion = myPref.getInt("SORT-GROUP-CRITERION", -1);
            int sortGroupType = myPref.getInt("SORT-GROUP-TYPE", -1);
            if(sortCriterion == SortUtil.CriterionDateAdded && sortType == SortUtil.TypeASC){
                SortUtil.sort(allImages, sortCriterion, sortType);
            }
            imageGroupsByDate = LocalStorageReader.getListImageGroupByDate(allImages);
            if (sortGroupCriterion != -1 && sortGroupType != -1){
                allImages = new ArrayList<>();
                for(int i = 0; i < imageGroupsByDate.size(); ++i){
                    ArrayList<Image> images = SortUtil.sort(imageGroupsByDate.get(i).getList(), sortGroupCriterion, sortGroupType);
                    imageGroupsByDate.get(i).setList(images);
                    allImages.addAll(images);
                }
            }

            loadAllAlbum();
            loadAllAlbumData(allImages);

            allImagesInMap = new HashMap<>();
            for (int i = 0; i < allImages.size(); ++i) {
                allImagesInMap.put(allImages.get(i).getIdInMediaStore(), allImages.get(i));
            }
            if(!updateViewManually){
                loadDeleteImage(); // delete expired images when loading images
            }
            loadFavouriteImage();
            loadImagesLocation();


            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if(frag instanceof ImageFragment && frag.isVisible()){
                SharedPreferences albumPref = getSharedPreferences("ALBUM", Activity.MODE_PRIVATE);
                String dataStr = albumPref.getString(album_list.get(curIdxAlbum).getName(), null);
                if(dataStr != null){
                    Gson gson = new Gson();
                    ArrayList<Integer> data = gson.fromJson(dataStr, new TypeToken<ArrayList<Integer>>() {}.getType());
                    if(data.get(0) != SortUtil.CriterionDateAdded && data.get(1) != SortUtil.TypeDESC){
                        updateCurAlbumInSort(album_list.get(curIdxAlbum), data.get(0), data.get(1));
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            gallery_fragment.updateView();
            album_fragment.updateView();
            if (imageFragment != null) {
                imageFragment.updateView();
            }
        }

    }
}
