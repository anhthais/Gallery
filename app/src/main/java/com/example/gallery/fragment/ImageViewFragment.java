package com.example.gallery.fragment;

import android.app.Activity;
import android.app.WallpaperManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.Toolbar;

import com.example.gallery.ImageActivity;
import com.example.gallery.R;
import com.example.gallery.ToolbarCallbacks;
import com.example.gallery.adapter.ImageViewPagerAdapter;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ImageViewFragment extends Fragment implements ToolbarCallbacks {
    private Context context;
    private ViewPager2 imageViewPager2;
    private ImageViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView btnv;
    private ArrayList<String> albums;
    private Menu menu;
    private Toolbar topBar;
    private boolean isSystemUiVisible = true;
    private ArrayList<Image> images;
    private int curPos = 0;

    public ImageViewFragment(Context context, ArrayList<Image> images,ArrayList<String> albums, int curPos) {
        this.context = context;
        this.albums=albums;
        this.images = images;
        this.curPos = curPos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // set views
        View view = inflater.inflate(R.layout.picture_fragment, container, false);
        imageViewPager2 = view.findViewById(R.id.view_pager);
        btnv = view.findViewById(R.id.navigation_bar_picture);
        topBar = view.findViewById(R.id.topAppBar);

        // set data
        viewPagerAdapter = new ImageViewPagerAdapter(context, images);
        viewPagerAdapter.setToolbarCallbacks(this);
        imageViewPager2.setAdapter(viewPagerAdapter);
        imageViewPager2.setCurrentItem(curPos, false);
        imageViewPager2.setPageTransformer(new MarginPageTransformer(Math.round(32*(getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT))));

        // handle topBar
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        topBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.btnSetAsWall) {
                    Bitmap bitmap = BitmapFactory.decodeFile(images.get(curPos).getPath());
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

                    try {
                        wallpaperManager.setBitmap(bitmap);
                        Toast.makeText(context, "Wallpaper Set Successfully!!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Setting WallPaper Failed!!", Toast.LENGTH_SHORT).show();
                    }

                }
                else if(id==R.id.btnAddToAlbum){
                    if(albums==null || albums.size()==0){
                        Toast.makeText(context, "No album found", Toast.LENGTH_SHORT).show();
                    }
                    else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.MyDialogTheme);

                    // Set Title.
                    builder.setTitle("Thêm vào album");

                    // Add a list
                    String[] arr=new String[albums.size()];
                    for(int i=0;i<albums.size();i++){
                        arr[i]=albums.get(i);
                    }

                    int checkedItem = 0; // Sheep
                    final Set<String> selectedItems = new HashSet<String>();
                    selectedItems.add(arr[checkedItem]);

                    builder.setSingleChoiceItems(arr, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do Something...
                            selectedItems.clear();
                            selectedItems.add(arr[which]);
                        }
                    });

                    //
                    builder.setCancelable(true);

                    // Create "Yes" button with OnClickListener.
                    builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(selectedItems.isEmpty()) {
                                Toast.makeText(context, "Chọn Album", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String album_name = selectedItems.iterator().next();
                            //put the new image to album
                            //intent với key: tên album
                            Intent intent=((ImageActivity)context).getIntent();
                            Gson gson=new Gson();
                            String json=intent.getStringExtra(album_name);
                            ArrayList<String> album_image;
                            if(json==null){
                                album_image=new ArrayList<String>();
                            }else{
                                album_image=gson.fromJson(json,new TypeToken<ArrayList<String>>(){}.getType());
                            }
                            album_image.add(images.get(imageViewPager2.getCurrentItem()).getPath());
                            intent.putExtra(album_name,gson.toJson(album_image));
                            //setResult để mainActivity xử lý khi ImageActivity kết thúc
                            ((ImageActivity)context).setResult(AppCompatActivity.RESULT_OK,intent);
                            //update in sharedprefence
                            updateAddImageToAlbum(album_name,images.get(imageViewPager2.getCurrentItem()).getPath());
                            // Close Dialog
                            dialog.dismiss();

                        }
                    });

                    // Create "Cancel" button with OnClickListener.
                    builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    // Create AlertDialog:
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                }
                return true;
            }
        });

        // handle bottom navigation bar
        btnv.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.btnLovePicture) {
                // TODO: check favorite
                item.setIcon(R.drawable.baseline_favorite_24);
            } else if (id == R.id.btnEditPicture) {

            } else if (id == R.id.btnSharePicture) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                Uri uriToImage= Uri.parse(new File(images.get(imageViewPager2.getCurrentItem()).getPath()).toString());
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, null));

            } else if (id == R.id.btnDeletePicture) {
                AlertDialog.Builder builder =new AlertDialog.Builder(context,R.style.MyDialogTheme);
                builder.setTitle("Chuyển ảnh vào thùng rác");
                builder.setCancelable(true);
                builder.setPositiveButton("Chuyển", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        String delete_path=images.get(imageViewPager2.getCurrentItem()).getPath();
                        //only 1 image in adapter
                        if(curPos==0&&images.size()==1){
                            ((ImageActivity) context).finish();
                            images.remove(curPos);
                        }
                        //get position of current item in viewpager
                        curPos=imageViewPager2.getCurrentItem();
                        images.remove(curPos);
                        //if the item was the last item choose the previous item
                        if(curPos==images.size()){
                            curPos--;
                        }
                        //update viewpageradapter
                        imageViewPager2.setCurrentItem(curPos, false);
                        imageViewPager2.getAdapter().notifyDataSetChanged();
                        // thêm ảnh bị xoá vào arraylist -> intent với key: Trash
                        Intent intent=((ImageActivity)context).getIntent();
                        Gson gson=new Gson();
                        String json=intent.getStringExtra("Trash");
                        ArrayList<String> delete_image_paths;
                        if(json==null){
                            delete_image_paths=new ArrayList<String>();
                        }else{
                            delete_image_paths=gson.fromJson(json,new TypeToken<ArrayList<String>>(){}.getType());
                        }
                        delete_image_paths.add(delete_path);
                        intent.putExtra("Trash",gson.toJson(delete_image_paths));
                        ((ImageActivity)context).setResult(AppCompatActivity.RESULT_OK,intent);
                        updateDeleteImage(delete_path);

                        //update in database
                        SharedPreferences albumPref= context.getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor=albumPref.edit();
                        String allTrash=albumPref.getString("TRASH","");
                        ArrayList<String>allTrashPath=gson.fromJson(allTrash,new TypeToken<ArrayList<String>>(){}.getType());
                        if(allTrashPath==null){
                            allTrashPath=new ArrayList<>();
                        }
                        allTrashPath.add(delete_path);
                        String tobesaved=gson.toJson(allTrashPath);
                        editor.putString("TRASH",tobesaved);
                        editor.commit();
                    }
                });
                builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Cancel
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog= builder.create();
                alertDialog.show();
            }

            return true;
        });

        return view;
    }

    @Override
    public void showOrHideToolbars (boolean show){
        if(show){
            topBar.setVisibility(View.VISIBLE);
            btnv.setVisibility(View.VISIBLE);
        }
        else {
            topBar.setVisibility(View.INVISIBLE);
            btnv.setVisibility(View.INVISIBLE);
        }
    }
    //update sharepreference after adding an image to an album
    public void updateAddImageToAlbum(String album,String path){
        Gson gson=new Gson();
        SharedPreferences albumPref= context.getSharedPreferences("GALLERY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=albumPref.edit();
        String all_path=albumPref.getString(album,"");
        ArrayList<String> album_save=gson.fromJson(all_path,new TypeToken<ArrayList<String>>(){}.getType());
        if(album_save==null ){
            album_save=new ArrayList<>();
        }
        album_save.add(path);
        String albumjson=gson.toJson(album_save);
        editor.putString(album,albumjson);
        editor.commit();
    }
    //update sharepreference after delete an image
    public void updateDeleteImage(String path){
        for(int i=0;i<albums.size();i++){
            String album_name=albums.get(i);
            Gson gson=new Gson();
            SharedPreferences albumPref= context.getSharedPreferences("GALLERY", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=albumPref.edit();
            String all_path=albumPref.getString(album_name,"");
            ArrayList<String> album_save=gson.fromJson(all_path,new TypeToken<ArrayList<String>>(){}.getType());
            if(album_save==null ){
                album_save=new ArrayList<>();
            }
            album_save.remove(path);
            String albumjson=gson.toJson(album_save);
            editor.putString(album_name,albumjson);
            editor.commit();
        }
    }
}