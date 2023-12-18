package com.example.gallery.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gallery.Animation.ViewPagerTransformAnimation;
import com.example.gallery.ImageActivity;
import com.example.gallery.R;
import com.example.gallery.adapter.ImageViewPagerHideAdapter;
import com.example.gallery.helper.FileManager;
import com.example.gallery.object.Image;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;

public class HidePagerFragment extends Fragment {
    private Context context;
    private ViewPager2 imageViewPager2;
    private ImageViewPagerHideAdapter viewPagerAdapter;
    private Toolbar topBar;
    private ArrayList<Image> images;
    private int curPos = 0;
    Button btnRedo, btnDelete;

    public HidePagerFragment(Context context, ArrayList<Image> images, int curPos) {
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
        View view = inflater.inflate(R.layout.hide_fullpicture_fragment, container, false);
        imageViewPager2 = view.findViewById(R.id.view_pagerhidefrag);
        btnRedo = view.findViewById(R.id.btnUnHide);
        btnDelete = view.findViewById(R.id.btnDeleteInHideFrag);
        topBar=view.findViewById(R.id.topAppBarHideFrag);
        // set data
        viewPagerAdapter = new ImageViewPagerHideAdapter(context, images);
        imageViewPager2.setAdapter(viewPagerAdapter);
        imageViewPager2.setCurrentItem(curPos, false);
        imageViewPager2.setPageTransformer(new ViewPagerTransformAnimation());
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=imageViewPager2.getCurrentItem();
                SharedPreferences hidePref= context.getSharedPreferences("GALLERY",Context.MODE_PRIVATE);
                String hideCurrentJSON=hidePref.getString("HIDE-CURRENT",null);
                String hideBeforeJSON=hidePref.getString("HIDE-BEFORE",null);
                Gson gson=new Gson();
                ArrayList<String> hideCurrent=gson.fromJson(hideCurrentJSON,new TypeToken<ArrayList<String>>(){}.getType());
                ArrayList<String> hideBefore=gson.fromJson(hideBeforeJSON,new TypeToken<ArrayList<String>>(){}.getType());
                if(new File(images.get(pos).getPath()).delete()){
                    hideBefore.remove(pos);
                    hideCurrent.remove(pos);
                    hideBeforeJSON=gson.toJson(hideBefore);
                    hideCurrentJSON=gson.toJson(hideCurrent);
                    SharedPreferences.Editor editor=hidePref.edit();
                    editor.putString("HIDE-BEFORE",hideBeforeJSON);
                    editor.putString("HIDE-CURRENT",hideCurrentJSON);
                    editor.commit();
                    //update intent
                    Intent intent=((ImageActivity)context).getIntent();
                    ArrayList<String> deletePaths=null;
                    String deleteJSON=intent.getStringExtra("Trash");
                    if(deleteJSON==null){
                        deletePaths=new ArrayList<>();
                    }else{
                        deletePaths=gson.fromJson(deleteJSON,new TypeToken<ArrayList<String>>(){}.getType());
                    }
                    deletePaths.add(images.get(pos).getPath());
                    intent.putExtra("Trash",gson.toJson(deletePaths));
                    ((ImageActivity)context).setResult(AppCompatActivity.RESULT_OK, intent);
                    images.remove(imageViewPager2.getCurrentItem());
                    imageViewPager2.getAdapter().notifyItemRemoved(pos);
                    if(images.size()==0){
                        getActivity().finish();
                    }
                }

                else{
                    Toast.makeText(context, R.string.cannot_delete, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=imageViewPager2.getCurrentItem();
                SharedPreferences hidePref= context.getSharedPreferences("GALLERY",Context.MODE_PRIVATE);
                String hideCurrentJSON=hidePref.getString("HIDE-CURRENT",null);
                String hideBeforeJSON=hidePref.getString("HIDE-BEFORE",null);
                Gson gson=new Gson();
                ArrayList<String> hideCurrent=gson.fromJson(hideCurrentJSON,new TypeToken<ArrayList<String>>(){}.getType());
                ArrayList<String> hideBefore=gson.fromJson(hideBeforeJSON,new TypeToken<ArrayList<String>>(){}.getType());
                String beforePath=hideBefore.get(pos);
                if(FileManager.moveFile(getActivity(),images.get(pos).getPath(),beforePath,context)){
                    hideBefore.remove(pos);
                    hideCurrent.remove(pos);
                    hideBeforeJSON=gson.toJson(hideBefore);
                    hideCurrentJSON=gson.toJson(hideCurrent);
                    SharedPreferences.Editor editor=hidePref.edit();
                    editor.putString("HIDE-BEFORE",hideBeforeJSON);
                    editor.putString("HIDE-CURRENT",hideCurrentJSON);
                    editor.commit();
                    //update intent
                    Intent intent=((ImageActivity)context).getIntent();
                    ArrayList<String> deletePaths=null;
                    String deleteJSON=intent.getStringExtra("Unhide");
                    if(deleteJSON==null){
                        deletePaths=new ArrayList<>();
                    }else{
                        deletePaths=gson.fromJson(deleteJSON,new TypeToken<ArrayList<String>>(){}.getType());
                    }
                    deletePaths.add(images.get(pos).getPath());
                    intent.putExtra("Unhide",gson.toJson(deletePaths));
                    ((ImageActivity)context).setResult(AppCompatActivity.RESULT_OK,intent);

                    images.remove(imageViewPager2.getCurrentItem());
                    imageViewPager2.getAdapter().notifyItemRemoved(pos);
                    if(images.size()==0){
                        getActivity().finish();
                    }
                }else{
                    Toast.makeText(context, R.string.cannot_unhide, Toast.LENGTH_SHORT).show();
                }
            }
        });

        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        return view;
    }
}

