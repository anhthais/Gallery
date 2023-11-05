package com.example.gallery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.Toolbar;

import com.example.gallery.R;
import com.example.gallery.ToolbarCallbacks;
import com.example.gallery.adapter.ImageViewPagerAdapter;
import com.example.gallery.object.Image;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ImageViewFragment extends Fragment implements ToolbarCallbacks {
    private Context context;
    private ViewPager2 imageViewPager2;
    private ImageViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView btnv;
    private Menu menu;
    private Toolbar topBar;
    private boolean isSystemUiVisible = true;
    private ArrayList<Image> images;
    private int curPos = 0;

    public ImageViewFragment(Context context, ArrayList<Image> images, int curPos) {
        this.context = context;
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
                switch (id) {
                    //TODO: set events
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

            } else if (id == R.id.btnDeletePicture) {

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
}
