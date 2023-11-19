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
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.Toolbar;

import com.example.gallery.ImageActivity;
import com.example.gallery.R;
import com.example.gallery.ToolbarCallbacks;
import com.example.gallery.adapter.ImageViewPagerAdapter;
import com.example.gallery.adapter.SlideShowAdapter;
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
import java.util.Timer;
import java.util.TimerTask;

public class SlideShowFragment extends Fragment {
    private Context context;
    private ViewPager2 imageViewPager2;
    private SlideShowAdapter viewPagerAdapter;
    private BottomNavigationView btnv;
    private Menu menu;
    private Toolbar topBar;
    private boolean stop = false;
    private ArrayList<Image> images;
    private int curPos = 0;
    private  Timer timer;
    private boolean reverse=false;
    final long DELAY_MS = 0;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;

    public SlideShowFragment(Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
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
        topBar.inflateMenu(R.menu.slide_show_menu);
        menu=topBar.getMenu();
        menu.findItem(R.id.btnChangeDescription).setVisible(false);
        menu.findItem(R.id.btnViewInfor).setVisible(false);
        menu.findItem(R.id.btnCompress).setVisible(false);
        menu.findItem(R.id.btnAddToAlbum).setVisible(false);
        menu.findItem(R.id.btnReadTextInImage).setVisible(false);
        menu.findItem(R.id.btnSetAsWall).setVisible(false);

        btnv.setVisibility(View.INVISIBLE);
        // set data
        viewPagerAdapter = new SlideShowAdapter(context, images);
        imageViewPager2.setAdapter(viewPagerAdapter);
        imageViewPager2.setCurrentItem(curPos, false);
        imageViewPager2.setPageTransformer(new MarginPageTransformer(Math.round(32*(getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT))));
        imageViewPager2.setUserInputEnabled(false);
        // handle topBar
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                if(timer!=null){
                    timer.cancel();
                    timer.purge();
                }
            }
        });
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if(reverse){
                    imageViewPager2.setCurrentItem(curPos--);
                }else{
                    imageViewPager2.setCurrentItem(curPos++);
                }
                if(curPos==images.size()-1){
                    reverse=!reverse;
                    curPos=images.size()-1;
                }else if(curPos==0){
                    reverse=!reverse;
                    curPos=0;
                }
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
        topBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.btnStop){
                    if(stop){
                        stop=!stop;
                        item.setTitle("Tạm dừng");
                        final Handler handler = new Handler();
                        final Runnable Update = new Runnable() {
                            public void run() {
                                if(reverse){
                                    imageViewPager2.setCurrentItem(curPos--);
                                }else{
                                    imageViewPager2.setCurrentItem(curPos++);
                                }
                                if(curPos==images.size()){
                                    reverse=!reverse;
                                    curPos=images.size()-1;
                                }else if(curPos==-1){
                                    reverse=!reverse;
                                    curPos=0;
                                }
                            }
                        };

                        timer = new Timer(); // This will create a new Thread
                        timer.schedule(new TimerTask() { // task to be scheduled
                            @Override
                            public void run() {
                                handler.post(Update);
                            }
                        }, DELAY_MS, PERIOD_MS);

                    }else{
                        stop=!stop;
                        timer.cancel();
                        timer.purge();
                        item.setTitle("Tiếp tục");
                    }
                }
                return false;
            }

        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(timer!=null){
            timer.cancel();
        }
    }
}